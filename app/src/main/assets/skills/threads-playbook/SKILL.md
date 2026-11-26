---
name: threads-playbook
description: Threads playbook: facts and specs, posting routes, engagement tactics, new-account warm-up, caps, AI-disclosure and never-list. Load when working on Threads.
license: MIT
---

# Threads playbook
App package: `com.instagram.barcelona`. Area: short posts and replies. Only use this when `threads` is in `enabled` of `social/PLATFORMS.json`.
Facts marked (unverified) or (heuristic) are hypotheses: test them against the owner's own numbers and never state them to the owner as certain.

## Facts, formats and specs
Formats: text posts up to 500 characters, up to 20 images or videos in a post, polls, topics (one topic tag per post, used for discovery). Conversational, back-and-forth posts win; it is tied to an Instagram account. Threads supports the fediverse (unverified current scope).

## Posting routes
Capability ladder for this platform. Metricool: supported on the free plan. Order on a PAID plan: 1 Metricool (skill `metricool-posting`), 2 `share_file` + `threads.finish_share` (video/image), 3 the app on screen (`threads.post_text`). On the FREE plan (default) the order is 2, 3, then Metricool only as the fallback and only while the monthly counter allows (20 posts a month in total, stop at 18).
Platform specifics: Metricool MCP (Threads is supported on free) if the quota allows; then threads.post_text on screen; share_file to com.instagram.barcelona + threads.finish_share for media.

## Engagement tactics
Ask a real question, add a real detail, reply to every reply, and reply to accounts in the niche. Use one topic tag per post.
Always through the engagement loop (skill `engagement-loop`) and the gate: `engage op=plan platform=threads action=threads.<action>`.

## Warm-up ramp for a NEW account
Follows the Instagram account's standing: if the Instagram account is new use the Instagram ramp; start with 2-3 replies a day and 1 post, then the caps.
During warm-up use half of the caps below and keep the ramp. Record the start date in `social/accounts/threads.md`.

## Daily caps (gate keys; the owner can change them in the app; `engage op=today` shows live counts)
threads.post=4, threads.comment=15, threads.reply=30, threads.like=30, threads.follow=8

## AI-disclosure rules
Meta's AI label applies to realistic AI media on Threads too; say so in the post.

## Research and tools
Search and topics on screen; web_search.
Platform skills to load for specific jobs: threads-growth, threads-post, text-post-and-microblog, reply-and-comment-writer, engagement-routine, hook-writer. Also `social-voice-and-slop` for every draft and `social-account-safety` before outward actions.

## Screen routines (`screen_routine routine=list`)
`threads.open_home`, `threads.read_activity`, `threads.scan_feed`, `threads.comment`, `threads.like`, `threads.post_text`, `threads.finish_share` (after `share_file package=com.instagram.barcelona`).
Labels are best-effort guesses for the app's English UI: on the first run a routine may fail with a clear reason. Read the screen once, add the real label to `files/routines/threads.<name>.json`, and record the fix as a lesson. Do not loop.

## Never
Copy posts from X unedited, hashtag spam, reply-guy spam, mass follow, link-only posts. Also: invent facts or results, bypass locks/CAPTCHAs/verification, exceed caps, act while the platform shows a warning.
