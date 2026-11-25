---
name: video-publish
description: Post a video (or image) with a caption to TikTok, Instagram, YouTube, Facebook, Reddit and others fast: prepare once, share_file, finish with a routine, verify. Use for any 'post this video' job.
license: MIT
---

# Publish a video fast
Goal: one finished video + per-platform captions posted to several apps in minutes, with minimal tokens.
**Prepare once (no screen):**
1. The video must be a file under Zafiro's files folder, e.g. `outputs/clip.mp4` (save or download it first). If you also need a thumbnail or cover, make it now (`make_card`).
2. Write each platform's caption separately (load `caption-writer`, `hashtag-strategy`, `platform-specs-and-validation`). Save them as `outputs/captions.json` so they survive a failure. Keep them native: different hook, length, tags per platform; Reddit needs a title and a subreddit that allows the post (read its rules first with `web_fetch` on r.jina.ai).
3. `engage op=plan` for each post (action `post`). Respect the answer (AUTO / DENIED / NEEDS_APPROVAL).
**For each platform (one at a time, the screen is shared):**
4. `share_file package=<app package> path=outputs/clip.mp4 text=<that platform's caption>`. The app opens on its share/create screen with the video attached and the caption copied to the clipboard.
5. `screen_routine routine=<platform>.finish_share params={text:<caption>}` taps Next/Continue until the caption screen, puts the caption in the caption field (paste or type), and taps Post/Share. If the routine does not exist or fails, read the screen ONCE and continue by hand with fast-screen rules, then save what worked with `save_as`.
6. Verify from the result's `on_screen_now`: a success banner, the post in the profile, or "posting"/"uploading" progress. If it says uploading, `wait` for completion before leaving the app. Then `engage op=done id=<id> note=<evidence>`.
7. Pace: `engage op=pace`, then the next platform.
**App packages:** TikTok `com.zhiliaoapp.musically` (some regions `com.ss.android.ugc.trill`), Instagram `com.instagram.android`, YouTube `com.google.android.youtube`, Facebook `com.facebook.katana`, Reddit `com.reddit.frontpage`, X `com.twitter.android`, LinkedIn `com.linkedin.android`, Threads `com.instagram.barcelona`, Pinterest `com.pinterest`.
**Honest limits:** not every app accepts a shared video straight into its composer (Instagram asks Reel/Story/Feed; Reddit asks for a community and title; Substack's app has no video share, so for Substack post text with a link to the video). The routine labels are best-effort guesses for the English UI and need a first-run fix. Say plainly what actually appeared on screen. Never report a post as done without seeing it.
