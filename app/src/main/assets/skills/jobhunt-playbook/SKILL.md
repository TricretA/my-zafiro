---
name: jobhunt-playbook
description: Careers (Kenya-first) operating playbook: platform facts, formats, engagement tactics, caps and routines. Load when working on Careers (Kenya-first).
license: MIT
---

# Careers (Kenya-first) playbook
App package: `com.google.android.gm`. Area: applications, outreach drafts and the tracker.

## What to know
Mission: remote and contract work, and Kenyan employers, for an Android developer who also does full-stack web and AI automation, based in Nairobi (UTC+3). Only channels that genuinely hire Kenyans (skill job-sources-kenya). EVERY job passes the eligibility gate BEFORE a draft is written (skill job-eligibility-gate): remote boards overstate who can apply (a RemoteOK sample was 64% US-only). Realistic yield is a few genuinely eligible roles a day: scan continuously, apply fast to every eligible match, spend the rest of the effort on high-touch applications; never pad with ineligible ones. NEVER auto-submit: drafts queue to the owner (first applications and anything for a Tier A employer need approval). NEVER fabricate experience, clients, metrics or credentials; use only the owner's approved profile file. Any fee, processing payment, M-Pesa registration, WhatsApp-only task or cheque offer is a scam: hard fail. Log every source, eligibility verdict and outcome in applications/tracker.csv so real response rates exist after a month.

## Daily caps (owner-adjustable in the workspace; `engage op=today` shows live counts)
post=15, dm=10, reply=20

## Engagement tactics
Reply to every real reply on the owner's own content first, then add one specific, useful comment on a relevant post, then like a few genuinely good posts. Quality over volume; stop for the day at the caps.

## Research and tools for this platform
Research: web_search for remote roles, r.jina.ai for job pages; log every application in applications/tracker.csv with save_file append.

## Screen routines (`screen_routine routine=list`)
`jobhunt.open_home`, `jobhunt.read_activity`, `jobhunt.scan_feed`, `jobhunt.comment`, `jobhunt.like`.
The labels in these routines are best-effort guesses for the app's English UI. On the first run each routine may fail with a clear reason: read the screen once, add the real label to `files/routines/jobhunt.<name>.json`, and record the fix as a lesson. Do not loop.

## Never
Invent facts or results, post ads, spam, bypass locks/CAPTCHAs/verification, exceed caps, or act while the platform shows a warning.
