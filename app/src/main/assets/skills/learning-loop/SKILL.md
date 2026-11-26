---
name: learning-loop
description: How the team learns from measured results: when to record outcomes, how to ask the bandit for the next experiment, how to add and use lessons, and the evidence rule. Load before any post, application or weekly review.
license: MIT
---

# Learning loop
Success is **evidence from the world**, never "the run finished". Replies, views, saves, follows, an interview invite: those are results. A run that only checked the date and did nothing is SKIPPED, not a success.
## 1. Record outcomes (`outcomes op=record`)
Always give `platform`, `kind` (post|application|reply|video), `artifact` (post id, URL or application id), `metric`, `value`, `lag`, and `evidence` (where you read the number: "TikTok stats screen", "Gmail reply from X"). A record without evidence is refused. Tag what was tested so the bandit can learn: `hook_type`, `format`, `length_bucket`, `time_bucket`, `topic` (short lower-case words).
- **Posts and videos:** read the stats at +1 h (`lag=1h`), +24 h (`24h`), +7 d (`7d`). Metrics: views, likes, comments, saves, shares, follows_gained (use what the screen shows; write nothing you did not see).
- **Applications:** +3 d, +7 d, +14 d (`3d`, `7d`, `14d`): metric `reply_status` with value 0 = no answer, 1 = auto-reply, 2 = human reply, 3 = interview invite, 4 = offer; evidence is the email or message.
- **Replies and comments:** likes and replies received at +24 h.
Schedule the checks yourself (`schedule op=create ... once_at=`) or put them on the board for the analyst at posting time. Observations are cheap; skipping them blinds the team.
## 2. Read results
`outcomes op=summary platform=<p> days=7` gives per-metric medians and the best items. Compare with your own median, not with other accounts. One viral post is an outlier: do not rebuild the playbook on it.
## 3. Choose the next experiment (`outcomes op=suggest`)
Before writing a post, `outcomes op=suggest platform=<p> dimension=hook_type metric=views arms=question,story,list` returns the arm to use and why. Use exactly that arm and record it as the tag. About 15% of picks are deliberate exploration and new arms are tried a few times before they are judged: that is intended, do not override it.
## 4. Lessons (`lessons`)
- `lessons op=top scope=<workspace> k=5` before you write or decide: it returns the best ACTIVE one-line lessons.
- After a pattern shows in at least two recorded results: `lessons op=add scope=<workspace> claim=<one specific sentence> evidence=o12,o15`. It starts as a CANDIDATE.
- When a later result agrees: `lessons op=confirm id=<id> evidence=<new outcome id>`. When it contradicts: `lessons op=harm id=<id> reason=...`. Only NEW evidence counts. Three confirmations promote it to ACTIVE; harm, age or silence retires it. The owner can confirm or retire on the Learning page.
- A claim states something about content or timing ("question hooks get about twice the replies", "posts after 18:00 Nairobi get more saves"). **Lessons can never change caps, approvals, budgets, safety rules, checklists or truthfulness rules**; such claims are refused. No agent edits those, in any run.
## 5. Weekly review (analyst)
Read `outcomes op=summary days=7`, `lessons op=list`, and the current `<workspace>-playbook` skill. Write a **proposal file** `<workspace>/proposals/<date>.md` in diff style (`- old line` / `+ new line`, each with its evidence ids) and add a board item for the owner. You propose; you never edit a playbook, rule or checklist yourself. Classify each change:
- **LOW risk** (wording, hook phrasing, example lists, topic ideas, timing hints, format preferences): the owner can batch-approve.
- **HIGH risk** (anything touching caps or quotas, approvals or gates, account safety or warm-up, spending, platform-rule interpretation, truthfulness or AI-disclosure, checklists): flag as HIGH, explain the evidence, and wait for the owner.
If there is too little data (fewer than about 10 recorded artifacts), say so and propose only experiments.
