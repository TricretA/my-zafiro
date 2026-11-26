---
name: quora-playbook
description: Quora playbook: facts and specs, posting routes, engagement tactics, new-account warm-up, caps, AI-disclosure and never-list. Load when working on Quora.
license: MIT
---

# Quora playbook (optional extra platform)
App package: `com.quora.android`. Area: answers, comments and Spaces. Only use this when `quora` is in `enabled` of `social/PLATFORMS.json`.
Facts marked (unverified) or (heuristic) are hypotheses: test them against the owner's own numbers and never state them to the owner as certain.

## Facts, formats and specs
Optional extra. Quora is long-tail search traffic: a good answer keeps getting views for years and is cited by AI search. Answers should be specific, first-person experience where real, with a short link only when it truly helps. Quora Spaces are topic communities. Quora's rules ban spam and self-promotion without disclosure.

## Posting routes
Capability ladder for this platform. Metricool: NOT supported by Metricool. Order: 1 `share_file` + `quora.finish_share` (not available here), 2 the app on screen (`quora.post_text`). Metricool is never used for this platform.
Platform specifics: No Metricool. Answers on screen (quora.post_text writes an answer to the question currently open).

## Engagement tactics
Answer new questions in the niche in the first hours, add one concrete example, follow up on comments.
Always through the engagement loop (skill `engagement-loop`) and the gate: `engage op=plan platform=quora action=quora.<action>`.

## Warm-up ramp for a NEW account
New account: complete the profile and credentials honestly, answer 1-2 questions a day, no links for the first 2 weeks.
During warm-up use half of the caps below and keep the ramp. Record the start date in `social/accounts/quora.md`.

## Daily caps (gate keys; the owner can change them in the app; `engage op=today` shows live counts)
quora.post=2, quora.comment=5, quora.reply=10

## AI-disclosure rules
Disclose AI help if the question or Space requires it; never invent credentials or experience.

## Research and tools
web_search for questions people ask; Quora search on screen.
Platform skills to load for specific jobs: reply-and-comment-writer, social-seo, audience-research, educational-content-and-how-to, ai-search-optimization, stop-slop. Also `social-voice-and-slop` for every draft and `social-account-safety` before outward actions.

## Screen routines (`screen_routine routine=list`)
`quora.open_home`, `quora.read_activity`, `quora.scan_feed`, `quora.comment`, `quora.like`, `quora.post_text`.
Labels are best-effort guesses for the app's English UI: on the first run a routine may fail with a clear reason. Read the screen once, add the real label to `files/routines/quora.<name>.json`, and record the fix as a lesson. Do not loop.

## Never
Copy answers, spam links, fake credentials, answer the same text to many questions. Also: invent facts or results, bypass locks/CAPTCHAs/verification, exceed caps, act while the platform shows a warning.
