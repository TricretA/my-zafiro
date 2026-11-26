---
name: discord-playbook
description: Discord playbook: facts and specs, posting routes, engagement tactics, new-account warm-up, caps, AI-disclosure and never-list. Load when working on Discord.
license: MIT
---

# Discord playbook
App package: `com.discord`. Area: server channels, threads and announcements. Only use this when `discord` is in `enabled` of `social/PLATFORMS.json`.
Facts marked (unverified) or (heuristic) are hypotheses: test them against the owner's own numbers and never state them to the owner as certain.

## Facts, formats and specs
Discord is community-first: the owner's own server (channels, threads, forum channels, announcement channels you can follow, events) and other servers where the owner is a member. Server rules differ and are enforced by moderators; self-promotion is usually restricted to a designated channel. Messages up to 2000 characters (4000 with Nitro); embeds and attachments up to 10 MB on free (unverified current limit). Discord's terms ban self-bots (user-token automation): only drive the official app on the phone.

## Posting routes
Capability ladder for this platform. Metricool: NOT supported by Metricool. Order: 1 `share_file` + `discord.finish_share` (video/image), 2 the app on screen (`discord.post_text`). Metricool is never used for this platform.
Platform specifics: No Metricool. Post and reply on screen (discord.post_text, discord.comment) or share_file to com.discord + discord.finish_share (pick the channel). Official bots (webhooks) are an alternative the owner can set up.

## Engagement tactics
Welcome new members by name, answer questions in threads, run a weekly event or Q&A, thank contributors, keep announcements short with one clear call to action.
Always through the engagement loop (skill `engagement-loop`) and the gate: `engage op=plan platform=discord action=discord.<action>`.

## Warm-up ramp for a NEW account
In other servers: read the rules, lurk several days, help before you ever mention the owner's project, and promote only where allowed. In the owner's server: seed channels with real content before inviting people.
During warm-up use half of the caps below and keep the ramp. Record the start date in `social/accounts/discord.md`.

## Daily caps (gate keys; the owner can change them in the app; `engage op=today` shows live counts)
discord.post=3, discord.reply=30, discord.dm=2

## AI-disclosure rules
State clearly when a post or answer is AI-assisted if the server asks for it; follow each server's AI rules.

## Research and tools
Read channel rules and pinned posts on screen; web_search for the server's public docs.
Platform skills to load for specific jobs: community-management, reply-and-comment-writer, text-post-and-microblog, crisis-and-moderation, social-strategy. Also `social-voice-and-slop` for every draft and `social-account-safety` before outward actions.

## Screen routines (`screen_routine routine=list`)
`discord.open_home`, `discord.read_activity`, `discord.scan_feed`, `discord.comment`, `discord.like`, `discord.post_text`, `discord.finish_share` (after `share_file package=com.discord`).
Labels are best-effort guesses for the app's English UI: on the first run a routine may fail with a clear reason. Read the screen once, add the real label to `files/routines/discord.<name>.json`, and record the fix as a lesson. Do not loop.

## Never
Self-bots, DMing members unsolicited, mass invites, spamming promo in help channels, posting in the wrong channel, scraping members. Also: invent facts or results, bypass locks/CAPTCHAs/verification, exceed caps, act while the platform shows a warning.
