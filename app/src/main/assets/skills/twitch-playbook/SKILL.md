---
name: twitch-playbook
description: Twitch playbook: facts and specs, posting routes, engagement tactics, new-account warm-up, caps, AI-disclosure and never-list. Load when working on Twitch.
license: MIT
---

# Twitch playbook
App package: `tv.twitch.android.app`. Area: stream announcements, chat and clips. Only use this when `twitch` is in `enabled` of `social/PLATFORMS.json`.
Facts marked (unverified) or (heuristic) are hypotheses: test them against the owner's own numbers and never state them to the owner as certain.

## Facts, formats and specs
Twitch is live video: growth comes from a consistent stream schedule, chat interaction, clips and repurposing clips to short-form platforms. Surfaces: stream schedule, chat, Clips, VODs, channel panels, Shorts-style Stories (unverified). Affiliate needs 50 followers, 8 streaming hours and 7 days over 30 days (unverified current thresholds). Streaming itself needs the owner (camera, game or app); the team handles announcements, schedule, chat replies when the owner is live, clip titles and repurposing.

## Posting routes
Capability ladder for this platform. Metricool: supported only on a PAID Metricool plan (not on the owner's free plan). Order: 1 `share_file` + `twitch.finish_share` (not available here), 2 the app on screen, 3 Metricool only if the owner has a paid plan that includes it (check `getBrandSettings`).
Platform specifics: Metricool MCP (Twitch stream scheduling) is unverified and may be paid: check getBrandSettings first; otherwise announce streams on the other enabled platforms and set the Twitch schedule by hand. Clips are repurposed with the video-studio workspace or captions-and-clipping.

## Engagement tactics
During a stream: greet by name, answer questions, thank follows and raids. After: review chat, pick 1-3 clips, write titles, post them as shorts on other enabled platforms.
Always through the engagement loop (skill `engagement-loop`) and the gate: `engage op=plan platform=twitch action=twitch.<action>`.

## Warm-up ramp for a NEW account
New channel: stream on a fixed schedule, tell friends, stay under 20-30 new follows from outreach a day; never follow-for-follow or view-bot. Twitch Partner/Affiliate rules are strict.
During warm-up use half of the caps below and keep the ramp. Record the start date in `social/accounts/twitch.md`.

## Daily caps (gate keys; the owner can change them in the app; `engage op=today` shows live counts)
twitch.post=1, twitch.reply=40

## AI-disclosure rules
Twitch requires content classification labels and branded-content disclosure; label AI-generated assets honestly (unverified specifics).

## Research and tools
web_fetch on public channel pages; clips on screen.
Platform skills to load for specific jobs: livestream-and-realtime, community-management, short-form-video-script, captions-and-clipping, opus-clip, social-strategy, reply-and-comment-writer. Also `social-voice-and-slop` for every draft and `social-account-safety` before outward actions.

## Screen routines (`screen_routine routine=list`)
`twitch.open_home`, `twitch.read_activity`, `twitch.scan_feed`, `twitch.comment`, `twitch.like`.
Labels are best-effort guesses for the app's English UI: on the first run a routine may fail with a clear reason. Read the screen once, add the real label to `files/routines/twitch.<name>.json`, and record the fix as a lesson. Do not loop.

## Never
View/follow bots, hate raids, ban-evading, unsolicited whispers, stream sniping, copyrighted music in VODs. Also: invent facts or results, bypass locks/CAPTCHAs/verification, exceed caps, act while the platform shows a warning.
