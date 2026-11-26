---
name: x-playbook
description: X (Twitter) playbook: facts and specs, posting routes, engagement tactics, new-account warm-up, caps, AI-disclosure and never-list. Load when working on X (Twitter).
license: MIT
---

# X (Twitter) playbook
App package: `com.twitter.android`. Area: posts, threads and replies. Only use this when `x` is in `enabled` of `social/PLATFORMS.json`.
Facts marked (unverified) or (heuristic) are hypotheses: test them against the owner's own numbers and never state them to the owner as certain.

## Facts, formats and specs
WARNING (owner fact): the owner's @tricreta account was permanently suspended (read-only banner seen 2026-11-13). Do not appeal or create accounts unasked: keep this platform off until the owner says the account is restored, or use only an account the owner created by hand. Formats: posts 280 characters (longer for Premium), threads, replies, quotes, images up to 4, video up to 2:20 on free accounts (longer on Premium; unverified), polls. Replies to larger accounts in the niche are the main lever. Metricool lists X only on paid plans.

## Posting routes
Capability ladder for this platform. Metricool: supported only on a PAID Metricool plan (not on the owner's free plan). Order: 1 `share_file` + `x.finish_share` (video/image), 2 the app on screen (`x.post_text`), 3 Metricool only if the owner has a paid plan that includes it (check `getBrandSettings`).
Platform specifics: Metricool is NOT available on the free plan: use share_file to com.twitter.android + x.finish_share or x.post_text on screen.

## Engagement tactics
Reply early (within 15 minutes) to larger accounts in the niche with something specific; reply to every reply on the owner's posts; keep threads tight with the hook in post 1.
Always through the engagement loop (skill `engagement-loop`) and the gate: `engage op=plan platform=x action=x.<action>`.

## Warm-up ramp for a NEW account
New account created by hand: complete profile, follow 10-20 relevant accounts over days, 3-5 replies a day, 1 post a day for week 1, then 2-4 posts. Avoid links in the first posts. Stop at any lock or verification prompt.
During warm-up use half of the caps below and keep the ramp. Record the start date in `social/accounts/x.md`.

## Daily caps (gate keys; the owner can change them in the app; `engage op=today` shows live counts)
x.post=4, x.comment=15, x.reply=30, x.like=30, x.follow=8, x.dm=3

## AI-disclosure rules
X's synthetic and manipulated media policy bans misleading AI media; label generated images honestly. If an account is automated, X expects the 'Automated' label (unverified requirement for screen-driven accounts).

## Research and tools
web_search; web_fetch via r.jina.ai on public posts (often blocked).
Platform skills to load for specific jobs: x-growth, thread-writer, reply-and-comment-writer, engagement-routine, hook-writer, trend-jacking, social-strategy, writing-style-and-tone, text-post-and-microblog. Also `social-voice-and-slop` for every draft and `social-account-safety` before outward actions.

## Screen routines (`screen_routine routine=list`)
`x.open_home`, `x.read_activity`, `x.scan_feed`, `x.comment`, `x.like`, `x.post_text`, `x.finish_share` (after `share_file package=com.twitter.android`).
Labels are best-effort guesses for the app's English UI: on the first run a routine may fail with a clear reason. Read the screen once, add the real label to `files/routines/x.<name>.json`, and record the fix as a lesson. Do not loop.

## Never
Evade the suspension (new account to dodge a ban), reply spam, mass follow/unfollow, hashtag stuffing, engagement pods, post in the same minute across accounts. Also: invent facts or results, bypass locks/CAPTCHAs/verification, exceed caps, act while the platform shows a warning.
