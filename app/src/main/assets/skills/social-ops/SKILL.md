---
name: social-ops
description: How the Social Media Manager team works: the live platform file, per-platform action keys, rotation, the file layout and the run loop. Load first in every social run.
license: MIT
---

# Social ops (read first)
One team serves ALL the owner's platforms, but only the ones he switched on. The app writes the live configuration to `social/PLATFORMS.json` (read it with `save_file op=read path=social/PLATFORMS.json`):
```
{"enabled":["instagram","tiktok"],"enabled_since":{"instagram":"2026-11-26"},"caps":{"instagram":{"post":1,"comment":15}},
 "apps":{"instagram":"com.instagram.android"},"metricool":{"plan":"free","monthly_limit":20,"support":{"instagram":"free","linkedin":"paid"}},"warmup_days":28}
```
## Rules for every run
1. Read the file FIRST. Work ONLY on platforms in `enabled`. A platform not listed is off: do not research it, draft for it, engage on it or post to it, even if an old file or memory mentions it.
2. `enabled` empty or the file missing: `notify` the owner once a day (title "Pick your platforms", content "Open Workspaces > Social Media Manager and switch on the platforms you use.") and end with `STATUS: DONE` and the result SKIPPED - no platforms enabled. Do nothing else.
3. For each platform you touch, `load_skill` `<platform>-playbook` (facts, specs, routes, ramp, caps, AI rules, never-list) and follow it. Playbooks list the platform's extra skills.
4. **Caps and the gate use per-platform keys**: `engage op=plan platform=<p> action=<p>.<action> ...` (e.g. `action=instagram.comment`). The caps are in `caps` and in `engage op=today` (keys look like `instagram.post`). A missing key falls back to the generic cap. Never exceed a cap. Serious actions (dm, email_send, payment, delete, apply) still wait for the owner: pass `serious=true` for them.
5. **Warm-up**: a platform enabled less than `warmup_days` days ago, or whose `social/accounts/<platform>.md` does not say `established`, follows the ramp in its playbook and half its caps. Record the account state, handle and start date in `social/accounts/<platform>.md` the first time you touch it. The owner creates accounts by hand: you never create accounts or verify them.
6. **Rotation**: a run covers at most 3 platforms (a posting run, 2). Keep `social/rotation.json` (`{"last":{"instagram":"2026-11-26T10:00"}}`), read it, pick the enabled platforms served longest ago, update it at the end. Over several runs every enabled platform gets its turn.
7. One screen job at a time (the phone is shared). Prefer non-screen work (research, drafting, Metricool analytics) in parallel.
## File layout (all under the Files page, `save_file` paths)
`social/PLATFORMS.json` (app-written, read-only for you) · `social/experience_bank.md` (owner's real anecdotes, prices, places; see social-voice-and-slop) · `social/metricool_count.txt` (monthly counter, see metricool-posting) · `social/rotation.json` · `social/accounts/<platform>.md` · `social/plans/<date>.md` · `social/drafts/<platform>/<date>-<slug>.md` · `social/numbers/<date>.md` · `social/RULES.md` (owner corrections, newest first: they override everything).
## Posting routes (capability ladder, see each playbook)
Owner on a PAID Metricool plan: 1 Metricool MCP, 2 `share_file` + `<platform>.finish_share`, 3 the app on screen. Owner on the FREE plan (default; `metricool.plan` says): 2 and 3 first, Metricool is the safety net (see metricool-posting). Never claim a post is live without proof (a post id, a success screen, or the post on the profile).
## Digest
The owner digest goes by notify and, if `whatsapp` is enabled, by the digest recipe in `whatsapp-playbook`.
