---
name: youtube-playbook
description: YouTube playbook: facts and specs, posting routes, engagement tactics, new-account warm-up, caps, AI-disclosure and never-list. Load when working on YouTube.
license: MIT
---

# YouTube playbook
App package: `com.google.android.youtube`. Area: shorts, videos and comment replies. Only use this when `youtube` is in `enabled` of `social/PLATFORMS.json`.
Facts marked (unverified) or (heuristic) are hypotheses: test them against the owner's own numbers and never state them to the owner as certain.

## Facts, formats and specs
Formats: Shorts (9:16, up to 3 min since Oct 2024 (unverified current limit), hook in the first second, keyword in title and a few tags), long form 16:9 (title and thumbnail promise one clear outcome and deliver in the first 30 seconds; thumbnail 1280x720 under 2 MB), Community posts, Live. Titles up to 100 characters, descriptions up to 5000, chapters need 3+ timestamps starting 0:00. Upload and Studio features live in YouTube Studio (com.google.android.apps.youtube.creator). New channels may be limited to 15 minutes per upload until phone-verified.

## Posting routes
Capability ladder for this platform. Metricool: supported on the free plan. Order on a PAID plan: 1 Metricool (skill `metricool-posting`), 2 `share_file` + `youtube.finish_share` (video/image), 3 the app on screen. On the FREE plan (default) the order is 2, 3, then Metricool only as the fallback and only while the monthly counter allows (20 posts a month in total, stop at 18).
Platform specifics: Metricool MCP (YouTube video and Shorts scheduling is supported on free) if the quota allows; then share_file to com.google.android.youtube + youtube.finish_share (needs YouTube Studio steps); the final public publish stays with the owner until a routine is verified.

## Engagement tactics
Pin a comment with a question, reply to every comment on the first day (a heart plus a real answer), and leave useful comments on videos of channels in the niche. Use end screens and playlists to chain videos.
Always through the engagement loop (skill `engagement-loop`) and the gate: `engage op=plan platform=youtube action=youtube.<action>`.

## Warm-up ramp for a NEW account
New channel: complete branding and About, verify the phone, upload 1 Short a day for 7 days as a test of formats, comment 5 a day on niche videos; watch retention in Studio after each post. Do not post identical re-uploads. (heuristic)
During warm-up use half of the caps below and keep the ramp. Record the start date in `social/accounts/youtube.md`.

## Daily caps (gate keys; the owner can change them in the app; `engage op=today` shows live counts)
youtube.post=1, youtube.comment=10, youtube.reply=30, youtube.like=20

## AI-disclosure rules
In YouTube Studio set 'Altered or synthetic content' = yes when the video shows realistic people, places or events that did not happen, or a synthetic voice of a real person. Metricool flag: isAiGeneratedContent (unverified name per platform). Reused or mass-produced content is demonetised: add real value.

## Research and tools
web_search + web_fetch on youtube.com/results?search_query=..., r.jina.ai for a video page and transcript; Apify 'streamers/youtube-scraper' if on; transcripts of winning competitors show hooks.
Platform skills to load for specific jobs: youtube-long-form, youtube-shorts, youtube-publishing-and-metadata, thumbnail-design, scripting-and-storyboarding, reply-and-comment-writer, community-management, social-seo, smc-youtube-thumbnail. Also `social-voice-and-slop` for every draft and `social-account-safety` before outward actions.

## Screen routines (`screen_routine routine=list`)
`youtube.open_home`, `youtube.read_activity`, `youtube.scan_feed`, `youtube.comment`, `youtube.like`, `youtube.finish_share` (after `share_file package=com.google.android.youtube`).
Labels are best-effort guesses for the app's English UI: on the first run a routine may fail with a clear reason. Read the screen once, add the real label to `files/routines/youtube.<name>.json`, and record the fix as a lesson. Do not loop.

## Never
Upload reused or scraped clips, fake or misleading thumbnails, comment spam, 'sub for sub' requests, re-upload the same video, buy views or subs. Also: invent facts or results, bypass locks/CAPTCHAs/verification, exceed caps, act while the platform shows a warning.
