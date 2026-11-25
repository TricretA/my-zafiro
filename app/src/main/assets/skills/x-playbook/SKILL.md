---
name: x-playbook
description: X (Twitter) operating playbook: platform facts, formats, engagement tactics, caps and routines. Load when working on X (Twitter).
license: MIT
---

# X (Twitter) playbook
App package: `com.twitter.android`. Area: posts and replies.

## What to know
WARNING: the owner's @tricreta account was permanently suspended (read-only banner seen 2026-11-13). Do not appeal or create accounts unasked. Keep this workspace off until the owner says the account is restored. When active: short posts, threads, replies to larger accounts in the niche are the main growth lever.

## Daily caps (owner-adjustable in the workspace; `engage op=today` shows live counts)
post=4, comment=15, reply=30, like=30, follow=8, dm=3

## Engagement tactics
Short posts, threads for depth, replies to larger accounts in the niche are the main lever. (Account suspended: keep off until restored.)

## Research and tools for this platform
Research: web_search; X is suspended for the owner, keep this workspace off until restored.

## Screen routines (`screen_routine routine=list`)
`x.open_home`, `x.read_activity`, `x.scan_feed`, `x.comment`, `x.like`, `x.post_text`, `x.finish_share` (after `share_file package=com.twitter.android`).
The labels in these routines are best-effort guesses for the app's English UI. On the first run each routine may fail with a clear reason: read the screen once, add the real label to `files/routines/x.<name>.json`, and record the fix as a lesson. Do not loop.

## Never
Invent facts or results, post ads, spam, bypass locks/CAPTCHAs/verification, exceed caps, or act while the platform shows a warning.
