---
name: linkedin-playbook
description: LinkedIn playbook: facts and specs, posting routes, engagement tactics, new-account warm-up, caps, AI-disclosure and never-list. Load when working on LinkedIn.
license: MIT
---

# LinkedIn playbook
App package: `com.linkedin.android`. Area: posts, comments and connection messages. Only use this when `linkedin` is in `enabled` of `social/PLATFORMS.json`.
Facts marked (unverified) or (heuristic) are hypotheses: test them against the owner's own numbers and never state them to the owner as certain.

## Facts, formats and specs
Formats: text posts up to 3000 characters (the feed cuts at about 210 characters, so line 1 is the hook), documents/carousels (PDF), single image, native video (vertical 9:16 up to 10 min), polls, newsletters, articles. Comments on other people's posts are the main discovery lever. LinkedIn's terms prohibit third-party automation tools: this team only drives the official app on the owner's phone at human pace with low caps. Metricool lists LinkedIn only on paid plans (not on the owner's free plan).

## Posting routes
Capability ladder for this platform. Metricool: supported only on a PAID Metricool plan (not on the owner's free plan). Order: 1 `share_file` + `linkedin.finish_share` (video/image), 2 the app on screen (`linkedin.post_text`), 3 Metricool only if the owner has a paid plan that includes it (check `getBrandSettings`).
Platform specifics: Metricool is NOT available on the free plan: use share_file to com.linkedin.android + linkedin.finish_share or linkedin.post_text on screen. Connection requests and DMs are always drafts the owner approves.

## Engagement tactics
Within the first hour of a post, comment thoughtfully on 5-10 posts of people in the niche; reply to every comment on the owner's post with a real thought. Links work better in the first comment than in the body (heuristic: test it).
Always through the engagement loop (skill `engagement-loop`) and the gate: `engage op=plan platform=linkedin action=linkedin.<action>`.

## Warm-up ramp for a NEW account
Established-profile first: complete headline, About, experience and a photo before activity. Week 1: 5 comments and 5 reactions a day, 0-2 connection requests with a personal note; week 2: 1 post every 2 days; week 3+: the caps. Limit connection requests to 10-15 a day at most (heuristic); stop at any restriction notice. Never fake experience.
During warm-up use half of the caps below and keep the ramp. Record the start date in `social/accounts/linkedin.md`.

## Daily caps (gate keys; the owner can change them in the app; `engage op=today` shows live counts)
linkedin.post=1, linkedin.comment=12, linkedin.reply=25, linkedin.like=25, linkedin.follow=5, linkedin.dm=5

## AI-disclosure rules
LinkedIn shows C2PA 'CR' labels on content with provenance metadata; no mandatory toggle (unverified). Be honest in the text that an image is generated. Never claim jobs, clients, certifications or metrics that are not real.

## Research and tools
web_search 'site:linkedin.com/posts <topic>', r.jina.ai on public posts; screen routines for comments.
Platform skills to load for specific jobs: linkedin-growth, linkedin-post-writer, linkedin-company-pages, profile-optimization, reply-and-comment-writer, social-selling-and-dm, engagement-routine, social-strategy, smc-post-writer. Also `social-voice-and-slop` for every draft and `social-account-safety` before outward actions.

## Screen routines (`screen_routine routine=list`)
`linkedin.open_home`, `linkedin.read_activity`, `linkedin.scan_feed`, `linkedin.comment`, `linkedin.like`, `linkedin.post_text`, `linkedin.finish_share` (after `share_file package=com.linkedin.android`).
Labels are best-effort guesses for the app's English UI: on the first run a routine may fail with a clear reason. Read the screen once, add the real label to `files/routines/linkedin.<name>.json`, and record the fix as a lesson. Do not loop.

## Never
Auto-connect in bulk, pitch in the first DM, pod/engagement-group comments, copy others' posts, fake credentials, tag people for reach, post AI-slop 'broetry'. Also: invent facts or results, bypass locks/CAPTCHAs/verification, exceed caps, act while the platform shows a warning.
