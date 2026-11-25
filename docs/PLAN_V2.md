# Zafiro V2 plan: from "basic dashboard" to a phone-sized Claude Code that works for you

Date 2026-11-25. Status: **research and proposal only, nothing built.** For discussion; the owner decides what is added or removed.
Evidence tags: **[V]** read on the primary source by a research agent, **[S]** secondary source, **[U]** unverified or an estimate, **[CODE]** read in our repo.
Raw research reports (agents' words) were summarised here; prices and limits move, re-check before building on them.

---------------------------------------------------------------------------------------------------------
## 0. The honest diagnosis

What is weak today [CODE]:
1. **Workspaces are per platform (13 of them).** You want ONE Social Media Manager with a toggle per platform, one Job Hunter, one Builder. The current split duplicates agents, tasks and prompts and has no shared strategy or learning.
2. **No capability ladder.** Everything goes through the screen. Posting should try API/MCP first, then a recorded routine, then LLM screen driving.
3. **No content factory.** The team can write text but cannot reliably make video, images, voice, captions. The phone cannot render (Helio A22, 4 GB) [S].
4. **No learning loop.** Agents write lessons to memory, but nothing measures results (views, replies, interviews) and nothing updates playbooks from evidence.
5. **Tool lists are prompt guidance only** [CODE]. For an unattended agent that reads comments, DMs and job posts this is a security gap (section 6).
6. **278 bundled skills, 27 on by default, many irrelevant** (8 GSAP, art, course, ADHD, theme, data/finance/product-management families). Their descriptions ride on every request.
7. **UI is a list of pages** (dashboard, teams, agents, tasks, files, settings). It does not show what the agent is doing, what it needs from you, what it learned, or what it costs.
8. **Nothing verified on the phone yet.** Every routine label, `share_file`, compact reads: unconfirmed. A clean phone is the right moment to calibrate once and record.

---------------------------------------------------------------------------------------------------------
## 1. Target architecture (one picture in words)

```
                      You (Inbox: approvals, questions, NEEDS_HUMAN)
                                    |
        +---------------------------+-----------------------------+
        |                           |                             |
  Social Media Manager         Job Hunter                      Builder (code/render)
  (1 workspace,               (1 workspace,                   (GitHub-backed)
   platform toggles)           feeds+ATS+email)
        |                           |                             |
  Director -> Researcher, Strategist, Writer, Reviewer(+AntiSlop), Engager, Publisher, Analyst
        |
  CAPABILITY LADDER for every outward action
   1) API / MCP        (official or aggregator; cheapest, safest)
   2) Recorded routine (zero-model replay of a verified screen sequence)
   3) Screen agent     (compact reads, LLM driven; slowest, riskiest)
   4) Human            (NEEDS_HUMAN: CAPTCHA, verification, money, first-N approvals)
        |
  Shared services: Memory+Lessons (with outcomes), Budget/cost guard, Safety layer, Media pipeline, Scheduler
```

Key design rules (each traced to evidence):
- **Doer and judge are separate contexts** (Claude Code `/goal` evaluator, Managed Agents "Outcomes" grader) [V]. Reviewer/grader never edits its own work.
- **State lives in the DB/files, not the chat** (long-running harness article: progress file + feature list + one feature at a time) [V]. We already have DB-as-truth; add a per-workspace `STATE` file and a `features` JSON checklist per mission.
- **Always-loaded text stays tiny; detail is fetched on demand** (skills progressive disclosure, MEMORY.md index of one-liners) [V]. Our scrubber and compact reads already move this way.
- **Deny rules and budgets are enforced in code, never only in the prompt** (compaction can drop conversational rules) [V].
- **Reader/writer split** for anything touching untrusted text (section 6).

---------------------------------------------------------------------------------------------------------
## 2. Workspace 1: Social Media Manager (one workspace, platform toggles)

### 2.1 Structure
- Workspace settings: platform toggles (Instagram, Facebook, TikTok, YouTube, X, Substack, Reddit, Threads, LinkedIn, Pinterest, Bluesky...), per-platform account handle, per-platform route (API/MCP vs screen), daily caps, "needs approval first N posts".
- **Shared agents** (platform-agnostic): Director, Researcher, Strategist, Writer, Reviewer, Analyst, Publisher, Engager. Platform specifics come from per-platform **skills** and per-platform **config**, not per-platform agents. This is also cheaper (fewer system prompts, one memory).
- **Mission cycle (what you described):** Monthly mission (1 sentence, audience, 3-5 pillars, proof metric) -> weekly plan (one flagship idea, format mix 80/20 value/promo) -> daily pieces derived from the flagship, repurposed per platform (rewritten, not pasted) [S].

### 2.2 Content factory (agents decide, create, post)
Recommended pipeline for a 30 s vertical video with captions and voiceover (research agent's design, prices [V] unless noted):

| Step | Service | Cost | Fallback |
|---|---|---|---|
| Idea, hook, script, scene list | Zafiro's main model | ~0 | backup model |
| Voiceover | Gemini TTS (free tier) | $0 | Google Cloud TTS free tier, Kokoro on runner, Edge TTS (unofficial) |
| Word timestamps, captions | Groq `whisper-large-v3-turbo` (free: 28,800 audio-s/day) | $0 | faster-whisper on runner |
| Images / stills | Cloudflare Workers AI FLUX-schnell (free 10k neurons/day) | $0 | Replicate ~$0.003/img, Gemini image ~$0.03-0.04 |
| AI video clips (optional) | Runway API gen4_turbo ~$0.05/s; Higgsfield API Kling 2.5 ~$0.042/s; Gemini Omni ~$0.07/s | ~$0.8-3 per 15-30 s | stills + ffmpeg motion |
| Assemble (concat, zoompan, burn captions, mix audio, 1080x1920) | **GitHub Actions** runner with ffmpeg (Remotion optional) | $0 | Rendi (free 1-min commands, card required), Termux ffmpeg at 720p |
| Publish | `share_file` + routine, or API aggregator | $0 | human |

Totals: stills option about $0 and 3-6 min; AI-clip option about $1-3 and 6-12 min [U, estimate].

### 2.3 Where video rendering runs: decision
- **GitHub Actions, not Codespaces** [V]. Codespaces has **no exec REST endpoint**; the only programmatic exec is `gh codespace ssh` over Microsoft Dev Tunnels (undocumented tokens, needs `gh` in Termux), and Codespaces ToS says it is for developing/testing the repo's software, not an automation back end [V]. Actions has a first-class REST trigger: `workflow_dispatch` with a fine-grained PAT (Actions: write), results via release asset or artifact [V].
- Free: public repos unlimited on 4 vCPU/16 GB runners; private ~2,000 min/month on 2 vCPU [V].
- **ToS caution** [V]: runners must not be used for activity unrelated to producing/testing/publishing the repo's software. Keep a repo whose real content is the video templates (Remotion/ffmpeg project) and run modest daily jobs. Public repos expose logs: no secrets or private prompts in logs.
- Call sequence: phone `PUT /contents/jobs/<id>.json` -> `POST /actions/workflows/render.yml/dispatches` -> poll run -> download release asset. All doable today with `http_request`. No new Android code needed for v0.

### 2.4 Posting routes per platform (best evidence now; re-verify)
| Platform | Preferred route | Why / catch |
|---|---|---|
| Instagram | Meta Graph API (own app, own accounts, no App Review for owned accounts) or an aggregator | Reels via container+publish; tokens expire (failure mode). Cap 100/24h (docs conflict 50): read `content_publishing_limit`. Buzzfy writes need a human confirm code, so **not unattended** [V] |
| Facebook Pages | Graph API `pages_manage_posts` | dev-mode app roles work without review for own page |
| Threads | Official API (250 posts, 1,000 replies / 24h) [V] | free |
| Bluesky, Mastodon, Telegram | native APIs | free, fully unattended; good "safe first channels" |
| TikTok | **Unaudited API = private-only** [V]. Use aggregator that holds an audited app, or `share_file` + screen routine | public posting needs an audit or an aggregator |
| YouTube | Data API upload (new projects locked private until audit) then flip public in Studio via screen; or aggregator | quota 10,000/day [V] |
| X | API pay-per-use (~$0.015/post, ~$0.20 with a URL) [S]; programmatic replies restricted since Feb 2026 [S]; "Automated" label required | budget cap needed |
| LinkedIn | Share API for personal posts [S]; **automation of comments/connections is prohibited** [V LinkedIn help] | agent drafts, you post or approve |
| Reddit | API needs approval since Nov 2025 [S]; Reddit now prompts suspected bots to verify human [S] | screen only, human-paced, low caps |
| Substack | no official API; unofficial `python-substack` (draft-first) [S] | cookie auth can break; final publish by screen/human |
| Pinterest | API v5 (Trial is sandbox-only until Standard approved) [S] | aggregator or screen |

**Aggregators worth testing (all remote MCP/HTTP):** Zernio (free for 2 connected accounts, then ~$3-6/account) [S], Postiz cloud ($29+; Bearer key MCP) [S], Blotato ($29) [S], Buffer (free plan incl. MCP) [S], Metricool (free MCP; `...ForReview` variants give a built-in approval gate) [V tool list]. Verify directly: free tiers, whether TikTok/YouTube posts go **public**, and that our MCP client accepts static header tokens (OAuth DCR is not accepted by every server) [S].

### 2.5 "Not obviously AI": the quality system
Platforms now penalise generic output (Instagram demotes unoriginal content; YouTube July 2026 "Generic or Repetitive Content"; LinkedIn demotes AI-slop posts/comments; Substack has an AI-detection tool) [S/V snippets]. Plan:
1. **Voice profile per persona** built from the owner's real writing (voice-profiler method); store and reload every session.
2. **Experience bank** (real anecdotes, prices, places, mistakes, opinions; Nairobi context). Every draft must use at least one item or a checkable specific.
3. **Anti-slop pass** = adapt **blader/humanizer** (MIT): 26-29 patterns tiered (Tier 1 dead giveaways: significance inflation, promo language, AI vocabulary, sycophancy, generic conclusions; Tier 4 weak signals such as em dashes) + "what still sounds like AI?" self-critique + personality section [V]. Plus bundled `stop-slop`.
4. **10-point draft checklist** (pass 8/10, automatic fail on any Tier 1 phrase, fabricated personal claim, or missing required AI label): specific detail, real opinion, varied sentence length, no AI vocabulary, differs from last 10 posts, no generic ending, matches voice profile, adds something not in the first search result, native to platform, "would the owner say it aloud".
5. **Format variation memory**: last 10 posts' openers/structures; block repeats.
6. **Replies**: short, specific, sometimes one line, sometimes none; never instant; never templated.
7. **AI disclosure compliance** per platform (Meta/TikTok/YouTube/Pinterest labels for realistic synthetic media; X Automated label).

### 2.6 New-account warm-up (hard rules, mostly heuristics [S/U])
- **You create accounts by hand** (verification is a NEEDS_HUMAN step); the agent never creates accounts or beats CAPTCHAs.
- One device, same IP, complete profile first, passive activity before outbound, ramp over 2-4 weeks (e.g. Instagram: days 1-3 browse + ~20 likes, 0 posts; week 2 3-4 posts, 30-50 likes, up to 20 follows, 5-10 comments; weeks 3-4 daily post, comments 10-15). Stop on any "action blocked/confirm you're human" prompt and wait 24-48 h.
- **Honest risk statement:** official policies prohibit automation that imitates humans (Instagram terms, LinkedIn help, X automation rules) [V snippets]. Posting via official API/aggregator is low risk; **engagement automation on screen is where bans concentrate.** Since these are dedicated new accounts you may accept that risk, but the plan defaults to: API posting, human-paced low-volume engagement, LinkedIn draft-only. You can raise caps per platform after seeing results.

### 2.7 Platform skill pack (to approve; nothing built)
General: social-account-safety, monthly-mission, weekly-plan-and-calendar, trend-research, voice-and-anti-slop, ai-disclosure-compliance, human-engagement, repurpose-across-platforms, analytics-and-learning, approval-and-escalation.
Per platform: instagram-ops, facebook-ops, tiktok-ops, youtube-ops, x-ops, substack-ops, reddit-ops, threads-ops, linkedin-ops, pinterest-ops, bluesky-ops (5-8 rules each, platform specific, from the research report). Many exist partially in the 175 bundled `smc-*`, `social-*`, platform skills; we **merge and trim**, not pile on.

---------------------------------------------------------------------------------------------------------
## 3. Workspace 2: Job Hunter (aggressive, but gated)

### 3.1 What "aggressive" should mean (and why not spray)
Evidence [S]: Greenhouse sees ~244 applications per job; Ashby reports ~291 applications per hire (~0.34% per application); ~19% of postings are ghost jobs. Blind volume is mostly noise and burns your reputation and your sending domain. So: **high volume with a fit gate and tiers**:
- **Tier A (up to ~10/day):** strong fit + eligibility confirmed: full tailoring and an email to a named human when one exists.
- **Tier B (20-30/day):** templated tailoring, direct apply.
- **Tier C:** skip.
50/day is reachable only with feeds + automation + gating; the agent parallelises scanning, scoring, drafting and sending.

### 3.2 Sources (verified live 2026-11-25 by curl unless noted) [V]
Feeds/APIs (no login, no screen): We Work Remotely RSS (full-stack 215 items; `<region>` tag), Remote OK `/api`, Remotive API (24 h delay, ~4 calls/day), Himalayas API (+ closed-job 410 check, `locationRestrictions`/`timezoneRestrictions`), Jobicy API, Working Nomads API, NoDesk RSS, Arbeitnow API; **HN "Who is hiring"** monthly via Algolia (Nov 2026 thread: 238 top-level comments, 28 explicitly worldwide/anywhere/EMEA; many apply by email); `hnrss.org/jobs`; **YC hiring dataset** (1,473 hiring companies, 729 remote) to seed ATS slugs.
ATS public JSON (best current postings): Greenhouse boards API, Lever postings API, Ashby posting API, SmartRecruiters, Personio, Breezy (Workable/Recruitee uncertain). A 70-company scan found real Africa-compatible hits: Canonical (92 "Worldwide", 89 EMEA), Supabase (36 of 82 Global/EMEA), ClickHouse (11 EMEA); no Android-titled roles in that sample.
Africa-focused: RemoteAfrica.io (direct postings), BrighterMonday/MyJobMag Kenya (no feeds, scams common), Telegram public channels via `t.me/s/<channel>`.
Freelance/contract: Upwork (bots banned; draft only), Contra, Toptal/Gun.io/Lemon (very selective), Turing/Andela, Crossover.
Bot-walled from the research IP (may work from your phone, unverified): Reddit JSON, X search, Fuzu, Indeed, Wellfound.
**That is 30+ sources**, with feeds that cost almost no tokens.

### 3.3 Open-world discovery (beyond the list)
HN threads, Telegram channels, Reddit r/forhire r/remotejs r/androiddev (screen or phone IP), X advanced search ("we're hiring" android/kotlin/full stack remote worldwide/Africa/EMEA) through the screen with your logged-in account, newsletters into a dedicated inbox, Google dorks against ATS domains, company career pages from the YC/remoteintech lists. LinkedIn: human browses; the agent only drafts (LinkedIn bans bots/extensions) [V].

### 3.4 Pipeline
scan feeds + ATS nightly -> dedupe -> **eligibility check** (Africa/Nairobi UTC+3, work authorization regexes, EOR) -> **ghost/scam check** (age >30 d, absent from employer ATS, 410/404 closed, any fee/M-Pesa/WhatsApp-only = hard fail) -> fit score + tier -> résumé variant (Android/Kotlin; full-stack TS; AI-automation) -> cover letter 120-180 words with one concrete linked project -> send by **Gmail drafts/REST** (dedicated mailbox, SPF/DKIM/DMARC, 25-40 cold emails/day ramped, pause on 4.7.x/5.7.x or bounce >2% [U judgment]) or **ATS form via screen** (take_over on CAPTCHA) -> tracker row -> follow-up day 5-7 and 14, max two -> inbox triage (interviews go to you).
It **asks you questions** when: salary/visa/attestations, an answer not in your profile file, ambiguous location eligibility, first contact with a Tier A employer, any money. You reply in the Inbox and it proceeds.
Skills (15): job-source-scanner, eligibility-checker, ghost-and-scam-check, open-world-discovery, ats-form-filler, email-applier, resume-variant-picker, cover-letter-writer, application-tracker, follow-up-manager, inbox-triage, freelance-proposal-writer, interview-prep, salary-negotiation, compliance-guard.
Never fabricate experience; use only your approved profile file.

---------------------------------------------------------------------------------------------------------
## 4. Workspace 3: Builder (code on GitHub, not on the phone)

- **Zafiro itself writes the code** with its own model using GitHub REST/MCP (commit to a branch via Contents API or the GitHub MCP with a fine-grained PAT scoped to one repo), and **GitHub Actions builds, tests and deploys** (Pages/Vercel/Netlify via git push). This is our own proven loop (CI + `ci-logs`). No Codespace needed.
- **Optional:** `anthropics/claude-code-action` inside Actions for heavy coding tasks (needs `ANTHROPIC_API_KEY` as a secret; costs money) [V].
- **Codespaces:** keep for you, interactively, when you want a full IDE. As an automation back end it has no exec API and its ToS limits it to developing/testing the repo's software [V]. If you later want it, the realistic path is `gh` in Termux (`pkg install gh` exists) plus `gh cs ssh`, whose token/permission model for fine-grained PATs is unverified [U].
- **Termux:** keep as an escape hatch (already supported through `ssh` + the `termux` skill). Use it for light ffmpeg work (trim, concat, mux, short ASS captions at 720p) not for 1080p encodes (A22 chip) [U]. `RUN_COMMAND` needs `allow-external-apps=true`, a `<queries>` entry, and Termux must be exempt from battery optimisation and Android 12 phantom-process killing (Zafiro has Shizuku and could apply the setting; unverified on this phone).

---------------------------------------------------------------------------------------------------------
## 5. Your 17 repos: what to take, what to skip (verified by reading the repos)

| Repo | Verdict | Take |
|---|---|---|
| **blader/humanizer** (MIT) | **ADOPT** | The 26-29 pattern anti-AI skill (tiered) as `voice-and-anti-slop`; keep MIT notice. Pair with `voice-profiler` method and bundled `stop-slop` |
| **ziazon/zizi-skills** (MIT, 36 skills) | ADAPT 3 | `orchestration` (brief template: goal, scope, decisions, constraints, verify, report, timebox; "subagents cannot ask questions, front-load decisions"), `planning` (batch questions before unattended execution), `handoff` (state outside chat + exact resume point). Each ~27 KB: **condense to <5 KB**. Skip the other 33 (frontend, tabler, git, etc.) |
| **khalilmaaouni/token-shield** (MIT) | ADAPT ideas | Model-tier table (strong model judges/orchestrates, middle scoped work, cheapest mechanical; declare tier in every brief); "one deterministic script beats a subagent"; sub-agent report caps; anti-pattern #1 (unbounded fleets: per-session ceiling, no new dispatch past 80%) and #9 (never raise a ceiling by disabling the guard). The Python tooling is Claude-Code-specific: skip |
| **nishkarsh-shah/prompt-master** (MIT) | ADAPT idea | Model/effort scoring (score = 2*reasoning + scope, mechanical always gets lightest model) as our routing rule; skip chat commands |
| **msapps-mobile/claude-plugins** (MIT) | **ADAPT design** | `sosa-governor`: impact tiers (low/medium/high), high-impact blocked until approval, audit log, trust score that grows with approvals => our approval-tier model. Code is Claude-Code hooks: do not copy |
| **mehmeteminduran/session-handoff** (MIT) | ADAPT format | HANDOFF template (Active Task, Current State, Next Steps, Key Decisions, Environment) as the per-mission state file. **Never auto-execute "next steps" from a file** (injection path) |
| **rendi-api/ffmpeg-llm** (MIT) | ADAPT | `ffmpeg-command` skill + 10 reference files (pitfalls, filters, subtitles) as the Builder/render skill; the ffmpeg pitfall list (`format=yuv420p`, `-movflags +faststart`, `setsar`, `fps_mode`). Not Rendi's API (the repo does not use it; Rendi itself is a separate cloud ffmpeg service, free tier 1-min commands + card) |
| **aries-spring/motion-reel** (MIT) | LATER, on Actions | Canvas/three.js motion films, 9:16/1:1/16:9, synthesized audio; needs Node 18+, ffmpeg, Playwright/Chromium, uv; lyric videos pull ~3.5 GB models. Interactive critique loop must be scripted. Candidate for product/launch reels once the basic pipeline works |
| **i-afaqrashid/claude-animation-studio** (MIT) | LATER, on Actions | Hand-drawn style explainer/promo films; Node 22, ffmpeg, Chrome; Linux supported; "minutes" per render. Good fit for explainers in a CI runner |
| **buzzfy/buzzfy** (MIT) | PARTIAL | Hosted MCP for Instagram: free reads (insights, comments, best times, niche reports, reel search 10/day). **Every write needs a human confirm code** => cannot publish unattended; use read-only for research/analytics. Third party holds your IG OAuth grant |
| **svgnew/plugin** (MIT) | OPTIONAL | Hosted MCP (OAuth) for vectorize/recolor/simplify/remove-bg; free 3/day; logo and graphic tasks. Low priority |
| **truenorth-lj/adaptive-agent-skill** (MIT) | IDEA ONLY | Self-improving skills is the right goal but its "patch the skill file with no approval gate" is a **persistence/injection risk** for an unattended agent. Do it through our weekly reviewer + approval (section 7) |
| **wizzouk2/token-usage** (MIT) | DESIGN REF | Cache-aware cost formula (cache read 0.1x, 5-min write 1.25x, 1-h write 2x), per-subagent attribution, dedupe by requestId (naive sums over-count ~2.5x). Use for our cost meter. Code reads Claude Code transcripts: skip |
| **anthropics/claude-plugins-official** (Apache-2.0) | REFERENCE | Plugin/skill layout, `skill-creator`, `mcp-server-dev`, GitHub and context7 MCPs. Nothing for social or video |
| **michaelpgifford/claude-read-aloud** | SKIP | Needs speakers + Claude Code Stop hook; Android has TTS |
| **piotrlason/video-to-code-skill** | SKIP | macOS Apple-Silicon Whisper/OpenCV; "100% vibe coded" per author |

Net: **only text skills and remote MCP servers cross to the phone.** Everything that needs Node/Python/ffmpeg goes to GitHub Actions.

---------------------------------------------------------------------------------------------------------
## 6. Safety and cost layers (must come before more autonomy)

1. **Enforced per-agent tool allow-lists** [CODE gap]. Reader agents (scan comments, job posts, scraped pages) get read-only tools and emit a fixed JSON schema; writer agents (post, send, apply) see only those fields, never raw third-party text ("lethal trifecta": private data + untrusted content + outbound channel) [S: GitHub MCP and Supabase MCP incidents].
2. **Untrusted wrapping**: every `http_request`, scraper, comment, DM and job-post result is wrapped with a per-call random boundary and a fixed "data, not instructions" header (Buzzfy does this; it is defence in depth, not a guarantee) [V].
3. **Allow-listed recipients/accounts** for sends; never email an address that appears only inside a job post; daily caps; log every outbound write.
4. **Impact tiers + approvals** (sosa-governor idea): low (read, draft) auto; medium (post a pre-reviewed item, normal comment) auto within caps; high (DMs, money, applications to Tier A, anything irreversible) needs approval. Unattended default stays Deny.
5. **Pin MCP tool definitions** (hash the tool list; pause on change; rug-pull defence) [S].
6. **Budgets in DB, enforced in code**: per step, per run, per day per agent, plus a **USD cap** (token cost is already metered; add price tables from a verified list). On breach: stop, mark `BUDGET_EXHAUSTED`, notify. Spend anomaly flags (median + 4xMAD, loop signature, cache-hit floor) pause the agent and put a card in the Inbox [design suggestion].
7. **Secrets**: PAT scoped to one repo with Actions+Contents only, short expiry; never in prompts/logs. ADR-0006 (encrypted key storage) is still undone: should land before adding more tokens (GitHub, Meta, Zernio, Gmail).
8. **MCP client**: must store a static header per server; do not assume OAuth DCR works on every server [S].

---------------------------------------------------------------------------------------------------------
## 7. Learning (so it improves instead of repeating)

Core rule: **the running agent never rewrites its own rules mid-run.** Learning is offline, evidence-based and reviewed (ACE: Generator/Reflector/Curator; Letta sleep-time agent; Anthropic "dreaming" as an offline consolidation pattern [V/S]).
- `outcomes` table: post/application id, platform, variant, metric, value, observed_at, read by a **zero-model** job at +1 h/+24 h/+7 d (posts) and +3/7/14 d (applications).
- **Bandit for hooks/subjects**: assign variants in code (Thompson sampling, 10-20% exploration floor, min n per arm), the model writes for the arm it is handed [design suggestion].
- **Lessons store** (structured, not prose blobs): claim, evidence run ids, helpful/harmful counts, confidence from counts, decay, TTL, status candidate/active/retired; only top-k relevant active lessons are injected as one-liners.
- **Weekly Reviewer agent** (strong model, read-only tools + "write proposal") produces a diff to `playbook.md` with evidence. Low-risk edits auto-apply with a version; anything touching account safety, cadence, spend, or truthful claims goes to the Inbox.
- **Regression guard**: a golden set of ~10-20 past tasks; reject a new playbook if the rubric score drops; one-tap rollback.
- Failure modes handled: reward hacking (engagement bait, false application claims) by a fixed policy checklist the learner cannot edit; Goodhart by a primary metric + guard metric; drift by delta edits and size caps; small-sample noise by thresholds.
- Success is **outcome evidence** (follows, replies, interview, reply received), never "run SUCCEEDED" (our backup showed no-op "successes").

---------------------------------------------------------------------------------------------------------
## 8. UI redesign (phone-sized mission control)

Five bottom tabs [pattern from Claude Code agent view / Devin / Cursor mobile]:
1. **Now**: spend today vs cap; "N need you" chip; state groups (Needs you, Working, Scheduled next, Done) with a one-line summary each; tap to peek/reply; FAB to start a task by voice.
2. **Inbox**: approval cards (action, target, draft/screenshot preview, risk badge, time-to-auto-deny), NEEDS_HUMAN blockers (CAPTCHA, verification), questions from the Job Hunter ("salary expectation?"), proposed playbook changes. Buttons: Approve / Edit / Deny / "Always allow this narrowly". Same buttons in notifications.
3. **Runs**: per-run timeline (thought summary, tool, screenshot thumbnail, result), chips for model/tokens/cost/steps, markers for retries and fallbacks, Stop / Take over / Replay / **Save as routine**.
4. **Memory**: Memory index (one-liners with scope), Lessons (confidence, evidence, retire/confirm), Playbooks (versions, diff, rollback), Experience bank and Voice profiles.
5. **Plan**: workspaces with platform toggles and caps; schedules (missed runs in red with why); cost by agent; reliability card (battery optimisation, autostart, exact alarm, Shizuku status with fix buttons).
Also: a **Studio** area inside Files for generated videos/images with preview, caption, "post to..." and the render job status.
Replace today's separate Teams/Agents/Tasks pages by a single Workspace detail screen (agents and tasks as sections).

---------------------------------------------------------------------------------------------------------
## 9. Skill library pruning (cost + quality)

278 skills bundled, 27 enabled by default [CODE]. Proposal: **ship a curated core (~80-100) and make the rest optional (installable on demand via `install-skill-from-github` / a library page).** Candidates to remove from defaults or from the bundle: GSAP x8, algorithmic-art, canvas-design, theme-factory, brand-guidelines, course-creation, i-have-adhd, internal-comms, web-artifacts-builder, data-* (11), finance-* (8), product-management-* (8), customer-support-* (5), most of marketing-*/mkt-* that are SEO/ads/CRO/SaaS-pricing (keep social/content/email/outreach/research subset), duplicated families (`marketing-*` vs `mkt-*`). Keep: platform growth/publishing skills, writing/voice, research, analytics, outreach, phone-use/fast-screen/screen-routines, skill-creator, termux, zafiro-config. You approve the exact list before anything is deleted.

---------------------------------------------------------------------------------------------------------
## 10. Phased roadmap (ordered by speed to revenue and risk)

| Phase | Deliver | Size |
|---|---|---|
| **P0 Foundation** | Enforced tool allow-lists; untrusted-data wrapping; MCP static headers; USD cost cap + meter; `outcomes` table; skill pruning list approved; single Workspace model with platform toggles; calibrate routines on the clean phone (record verified routines per app) | M |
| **P1 Job Hunter** (fastest to revenue) | Feed + ATS scanner (zero-model), eligibility + ghost checks, tracker, Gmail drafts/send with limiter, ATS form routines, Inbox questions, daily digest | M-L |
| **P2 Social Manager core** | One workspace, toggles, mission/weekly planner, voice profile + experience bank + humanizer gate, API/aggregator posting for 2-3 platforms, warm-up scheduler, engagement caps | L |
| **P3 Media pipeline** | `render-video.yml` in a studio repo (ffmpeg first), job JSON via Contents API, TTS + Whisper + FLUX integration, `share_file` publish, Studio screen | M |
| **P4 UI redesign** | Five tabs, Inbox, Runs timeline, Memory/Lessons, cost meter | L |
| **P5 Learning loop** | Metrics ingestion, bandit, lessons store, weekly reviewer + regression guard | M-L |
| **P6 Builder** | GitHub MCP/REST coding flow, optional claude-code-action, Remotion/animation-studio on Actions | M |

Everything unverified stays labelled until you confirm on the phone.

---------------------------------------------------------------------------------------------------------
## 11. Decisions I need from you

1. **Engagement risk**: accept screen-driven likes/comments at low human pace on the new accounts (ban risk is real), or API/drafts only for X, LinkedIn, Reddit? (Recommendation: low caps on screen for IG/TikTok/Threads/Substack/Reddit/Facebook; LinkedIn drafts only.)
2. **Aggregator**: OK to try a free tier (Zernio/Buffer/Metricool) and, if TikTok/YouTube public posting works, pay ~$29 for Postiz/Blotato? Or screen-only for those?
3. **Gmail**: use a dedicated mailbox on your own domain for applications (needed for deliverability)? Do you have a domain?
4. **Job profile file**: will you write one authoritative profile (experience, projects with links, rates, availability, salary expectation, work authorization) the agent may rely on? Without it the Job Hunter must ask you often.
5. **GitHub**: create a `zafiro-studio` repo (public, video templates; render jobs) and a fine-grained PAT (one repo, Actions+Contents)?
6. **Model budget**: monthly cap in USD and which models for planning vs tap steps.
7. **Pruning**: approve the skill-removal list (I will send exact ids).
8. **Order**: confirm P0 -> P1 (jobs) -> P2/P3 (social + media) -> P4 (UI) or reorder.

## 12. What I could not verify (so you do not trust it blindly)
Platform policy pages were mostly read through search snippets (several hosts blocked in the sandbox); warm-up numbers are community heuristics, not platform rules; aggregator free tiers and whether they post publicly on TikTok/YouTube; Reddit's current API approval path; Substack ToS for cookie automation; X exact current pricing; whether Reddit/X/Fuzu work from the phone's own IP; `gh cs ssh` with fine-grained PATs; render times on Actions; Remotion on Actions (documented, untested here); every phone-side behaviour (`share_file` from background, routine labels, Termux phantom-process fix); Buzzfy plan prices; Rendi in practice.
