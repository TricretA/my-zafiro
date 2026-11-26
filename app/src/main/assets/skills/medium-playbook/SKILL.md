---
name: medium-playbook
description: Medium playbook: facts and specs, posting routes, engagement tactics, new-account warm-up, caps, AI-disclosure and never-list. Load when working on Medium.
license: MIT
---

# Medium playbook (optional extra platform)
App package: `com.medium.reader`. Area: articles and responses. Only use this when `medium` is in `enabled` of `social/PLATFORMS.json`.
Facts marked (unverified) or (heuristic) are hypotheses: test them against the owner's own numbers and never state them to the owner as certain.

## Facts, formats and specs
Optional extra. Medium is a long-form reading platform: publish stories (title, subtitle, 5 tags, one strong image), submit to publications for reach, and respond to other writers. The Partner Program pays by member reading time (eligibility: Kenya support is unverified). Medium's policy asks writers to disclose AI-generated writing and limits distribution of undisclosed AI content (unverified details). Publishing is done in the app or web editor; there is no official write API for new integrations.

## Posting routes
Capability ladder for this platform. Metricool: NOT supported by Metricool. Order: 1 `share_file` + `medium.finish_share` (not available here), 2 the app on screen. Metricool is never used for this platform.
Platform specifics: No Metricool. Draft as a file with the writer + reviewer chain, then the owner publishes (or screen routine once verified). Responses via medium.comment.

## Engagement tactics
Respond thoughtfully to 3-5 stories in your tags, follow writers you read, submit to 1-2 fitting publications, add a canonical link when cross-posting from Substack or a blog.
Always through the engagement loop (skill `engagement-loop`) and the gate: `engage op=plan platform=medium action=medium.<action>`.

## Warm-up ramp for a NEW account
New account: complete the profile, publish one strong story, respond to 3 stories a day, follow slowly.
During warm-up use half of the caps below and keep the ramp. Record the start date in `social/accounts/medium.md`.

## Daily caps (gate keys; the owner can change them in the app; `engage op=today` shows live counts)
medium.post=1, medium.comment=5, medium.like=20, medium.follow=5

## AI-disclosure rules
Disclose AI assistance per Medium's policy; do not publish unreviewed AI text.

## Research and tools
web_search 'site:medium.com <topic>'; rss at https://medium.com/feed/@<user> (unverified for others).
Platform skills to load for specific jobs: email-and-newsletter, writing-style-and-tone, voice-builder, stop-slop, social-seo, educational-content-and-how-to, storytelling-and-narrative. Also `social-voice-and-slop` for every draft and `social-account-safety` before outward actions.

## Screen routines (`screen_routine routine=list`)
`medium.open_home`, `medium.read_activity`, `medium.scan_feed`, `medium.comment`, `medium.like`.
Labels are best-effort guesses for the app's English UI: on the first run a routine may fail with a clear reason. Read the screen once, add the real label to `files/routines/medium.<name>.json`, and record the fix as a lesson. Do not loop.

## Never
Clickbait, plagiarism, spam responses with links, publishing unedited AI output. Also: invent facts or results, bypass locks/CAPTCHAs/verification, exceed caps, act while the platform shows a warning.
