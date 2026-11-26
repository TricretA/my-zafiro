---
name: pinterest-playbook
description: Pinterest playbook: facts and specs, posting routes, engagement tactics, new-account warm-up, caps, AI-disclosure and never-list. Load when working on Pinterest.
license: MIT
---

# Pinterest playbook
App package: `com.pinterest`. Area: pins and boards. Only use this when `pinterest` is in `enabled` of `social/PLATFORMS.json`.
Facts marked (unverified) or (heuristic) are hypotheses: test them against the owner's own numbers and never state them to the owner as certain.

## Facts, formats and specs
Pinterest is a visual search engine: pin titles (up to 100 characters), descriptions (up to 500), board names and alt text carry keywords. Standard pin 2:3 (1000x1500); video pins 9:16 or 1:1, 4 s to 15 min; Idea pins were merged into regular pins. Fresh pins (new image) to the same URL beat repeats. Boards should be topical and keyword-named. Steady daily volume beats bursts; pins keep earning for months.

## Posting routes
Capability ladder for this platform. Metricool: supported on the free plan. Order on a PAID plan: 1 Metricool (skill `metricool-posting`), 2 `share_file` + `pinterest.finish_share` (video/image), 3 the app on screen. On the FREE plan (default) the order is 2, 3, then Metricool only as the fallback and only while the monthly counter allows (20 posts a month in total, stop at 18).
Platform specifics: Metricool MCP (Pinterest pins supported on free) if the quota allows; then share_file to com.pinterest + pinterest.finish_share; then screen. The team prepares pin images with make_card (1000x1500), titles, descriptions and the target board as files.

## Engagement tactics
Save good pins from others to relevant boards, follow topical boards, reply to comments on the owner's pins. Engagement matters less than fresh keyword-rich pins and consistent boards.
Always through the engagement loop (skill `engagement-loop`) and the gate: `engage op=plan platform=pinterest action=pinterest.<action>`.

## Warm-up ramp for a NEW account
New account: claim the website, complete the profile, create 5-8 keyword boards, pin 1-2 a day in week 1, then 3-5 a day; do not pin more than 10 a day at first (heuristic).
During warm-up use half of the caps below and keep the ramp. Record the start date in `social/accounts/pinterest.md`.

## Daily caps (gate keys; the owner can change them in the app; `engage op=today` shows live counts)
pinterest.post=5, pinterest.like=20, pinterest.follow=10

## AI-disclosure rules
Pinterest labels AI-modified pins ('AI modified') automatically from metadata and lets users filter them; add 'made with AI' in the description for generated images.

## Research and tools
Pinterest search suggestions via the app; make_card for pin images; trends.pinterest.com if reachable via web_fetch (unverified).
Platform skills to load for specific jobs: pinterest-growth, pinterest-pin-design, pinterest-seo, canva, caption-writer, social-seo, link-in-bio-and-traffic. Also `social-voice-and-slop` for every draft and `social-account-safety` before outward actions.

## Screen routines (`screen_routine routine=list`)
`pinterest.open_home`, `pinterest.read_activity`, `pinterest.scan_feed`, `pinterest.comment`, `pinterest.like`, `pinterest.finish_share` (after `share_file package=com.pinterest`).
Labels are best-effort guesses for the app's English UI: on the first run a routine may fail with a clear reason. Read the screen once, add the real label to `files/routines/pinterest.<name>.json`, and record the fix as a lesson. Do not loop.

## Never
Repin the same pin to many boards at once, misleading images, link to broken pages or unrelated destinations, buy followers. Also: invent facts or results, bypass locks/CAPTCHAs/verification, exceed caps, act while the platform shows a warning.
