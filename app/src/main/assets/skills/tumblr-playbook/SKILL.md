---
name: tumblr-playbook
description: Tumblr playbook: facts and specs, posting routes, engagement tactics, new-account warm-up, caps, AI-disclosure and never-list. Load when working on Tumblr.
license: MIT
---

# Tumblr playbook (optional extra platform)
App package: `com.tumblr`. Area: posts, reblogs and tags. Only use this when `tumblr` is in `enabled` of `social/PLATFORMS.json`.
Facts marked (unverified) or (heuristic) are hypotheses: test them against the owner's own numbers and never state them to the owner as certain.

## Facts, formats and specs
Optional extra. Tumblr is tag-driven, fandom and aesthetic led: reblogs spread posts, tags (first 20 are indexed, the first ~5 matter most) drive discovery, and original media with personality does best. Posts can be text, photo, gif, video and quotes. Heavy promotion is disliked; humour and visual craft travel.

## Posting routes
Capability ladder for this platform. Metricool: NOT supported by Metricool. Order: 1 `share_file` + `tumblr.finish_share` (video/image), 2 the app on screen (`tumblr.post_text`). Metricool is never used for this platform.
Platform specifics: No Metricool. tumblr.post_text on screen; share_file to com.tumblr + tumblr.finish_share for media.

## Engagement tactics
Reblog with a funny or useful line, answer asks, tag consistently, queue posts through Tumblr's own queue.
Always through the engagement loop (skill `engagement-loop`) and the gate: `engage op=plan platform=tumblr action=tumblr.<action>`.

## Warm-up ramp for a NEW account
New blog: set up the theme and bio, follow 20 tags, reblog 5-10 a day for a few days, post originals 1-2 a day, then the caps.
During warm-up use half of the caps below and keep the ramp. Record the start date in `social/accounts/tumblr.md`.

## Daily caps (gate keys; the owner can change them in the app; `engage op=today` shows live counts)
tumblr.post=3, tumblr.comment=8, tumblr.like=30, tumblr.follow=10, tumblr.restack=10

## AI-disclosure rules
Tag AI-generated art honestly (the community expects it).

## Research and tools
Tag searches on screen.
Platform skills to load for specific jobs: text-post-and-microblog, meme-and-culture, hashtag-strategy, caption-writer, community-management. Also `social-voice-and-slop` for every draft and `social-account-safety` before outward actions.

## Screen routines (`screen_routine routine=list`)
`tumblr.open_home`, `tumblr.read_activity`, `tumblr.scan_feed`, `tumblr.comment`, `tumblr.like`, `tumblr.post_text`, `tumblr.finish_share` (after `share_file package=com.tumblr`).
Labels are best-effort guesses for the app's English UI: on the first run a routine may fail with a clear reason. Read the screen once, add the real label to `files/routines/tumblr.<name>.json`, and record the fix as a lesson. Do not loop.

## Never
Tag spam, reposting art without credit, mass follow, self-promo only. Also: invent facts or results, bypass locks/CAPTCHAs/verification, exceed caps, act while the platform shows a warning.
