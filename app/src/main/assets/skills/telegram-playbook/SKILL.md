---
name: telegram-playbook
description: Telegram playbook: facts and specs, posting routes, engagement tactics, new-account warm-up, caps, AI-disclosure and never-list. Load when working on Telegram.
license: MIT
---

# Telegram playbook
App package: `org.telegram.messenger`. Area: channel posts, group replies and DMs. Only use this when `telegram` is in `enabled` of `social/PLATFORMS.json`.
Facts marked (unverified) or (heuristic) are hypotheses: test them against the owner's own numbers and never state them to the owner as certain.

## Facts, formats and specs
Telegram surfaces: Channels (one-to-many broadcast, posts up to 4096 characters, media captions up to 1024, files up to 2 GB (4 GB with Premium), scheduled posts supported in-app), Groups and supergroups (communities with admin rules), bots (official Bot API) and DMs. Discovery is mostly by links, forwards and cross-promotion between channels; public usernames make a channel searchable. Telegram prohibits spam and unsolicited bulk messaging; accounts get limited quickly for it. Official Bot API is the safe automation route (owner builds a bot via @BotFather; unverified here).

## Posting routes
Capability ladder for this platform. Metricool: NOT supported by Metricool. Order: 1 `share_file` + `telegram.finish_share` (video/image), 2 the app on screen (`telegram.post_text`). Metricool is never used for this platform.
Platform specifics: No Metricool. Post to a channel on screen with telegram.post_text or share_file to org.telegram.messenger + telegram.finish_share (pick the channel). Use Telegram's own scheduled-send for timing (long-press Send, Schedule message).

## Engagement tactics
Reply to every group question helpfully, pin the best post, keep a regular posting rhythm (1-3 a day), use polls and replies, and cross-promote with related channels by agreement only.
Always through the engagement loop (skill `engagement-loop`) and the gate: `engage op=plan platform=telegram action=telegram.<action>`.

## Warm-up ramp for a NEW account
New channel: seed it with 5-10 useful posts before sharing it, invite people who know you, grow slowly via personal invites; add at most 20 people to groups a day (heuristic) and only with consent.
During warm-up use half of the caps below and keep the ramp. Record the start date in `social/accounts/telegram.md`.

## Daily caps (gate keys; the owner can change them in the app; `engage op=today` shows live counts)
telegram.post=3, telegram.reply=30, telegram.dm=3

## AI-disclosure rules
Be honest in captions about generated media; channel rules and local laws apply.

## Research and tools
Public channels are readable at https://t.me/s/<channel> via web_fetch; the owner may supply a bot token for the official Bot API (never store it in prompts).
Platform skills to load for specific jobs: community-management, reply-and-comment-writer, caption-writer, text-post-and-microblog, social-selling-and-dm. Also `social-voice-and-slop` for every draft and `social-account-safety` before outward actions.

## Screen routines (`screen_routine routine=list`)
`telegram.open_home`, `telegram.read_activity`, `telegram.scan_feed`, `telegram.comment`, `telegram.like`, `telegram.post_text`, `telegram.finish_share` (after `share_file package=org.telegram.messenger`).
Labels are best-effort guesses for the app's English UI: on the first run a routine may fail with a clear reason. Read the screen once, add the real label to `files/routines/telegram.<name>.json`, and record the fix as a lesson. Do not loop.

## Never
Add strangers to groups, mass-DM, scrape member lists, post in groups that ban promotion, sell restricted goods, use unofficial Telegram clients. Also: invent facts or results, bypass locks/CAPTCHAs/verification, exceed caps, act while the platform shows a warning.
