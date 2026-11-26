---
name: instagram-playbook
description: Instagram playbook: facts and specs, posting routes, engagement tactics, new-account warm-up, caps, AI-disclosure and never-list. Load when working on Instagram.
license: MIT
---

# Instagram playbook
App package: `com.instagram.android`. Area: reels, carousels, stories and comments. Only use this when `instagram` is in `enabled` of `social/PLATFORMS.json`.
Facts marked (unverified) or (heuristic) are hypotheses: test them against the owner's own numbers and never state them to the owner as certain.

## Facts, formats and specs
Formats: Reels 9:16 up to 3 min (best 7-30 s; keep key text out of the bottom 20% and top 14%), carousels up to 20 images (4:5 = 1080x1350; saves and shares drive reach), single photo, Stories 9:16 24 h, Notes. The first caption line is the hook; Instagram search reads captions, alt text and on-screen text, so use keywords rather than long hashtag blocks (3-5 relevant tags). Professional accounts (creator or business) are needed for scheduling and insights. Trial reels show a Reel to non-followers first (unverified availability).

## Posting routes
Capability ladder for this platform. Metricool: supported on the free plan. Order on a PAID plan: 1 Metricool (skill `metricool-posting`), 2 `share_file` + `instagram.finish_share` (video/image), 3 the app on screen. On the FREE plan (default) the order is 2, 3, then Metricool only as the fallback and only while the monthly counter allows (20 posts a month in total, stop at 18).
Platform specifics: Metricool MCP (Reels, posts, Stories, carousels supported on free) if the quota allows; then share_file to com.instagram.android + instagram.finish_share (pick Reel for video); then the Instagram composer on screen. A Buzzfy/Instagram MCP may exist if the owner enabled it: prefer drafts.

## Engagement tactics
Reply to every comment in the first hour, then comment thoughtfully on 10 accounts in the niche (specific, 1-3 sentences). Reshare to Stories. Use the first 30 minutes after posting for replies: early engagement is the strongest signal creators report.
Always through the engagement loop (skill `engagement-loop`) and the gate: `engage op=plan platform=instagram action=instagram.<action>`.

## Warm-up ramp for a NEW account
New account: days 1-3 browse, about 20 likes a day, 0 posts, 0 follows; week 1 complete the profile, 1 post every other day, 30 likes, up to 10 follows, 3 comments; week 2 3-4 posts, 30-50 likes, up to 20 follows, 5-10 comments; weeks 3-4 a post a day, 10-15 comments. Stop on any 'action blocked' or 'confirm you are human' prompt and wait 24-48 h. (heuristic from PLAN_V2)
During warm-up use half of the caps below and keep the ramp. Record the start date in `social/accounts/instagram.md`.

## Daily caps (gate keys; the owner can change them in the app; `engage op=today` shows live counts)
instagram.post=1, instagram.comment=15, instagram.reply=30, instagram.like=30, instagram.follow=8, instagram.dm=4

## AI-disclosure rules
Turn on Meta's AI label for realistic AI images, video or audio ('AI info'); the Metricool flag is isAiGenerated. Do not remove metadata. Instagram's terms forbid automation that imitates a human: keep caps low and engage like a person.

## Research and tools
Apify 'apify/instagram-scraper' if on; buzzfy MCP niche reports if the owner connected it; make_card for carousel slides; Canva MCP if on.
Platform skills to load for specific jobs: instagram-growth, instagram-reels-publishing, instagram-seo, reels-script, carousel-writer, caption-writer, hashtag-strategy, reply-and-comment-writer, canva, capcut, engagement-routine, smc-post-writer. Also `social-voice-and-slop` for every draft and `social-account-safety` before outward actions.

## Screen routines (`screen_routine routine=list`)
`instagram.open_home`, `instagram.read_activity`, `instagram.scan_feed`, `instagram.comment`, `instagram.like`, `instagram.finish_share` (after `share_file package=com.instagram.android`).
Labels are best-effort guesses for the app's English UI: on the first run a routine may fail with a clear reason. Read the screen once, add the real label to `files/routines/instagram.<name>.json`, and record the fix as a lesson. Do not loop.

## Never
Follow/unfollow churn, mass-DM, comment the same sentence twice, engagement pods, buy followers, repost other creators' reels without credit, use banned or irrelevant hashtags. Also: invent facts or results, bypass locks/CAPTCHAs/verification, exceed caps, act while the platform shows a warning.
