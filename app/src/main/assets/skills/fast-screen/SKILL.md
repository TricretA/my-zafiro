---
name: fast-screen
description: Speed rules for driving the phone screen: compact reads, bare-number taps, no re-reads, routines, share_file. Load before any screen work.
license: MIT
---

# Fast screen work
Every screen step costs time and money, and old steps are re-billed on every later step. Work like this:
1. **One call, one change.** Each `screen_operation` tap/type/scroll already returns the NEW screen. Never call `read` right after an action. `read` only for the first look.
2. **Tap by number.** Use the number in [brackets] as `token` (`token:"12"`). Lines look like `[12]btn "Post" tap`. A line ending `edit` is a text field (use `set_text`). `format:"full"` exists but costs 5x more: only if the compact list hides what you need.
3. **Long screens:** if it says "N more nodes hidden", use `operation:"search"` with keywords, or scroll. Do not read the same screen twice.
4. **If the result says "screen content unchanged"** your action did nothing: do not repeat it; try another element, scroll, wait 1-2 s, or report.
5. **Routines first.** `screen_routine routine=list`. A routine does 5-30 steps in one call and verifies each tap. For a new multi-step job write the steps once with `screen_routine steps=[...] save_as="<platform>.<job>" params={...}`: if it succeeds it is saved and next time costs almost nothing.
6. **Media goes by sharing, not by browsing.** To post a video or image use `share_file` (opens the target app's share screen with the file and caption ready), then only tap Next/Post. Never scroll a gallery to find a file.
7. **Open apps directly** with `open`/`launch_app`/`open_uri` (deep links such as whatsapp://send?phone=..&text=..). Never navigate the launcher.
8. **Websites:** for forms, logins and JavaScript pages use the in-app browser (skill `agent-browser`): it does not need the phone screen. **Do not screenshot** unless the compact list is empty or the screen is a canvas/game. Screenshots are expensive; the tree is enough.
9. **Wait smartly.** Use a routine `wait` op with labels (returns as soon as the label appears) instead of fixed sleeps.
10. **Stop loops.** The same failing action twice means change approach; three failed attempts means report FAILED with the screen text.
