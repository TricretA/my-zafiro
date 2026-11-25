---
name: instagram-playbook
description: Instagram operating playbook: platform facts, formats, engagement tactics, caps and routines. Load when working on Instagram.
license: MIT
---

# Instagram playbook
App package: `com.instagram.android`. Area: reels, carousels, stories and comments.

## What to know
Instagram growth comes from Reels (reach), carousels (saves and shares), consistent Stories, and real comments on accounts in the niche. Publishing: prepare the video, caption and hashtags as files, then use share_file (skill video-publish) to open Instagram's share screen with the video attached and finish with the instagram.finish_share routine (its labels may need a first-run fix: look at the screen once and correct the routine file). Instagram asks Reel/Story/Feed after sharing: pick Reel for video. Comments and replies run through the engagement loop.

## Daily caps (owner-adjustable in the workspace; `engage op=today` shows live counts)
post=1, comment=15, reply=30, like=30, follow=8, dm=4

## Engagement tactics
Reels drive reach; carousels drive saves and shares. First caption line is the hook. 3-5 relevant tags. After posting, reply to every comment in the first hour and leave real comments on 10 accounts in the niche. Add the Reel to Stories.

## Research and tools for this platform
Research: Apify 'apify/instagram-scraper' (if on) for top posts and hashtags; make_card for carousel slides; Canva MCP if switched on.

## Screen routines (`screen_routine routine=list`)
`instagram.open_home`, `instagram.read_activity`, `instagram.scan_feed`, `instagram.comment`, `instagram.like`, `instagram.finish_share` (after `share_file package=com.instagram.android`).
The labels in these routines are best-effort guesses for the app's English UI. On the first run each routine may fail with a clear reason: read the screen once, add the real label to `files/routines/instagram.<name>.json`, and record the fix as a lesson. Do not loop.

## Never
Invent facts or results, post ads, spam, bypass locks/CAPTCHAs/verification, exceed caps, or act while the platform shows a warning.
