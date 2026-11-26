---
name: mastodon-playbook
description: Mastodon playbook: facts and specs, posting routes, engagement tactics, new-account warm-up, caps, AI-disclosure and never-list. Load when working on Mastodon.
license: MIT
---

# Mastodon playbook (optional extra platform)
App package: `org.joinmastodon.android`. Area: toots, boosts and replies. Only use this when `mastodon` is in `enabled` of `social/PLATFORMS.json`.
Facts marked (unverified) or (heuristic) are hypotheses: test them against the owner's own numbers and never state them to the owner as certain.

## Facts, formats and specs
Optional extra. Federated: the server (instance) sets the rules and the character limit (default 500). Hashtags are the discovery mechanism (CamelCase them for accessibility); no algorithmic feed; alt text on images is a strong norm; content warnings (CW) are used for sensitive topics. Self-promotion is tolerated but unwelcome when repetitive. Several apps exist; this routine set assumes the official app (org.joinmastodon.android).

## Posting routes
Capability ladder for this platform. Metricool: NOT supported by Metricool. Order: 1 `share_file` + `mastodon.finish_share` (video/image), 2 the app on screen (`mastodon.post_text`). Metricool is never used for this platform.
Platform specifics: No Metricool. Post on screen with mastodon.post_text.

## Engagement tactics
Use relevant CamelCase hashtags, boost useful posts, reply kindly, add alt text, read the server rules and local timeline first.
Always through the engagement loop (skill `engagement-loop`) and the gate: `engage op=plan platform=mastodon action=mastodon.<action>`.

## Warm-up ramp for a NEW account
New account: introduction post with hashtags, follow 20-30 people from your niche gradually, 3 replies a day for week 1, then post daily.
During warm-up use half of the caps below and keep the ramp. Record the start date in `social/accounts/mastodon.md`.

## Daily caps (gate keys; the owner can change them in the app; `engage op=today` shows live counts)
mastodon.post=4, mastodon.comment=10, mastodon.reply=25, mastodon.like=30, mastodon.follow=10

## AI-disclosure rules
Mark AI-generated images (many instances require it). Do not post AI scraping notices as spam.

## Research and tools
Hashtag timelines on screen; public timeline JSON of the owner's instance via web_fetch (unverified).
Platform skills to load for specific jobs: text-post-and-microblog, reply-and-comment-writer, community-management, engagement-routine, hashtag-strategy. Also `social-voice-and-slop` for every draft and `social-account-safety` before outward actions.

## Screen routines (`screen_routine routine=list`)
`mastodon.open_home`, `mastodon.read_activity`, `mastodon.scan_feed`, `mastodon.comment`, `mastodon.like`, `mastodon.post_text`, `mastodon.finish_share` (after `share_file package=org.joinmastodon.android`).
Labels are best-effort guesses for the app's English UI: on the first run a routine may fail with a clear reason. Read the screen once, add the real label to `files/routines/mastodon.<name>.json`, and record the fix as a lesson. Do not loop.

## Never
Ignore server rules, auto-post without reading, hashtag stuffing, harassing, bridging bots without consent. Also: invent facts or results, bypass locks/CAPTCHAs/verification, exceed caps, act while the platform shows a warning.
