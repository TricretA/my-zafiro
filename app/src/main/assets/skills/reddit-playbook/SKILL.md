---
name: reddit-playbook
description: Reddit playbook: facts and specs, posting routes, engagement tactics, new-account warm-up, caps, AI-disclosure and never-list. Load when working on Reddit.
license: MIT
---

# Reddit playbook
App package: `com.reddit.frontpage`. Area: comments and posts in relevant subreddits. Only use this when `reddit` is in `enabled` of `social/PLATFORMS.json`.
Facts marked (unverified) or (heuristic) are hypotheses: test them against the owner's own numbers and never state them to the owner as certain.

## Facts, formats and specs
Reddit punishes self-promotion and obvious automation. Posts need a title (up to 300 characters) and sometimes flair; every subreddit has its own rules, minimum karma and account age, and filters new accounts. The 'one-ninth' rule of thumb: keep promotion under 10% of activity. Reddit has no web fallback from the phone (reddit.com JSON/RSS return 403), so rounds drive the app. Reddit prompts suspected bots to verify they are human (unverified detail); the Data API now needs approval for new apps.

## Posting routes
Capability ladder for this platform. Metricool: NOT supported by Metricool. Order: 1 `share_file` + `reddit.finish_share` (video/image), 2 the app on screen (`reddit.post_text`). Metricool is never used for this platform.
Platform specifics: No Metricool. share_file to com.reddit.frontpage + reddit.finish_share (asks for community and title) or reddit.post_text on screen. Read the subreddit rules first.

## Engagement tactics
Contribute first: answer questions with real detail for days before any post. One genuinely useful comment beats a link. Never post the same text in several subreddits; no links in first comments.
Always through the engagement loop (skill `engagement-loop`) and the gate: `engage op=plan platform=reddit action=reddit.<action>`.

## Warm-up ramp for a NEW account
New account: 1-2 weeks of only reading and 3-5 helpful comments a day in small subs; build karma by being useful; first post only after the rules are clear and the account passes the sub's minimums. Never use multiple accounts to boost.
During warm-up use half of the caps below and keep the ramp. Record the start date in `social/accounts/reddit.md`.

## Daily caps (gate keys; the owner can change them in the app; `engage op=today` shows live counts)
reddit.post=1, reddit.comment=8, reddit.reply=15, reddit.like=20

## AI-disclosure rules
Many subreddits require disclosing AI-generated text or images in the post; read the rules and follow them, and always disclose affiliation when promoting.

## Research and tools
Subreddit rules via web_fetch on https://r.jina.ai/https://www.reddit.com/r/<sub>/about/rules; top posts through the app routines.
Platform skills to load for specific jobs: reddit-marketing, reply-and-comment-writer, community-management, engagement-routine, audience-research. Also `social-voice-and-slop` for every draft and `social-account-safety` before outward actions.

## Screen routines (`screen_routine routine=list`)
`reddit.open_home`, `reddit.read_activity`, `reddit.scan_feed`, `reddit.comment`, `reddit.like`, `reddit.post_text`, `reddit.finish_share` (after `share_file package=com.reddit.frontpage`).
Labels are best-effort guesses for the app's English UI: on the first run a routine may fail with a clear reason. Read the screen once, add the real label to `files/routines/reddit.<name>.json`, and record the fix as a lesson. Do not loop.

## Never
Vote manipulation, multiple accounts, cross-posting spam, hidden self-promotion, DMs to strangers, ignoring rules, arguing with moderators. Also: invent facts or results, bypass locks/CAPTCHAs/verification, exceed caps, act while the platform shows a warning.
