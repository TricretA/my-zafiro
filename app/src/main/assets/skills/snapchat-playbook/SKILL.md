---
name: snapchat-playbook
description: Snapchat playbook: facts and specs, posting routes, engagement tactics, new-account warm-up, caps, AI-disclosure and never-list. Load when working on Snapchat.
license: MIT
---

# Snapchat playbook
App package: `com.snapchat.android`. Area: Spotlight, Stories and chats. Only use this when `snapchat` is in `enabled` of `social/PLATFORMS.json`.
Facts marked (unverified) or (heuristic) are hypotheses: test them against the owner's own numbers and never state them to the owner as certain.

## Facts, formats and specs
Formats: vertical 9:16 video. Spotlight (the public short-video feed) takes up to 60 s (unverified current cap) and is the discovery surface; Stories last 24 h (public Stories need a Public Profile); Snaps are direct chats. Snapchat has no like counter in the usual sense; engagement signals are views, shares, replies, saves and Spotlight watch time. Creators monetise through the Snap Star / Spotlight programs (eligibility rules change; unverified). Posting from the phone camera roll is allowed; there is no public scheduling API for personal accounts, so Metricool does not apply.

## Posting routes
Capability ladder for this platform. Metricool: NOT supported by Metricool. Order: 1 `share_file` + `snapchat.finish_share` (video/image), 2 the app on screen. Metricool is never used for this platform.
Platform specifics: No Metricool. share_file to com.snapchat.android (opens the send/Spotlight picker, unverified) + snapchat.finish_share; otherwise screen. Prepare the video file, caption and tags first. A Public Profile is needed for public Stories (owner sets it up by hand).

## Engagement tactics
Reply to every direct Snap and Story reply in a few lines, post to Spotlight with a clear topic caption, use topic tags, and remix or reply to niche Spotlights thoughtfully. Chats are private: never send unsolicited promotional Snaps.
Always through the engagement loop (skill `engagement-loop`) and the gate: `engage op=plan platform=snapchat action=snapchat.<action>`.

## Warm-up ramp for a NEW account
New account: complete the profile and Bitmoji, add real contacts by hand, 1 Story a day for week 1, Spotlight 1 a day from week 2; keep adds under 10 a day (heuristic). Stop on any 'unusual activity' notice.
During warm-up use half of the caps below and keep the ramp. Record the start date in `social/accounts/snapchat.md`.

## Daily caps (gate keys; the owner can change them in the app; `engage op=today` shows live counts)
snapchat.post=3, snapchat.reply=20, snapchat.dm=3, snapchat.follow=5

## AI-disclosure rules
Snapchat labels AI-generated content made with its tools ('Made with AI') and expects honest disclosure for realistic synthetic media (unverified details). Say it in the caption.

## Research and tools
Spotlight and Discover feeds on screen; web_search for current Snapchat Spotlight guidance; unverified: no reliable public scraper.
Platform skills to load for specific jobs: short-form-video-script, hook-writer, capcut, caption-writer, reply-and-comment-writer, community-management, trend-jacking, social-strategy. Also `social-voice-and-slop` for every draft and `social-account-safety` before outward actions.

## Screen routines (`screen_routine routine=list`)
`snapchat.open_home`, `snapchat.read_activity`, `snapchat.scan_feed`, `snapchat.comment`, `snapchat.like`, `snapchat.finish_share` (after `share_file package=com.snapchat.android`).
Labels are best-effort guesses for the app's English UI: on the first run a routine may fail with a clear reason. Read the screen once, add the real label to `files/routines/snapchat.<name>.json`, and record the fix as a lesson. Do not loop.

## Never
Mass-add strangers, send unsolicited promo Snaps, use third-party Snapchat clients or modded apps (account lock), post minors or anything unsafe, bypass age gates. Also: invent facts or results, bypass locks/CAPTCHAs/verification, exceed caps, act while the platform shows a warning.
