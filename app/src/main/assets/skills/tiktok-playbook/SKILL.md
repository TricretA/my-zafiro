---
name: tiktok-playbook
description: TikTok operating playbook: platform facts, formats, engagement tactics, caps and routines. Load when working on TikTok.
license: MIT
---

# TikTok playbook
App package: `com.zhiliaoapp.musically`. Area: videos and comments.

## What to know
TikTok growth depends on the first 2 seconds, watch-through rate and trend/sound fit. The team writes scripts, shot lists, captions and hashtags and queues them as files; video creation uses the owner's tools (CapCut, generators); publish with share_file (skill video-publish) and the tiktok.finish_share routine (labels may need a first-run fix). Never claim a post went live without seeing it on screen. Some regions use the package com.ss.android.ugc.trill.

## Daily caps (owner-adjustable in the workspace; `engage op=today` shows live counts)
post=1, comment=15, reply=25, like=30, follow=8

## Engagement tactics
Hook inside the first 2 seconds; vertical 9:16; short caption with 3-5 specific tags. After posting, spend 15 minutes replying to every comment (a video reply to a good question is a second post) and leave useful comments on 10 niche videos. Comment on fresh videos (under 2 hours old) where the creator replies.

## Research and tools for this platform
Research: Apify 'clockworks/tiktok-scraper' (if on) for trending sounds and top videos in the niche; web_fetch via r.jina.ai on tiktok.com/tag/<tag>. Higgsfield/fal.ai MCP for video clips if switched on; CapCut skill for editing steps.

## Screen routines (`screen_routine routine=list`)
`tiktok.open_home`, `tiktok.read_activity`, `tiktok.scan_feed`, `tiktok.comment`, `tiktok.like`, `tiktok.finish_share` (after `share_file package=com.zhiliaoapp.musically`).
The labels in these routines are best-effort guesses for the app's English UI. On the first run each routine may fail with a clear reason: read the screen once, add the real label to `files/routines/tiktok.<name>.json`, and record the fix as a lesson. Do not loop.

## Never
Invent facts or results, post ads, spam, bypass locks/CAPTCHAs/verification, exceed caps, or act while the platform shows a warning.
