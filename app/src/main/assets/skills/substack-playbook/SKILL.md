---
name: substack-playbook
description: Substack playbook: facts and specs, posting routes, engagement tactics, new-account warm-up, caps, AI-disclosure and never-list. Load when working on Substack.
license: MIT
---

# Substack playbook
App package: `com.substack.app`. Area: Notes, posts and comments. Only use this when `substack` is in `enabled` of `social/PLATFORMS.json`.
Facts marked (unverified) or (heuristic) are hypotheses: test them against the owner's own numbers and never state them to the owner as certain.

## Facts, formats and specs
Two surfaces: Notes (short posts in a feed, replies, restacks, likes) and long posts (newsletter, comments). Growth comes mostly from Notes and thoughtful comments on other writers' posts and notes, plus recommendations from other publications. Notes that are only links travel less. A restack shares someone's note or post with your followers (add your own thought). The Activity tab shows replies, likes, new subscribers and restacks. The owner's publication: tricreta.substack.com (Android developer; websites, AI features, dashboards, design and video). Content pillars come from the owner's content plan; WhatsApp/M-Pesa/bot topics are at most 1 in 6 pieces. Substack has an AI-detection tool for flagged content (unverified detail).

## Posting routes
Capability ladder for this platform. Metricool: NOT supported by Metricool. Order: 1 `share_file` + `substack.finish_share` (not available here), 2 the app on screen (`substack.post_text`). Metricool is never used for this platform.
Platform specifics: No Metricool and no official API. Notes via substack.post_text on screen; long posts are drafted as files and published by the owner or by screen. Substack's app has no video share: post text with a link to the video.

## Engagement tactics
Notes first: 3-6 Notes a day max. Comments on larger writers' posts in the niche are the best discovery lever (one specific insight, no pitch). Reply to every comment on the owner's posts. Restack a few good notes with a line of your own.
Always through the engagement loop (skill `engagement-loop`) and the gate: `engage op=plan platform=substack action=substack.<action>`.

## Warm-up ramp for a NEW account
New publication: write 3 strong posts before promoting, 2-3 Notes a day for week 1, comment on 5 writers' posts a day, ask 3 peers for recommendations by hand. (heuristic)
During warm-up use half of the caps below and keep the ramp. Record the start date in `social/accounts/substack.md`.

## Daily caps (gate keys; the owner can change them in the app; `engage op=today` shows live counts)
substack.note=6, substack.post=1, substack.comment=15, substack.reply=40, substack.like=30, substack.restack=10, substack.follow=10, substack.dm=5

## AI-disclosure rules
Be honest about AI assistance in long posts when it is substantial; never publish text you have not reviewed and humanized.

## Research and tools
rss_read on https://<writer>.substack.com/feed for each writer in the niche; web_search for niche topics.
Platform skills to load for specific jobs: social-strategy, reply-and-comment-writer, engagement-routine, email-and-newsletter, text-post-and-microblog, content-pillars, content-calendar, audience-research, competitor-analysis, writing-style-and-tone, voice-builder, stop-slop. Also `social-voice-and-slop` for every draft and `social-account-safety` before outward actions.

## Screen routines (`screen_routine routine=list`)
`substack.open_home`, `substack.read_activity`, `substack.scan_feed`, `substack.comment`, `substack.like`, `substack.post_text`.
Labels are best-effort guesses for the app's English UI: on the first run a routine may fail with a clear reason. Read the screen once, add the real label to `files/routines/substack.<name>.json`, and record the fix as a lesson. Do not loop.

## Never
Auto-follow bursts, link-only Notes, pitch comments, copying other writers, fake testimonials. Also: invent facts or results, bypass locks/CAPTCHAs/verification, exceed caps, act while the platform shows a warning.
