# BUILD_LEARN: Share-to-Zafiro receiver and the learning loop (branch feat/learn)

All phone behaviour below is UNVERIFIED until the owner confirms it on the Tecno. CI results are the only proof of compilation and tests.

## Part 1: Share-to-Zafiro
- `ShareReceiverActivity` (autonomy/android, exported, no UI): ACTION_SEND and ACTION_SEND_MULTIPLE for image/*, video/*, audio/*, text/plain, application/pdf. Label "Zafiro inbox". It stays alive while copying because the sender's temporary read grant ends with the activity. Only content:// streams are accepted (never file://). EXTRA_TEXT is saved as a .txt. Source app = referrer package when Android provides it.
- `InboxStore` copies to `files/outputs/inbox/<yyyyMMdd-HHmmss>-<n>-<safe-name>.<ext>` (via a .part file, 300 MB cap, partial files removed), appends to `index.jsonl` (id, t, src, mime, size, path, kind). Names are sanitised by `InboxNames` (autonomy/core): no separators, no leading dots, 40 chars, extension from name or mime. The inbox is inside `outputs`, so the Files page shows it.
- Tool `inbox` (op=list since= kind= limit=, op=take id= [to=], op=delete id=) forwards to `InboxService` (business:api). `to` must resolve inside Zafiro's files folder.
- Skill `share-inbox`; `content-factory-screen` now prefers it and keeps MediaStore/shell as the fallback. Added to every executor skill map, video-media-operator and the video-studio skill list; the executor tool lists gained `inbox`.
- Not verified: that ChatGPT/Gemini/Flow/ElevenLabs/Chrome show "Zafiro inbox" in their share sheets, the share-icon labels in the skill, and the toast on the Tecno.

## Part 2: learning loop
- Room schema v5 (`MIGRATION_4_5`, new tables only): `outcome`, `lesson`; `LearnDao` and `LearnEntities.kt` are separate files. `AutonomyRepository.learnDao()`.
- Pure core (autonomy/core): `Bandit` (Thompson sampling over Beta posteriors, injected `Random`, exploration floor clamped to 10-20 percent, arms with fewer than 3 samples are tried first), `Rewards` (value divided by the trailing median of the same workspace+platform, capped at 5x, reward = r/(1+r)), `OutcomeAnalysis`, `Lesson*` (Wilson lower bound on counts that halve every 30 days; candidate to active at 3 helpful, 2 distinct evidence, confidence 0.5, harm at most a third of help; retire on harm >= help+2, confidence under 0.35 with 3+ decayed samples, idle past ttl (90 d active) or 21 d (candidate); near-duplicate merge by token similarity 0.75), `LessonGuard`, `LessonsPrompt`, `RunContext`.
- Tools `outcomes` (record, list, summary, suggest) and `lessons` (add, confirm, harm, top, list) forward to `LearnService` (business:api), implemented by `LearnServiceImpl`. Record requires evidence. A confirmation only counts with evidence not already attached to the lesson, so one agent cannot inflate counts by repeating itself.
- Prompt injection: `RunExecutor` and `DelegateRunner` add "LESSONS" lines (top 5 ACTIVE, unexpired, scope = workspace or global, one line each) next to the run history. Candidates and retired lessons never reach a prompt.
- Safety: `LessonGuard` refuses claims that mention caps, quotas, approvals, budgets, checklists, CAPTCHA, consent, bypass/disable/override and similar. Caps, quotas, safety fields and checklists are not reachable from any learning tool. The weekly reviewer only writes a proposal file; an owner applies it.
- Weekly task `review` (analyst, SUN 16:30 + workspace offset) in every generated workspace; proposals go to `<workspace>/proposals/<date>.md` with LOW/HIGH labels plus a board item for the owner. Workspace definitions are now version 2 so existing installs receive the new skills and task on their next install pass (note: this also refreshes bundled agent instructions).
- Skill `learning-loop` (recording schedule +1h/+24h/+7d for posts, +3/7/14 d for applications, suggest, lessons, weekly review, risk classification). Added to the skill maps of director, strategist, writer, engager, analyst.
- UI: History menu tile "Learning" opens `LearningPage` (lessons with confidence, evidence count, confirm/retire; last-7-day results per platform).

## Deviations and not done
- The regression guard (golden set) and automatic application of LOW-risk proposals are not built: every proposal goes to the owner.
- Outcomes are recorded by agents (tool calls), not by a zero-model reader job; no automatic +1h/+24h scheduling yet (the skill tells agents to schedule them).
- Harm reasons are required but not stored.
