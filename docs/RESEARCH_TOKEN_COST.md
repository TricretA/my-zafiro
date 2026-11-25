# Research: why Zafiro burns tokens, and how phone/browser agents do it cheaper

Date: 2026-11-25. Status: research only, nothing built. Every claim is tagged:
**[CODE]** read in our source today · **[PRIMARY]** read on the project's own README/docs ·
**[VENDOR]** the project's own benchmark, not independently reproduced · **[SECONDARY]** blog/third-party ·
**[ESTIMATE]** my arithmetic from the above, not measured · **[UNVERIFIED]** could not confirm.

---

## 1. The problem in one paragraph

A task "post a video with captions to TikTok, Instagram, Substack and Reddit" took ~46 minutes and used up ~$100 of
Anthropic credit before finishing. Two separate causes: **(a)** the loop re-sends an ever-growing history on every
step, and **(b)** every step is a slow LLM round-trip. Screenshots are *not* the main cause in Zafiro (see 2.3);
the big cost is stale screen dumps piling up in context, plus a 1M-token context window that almost never compacts.

---

## 2. How Zafiro controls the screen today (and where tokens go)

### 2.1 Mechanism [CODE]
- Android **AccessibilityService** reads the UI node tree, prunes it (drops off-screen, empty shells, collapses
  containers), caps it at **200 nodes / depth 20**, and formats it as YAML-ish text: one line per node with index,
  type, **pixel bounds**, position word, text, content-desc and flags (`PruningRules.kt`, `TreeFormatter.kt`).
- Actions (tap, swipe, set_text, key, scroll, long_click) go through the accessibility API; a verified-routine layer
  (`RoutineEngine`) re-checks that each tap changed the screen.
- `screen_operation read` returns the **entire tree every call**. `act` operations return only a status, so the
  agent must call `read` again after almost every action. => about **2 tool calls per UI step**.
- `screenshot` saves an image to the sandbox and returns only a path + size; the model sees pixels only if it then
  calls a view-image/vision tool. So screenshots are opt-in, not per-step.

### 2.2 Cost drivers found in the code [CODE]
| # | Driver | Why it hurts |
|---|---|---|
| 1 | Context window default = **1,000,000** tokens, compaction at **70%** => nothing is summarised until ~700k tokens | A run can legally carry hundreds of thousands of tokens of history; every step re-sends all of it |
| 2 | Old `read` results are never dropped | Each read is a full ~200-node dump (my estimate: 4–8k tokens). After 60 reads, ~300k tokens of *stale* screens ride along forever |
| 3 | `read` has no delta/diff and no "interactive-only" mode | Same big tree sent even if 1 element changed; bounds on every node add tokens the model rarely needs |
| 4 | `act` returns nothing useful | Forces an extra `read` per step |
| 5 | One action per tool call | A 6-tap form fill = 6 LLM round-trips (also the main latency cost) |
| 6 | System prompt says skills are "mandatory… err on the side of loading" | Each loaded SKILL.md (largest bundled ≈ 36 KB ≈ 9k tokens) lands in history and is re-sent every later step. 275 skills bundled (27 enabled by default) |
| 7 | Anthropic path already sets `cache_control` (5-min ephemeral) | Good, but caching only discounts re-sent tokens (cache reads ≈ 10% of input price [SECONDARY]); it does not stop growth, and it is lost if a gap > 5 min occurs or history is rewritten |

### 2.3 Why $100 is plausible [ESTIMATE, not measured]
Input tokens billed ≈ sum over steps of the history size at that step. With ~6k tokens added per read and 100 reads:
`6k × 100 × 101 / 2 ≈ 30M` input tokens in ONE run. At ~$3/M uncached that is ~$90; at ~10% cache-read price ~$9–15
(plus cache-write surcharge). Multiply by four platforms and retries and credits vanish. The quadratic growth is the
point: **doubling the steps quadruples the cost.** This is arithmetic, not telemetry; step 1 of the fix is to measure.

---

## 3. What other agents do (how they see + act)

### 3.1 Phone agents (open source)
| Project | See | Act | Notes |
|---|---|---|---|
| **Open-AutoGLM** (zai-org, Apache-2.0) [PRIMARY] | screenshots only; own 9B vision model (self-host 24GB+ VRAM, or hosted API) | ADB + ADB Keyboard | action space: Launch/Tap/Type/Swipe/Back/Home/LongPress/DoubleTap/Wait/**Take_over**; 100-step cap; no token tricks documented. Research/learning-only disclaimer |
| **Mobile-Agent v3/v3.5** (X-PLUG/Alibaba, MIT) [PRIMARY] | screenshots; GUI-Owl models 2B–32B | ADB | multi-agent (manager/worker/reflector/notetaker); AndroidWorld 68–73% [VENDOR]; small 2B/4B models for cheap/edge use |
| **UI-TARS** (ByteDance, Apache-2.0, **repo archived 2026-11-09**) [PRIMARY] | screenshots; 7B open | model+parser only | AndroidWorld 64.2 [VENDOR]. Not a ready phone agent |
| **AppAgent** (Tencent, MIT) [PRIMARY] | screenshot + numbered element tags | ADB | learns app docs by exploring/demonstration, then reuses them. ~$0.03/request on GPT-4V (2023) |
| **Mobilerun / DroidRun** (MIT, 10k★) [PRIMARY] | **a11y tree via Portal app** (JSON, drops <1%-visible nodes, overlay indices), optional screenshots or vision-only | Portal accessibility service or ADB | AndroidWorld "100%" is [VENDOR]; **no token numbers published**. Framework runs on a PC/cloud, not on the phone. Closest to what we do |
| **nebigoktug/android-agent** (MIT, 8★, v0.4) [PRIMARY] | numbered **text list** from a11y tree, **delta reads** after the first look, screenshots/OCR only as fallback | ADB or a local companion app | **record/replay macros with zero model calls**. Claims ~10x cheaper than screenshots, but its benchmark is its own: chars/3.5 token estimate vs an *assumed* 1,500 tokens/screenshot, 19 reads, one phone. Treat 10x as a hypothesis. Very early |
| **Midscene** (MIT) [PRIMARY] | screenshots (vision) for web/Android/iOS | ADB etc. | YAML scripts, replay reports; AndroidWorld 93.1% / $0.59 for 58 of 60 tasks on its own benchmark [VENDOR] |

**Takeaway:** the vision-only family (AutoGLM, Mobile-Agent, UI-TARS, Midscene) pays image tokens every step but
works on any app (games, Flutter, WebViews, apps that hide their tree). The tree family (Mobilerun, android-agent,
Zafiro) is cheaper per step but breaks when an app exposes no tree. None of the four vision repos documents any
history trimming. Honest reading: **nobody has a published, independent, apples-to-apples token benchmark for phones.**

### 3.2 Browser / computer agents
| Project | Approach | Evidence |
|---|---|---|
| **Playwright MCP** | full a11y snapshot every step | anecdotes of 27k–114k tokens per session, ~100k+ per test [SECONDARY]; snapshots accumulate in context — same problem as ours |
| **Vercel agent-browser** (Apache-2.0, 54k★) [PRIMARY] | compact snapshot with **`@e1` refs**, `snapshot -i` = interactive elements only, daemon over CDP | "up to 93% less context than Playwright MCP" is Vercel's claim; independent Pulumi test found **82.5%** [SECONDARY] |
| **browser-use / browser-harness** (MIT) [PRIMARY] | agent drives real Chrome over CDP and **writes its own Python helpers** that persist | its own benchmark (Codex, 100 tasks, 1 run each) [VENDOR]: harness 2.1M median input tokens/$0.042 vs Playwright MCP 4.4M/$0.066 vs DevTools MCP 6.7M/$0.097 vs agent-browser 3.3M/$0.056; **scores statistically tied**, harness was *slower* |
| **Stagehand** (MIT) [PRIMARY] | trimmed a11y tree; `observe()` returns selectors once, then deterministic Playwright; `cache: true` skips the LLM for repeated actions | no token numbers published; cache is Browserbase-server-side |
| **Skyvern** (AGPL) [SECONDARY] | vision-first | WebVoyager 85.9% (cloud, live bot protection) vs browser-use 89.1% (local) — not comparable |
| **Anthropic computer use / Google Gemini 2.5 Computer Use / Mariner** [SECONDARY] | screenshot loop | screenshots are billed as image tokens each step; Mariner is Chrome-only, foreground tab only |

**Takeaway:** the cheap browser agents win by (1) *interactive-only compact snapshots with short refs* and
(2) *not re-sending old snapshots* / writing reusable code. Scores are roughly tied, so cost and speed are the
differentiators, exactly your three criteria.

### 3.3 "Give the agent eyes" and Agent-Reach (read-only) [PRIMARY]
Agent-Reach (MIT) is a CLI/skill wrapping existing tools (yt-dlp, Jina Reader, Exa, bili-cli, OpenCLI…).
Channels: GitHub, YouTube, web, V2EX, Bilibili (default); Reddit, LinkedIn, Xiaohongshu… (login); Twitter/X, Facebook,
**Instagram (experimental, "can break at any time")**. The README states plainly: **"Read-only: it never posts, comments,
likes or sends messages."** TikTok is not in the list. Logins are copied from your Chrome profile (its own advice: use a
secondary account). Verdict: excellent for *reading* comments/trends cheaply (text, no screenshots) → use for the
engagement-reading half; useless for posting. Note it targets desktop Chrome; on Android we would reuse only its
underlying tools (Jina Reader URL fetch works from plain HTTP — we already have `http_request`).

### 3.4 The "Python script" idea (your Instagram tip) — verdict: **real, and the best lever**
The pattern has several independent implementations, all the same shape *run once with the LLM → save → replay with no LLM → on failure fall back to the LLM and re-save*:
- Microsoft CUAWright (cache generated Playwright scripts), HyperAgent (record actions, replay without LLM, export TypeScript), Stagehand cache, rtrvr.ai "AI Subroutines", Reelier (compiles a run into a SKILL.md) [SECONDARY].
- android-agent `record`/`play` macros "zero tokens" [PRIMARY]. Research: **AutoDroid-V2** (agent generates code scripts, avoids per-step LLM calls) and **DroidAgent** (emits replayable UIAutomator2 scripts) [SECONDARY]. AutoDroid-V2's own stated weakness: scripts break when the GUI changes.
- We already have the skeleton: `RoutineEngine` + JSON routines in `files/routines/` with per-tap verification.

Caveats (important, so we do not oversell): "zero tokens" means zero *model* calls. Replay still needs time, still fails when apps update or show a popup, and TikTok/Instagram A/B-test their UI constantly. Needs a fallback + self-repair loop. Also: a script can only automate steps that don't need judgment; the caption/hashtag *writing* is cheap one-shot text and should be separated from the tapping.

### 3.5 Skip the screen entirely where an API exists
| Platform | Official/practical route | Catch |
|---|---|---|
| TikTok | Content Posting API (direct post) [PRIMARY docs] | **Unaudited apps can only post privately**; max 5 users/24h; public posting needs a separate Content Posting API audit (days–2 weeks). Not usable for public posts until audited |
| Instagram | Graph API content publishing (Reels supported) [PRIMARY docs] | Business/Creator account + Meta app + review; published-post limit per 24h (docs disagree: 25/50/100 — query `content_publishing_limit`) |
| Reddit | OAuth `submit` [SECONDARY] | API access/approval tightened; per-subreddit rules (karma/age/flair) reject posts regardless; posting automation is a ban risk |
| Substack | no official API; unofficial `python-substack` (same endpoints as web editor) [SECONDARY] | can break without notice; cookie-based |
| Any | Postiz (AGPL, self-host or cloud, TikTok/Instagram/Reddit, public API + agent CLI) [SECONDARY] | needs a server; **rate limit 30 req/h**; still depends on each platform's audit/approval; Metricool connector is already available in this account |

An API post costs one tool call (~hundreds of tokens) versus 100+ screen steps. Where it works it is ~1000x cheaper and
10x faster. Where it can't (TikTok public, Substack) fall back to scripted UI routines.

---

## 4. What to do (proposal — nothing is built; needs your approval)

Ordered by money saved per effort. Tiers are tried top to bottom, cheapest first.

**Tier 0 — Measure (1 day).** Log input/output/cache tokens and wall-time **per step and per run** (UsageMeter already
counts usage; add a per-step trace in the run view). Run the 4-platform post once and look. Without this every number
above is a guess.

**Tier 1 — Stop the bleeding (small, mostly config/prompt):**
1. Make `read` return a *compact* tree: interactive/text nodes only, short refs (`@12`), no pixel bounds, truncate text; add `read` modes `interactive` / `changed` (delta since last read). [ESTIMATE 3–6x smaller]
2. **Clear stale tool results**: keep only the latest screen dump in context (Anthropic's documented "tool-result clearing" pattern). Biggest single win: converts quadratic growth to ~linear. Must be done carefully to keep the prompt cache prefix stable.
3. Effective context cap for *scheduled runs* ~100–150k with compaction at ~60% (our setting `contextWindowTokens` is a per-config value; scheduled agents should not inherit 1M).
4. Return the new screen (or a changed-nodes summary) from `act` so no extra `read` is needed.
5. Allow **batched actions** in one call (list of taps/typing) to cut round-trips (this is the main fix for the 46 minutes).
6. Remove "err on the side of loading skills" for scheduled runs; load only the skill the task names.
7. Hard **dollar budget** per run (we have step/time/token budgets; add cost) and a per-day cap, stop with NEEDS_HUMAN, not silent burn.
8. Use a cheap model for tap-level steps and a strong one only to plan/recover (price spread is large; Anthropic list prices from secondary sources conflict, so check the live pricing page before choosing).

**Tier 2 — Scripts that run without the model (the big structural win):** record a successful run into a routine
(JSON of verified steps with element matchers), replay it for free, and when verification fails hand control back to the
LLM with the failing step, then re-record. We already have `RoutineEngine`; the new parts are *recording from a live
run*, *parameter slots* (caption, file), and *fallback/repair*. Expect: first posting run costs LLM money, later runs
cost ≈ nothing until the app UI changes.

**Tier 3 — API-first posting** per platform where approval is realistic (Instagram Graph, Reddit OAuth, Substack
unofficial, TikTok after audit; or Postiz/Metricool as the gateway). UI routines remain the fallback.

**Tier 4 — Read cheaply:** Jina Reader / yt-dlp style text fetch (Agent-Reach's tools) for comments and trends instead
of opening the apps. Optional later: vision fallback only for screens with no tree.

**Not recommended:** adopting a vision-only stack (AutoGLM/Mobile-Agent/UI-TARS) wholesale — needs a 9B+ vision model
(GPU or paid API), pays image tokens every step, and the repos document no history trimming. Also not recommended:
bypassing platform audits or running accounts in ways that violate platform rules (ban risk to your accounts).

---

## 5. Decisions needed from you
1. Approve Tier 0 + Tier 1 first? (cheap, fast, no new dependencies.)
2. Which platform to make the first scripted routine: suggest **Substack** (no API, simplest UI) or **Reddit**.
3. Are you willing to apply for the TikTok Content Posting API audit and Instagram Graph app review (days to weeks, not under our control)?
4. Hard daily cost cap in USD to enforce.
5. Which model should tap-level steps use (cost vs accuracy)?

## 6. What I could not verify (so you don't trust it blindly)
- Actual tokens/cost of your 46-minute run (no logs available to me).
- Any independent benchmark of phone agents' token use; all published scores are self-reported.
- Anthropic's exact current per-model prices and cache-read multipliers (sources conflict; check the pricing page).
- Details of Mobilerun's tree format and Agent-Reach's per-channel tool mapping/output format.
- The "Artemis" Google agent you mentioned — I found no such Google product; the closest are Project Mariner (Chrome) and Gemini 2.5 Computer Use. If you have a link, send it.
- The browser repo that "gives agents eyes": best match is browser-use `browser-harness` / `agent-browser`; confirm if you meant another.
- Whether platform ToS tolerate this kind of automation (they generally restrict it; account bans are a real risk).

## Sources
Primary READMEs: github.com/zai-org/Open-AutoGLM, X-PLUG/MobileAgent, bytedance/UI-TARS, TencentQQGYLab/AppAgent,
droidrun/mobilerun (+ mobilerun-portal), nebigoktug/android-agent (+BENCHMARK.md), vercel-labs/agent-browser,
browser-use/browser-harness, browserbase/stagehand, web-infra-dev/midscene, Panniantong/Agent-Reach.
Docs: developers.tiktok.com content-sharing-guidelines, developers.facebook.com instagram content-publishing,
platform.claude.com computer-use + pricing. Secondary: dev.to/Pulumi/testdino Playwright-MCP token measurements,
arXiv 2601.06007 (prompt caching in long agent tasks), arXiv 2412.18116 (AutoDroid-V2), github.com/coinse/droidagent,
Microsoft CUAWright PR #6, hyperbrowser action-cache docs, postiz docs, python-substack.
