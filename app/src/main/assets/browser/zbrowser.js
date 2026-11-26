/*
 * Zafiro agent browser: DOM reader and actor, injected into every page by WebBrowserSession.
 * Installs window.__zb once per document. Every function returns a JSON string.
 * snapshot() output is parsed by WebSnapshotParser (Kotlin); keep the two in step.
 *   {"url","title","vw","vh","sx","sy","sw","sh","truncated","dialog",
 *    "n":[{"k":kind,"t":text,"d":desc,"r":[l,t,r,b] (css px, clipped to the viewport),
 *          "c":1 clickable,"e":1 editable,"s":1 scrollable,"x":1 checked}]}
 * kinds: btn link in txt h img item list sw chk tab box
 * Element references live in __zb.els; index i is the node number of the LAST snapshot.
 */
(function () {
  if (window.__zb && window.__zb.v === 1) return;

  var MAX_NODES = 300;
  var MAX_VISITS = 9000;
  var BUDGET_MS = 600;
  var els = [];

  var SKIP = {SCRIPT: 1, STYLE: 1, NOSCRIPT: 1, TEMPLATE: 1, HEAD: 1, META: 1, LINK: 1, SVG: 1, CANVAS: 0, IFRAME: 0, OPTION: 1, OPTGROUP: 1, DATALIST: 1};
  var INLINE = {A: 0, B: 1, I: 1, EM: 1, STRONG: 1, SPAN: 1, SMALL: 1, SUB: 1, SUP: 1, MARK: 1, ABBR: 1, CODE: 1, TIME: 1, U: 1, S: 1, CITE: 1, Q: 1, BR: 1, FONT: 1};
  var INTERACTIVE = 'a[href],button,input:not([type=hidden]),select,textarea,summary,[role=button],[role=link],[role=tab],[role=checkbox],[role=switch],[role=radio],[role=menuitem],[role=option],[role=textbox],[role=searchbox],[role=combobox],[onclick],[contenteditable=""],[contenteditable="true"]';
  var TEXT_INPUT = {text: 1, search: 1, email: 1, tel: 1, url: 1, number: 1, password: 1, date: 1, datetime: 1, 'datetime-local': 1, month: 1, week: 1, time: 1, '': 1};

  function norm(s, max) {
    s = (s == null ? '' : String(s)).replace(/\s+/g, ' ').trim();
    return s.length > max ? s.slice(0, max) : s;
  }

  function attr(el, n) { var v = el.getAttribute && el.getAttribute(n); return v ? v : ''; }

  function matches(el, sel) { try { return el.matches(sel); } catch (e) { return false; } }

  function labelOf(el) {
    var t = '';
    try {
      if (el.labels && el.labels.length) t = norm(el.labels[0].textContent, 80);
    } catch (e) {}
    if (!t && el.id) {
      var l = document.querySelector('label[for="' + String(el.id).replace(/"/g, '') + '"]');
      if (l) t = norm(l.textContent, 80);
    }
    return t;
  }

  function visibleStyle(cs) {
    return cs.visibility !== 'hidden' && cs.display !== 'none' && parseFloat(cs.opacity || '1') > 0.02;
  }

  function classify(el) {
    var tag = el.tagName;
    var role = attr(el, 'role');
    if (tag === 'INPUT') {
      var type = (el.type || 'text').toLowerCase();
      if (type === 'checkbox') return role === 'switch' ? 'sw' : 'chk';
      if (type === 'radio') return 'chk';
      if (type === 'submit' || type === 'button' || type === 'reset' || type === 'image' || type === 'file' || type === 'color' || type === 'range') return 'btn';
      return 'in';
    }
    if (tag === 'TEXTAREA' || tag === 'SELECT') return 'in';
    if (el.isContentEditable || role === 'textbox' || role === 'searchbox' || role === 'combobox') return 'in';
    if (role === 'switch') return 'sw';
    if (role === 'checkbox' || role === 'radio') return 'chk';
    if (role === 'tab') return 'tab';
    if (tag === 'A' || role === 'link') return 'link';
    return 'btn';
  }

  function isEditable(el) {
    var tag = el.tagName;
    if (tag === 'INPUT') return !!TEXT_INPUT[(el.type || 'text').toLowerCase()] && !el.readOnly && !el.disabled;
    if (tag === 'TEXTAREA') return !el.readOnly && !el.disabled;
    if (tag === 'SELECT') return !el.disabled;
    return !!el.isContentEditable || attr(el, 'role') === 'textbox' || attr(el, 'role') === 'searchbox';
  }

  function describe(el, kind) {
    var tag = el.tagName, text = '', desc = '';
    if (tag === 'INPUT' || tag === 'TEXTAREA') {
      var type = (el.type || 'text').toLowerCase();
      if (kind === 'btn') {
        text = norm(el.value || attr(el, 'aria-label') || attr(el, 'title') || (type === 'file' ? 'Choose file' : ''), 80);
      } else if (kind === 'chk' || kind === 'sw') {
        text = labelOf(el) || norm(attr(el, 'aria-label') || el.name, 80);
      } else {
        text = type === 'password' ? '' : norm(el.value, 120);
        desc = norm(attr(el, 'aria-label') || attr(el, 'placeholder') || labelOf(el) || el.name, 80);
        if (type === 'password') desc = (desc ? desc + ' ' : '') + '(password field)';
      }
    } else if (tag === 'SELECT') {
      var sel = el.options && el.options[el.selectedIndex];
      text = norm(sel ? sel.textContent : '', 80);
      var names = [];
      for (var i = 0; el.options && i < el.options.length && i < 6; i++) names.push(norm(el.options[i].textContent, 24));
      desc = norm((attr(el, 'aria-label') || labelOf(el) || el.name) + ' options: ' + names.join(' | '), 140);
    } else {
      var aria = norm(attr(el, 'aria-label'), 80);
      text = norm(el.innerText || el.textContent, 100);
      if (!text) {
        var img = el.querySelector && el.querySelector('img[alt],svg title');
        text = img ? norm(img.alt || img.textContent, 60) : '';
      }
      if (!text) text = norm(attr(el, 'title') || attr(el, 'value'), 60);
      if (aria && aria !== text) desc = aria;
      if (kind === 'link') {
        var href = attr(el, 'href');
        if (href && href.charAt(0) !== '#' && href.indexOf('javascript:') !== 0) {
          try { var u = new URL(href, location.href); desc = (desc ? desc + ' ' : '') + '-> ' + (u.host === location.host ? u.pathname + u.search : u.host + u.pathname); } catch (e) {}
          if (desc.length > 90) desc = desc.slice(0, 90);
        }
      }
    }
    return {t: text, d: desc};
  }

  function isChecked(el) {
    if (el.tagName === 'INPUT' && (el.type === 'checkbox' || el.type === 'radio')) return !!el.checked;
    var a = attr(el, 'aria-checked') || attr(el, 'aria-pressed') || attr(el, 'aria-selected');
    return a === 'true';
  }

  function inlineText(el, consumed) {
    var out = '';
    for (var n = el.firstChild; n; n = n.nextSibling) {
      if (n.nodeType === 3) {
        out += n.nodeValue + ' ';
      } else if (n.nodeType === 1) {
        var tag = n.tagName;
        if (INLINE[tag] === 1 && !SKIP[tag]) {
          if (tag === 'BR') { out += ' '; continue; }
          if (matches(n, INTERACTIVE) || n.querySelector(INTERACTIVE)) continue;
          var inner = inlineText(n, consumed);
          if (inner) { out += inner + ' '; consumed.push(n); }
        }
      }
    }
    return norm(out, 400);
  }

  function snapshot() {
    var t0 = Date.now();
    var vw = window.innerWidth || document.documentElement.clientWidth || 0;
    var vh = window.innerHeight || document.documentElement.clientHeight || 0;
    var out = [];
    var visits = 0;
    var truncated = false;
    var consumed = [];
    els = [];

    function push(el, kind, text, desc, r, c, e, s, x) {
      if (out.length >= MAX_NODES) { truncated = true; return false; }
      els.push(el);
      out.push({k: kind, t: text, d: desc, r: r, c: c ? 1 : 0, e: e ? 1 : 0, s: s ? 1 : 0, x: x ? 1 : 0});
      return true;
    }

    function clip(rect) {
      var l = Math.max(0, Math.round(rect.left)), t = Math.max(0, Math.round(rect.top));
      var r = Math.min(vw, Math.round(rect.right)), b = Math.min(vh, Math.round(rect.bottom));
      return [l, t, r, b];
    }

    function inView(rect) {
      return rect.width > 0 && rect.height > 0 && rect.bottom > 0 && rect.top < vh && rect.right > 0 && rect.left < vw;
    }

    function covered(el, rect) {
      if (el.getRootNode && el.getRootNode() !== document) return false;
      var cx = Math.min(vw - 1, Math.max(0, (rect.left + rect.right) / 2));
      var cy = Math.min(vh - 1, Math.max(0, (rect.top + rect.bottom) / 2));
      var hit = document.elementFromPoint(cx, cy);
      if (!hit) return false;
      return !(hit === el || el.contains(hit) || hit.contains(el));
    }

    function scrollableBox(el) {
      if (el === document.body || el === document.documentElement) return false;
      if (el.scrollHeight <= el.clientHeight + 8 || el.clientHeight < 40) return false;
      var oy = getComputedStyle(el).overflowY;
      return oy === 'auto' || oy === 'scroll';
    }

    // The page itself, so the model can scroll without choosing a container.
    var de = document.scrollingElement || document.documentElement;
    var pageScroll = de.scrollHeight > vh + 8;
    if (pageScroll) push(de, 'list', 'page', '', [0, 0, vw, vh], false, false, true, false);

    function walk(el, suppress) {
      if (out.length >= MAX_NODES || visits++ > MAX_VISITS) { truncated = true; return; }
      if (Date.now() - t0 > BUDGET_MS) { truncated = true; return; }
      var tag = el.tagName;
      if (SKIP[tag]) return;
      if (el.hidden || attr(el, 'aria-hidden') === 'true' && !matches(el, INTERACTIVE)) {
        if (el.hidden) return;
      }
      var rect = el.getBoundingClientRect();
      var zero = rect.width === 0 && rect.height === 0;
      if (zero) {
        var cs0 = getComputedStyle(el);
        if (cs0.display === 'none') return;
      } else if (rect.width > 0 && rect.height > 0) {
        var cs = getComputedStyle(el);
        if (!visibleStyle(cs)) {
          if (cs.display === 'none' || cs.visibility === 'hidden' && !el.firstElementChild) return;
          if (parseFloat(cs.opacity || '1') <= 0.02) return;
        }
      }
      var visible = inView(rect);
      var interactive = false, nowSuppress = suppress;

      if (visible && consumed.indexOf(el) < 0) {
        var strict = matches(el, INTERACTIVE);
        var pointer = false;
        if (!strict && !suppress && (tag === 'DIV' || tag === 'SPAN' || tag === 'LI' || tag === 'IMG' || tag === 'I' || tag === 'SVG' || tag === 'TD') &&
            rect.width * rect.height < vw * vh * 0.8) {
          var p = el.parentElement;
          var ccs = getComputedStyle(el);
          if (ccs.cursor === 'pointer' && !(p && getComputedStyle(p).cursor === 'pointer')) pointer = true;
        }
        if ((strict || pointer) && (!suppress || strict)) {
          var disabled = el.disabled === true || attr(el, 'aria-disabled') === 'true';
          if (!disabled && !covered(el, rect)) {
            var kind = strict ? classify(el) : 'btn';
            var d = describe(el, kind);
            if (d.t || d.d || kind === 'in' || kind === 'chk' || kind === 'sw') {
              push(el, kind, d.t, d.d, clip(rect), kind !== 'in' || tag === 'SELECT', isEditable(el), scrollableBox(el), isChecked(el));
              interactive = true;
              nowSuppress = true;
            }
          }
        }
        if (!interactive && !suppress) {
          if (/^H[1-6]$/.test(tag) || attr(el, 'role') === 'heading') {
            var ht = norm(el.innerText || el.textContent, 160);
            if (ht) { push(el, 'h', ht, '', clip(rect), false, false, false, false); nowSuppress = true; }
          } else if (tag === 'IMG') {
            var alt = norm(attr(el, 'alt') || attr(el, 'title'), 80);
            if (alt) push(el, 'img', alt, '', clip(rect), false, false, false, false);
          } else if (scrollableBox(el)) {
            push(el, 'list', '', '', clip(rect), false, false, true, false);
          } else {
            var tx = inlineText(el, consumed);
            if (tx.length >= 2 || /\d/.test(tx)) push(el, tag === 'LI' ? 'item' : 'txt', tx.length > 160 ? tx.slice(0, 160) : tx, '', clip(rect), false, false, false, false);
          }
        }
      }

      var kids = el.children;
      for (var i = 0; i < kids.length; i++) walk(kids[i], nowSuppress);
      if (el.shadowRoot) {
        var sk = el.shadowRoot.children;
        for (var j = 0; j < sk.length; j++) walk(sk[j], nowSuppress);
      }
      if (tag === 'IFRAME') {
        try {
          var doc = el.contentDocument;
          if (doc && doc.body) walk(doc.body, nowSuppress);
        } catch (e) {}
      }
    }

    if (document.body) walk(document.body, false);

    return JSON.stringify({
      url: location.href,
      title: norm(document.title, 160),
      vw: vw, vh: vh,
      sx: Math.round(window.scrollX), sy: Math.round(window.scrollY),
      sw: de.scrollWidth, sh: de.scrollHeight,
      truncated: truncated,
      dialog: window.__zbDialog || '',
      n: out
    });
  }

  function res(ok, code, extra) {
    var o = {ok: ok};
    if (code) o.code = code;
    if (extra) for (var k in extra) o[k] = extra[k];
    return JSON.stringify(o);
  }

  function center(el) {
    var r = el.getBoundingClientRect();
    return {x: r.left + r.width / 2, y: r.top + r.height / 2};
  }

  function fire(el, type, x, y, pointer) {
    var init = {bubbles: true, cancelable: true, composed: true, view: window, clientX: x, clientY: y, button: 0, buttons: type.indexOf('up') >= 0 ? 0 : 1};
    var ev;
    try {
      if (pointer && typeof PointerEvent === 'function') {
        init.pointerId = 1; init.pointerType = 'touch'; init.isPrimary = true;
        ev = new PointerEvent(type, init);
      } else {
        ev = new MouseEvent(type, init);
      }
    } catch (e) { return; }
    el.dispatchEvent(ev);
  }

  function clickEl(el) {
    try { el.scrollIntoView({block: 'center', inline: 'center'}); } catch (e) {}
    var c = center(el);
    fire(el, 'pointerdown', c.x, c.y, true);
    fire(el, 'mousedown', c.x, c.y, false);
    try { if (el.focus) el.focus({preventScroll: true}); } catch (e) {}
    fire(el, 'pointerup', c.x, c.y, true);
    fire(el, 'mouseup', c.x, c.y, false);
    if (typeof el.click === 'function') el.click(); else fire(el, 'click', c.x, c.y, false);
  }

  function byIndex(i) { return els[i - 1] || null; }

  function tap(i) {
    var el = byIndex(i);
    if (!el || !el.isConnected) return res(false, 'NODE_STALE');
    if (el.tagName === 'SELECT') return res(false, 'USE_SET_TEXT', {hint: 'select: use set_text with an option text'});
    if (el === (document.scrollingElement || document.documentElement)) return res(false, 'NOT_TAPPABLE');
    clickEl(el);
    return res(true);
  }

  function longTap(i) {
    var el = byIndex(i);
    if (!el || !el.isConnected) return res(false, 'NODE_STALE');
    try { el.scrollIntoView({block: 'center'}); } catch (e) {}
    var c = center(el);
    fire(el, 'pointerdown', c.x, c.y, true);
    fire(el, 'mousedown', c.x, c.y, false);
    fire(el, 'contextmenu', c.x, c.y, false);
    fire(el, 'pointerup', c.x, c.y, true);
    fire(el, 'mouseup', c.x, c.y, false);
    return res(true);
  }

  function tapAt(x, y, long) {
    var el = document.elementFromPoint(x, y);
    if (!el) return res(false, 'NO_ELEMENT_AT_POINT');
    if (long) {
      fire(el, 'pointerdown', x, y, true); fire(el, 'mousedown', x, y, false);
      fire(el, 'contextmenu', x, y, false);
      fire(el, 'pointerup', x, y, true); fire(el, 'mouseup', x, y, false);
      return res(true);
    }
    fire(el, 'pointerdown', x, y, true);
    fire(el, 'mousedown', x, y, false);
    try { if (el.focus) el.focus({preventScroll: true}); } catch (e) {}
    fire(el, 'pointerup', x, y, true);
    fire(el, 'mouseup', x, y, false);
    if (typeof el.click === 'function') el.click(); else fire(el, 'click', x, y, false);
    return res(true);
  }

  function setText(i, text) {
    var el = byIndex(i);
    if (!el || !el.isConnected) return res(false, 'NODE_STALE');
    try { el.scrollIntoView({block: 'center'}); } catch (e) {}
    if (el.tagName === 'SELECT') {
      var want = String(text).toLowerCase(), hit = -1;
      for (var k = 0; k < el.options.length; k++) {
        var o = el.options[k];
        if (o.value.toLowerCase() === want || o.textContent.trim().toLowerCase() === want) { hit = k; break; }
      }
      if (hit < 0) for (var m = 0; m < el.options.length; m++) {
        if (el.options[m].textContent.toLowerCase().indexOf(want) >= 0) { hit = m; break; }
      }
      if (hit < 0) return res(false, 'OPTION_NOT_FOUND');
      el.selectedIndex = hit;
      el.dispatchEvent(new Event('input', {bubbles: true}));
      el.dispatchEvent(new Event('change', {bubbles: true}));
      return res(true);
    }
    if (el.tagName === 'INPUT' || el.tagName === 'TEXTAREA') {
      if (!isEditable(el)) return res(false, 'NOT_EDITABLE');
      try { el.focus({preventScroll: true}); } catch (e) {}
      var proto = el.tagName === 'TEXTAREA' ? HTMLTextAreaElement.prototype : HTMLInputElement.prototype;
      var desc = Object.getOwnPropertyDescriptor(proto, 'value');
      if (desc && desc.set) desc.set.call(el, text); else el.value = text;
      try {
        el.dispatchEvent(new InputEvent('input', {bubbles: true, inputType: 'insertText', data: text}));
      } catch (e) { el.dispatchEvent(new Event('input', {bubbles: true})); }
      el.dispatchEvent(new Event('change', {bubbles: true}));
      return res(true);
    }
    if (el.isContentEditable) {
      el.focus();
      var done = false;
      try {
        document.execCommand('selectAll', false, null);
        done = document.execCommand('insertText', false, text);
      } catch (e) {}
      if (!done) {
        el.textContent = text;
        el.dispatchEvent(new InputEvent('input', {bubbles: true, inputType: 'insertText', data: text}));
      }
      return res(true);
    }
    return res(false, 'NOT_EDITABLE');
  }

  function canScroll(el, dy) {
    if (dy > 0) return el.scrollTop + el.clientHeight < el.scrollHeight - 1;
    return el.scrollTop > 0;
  }

  function scrollNode(i, forward) {
    var el = i ? byIndex(i) : null;
    var de = document.scrollingElement || document.documentElement;
    if (i && !el) return res(false, 'NODE_STALE');
    if (!el || el === de) el = null;
    var before, after;
    if (el) {
      before = el.scrollTop;
      el.scrollBy(0, (forward ? 1 : -1) * Math.round(el.clientHeight * 0.8));
      after = el.scrollTop;
    } else {
      before = window.scrollY;
      window.scrollBy(0, (forward ? 1 : -1) * Math.round(window.innerHeight * 0.8));
      after = window.scrollY;
    }
    return res(true, null, {moved: Math.abs(after - before) > 1});
  }

  function scrollAt(x, y, dx, dy) {
    var el = document.elementFromPoint(x, y);
    while (el && el !== document.body && el !== document.documentElement) {
      if (scrollableBoxLoose(el, dy)) { var b = el.scrollTop; el.scrollBy(dx, dy); return res(true, null, {moved: Math.abs(el.scrollTop - b) > 1}); }
      el = el.parentElement;
    }
    var before = window.scrollY;
    window.scrollBy(dx, dy);
    return res(true, null, {moved: Math.abs(window.scrollY - before) > 1});
  }

  function scrollableBoxLoose(el, dy) {
    if (el.scrollHeight <= el.clientHeight + 4) return false;
    var oy = getComputedStyle(el).overflowY;
    return (oy === 'auto' || oy === 'scroll') && canScroll(el, dy);
  }

  function key(name) {
    var el = document.activeElement && document.activeElement !== document.body ? document.activeElement : document.body;
    var codes = {Enter: 13, Escape: 27, Tab: 9, PageUp: 33, PageDown: 34};
    var init = {key: name, code: name, keyCode: codes[name] || 0, which: codes[name] || 0, bubbles: true, cancelable: true};
    var proceed = el.dispatchEvent(new KeyboardEvent('keydown', init));
    if (name === 'Enter') el.dispatchEvent(new KeyboardEvent('keypress', init));
    el.dispatchEvent(new KeyboardEvent('keyup', init));
    if (name === 'Enter' && proceed && el.tagName === 'INPUT' && el.form) {
      if (typeof el.form.requestSubmit === 'function') el.form.requestSubmit(); else el.form.submit();
    }
    if (name === 'PageDown') window.scrollBy(0, Math.round(window.innerHeight * 0.8));
    if (name === 'PageUp') window.scrollBy(0, -Math.round(window.innerHeight * 0.8));
    return res(true);
  }

  window.__zb = {v: 1, snapshot: snapshot, tap: tap, longTap: longTap, tapAt: tapAt, setText: setText, scrollNode: scrollNode, scrollAt: scrollAt, key: key, els: els};
})();
