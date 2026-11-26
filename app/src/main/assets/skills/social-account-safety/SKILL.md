---
name: social-account-safety
description: New-account warm-up ramps, daily caps, human pacing and what to do on any block prompt. Load before any outward action on a social platform.
license: MIT
---

# Social account safety
Platform policies prohibit automation that imitates humans. Engagement automation on a screen is where bans concentrate, so the team works like a careful person on the owner's own phone, with low caps and no tricks. These numbers are community heuristics, not platform rules (unverified): the analyst checks them against the owner's own results and the owner can change caps in the app.
1. **Accounts are created and verified by the owner by hand.** Never create accounts, solve CAPTCHAs, bypass phone/email verification, 2FA, age gates or consent screens: `take_over` or NEEDS_HUMAN.
2. **Warm-up ramp for a NEW account (first 28 days):** days 1-3 browse and react only (about 10-20 likes a day, no posts, no follows); week 1 finish the profile, 1 post every other day, a few comments; week 2 3-4 posts, 5-10 comments, up to 20 follows on follow-friendly apps; weeks 3-4 a post a day and 10-15 comments; after that the caps. Use half of the daily caps during warm-up. The exact ramp is in the platform's playbook.
3. **Pace like a person.** `engage op=pace` before every action, then `wait`. Vary the order (a like, a scroll, a comment, a long pause). No bursts, no fixed minute marks, no night activity beyond the schedule, one device and one network.
4. **Stop on any warning.** "Action blocked", "try again later", "confirm you are human", a CAPTCHA, a login challenge, a temporary restriction, a policy strike: stop ALL actions on that platform for 24-48 hours, write it in `social/accounts/<platform>.md`, `notify` the owner once, and continue with the other platforms. Never retry the blocked action and never "test" whether it still works.
5. **Never**: follow/unfollow churn, mass DMs, identical text twice, link drops in comments, engagement pods, buying followers or views, posting the same asset on a platform twice, impersonation, scraped contact lists, a second account to dodge a limit.
6. **Honesty**: no invented experience, results, clients or testimonials. Label realistic AI media where the platform requires it (see playbook) and keep metadata.
7. When a cap is reached stop that action for the day. Do not fill the day with filler: quality over volume.
