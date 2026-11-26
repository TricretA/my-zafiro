---
name: tiktok-playbook
description: TikTok playbook: facts and specs, posting routes, engagement tactics, new-account warm-up, caps, AI-disclosure and never-list. Load when working on TikTok.
license: MIT
---

# TikTok playbook
App package: `com.zhiliaoapp.musically`. Area: videos, photo posts and comments. Only use this when `tiktok` is in `enabled` of `social/PLATFORMS.json`.
Facts marked (unverified) or (heuristic) are hypotheses: test them against the owner's own numbers and never state them to the owner as certain.

## Facts, formats and specs
Formats: vertical 9:16 video (1080x1920, 15-60 s works for most niches; up to 10 min allowed), photo mode carousels, text in the first frame. The first 2 seconds, watch-through rate, rewatches and shares drive distribution; sound and trend fit matter. Search is a real discovery channel: say the keyword aloud and in on-screen text and caption. Some regions use the package com.ss.android.ugc.trill. Posts made through third-party schedulers may stay private until the vendor's app is audited by TikTok (unverified): always test one post.

## Posting routes
Capability ladder for this platform. Metricool: supported on the free plan. Order on a PAID plan: 1 Metricool (skill `metricool-posting`), 2 `share_file` + `tiktok.finish_share` (video/image), 3 the app on screen. On the FREE plan (default) the order is 2, 3, then Metricool only as the fallback and only while the monthly counter allows (20 posts a month in total, stop at 18).
Platform specifics: Metricool MCP (TikTok is supported; unverified whether API posts go public on the free plan, test with draft:true then one real post) if the quota allows; then share_file to com.zhiliaoapp.musically + tiktok.finish_share; then the screen. Video creation uses the owner's tools (CapCut, generators, video-studio workspace).

## Engagement tactics
After posting, spend 15 minutes replying to every comment (a video reply to a good question is a second post) and leave useful comments on 10 niche videos under 2 hours old whose creators reply. Never use trending sounds with a commercial-use restriction on a business account (check the sound is in the commercial library).
Always through the engagement loop (skill `engagement-loop`) and the gate: `engage op=plan platform=tiktok action=tiktok.<action>`.

## Warm-up ramp for a NEW account
New account: days 1-3 watch the For You feed for 20-30 min a day, like 10-20, follow 0-5, 0 posts; week 1 1 post a day of the best content, 10 comments; week 2 1-2 posts a day, 15 comments, 8 follows; then the caps. Stay on one device and network. (heuristic)
During warm-up use half of the caps below and keep the ramp. Record the start date in `social/accounts/tiktok.md`.

## Daily caps (gate keys; the owner can change them in the app; `engage op=today` shows live counts)
tiktok.post=1, tiktok.comment=15, tiktok.reply=25, tiktok.like=30, tiktok.follow=8

## AI-disclosure rules
TikTok requires the 'AI-generated content' toggle for realistic AI imagery, audio or video, and auto-labels C2PA content. Metricool flag: isAigc. Never impersonate a real person with synthetic voice or face.

## Research and tools
Apify 'clockworks/tiktok-scraper' if on for sounds and top videos; web_fetch via r.jina.ai on tiktok.com/tag/<tag>; Higgsfield MCP for clips if on.
Platform skills to load for specific jobs: tiktok-growth, tiktok-script, tiktok-video-publishing, tiktok-photo-mode, short-form-video-script, hook-writer, capcut, hashtag-strategy, trend-jacking, engagement-routine, viral-reverse-engineering. Also `social-voice-and-slop` for every draft and `social-account-safety` before outward actions.

## Screen routines (`screen_routine routine=list`)
`tiktok.open_home`, `tiktok.read_activity`, `tiktok.scan_feed`, `tiktok.comment`, `tiktok.like`, `tiktok.finish_share` (after `share_file package=com.zhiliaoapp.musically`).
Labels are best-effort guesses for the app's English UI: on the first run a routine may fail with a clear reason. Read the screen once, add the real label to `files/routines/tiktok.<name>.json`, and record the fix as a lesson. Do not loop.

## Never
Post the same video to many accounts, use stolen clips, buy views, comment links, use hashtags unrelated to the video, mention 'link in bio' spam in comments, try to bypass age or region checks. Also: invent facts or results, bypass locks/CAPTCHAs/verification, exceed caps, act while the platform shows a warning.
