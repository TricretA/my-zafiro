---
name: facebook-playbook
description: Facebook playbook: facts and specs, posting routes, engagement tactics, new-account warm-up, caps, AI-disclosure and never-list. Load when working on Facebook.
license: MIT
---

# Facebook playbook
App package: `com.facebook.katana`. Area: page posts, reels, group posts and comments. Only use this when `facebook` is in `enabled` of `social/PLATFORMS.json`.
Facts marked (unverified) or (heuristic) are hypotheses: test them against the owner's own numbers and never state them to the owner as certain.

## Facts, formats and specs
Formats: text, photo (any ratio; 4:5 and 1:1 are safe), Reels (9:16, 3 s to 90 s is the sweet spot, up to 20 min allowed (unverified)), Stories (9:16, 24 h), link posts (weakest reach), group posts. Reels and native video get more reach than links. Pages and personal profiles are separate: the owner's Page may need the Pages Manager app (com.facebook.pages.app). Groups: every group has its own rules and often admin approval; one genuinely helpful comment beats a link drop.

## Posting routes
Capability ladder for this platform. Metricool: supported on the free plan. Order on a PAID plan: 1 Metricool (skill `metricool-posting`), 2 `share_file` + `facebook.finish_share` (video/image), 3 the app on screen (`facebook.post_text`). On the FREE plan (default) the order is 2, 3, then Metricool only as the fallback and only while the monthly counter allows (20 posts a month in total, stop at 18).
Platform specifics: Metricool MCP (Facebook Page posts, Reels, Stories are supported on the free plan) if the quota allows; then share_file to com.facebook.katana + facebook.finish_share; then the screen composer (facebook.post_text).

## Engagement tactics
Reply to every comment on the owner's posts within the first hour. Comment usefully on 5-10 posts in niche Pages and groups (add a detail, never a pitch). Share a Reel to Stories. Ask one real question per post.
Always through the engagement loop (skill `engagement-loop`) and the gate: `engage op=plan platform=facebook action=facebook.<action>`.

## Warm-up ramp for a NEW account
New account: days 1-3 browse, react to about 10 posts a day, 0 posts; days 4-7 one text or photo post a day, 5 comments; week 2 one post a day plus a Reel every second day, 8 comments, join 1-2 groups and only read; week 3+ the full caps. Complete the profile (photo, bio, work) first. (heuristic)
During warm-up use half of the caps below and keep the ramp. Record the start date in `social/accounts/facebook.md`.

## Daily caps (gate keys; the owner can change them in the app; `engage op=today` shows live counts)
facebook.post=2, facebook.comment=10, facebook.reply=25, facebook.like=25, facebook.dm=3

## AI-disclosure rules
Meta labels realistic AI images, video and audio. Use the 'AI info' / 'Made with AI' label when you publish realistic synthetic media, and never strip C2PA/IPTC metadata. Say so in the caption for synthetic voice or faces.

## Research and tools
Group rules via the app; Apify 'apify/facebook-posts-scraper' if that MCP is on; Meta Business Suite for the owner's own Page insights.
Platform skills to load for specific jobs: facebook-strategy, facebook-groups, caption-writer, reply-and-comment-writer, community-management, engagement-routine, short-form-video-script, social-strategy. Also `social-voice-and-slop` for every draft and `social-account-safety` before outward actions.

## Screen routines (`screen_routine routine=list`)
`facebook.open_home`, `facebook.read_activity`, `facebook.scan_feed`, `facebook.comment`, `facebook.like`, `facebook.post_text`, `facebook.finish_share` (after `share_file package=com.facebook.katana`).
Labels are best-effort guesses for the app's English UI: on the first run a routine may fail with a clear reason. Read the screen once, add the real label to `files/routines/facebook.<name>.json`, and record the fix as a lesson. Do not loop.

## Never
Spam groups, post the same text in several groups, tag people who did not ask, buy likes, use clickbait 'engagement bait' (like/share/tag to win), friend-request strangers in bursts. Also: invent facts or results, bypass locks/CAPTCHAs/verification, exceed caps, act while the platform shows a warning.
