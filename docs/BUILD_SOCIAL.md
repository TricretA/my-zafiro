# Social Media Manager (feat/social) - build notes

All of this is UNVERIFIED on a phone. Routine labels are best-effort guesses; platform facts marked (unverified)/(heuristic) in the playbooks are hypotheses.

## What changed
- One workspace `social` ("Social Media Manager"): 8 shared agents (`social-director` ... `social-analyst`), 12 shared tasks that loop over the enabled platforms (max 3 per run, rotation file `social/rotation.json`). Generator: `tools/autonomous/gen_social.py` + data `tools/autonomous/social_platforms.py`, called from `gen_workspaces.py`.
- 21 platforms (16 required + bluesky, mastodon, quora, tumblr, medium as optional extras). Each has a `<id>-playbook` skill (facts/specs, posting ladder, engagement, warm-up ramp, caps, AI disclosure, never-list), 5-7 routines (`open_home`, `read_activity`, `scan_feed`, `comment`, `like`, `post_text`, `finish_share`) and an entry in `assets/social/platforms.json`.
- New skills: `social-ops`, `social-account-safety`, `social-voice-and-slop`, `metricool-posting` (all start disabled). `humanizer` and `stop-slop` are wired into director/writer/reviewer.
- Old per-platform workspaces (substack, x, linkedin, instagram, facebook, tiktok, youtube, pinterest, reddit, threads, whatsapp) are removed from assets. Their routines are regenerated under the same ids by the social generator.

## Platform toggles
- Settings file `files/settings/social_platforms.json` (`enabled` = id -> day switched on, `caps` = per-platform overrides). API in `WorkspaceManager`: `enabledPlatforms()`, `setPlatformEnabled(id, on)`, `setPlatformCaps(id, caps)`, `enableInstalledPlatforms()`.
- On every change the app rewrites `files/outputs/social/PLATFORMS.json` (agents read it with `save_file op=read path=social/PLATFORMS.json`): `enabled`, `enabled_since`, `caps`, `apps`, `metricool {plan, monthly_limit, support}`, `warmup_days`.
- Caps use the existing quota mechanism: workspace quotas become `instagram.post=1,instagram.comment=15,...` plus generic fallback keys. Agents gate with `engage op=plan platform=<p> action=<p>.<action>`. Core change: `Quotas.check` falls back from `p.action` to `action`; `Tiering.gate` uses the part after the last dot (so `instagram.dm` is still serious). Tested in `TeamTest`.
- Zero platforms: switching the workspace on does nothing, posts a notification and a toast asking to pick platforms. Turning the last platform off pauses the workspace. Agents also handle an empty list (notify once, SKIPPED).
- Skills: switching a platform on enables its playbook and skills; switching it off disables the ones no other enabled platform uses (`SocialPlatforms.exclusiveSkills`).

## Metricool
Owner plan is FREE (20 posts a month in total, no LinkedIn/X): the ladder is plan-aware. Free: share_file + finish_share, then screen, Metricool only as fallback (counter `social/metricool_count.txt`, stop at 18). Paid: Metricool first. See skill `metricool-posting`. Not verified: whether TikTok/YouTube posts via Metricool go public, AI flag field names per network.

## Migration
`WorkspaceManager.migrateSupersededWorkspaces` runs once at startup (flag `settings/social_migrated.flag`): old workspaces are paused but their agents, tasks, board items and history stay in the DB; those that were ON become platform toggles and the social workspace is switched on.

## Not done
- Per-platform enable of tasks (the schedule is shared), enforced per-agent tool lists, real Metricool plan detection (plan is fixed to "free" in PLATFORMS.json), any phone verification, routine label calibration.
