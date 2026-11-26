---
name: agent-browser
description: Use Zafiro's own in-app web browser (screens kind=browser): when to pick it over the phone screen or http_request, one-time logins, compact reading, limits. Load before any website work.
license: MIT
---

# Agent browser
A real browser inside Zafiro (Android WebView) with its own saved logins, separate from Chrome. It runs without taking over the phone, so it is the fast lane for websites.
**Pick the right lane**
- `http_request` / `web_fetch`: public pages and APIs with no login and no JavaScript. Cheapest. Try first.
- **Agent browser:** sites that need JavaScript, a login you can do once, or a form: job application forms, ATS pages, Metricool, GitHub, dashboards, directories.
- **Phone screen + the real Chrome app:** Google sign-in, sites behind Cloudflare/bot checks, and anything an app does better.
**Honest limits:** Google blocks sign-in inside embedded browsers (`disallowed_useragent`), so never try Google login here; Cloudflare-protected sites may show a challenge: do not fight it, switch to Chrome on the phone screen or report NEEDS_HUMAN. CAPTCHAs, 2FA and consent screens always go to `take_over`. File upload and downloads are blocked; popups open in the same tab; non-web links (intent:, market:, tel:) are blocked.
**Use**
1. `screens operation=open kind=browser` returns an id such as `web-1` (one per conversation; reopening returns the same). If it fails with `BROWSER_DISABLED` the owner switched it off: use another lane. `BROWSER_LIMIT` or `TAB_LIMIT`: close a tab or screen first.
2. Load a page: `open kind=global screen=web-1 uri=https://...` (or `screen_operation operation=new_tab url=...`). Bare words are searched.
3. Read with `screen_operation screen=web-1 operation=read`. Lines look like `[12]link "Pricing" tap`, `[13]in "Email" edit`, `[14]h "Welcome"`. The `url:` and `page:` lines show the address and scroll position ("more below" means scroll). `[1]list "page" scroll` is the page itself.
4. Act by number: `tap token:"12"`, `set_text token:"13" text:"..."` (for a dropdown pass the option text), `scroll_forward token:"1"`, `key code:66` (Enter submits a form), `key code:4` or `history_back` to go back. Every action returns the NEW page: never call read straight after it.
5. Only what is on screen is listed (about 300 nodes). Scroll to see more. Long text is cut in the compact view: use `format:"full"` once when you must read a whole passage.
6. Tabs: `tabs`, `new_tab`, `switch_tab`, `close_tab` (max 3 per screen, 4 in the whole app). Close tabs you do not need.
7. `screenshot screen=web-1` returns a picture when text is not enough (charts, canvas).
**Logging in once:** you cannot type the owner's passwords from memory. Ask the owner (`take_over` / NEEDS_HUMAN) to open Zafiro > Browser, sign in to the site, then retry: the cookies stay saved. If a page shows a login wall, say which site needs it.
**Costs and safety:** same as the phone: one change per call, no re-reads, stop after two identical failures. Close the screen when the job is done (`screens operation=close screen=web-1`).
