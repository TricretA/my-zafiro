---
name: whatsapp-playbook
description: WhatsApp playbook: facts and specs, posting routes, engagement tactics, new-account warm-up, caps, AI-disclosure and never-list. Load when working on WhatsApp.
license: MIT
---

# WhatsApp playbook
App package: `com.whatsapp.w4b`. Area: Status, Channels, Communities and the owner digest. Only use this when `whatsapp` is in `enabled` of `social/PLATFORMS.json`.
Facts marked (unverified) or (heuristic) are hypotheses: test them against the owner's own numbers and never state them to the owner as certain.

## Facts, formats and specs
WhatsApp Business (com.whatsapp.w4b) is the owner's channel. Surfaces: Status (24 h, text, photo or video, good for daily updates to contacts), Channels (one-to-many broadcast, followers cannot see each other; channel updates are public if discoverable), Communities (groups under an umbrella with announcement group), Business catalogue and quick replies. Status posts reach saved contacts only. OWNER DIGEST RECIPE (works when the phone is unlocked): write the report file, open_uri whatsapp://send?phone=<number>&text=<urlencoded>, wait about 10 s, find and tap Send through the accessibility tree, verify the Sent tick. Reports are well formatted: bold title (*...*), numbered bold sections (What ran / failed and why / done with counts / fixed / plan / needs from you), '- ' bullets, blank lines, newlines as %0A. Lead messages are typed as DRAFTS and NOT sent: the owner reviews and taps send.

## Posting routes
Capability ladder for this platform. Metricool: NOT supported by Metricool. Order: 1 `share_file` + `whatsapp.finish_share` (video/image), 2 the app on screen. Metricool is never used for this platform.
Platform specifics: No Metricool. Status: share_file to com.whatsapp.w4b opens the 'My status' / chat picker (unverified) + whatsapp.finish_share. Channels and Communities are posted on screen. The digest uses the recipe above.

## Engagement tactics
Reply to every customer or lead message quickly, as drafts for the owner unless it is a pre-approved type. Post one useful Status a day (offer, tip, behind the scenes). Use Channels for regular announcements; never add people to groups without consent.
Always through the engagement loop (skill `engagement-loop`) and the gate: `engage op=plan platform=whatsapp action=whatsapp.<action>`.

## Warm-up ramp for a NEW account
Never message strangers in bulk. A new number: use it normally for a week, message people who know you, grow Status viewers gradually, and never send identical messages to many chats (WhatsApp bans unusual patterns). Opt-in only.
During warm-up use half of the caps below and keep the ramp. Record the start date in `social/accounts/whatsapp.md`.

## Daily caps (gate keys; the owner can change them in the app; `engage op=today` shows live counts)
whatsapp.post=2, whatsapp.reply=20, whatsapp.dm=10

## AI-disclosure rules
Be honest that an assistant helps with replies when asked; label AI-generated images in Status if realistic.

## Research and tools
Use the digest recipe; drafts only for leads. No scraping.
Platform skills to load for specific jobs: social-selling-and-dm, customer-support-draft-response, community-management, caption-writer. Also `social-voice-and-slop` for every draft and `social-account-safety` before outward actions.

## Screen routines (`screen_routine routine=list`)
`whatsapp.open_home`, `whatsapp.read_activity`, `whatsapp.scan_feed`, `whatsapp.comment`, `whatsapp.like`, `whatsapp.finish_share` (after `share_file package=com.whatsapp.w4b`).
Labels are best-effort guesses for the app's English UI: on the first run a routine may fail with a clear reason. Read the screen once, add the real label to `files/routines/whatsapp.<name>.json`, and record the fix as a lesson. Do not loop.

## Never
Bulk or unsolicited messages, scraping numbers, buying contact lists, adding people to groups without consent, sending a message the owner did not approve (except the digest to the owner), sharing private chats. Also: invent facts or results, bypass locks/CAPTCHAs/verification, exceed caps, act while the platform shows a warning.
