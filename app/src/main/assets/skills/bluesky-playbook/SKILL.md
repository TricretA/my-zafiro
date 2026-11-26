---
name: bluesky-playbook
description: Bluesky playbook: facts and specs, posting routes, engagement tactics, new-account warm-up, caps, AI-disclosure and never-list. Load when working on Bluesky.
license: MIT
---

# Bluesky playbook (optional extra platform)
App package: `xyz.blueskyweb.app`. Area: short posts, replies and custom feeds. Only use this when `bluesky` is in `enabled` of `social/PLATFORMS.json`.
Facts marked (unverified) or (heuristic) are hypotheses: test them against the owner's own numbers and never state them to the owner as certain.

## Facts, formats and specs
Optional extra. Posts up to 300 characters, up to 4 images, video up to 60 s (unverified current cap), threads, custom feeds and starter packs for discovery, handles can be your own domain. The audience is conversational and sensitive to marketing. Bluesky's rules ask automated accounts to be labelled and to avoid spam replies.

## Posting routes
Capability ladder for this platform. Metricool: supported on the free plan. Order on a PAID plan: 1 Metricool (skill `metricool-posting`), 2 `share_file` + `bluesky.finish_share` (video/image), 3 the app on screen (`bluesky.post_text`). On the FREE plan (default) the order is 2, 3, then Metricool only as the fallback and only while the monthly counter allows (20 posts a month in total, stop at 18).
Platform specifics: Metricool MCP (Bluesky supported; unverified on the free plan) if the quota allows; then bluesky.post_text on screen.

## Engagement tactics
Reply with substance, join relevant custom feeds, follow people you actually read, post at a human rhythm, use alt text.
Always through the engagement loop (skill `engagement-loop`) and the gate: `engage op=plan platform=bluesky action=bluesky.<action>`.

## Warm-up ramp for a NEW account
New account: verify the domain handle if possible, follow 20 relevant people over a few days, 3 replies a day, 1 post a day for week 1, then the caps.
During warm-up use half of the caps below and keep the ramp. Record the start date in `social/accounts/bluesky.md`.

## Daily caps (gate keys; the owner can change them in the app; `engage op=today` shows live counts)
bluesky.post=4, bluesky.comment=15, bluesky.reply=30, bluesky.like=30, bluesky.follow=10, bluesky.dm=3

## AI-disclosure rules
Label AI-generated images with a content label (unverified UI) and disclose automation if any.

## Research and tools
web_search; public posts via web_fetch on bsky.app (public API at public.api.bsky.app, unverified).
Platform skills to load for specific jobs: text-post-and-microblog, thread-writer, reply-and-comment-writer, engagement-routine, hook-writer, community-management. Also `social-voice-and-slop` for every draft and `social-account-safety` before outward actions.

## Screen routines (`screen_routine routine=list`)
`bluesky.open_home`, `bluesky.read_activity`, `bluesky.scan_feed`, `bluesky.comment`, `bluesky.like`, `bluesky.post_text`, `bluesky.finish_share` (after `share_file package=xyz.blueskyweb.app`).
Labels are best-effort guesses for the app's English UI: on the first run a routine may fail with a clear reason. Read the screen once, add the real label to `files/routines/bluesky.<name>.json`, and record the fix as a lesson. Do not loop.

## Never
Reply-guy spam, mass follow/unfollow, unlabelled bots, scraping to train models, dunking. Also: invent facts or results, bypass locks/CAPTCHAs/verification, exceed caps, act while the platform shows a warning.
