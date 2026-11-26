---
name: messenger-playbook
description: Messenger playbook: facts and specs, posting routes, engagement tactics, new-account warm-up, caps, AI-disclosure and never-list. Load when working on Messenger.
license: MIT
---

# Messenger playbook
App package: `com.facebook.orca`. Area: Page inbox and direct chats. Only use this when `messenger` is in `enabled` of `social/PLATFORMS.json`.
Facts marked (unverified) or (heuristic) are hypotheses: test them against the owner's own numbers and never state them to the owner as certain.

## Facts, formats and specs
Messenger is mostly the inbox of the owner's Facebook Page and personal contacts. Business replies from a Page must follow Meta's 24-hour messaging window: outside 24 h after the person's last message only specific tagged message types are allowed (unverified details). Quick replies, saved replies and away messages are settings the owner configures. Typical value: fast, friendly answers to leads and customers.

## Posting routes
Capability ladder for this platform. Metricool: NOT supported by Metricool. Order: 1 `share_file` + `messenger.finish_share` (video/image), 2 the app on screen. Metricool is never used for this platform.
Platform specifics: No Metricool. Reply on screen (messenger.comment routine types a reply). Replies to leads are drafts for the owner unless pre-approved; never send unsolicited first messages.

## Engagement tactics
Answer within minutes during the owner's day, use the person's name, answer the question first and offer one next step. Escalate complaints to the owner. Keep a short FAQ in memory.
Always through the engagement loop (skill `engagement-loop`) and the gate: `engage op=plan platform=messenger action=messenger.<action>`.

## Warm-up ramp for a NEW account
Not applicable as outreach: Messenger is reactive. Do not message people who did not message you.
During warm-up use half of the caps below and keep the ramp. Record the start date in `social/accounts/messenger.md`.

## Daily caps (gate keys; the owner can change them in the app; `engage op=today` shows live counts)
messenger.reply=30, messenger.dm=5

## AI-disclosure rules
Say that a team assistant helps with replies when asked; Meta requires honesty about automated messaging for business Pages (unverified wording).

## Research and tools
None: reads the inbox on screen.
Platform skills to load for specific jobs: customer-support-draft-response, social-selling-and-dm, community-management, reply-and-comment-writer. Also `social-voice-and-slop` for every draft and `social-account-safety` before outward actions.

## Screen routines (`screen_routine routine=list`)
`messenger.open_home`, `messenger.read_activity`, `messenger.scan_feed`, `messenger.comment`, `messenger.like`, `messenger.finish_share` (after `share_file package=com.facebook.orca`).
Labels are best-effort guesses for the app's English UI: on the first run a routine may fail with a clear reason. Read the screen once, add the real label to `files/routines/messenger.<name>.json`, and record the fix as a lesson. Do not loop.

## Never
Unsolicited bulk messages, spam links, scraping chats, impersonating the owner in a way that deceives, replying outside the 24 h window without a permitted tag. Also: invent facts or results, bypass locks/CAPTCHAs/verification, exceed caps, act while the platform shows a warning.
