# Zafiro V3 plan: an autonomous company on your phone (supersedes PLAN_V2 where they differ)

Date 2026-11-25. **Research and proposal only. Nothing in this plan is built or verified on a phone.**
Tags: **[V]** read on a primary source / tested by a research agent, **[S]** secondary, **[U]** unverified or estimate, **[CODE]** read in our repo.

---------------------------------------------------------------------------------------------------------
## 0. What V2 got wrong, and the correction

1. **Three workspaces was too narrow.** Social, Jobs and Builder were examples, not the design. V3 is a *company*: a Chief of Staff, a library of departments (templates) you switch on, shared services (budget, scheduler, memory, approvals, channels), and one capability ladder. New departments are data (a template), not new code.
2. **I assumed paid AI media APIs.** Your rule: never pay for AI images/video. Images, voice and any AI video come through **apps and websites driven on the screen** (ChatGPT/Gemini, ElevenLabs web, Google Flow). Real videos come from **code** (Remotion, HyperFrames, canvas engines, ffmpeg) rendered on GitHub Actions.
3. **I recommended job sources that ignore Kenyans.** Replaced by a Kenya-first channel list (section 6).
4. **I ignored your own video studio.** `TricretA/Claude-Video-Editor` is a mature production system (section 4). It becomes the Video Studio department's brain.
5. **I treated "AI penalties" as a reason to worry.** Your answer is right: that is the job of the humanizer stack (section 5), plus real platform labels where required.

---------------------------------------------------------------------------------------------------------
## 1. The system in one picture

```
You  <-- WhatsApp (daily/weekly report, questions, commands) -->  CHIEF OF STAFF
   |                                                                  |
   | Inbox tab: approvals, blockers, questions                        | plans, delegates, verifies, reports
   v                                                                  v
 Departments (templates; you toggle; each has agents, skills, playbook, budget, caps, KPIs)
  Revenue:   Gig Radar & Proposals | Careers (Kenya) | SME Leads & Audits | Web/App Studio | Social-as-a-Service | Billing & Collections
  Content:   Video Studio | Social Studio (own brands) | SEO/Blog & Newsletter | Personal Brand & Portfolio
  Ops:       Inbox & Calendar | WhatsApp/Support | Research & Intel | QA & Testing | App Publishing | Learning
  Hygiene:   Phone Health & Backup | Security | Life Admin
        |
 Capability ladder for every outward action:  API/MCP -> recorded routine -> screen agent (compact reads) -> in-app browser -> human
        |
 Shared services: Resource scheduler (screen lane, LLM tokens, CPU, cloud render, account rate limits, human attention)
                  Budget/cost guard (USD, tokens) | Verifier (outcome evidence) | Memory + Lessons | Editable company folder
                  Safety layer (untrusted-text wrapping, tool allow-lists, approval tiers) | Channels (WhatsApp, Telegram)
```
After build, your flow is exactly as you described: you create accounts, connect MCPs, sit with Zafiro in a **goal session** (it interviews you, writes `MISSION.md` + `PLAN.md`, you edit and approve), say "take over", plug in the phone, and watch the Inbox and WhatsApp. A department that is not connected simply stays off.

---------------------------------------------------------------------------------------------------------
## 2. Department catalogue (to approve; ranked by speed to revenue for you)

Evidence is thin on solo-operator revenue (vendor-heavy sources) [S]. The research's strongest point: **services sold with an agent-built audit as the hook are the fastest money**; passive products are slow. Kenyan SME web sites are quoted ~KES 20k-120k + KES 5-20k/month maintenance, chatbot retainers $200-2,000/month are *asking* prices [S]; WhatsApp is the top SME sales channel in Kenya [S].

**Tier A (cash in 1-4 weeks)**
1. **Gig Radar & Proposals**: 10-20 ranked jobs + 5 tailored drafts a day (Upwork, Contra, Fiverr, forhire threads). Drafts only: Upwork bans scripts that submit faster than a human; you press send [S].
2. **Careers (Kenya-first)**: section 6.
3. **SME Audit-and-Pitch**: find 20 local businesses/day, make a one-page audit (no site, weak Google Business Profile, slow site, no WhatsApp) and a drafted WhatsApp/email pitch. First contact is sent by you or from your own phone with an opt-out (spam and Kenya data-protection exposure is unverified: check the Data Protection Act before scaling).
4. **Web/App Studio** (the "Builder"): delivers what 2-3 and 5 sell. Zafiro writes code through GitHub; Actions builds/tests; deploy to staging; you approve production.
5. **Social-Media-as-a-Service**: 12-20 posts per client per week via Metricool, monthly report.
6. **Billing & Collections**: invoices, reminders, receivables list (M-Pesa Daraja later; drafts for approval first).
7. **Treasury & KPIs** (part of the Chief of Staff): runway, revenue and token spend per department, kill-or-keep recommendation. This enforces your cash-flow rule.

**Tier B (1-3 months):** Video Studio, Social Studio (own brands), Personal Brand/Portfolio (every delivered job becomes a case study), SEO/Blog engine, WhatsApp Business replies, Inbox & Calendar, Research & Intel, App publishing/ASO (Play personal accounts created after 2023-11-13 need a 12-tester, 14-day closed test [V]), Affiliate/digital products (slow, last).
**Tier C:** QA & Testing, Community management, Learning/courses, Phone Health & Backup (mandatory), Security, Life admin.

I will not start more than 3-5 departments after your interview; the catalogue grows as you approve templates (your `docs/APPROVAL_LIST.md` rule).

---------------------------------------------------------------------------------------------------------
## 3. Parallel execution on one phone (the honest version)

- **Non-screen agents** (API, MCP, HTTP, GitHub/Actions, LLM-only research, drafting) are mostly waiting on the network: run 4-6 at once as a starting guess [U, measure].
- **The screen is one exclusive lane** with a lease, used in short slices (about 15 steps), then yielded; batch by app; one task per app and per account at a time.
- **A second lane needs proof.** Virtual displays exist in upstream, but on Android 12 per-display gesture targeting may not exist (I believe API 32) [U], apps may refuse secondary displays, and a Helio A22 with 3-4 GB RAM may thrash. Treat as an experiment behind a flag after measuring.
- **The in-app browser is the real parallelism lever** (section 8): a WebView that Zafiro owns can run its own agent task *without touching the main screen*.
- **Scheduler resource classes:** SCREEN(1), LLM (token bucket per provider, 3-4 concurrent), NET, CPU_LOCAL(1), CLOUD_RENDER (Actions minutes), ACCOUNT_RATE (per-platform caps), HUMAN (max N questions/day). Revenue-weighted priority with aging; per-department daily token and USD budget; per-run step and token caps; repetition detector; hourly spend cap; 3 failures on an item -> dead-letter column, never infinite retries.
- **Why this order matters:** OpenClaw-style agents burned tokens in overnight loops (about 900 looped tool calls until the balance ran out; no native hard dollar cap; heartbeats resend the whole conversation) [V issue reports, fix status U]. Budgets and circuit breakers come BEFORE new departments.
- **Recovery:** foreground service, battery exemption, OEM autostart, watchdog recording missed runs; leases with heartbeat; at boot reconcile RUNNING -> INTERRUPTED; idempotency keys so retries never double-post; Shizuku down pauses only SCREEN jobs.

---------------------------------------------------------------------------------------------------------
## 4. Video Studio, built on YOUR repo (`TricretA/Claude-Video-Editor`, read today)

What the repo already is [V, read in the clone]: a 1.3 GB workspace with **11 production skills** (about 42 KB total), tools and projects.
| Piece | What it gives us |
|---|---|
| `studio/skills/studio-director` | Executive-producer role chain (brief, reference analyst, story, scene/motion, assets, compose, sound, QA, delivery), standing owner rules (graphics-first, 80% graphics/footage, new visual event every 1.5-2.5 s, hook in 2 s, plain language, real assets, honesty), project.json resume protocol |
| `motion-craft`, `shortform-playbook`, `graphics-toolkit`, `style-bank`, `editing-rules`, `editing-styles`, `reference-analyst`, `shorts-clipper`, `qa-gates`, `canvas-motion-engine` | Measured motion laws, hooks/retention/pacing/captions, 456-item HyperFrames registry + 157 shot cards, style library, reference-reel analysis, gate numbers |
| `studio/tools/render_canvas.mjs` | Headless Playwright + ffmpeg renderer for canvas films, offline fonts, synthesized sound; **verified on Linux** (35 s render, 1080x720) |
| `studio/tools/video_eyes.py` | Frame sheet, cuts, gates (G2/G3 motion/end-card), report |
| `studio/LESSONS.md` | Your hard-won rules (e.g. canvas engines beat the HyperFrames generator on motion; ElevenLabs credits run out; hand-built beats win) |
| `brand/`, `series_v2/_gen`, `video-pipeline/` | Brand kits (Tricid, BingwaOne), layer-DSL generator, audio mixer/ducking scripts |

Plan:
1. **Port the 11 skills** into Zafiro's skill format (they are already SKILL.md). Condense any over ~5 KB, keep your rules verbatim, add a `brand-profile` skill (Tricid, BingwaOne colours/fonts, voice rules).
2. **Create `zafiro-studio`** (you create it): `.github/workflows/render.yml`, `jobs/`, `templates/` (canvas engine, HyperFrames, Remotion), `tools/` (render_canvas.mjs, video_eyes.py). Job flow: Zafiro commits `jobs/<id>.json` (scenes, script, asset URLs, voice file, captions) -> `workflow_dispatch` -> Actions renders -> output as a Release asset -> Zafiro polls and downloads [V: REST triggers and release assets documented].
3. **IP caution (important):** your repo contains the Nafay canvas engines (`engines/nafay/`) that you downloaded from a library whose email gate is "UI only", plus copies of third-party skill sources (`prompt & music/...`). **Do not publish those in a public repo** without checking each licence. Options: keep `zafiro-studio` private (Actions free allowance is ~2,000 min/month for private repos on the free plan [V]) or keep Nafay/third-party sources out and rebuild the engines from your own code. Public repos get free unlimited minutes but expose scripts and logs.
4. **Rendering ToS:** GitHub's terms say runners are for producing/testing/publishing the repo's software, not unrelated media [V]; a repo whose content is the video-template project with modest daily jobs is defensible. Keep it that way.
5. **Engines to support:** canvas-motion-engine (default for kinetic/typographic), HyperFrames (Apache-2.0, Node 22 + ffmpeg + Chrome; runs in Docker/CI [V README]; 9 skills, ~450 registry blocks), Remotion (free for individuals and companies of 3 or fewer incl. commercial [V]; official `remotion-dev/skills`), **reanimate** (MIT, Bun; records a reference animation in headless Chromium and rebuilds it in CSS/GSAP/three.js with frame comparison: use it to recreate UI/web animations, it does not output MP4 by itself).
6. **Logo design:** I could not find a "logo-design" skill repo on GitHub from the sandbox (code search needs sign-in). A `logo-design` skill exists in the Claude skills I can see locally, and your repo already has `brand/logo-kit`. **Send me the exact repo URL** and I will review it; until then the Video Studio uses your logo kit.

---------------------------------------------------------------------------------------------------------
## 5. Content factory under your rules (screen-driven, no paid AI APIs)

**Voice.** ElevenLabs **Free forbids commercial use** and content made outside a paid plan can never be used commercially later; public posts need "elevenlabs.io"/"11.ai" in the title [V]. So free ElevenLabs is fine for non-monetised experiments only. Starter is $6/month (30k credits, commercial licence; a $1 first-month Black Friday offer was shown, ends Dec 1 [V]; re-check). Free alternatives with no UI at all: **Gemini TTS in AI Studio** (free of charge, but unpaid use lets Google train on inputs/outputs: send nothing sensitive [V]) and **Kokoro/Piper in Actions** (open weights; licences [U]). Whisper (Groq free) gives word timestamps for captions [V].
**Images.** ChatGPT and Gemini apps by screen. Limits are not published as hard numbers (ChatGPT free "separate limits"; Gemini compute limits refreshing every 5 hours + weekly cap; Pro 4x) [V]. Both embed provenance (OpenAI: C2PA + SynthID; Google: SynthID + C2PA) [V]: **assume posts may be labelled AI; always set Metricool's AI flags (isAiGenerated / isAigc / isAiGeneratedContent)**, do not strip metadata. **ToS risk:** OpenAI's terms ban programmatically extracting Output [V]; automating the ChatGPT app is plausibly covered. Use one real account, human pacing, expect possible account action. Prefer Gemini first (no explicit UI-automation ban found) [U].
**AI video (only if needed).** Google Flow: free accounts get 50 credits/day; Veo 3.1 Lite = 10 credits per 4/6/8 s clip; Kenya supported; peak-hour limits for free users (about 17:00-20:00 Nairobi); a Flow Android app exists [V]. Pro adds 1,000 credits/month.
**Real video from code** (preferred; no quota, no ToS risk): Remotion/HyperFrames/canvas engines on Actions, voiceover from Gemini TTS/Kokoro, captions from Whisper.
**Getting files back from the apps.** Best: add an **ACTION_SEND receiver ("Share to Zafiro")** so you or the agent taps Share in ChatGPT/Gemini and picks Zafiro; no storage permission needed [U on-device]. Fallbacks: MediaStore query (date_added after a timestamp taken *before* pressing generate, IS_PENDING=0), or shell `ls -t`. Never trust "latest overall"; verify size and decodability [V design].
**Posting: Metricool MCP first.** `https://ai.metricool.com/mcp`, OAuth 2.1, works on **any plan including Free** [V]; header auth and the API need Advanced. Media must be **public URLs** (no upload tool): host on GitHub Pages or a separate public `media` repo (raw.githubusercontent serves mp4 as octet-stream; release URLs redirect to 30-minute signed links, so Metricool may not accept them [V tested]) and test with a 3-second `draft:true` post first. Free plan: 1 brand, 20 posts/month, no LinkedIn/X [V]; **tell me your plan**. Tools include createScheduledPost and review variants (team plans). **Screen fallback built in:** `autoPublish:false` sends a push to the Metricool mobile app and the agent finishes the post on screen [V schema]. TikTok/YouTube first posts may not be public until the vendor's app is audited [U]: test.
**Quality gate:** the humanizer stack (section 5a) + qa-gates for video.

### 5a. Humanizer stack (the "not obviously AI" system)
`blader/humanizer` (MIT; 26-29 tiered patterns from Wikipedia's AI-writing guide + a personality pass), `voice-profiler` method (build a persona profile from your real writing), bundled `stop-slop`, `instagram-agent-skill` text skills (13, MIT: `ig-human` anti-slop, `ig-reel`, `ig-caption`, `ig-carousel`, `ig-comment`, `ig-reply`, `ig-dm`...). Honest note: that repo's own README says phone/browser automation violates Instagram's terms, and its hook scorer has AUC 0.56 (weak), so we take the writing methods and ignore the scoring claims. Plus an **experience bank** (your real anecdotes, prices, Nairobi context) and the 10-point draft checklist from V2. These run as text skills on the phone.

---------------------------------------------------------------------------------------------------------
## 6. Careers department (Kenya-first), from live checks on 2026-11-25

**Why applications get ignored [V samples]:** "remote" boards overstate who can apply. RemoteOK sample: 64 of 100 jobs "US Only", 9 Worldwide. Himalayas lists a per-job country list: in 240 jobs only 4 named Kenya and 7 another African country. WeWorkRemotely shows 81 of 100 "Anywhere" but that is unaudited. "EMEA" roles often exclude Africans in practice [S]. **Fix: a country/timezone eligibility gate runs before any draft is written.**
**Honest ceiling:** only about 3-15 genuinely qualified, Kenya-eligible tech roles appear per day across all feeds combined [U estimate]. 50 a day is possible only with a loose gate, and loose-gate applications are the ones that get ignored. So "aggressive" = scan everything continuously, apply to every eligible match fast, and put the rest of the effort into high-touch channels. No published Kenyan response rates exist for any board; we will measure our own from day one.

**Start today (ordered):**
1. Himalayas API (cursor JSON with country + timezone lists; gate: Kenya/Africa/empty + UTC+3).
2. **M-KOPA** Ashby feed (51 postings; Senior Backend Engineer .NET in Nairobi open now).
3. **One Acre Fund / Tupande** Greenhouse feed (AI Engineering Lead, Automation Engineering Lead, Nairobi).
4. **Moniepoint** Greenhouse feed (Remote Kenya roles).
5. **ReliefWeb Kenya RSS** (`reliefweb.int/jobs/rss.xml?advanced-search=(C131)`).
6. **JobWeb Kenya RSS** (`jobwebkenya.com/feed/`, tech-aware, live).
7. **BrighterMonday and MyJobMag via email alerts parsed from Gmail** (no feeds; robots.txt blocks scraping BrighterMonday job pages).
8. **Upwork**: strongest verified payout route for Kenya (M-Pesa direct) [S]; proposals drafted by the agent, **you press send** (bots that submit proposals are banned).
9. **Turing and Andela** one-time vetted-network applications (do deliberately, Turing retake wait ~6 months).
10. Telegram `t.me/s/TechPsyche` (daily global remote jobs, some open to Kenya) and `@africajobs`.

**Other verified sources:** WWR RSS (region tag), RemoteOK API, Remotive API (worldwide only), Jobicy, VC4A RSS, Career Point Kenya RSS, Greenhouse/Lever/Ashby JSON for any company slug, Moniepoint/Jumia/Apollo Agriculture boards; Toptal, Crossover, Arc, Lemon.io, Gebeya, micro1, OpenTrain, Alignerr, Mindrift as high-touch applications (Kenya eligibility of AI-training platforms varies: confirm each). **Closed to Kenya [S, tracker dated 2026-10-24]:** Outlier, DataAnnotation, Remotasks, Mercor. Never rent or buy accounts. Bot-blocked, check by hand: Fuzu, Safaricom, Cellulant, Africa's Talking, Twiga, UN Careers, UNjobs, Impactpool, Devex, Wellfound, YC Work at a Startup. Facebook/X/LinkedIn job posts cannot be read programmatically; use alerts and treat every Facebook job group as hostile until the poster is verified (documented fake pages: GetJobs Kenya, fake Naivas/Rivatex/KPA/KeRRA/Kenya Railways ads).
**Mix:** 40% eligibility-gated feeds, 25% Kenyan boards via email alerts, 15% marketplaces and vetted networks, 10% AI-training contracts, 10% referral/direct outreach to employers found on Kenyan ATS boards.

**Rules the agent follows:** read feeds/alerts, never scrape against robots.txt or ToS; **never auto-submit** (drafts queue to you; ATS forms by screen/in-app browser with your approval at first); **any fee, "processing", M-Pesa registration, WhatsApp-only "task release", cheque or account-rental offer = hard fail**; log source, eligibility verdict and outcome for every job so real response rates exist after a month; say your timezone overlap in line one (UTC+3 = full European day, afternoon overlap with US East); sell shipped Android and AI-automation outcomes at fixed scope, not "cheap Kenyan dev".
**Money pointers (verify before relying; not tax advice):** Upwork M-Pesa direct [S]; PayPal to M-Pesa ~3% [S]; Payoneer ~2-3% all-in to a Kenyan bank [S]; Wise's current Kenya terms are contradictory across sources: confirm in-account; keep a second payout route (Wise/Sendwave reportedly restricted some Kenya transfers [S]). Kenyan residents must declare income from any source; April 2026 tribunal sided with KRA on foreign-client income [S]; check kra.go.ke or an adviser.
**Skills (15):** job-source-scanner, eligibility-checker (country/timezone/work-authorization regexes), ghost-and-scam-check, kenya-channel-reader (Telegram/RSS/alerts), ats-form-filler, email-applier, resume-variant-picker, cover-letter-writer, application-tracker, follow-up-manager, inbox-triage, freelance-proposal-writer, interview-prep, salary-negotiation, compliance-guard.

---------------------------------------------------------------------------------------------------------
## 7. Connectors: catalogue, MCP client, marketplace (modelled on claude.ai plugin categories)

Could not read claude.ai/plugins (403); I used your category list [U on exact scope]. Essentials per category (URLs and auth probed live by an agent on 2026-11-25 [V], free-tier notes marked ~ are from memory):
- **AI models & agents:** Hugging Face (`huggingface.co/mcp`), fal, ElevenLabs (KEY header; remember its free-tier rule), Replicate. NVIDIA NIM stays a model provider.
- **APIs/Research:** Apify (`mcp.apify.com`, DCR+CIMD or token), Exa, Firecrawl, Jina, Tavily.
- **Automation:** Zapier, Make, n8n (self-hosted).
- **Code/Engineering:** GitHub (`api.githubcopilot.com/mcp/`; **no DCR**: use a PAT header or ship an OAuth app/device flow), Context7, DeepWiki, Microsoft Learn, Linear, Sentry, PostHog.
- **Contacts & leads / Sales:** Apollo, Hunter, Attio, HubSpot (fixed client), Indeed MCP (terms for other clients [U]); LinkedIn has no official MCP: skip scrapers.
- **Databases:** Supabase, Neon, Airtable, Upstash.
- **Designs & media:** Canva, Higgsfield (credits), Figma, Cloudinary, fal.
- **Email & messages:** Gmail/Drive/Calendar via Google remote MCP (**fixed OAuth client you create**; Gmail returns 401 only on `tools/call`), Resend, Brevo, Mailchimp, Slack (fixed). Telegram/WhatsApp only community servers: keep them as the app's own channels.
- **Finances:** Stripe, PayPal, Xero, QuickBooks; Kenya M-Pesa/Paystack MCPs are community stdio only [U quality]. Money-moving tools are approve-only; unattended denies.
- **Logs & errors:** Sentry, Axiom. **Marketing:** Metricool (OAuth), Buffer, Postiz, Semrush. **Notes & memory:** Notion (app memory stays default). **Reference docs:** Context7, Cloudflare docs. **Servers & cloud/Websites:** Cloudflare, Vercel, Netlify, Railway, Render, Webflow. **Tickets & tasks:** Todoist, Linear, Asana (fixed), ClickUp, Google Calendar. **Files & documents:** Google Drive, Dropbox, Box. **Claude history:** no remote MCP; offer built-ins (search our run history; import a claude.ai data export).

**MCP client must support** (per spec 2025-11-25 [V]): Streamable HTTP (session id, protocol-version header, either JSON or SSE response, 404 -> re-initialize) and legacy SSE; stdio only via a Termux/SSH bridge ("needs bridge" badge); auth modes: none, static/custom headers (incl. multi-header like Cloudinary), query-string tokens (mask in UI/logs), OAuth 2.1 + PKCE with discovery (RFC 9728/8414), **client registration order: pre-registered/user client, Client ID Metadata Document, then DCR**, `resource` parameter on both requests, step-up on `insufficient_scope`, user-supplied client id/secret for fixed servers (Google, GitHub, Slack, Asana, Box, Xero, HubSpot), `authServerMetadataUrl` override. **Token store must keep the whole record** (access, refresh, expiry, client id/secret, issuer, token endpoint, resource, scopes), encrypted with Android Keystore (our current "Authorization header only" design cannot refresh, and ADR-0006 is still undone), refresh lazily with a single-flight mutex per server, handle 401 on any request, `invalid_grant` -> "needs re-auth" notification and NEEDS_HUMAN for unattended runs (do not rely on the 10-minute loop: Tecno/Samsung kill it). Redirect: HTTPS App Link or loopback; use Custom Tabs; persist PKCE state across process death. UI: add by URL (auto-probe), add by JSON (`mcpServers`), import from Claude/Cursor config, per-server and **per-tool enable/disable**, **delete (removes tokens + registration)**, test connection, status/expiry, tool annotations (write/destructive default to "ask").
**Marketplace screen:** category grid (your 20), search, chips (Skills, MCP, Workspaces; Installed; Free; No account), cards with Verified/Community/Needs account/Needs key/Needs OAuth app/Paid/Bridge-needed/Writes data/Can spend money, install-disabled-by-default, detail sheet with tool switches, SKILL.md preview, licence, pinned commit SHA. `catalog.json` in app assets with `verifiedOn`; stale after 90 days; no telemetry.
**Safe skill install from GitHub URL** (design from research): github.com only, resolve to commit SHA, size/file caps, no symlinks/executables, frontmatter validation, licence allow-list (MIT/Apache/BSD/ISC/CC0; warn on none/GPL/LGPL/personal-use), preview before enabling, scripts never auto-run, ignore `allowed-tools`, show injection-phrase warnings, "update available" with diff, never auto-update.
**Delete skills and MCPs (your complaint).** Read in code: `SkillFileRepository.delete` removes any skill, and the seeder already remembers seeded ids so deleted defaults stay deleted [CODE; an earlier version of this plan claimed the opposite and was wrong]. The real gap was that delete lived only inside the skill detail page; v1.19.0 adds a delete button to every row of the list. MCP delete must also clear stored tokens (not done).

---------------------------------------------------------------------------------------------------------
## 8. In-app browser (agents can use it; you can toggle it)

Today: only an **in-app URL previewer** exists (`ShowTarget.InAppPreview`); the `BrowserSession`/`BrowserScreen` interface for agents is documented in code as **not implemented** [CODE], which is why you never saw an agent browser.
Proposal: implement `BrowserScreen` on an Android **WebView** that the app owns:
- Compact DOM snapshot via injected JS (interactive elements with short refs, same text format as the screen reader) = fewer tokens and no accessibility tree walk; clicks/typing through JS or WebView input.
- Runs **without the main screen** -> a real parallel lane; also usable on a virtual window.
- Own cookie jar (separate from Chrome), per-site logins that persist; global toggle + per-agent allow.
- Honest limits [S/K, test]: Google blocks sign-in inside embedded WebViews (`disallowed_useragent`), and Cloudflare-protected sites may challenge it; for those sites (Google/ChatGPT/ElevenLabs logins) use the real Chrome app on the screen. Good for: public sites, job forms, ATS pages, scraping, Metricool/GitHub web, anything that accepts a WebView login.

---------------------------------------------------------------------------------------------------------
## 9. Speed plan for screen operation (measure first)

**Findings:** every action currently does a settle loop that walks the whole tree every 50 ms (min ~500 ms with the 200 ms dwell and 300 ms quiet) and then another full snapshot; routines add snapshots [CODE]. Only calls that cross Binder are slow (`getChild`/`getParent` on uncached nodes, `refresh`, `findAccessibilityNodeInfosBy*`, `performAction`); `getText`/`isClickable` on a fetched node are local; **FLAG_PREFETCH_* needs API 33+, so not on your Android 12** [V docs]. No published `getChild` benchmark on Helio-class chips: per-tree costs below are estimates.
**Order of work (each measured on the phone):**
0. **Per-phase timing log** (LLM TTFB/total, act, settle, snapshot, node count, getChild count) as one JSON line per step. Without it every gain is a guess.
1. **Event-driven settle**: wait for window-state/content events, quiet 120-150 ms, cap ~1.2-1.5 s; no tree hashing during settle (est. save 0.3-1.2 s/step).
2. **One snapshot per step**; routines reuse it until a newer event (0.2-0.8 s per routine step).
3. **Pruned walk**: skip invisible/off-screen subtrees, cap nodes/depth (30-70% off snapshot time).
4. **Animation scales to 0** via Shizuku, restored when you use the phone (est. 0.2-0.6 s/step).
5. **Expected-element waits** in routines (`findAccessibilityNodeInfosBy*`) instead of full snapshots.
6. **Batch 2-5 actions per model call** with abort-on-mismatch (Artemis chains taps; est. 30-60% fewer model calls).
7. **Recorded routines as zero-model macros** with verification (a step 0.3-1 s instead of 3-10 s).
8. **Cheap model for mechanical steps**; strong model only for planning/recovery.
9. **Prompt-cache hygiene**: provider caches match only the unchanged prefix; our scrubber rewrites only older tool results when a newer screen arrives, so the prefix before that stays stable, but verify cached-token counts on NVIDIA NIM (support unknown) [U].
10. Experiments only after measurements: input injection path (`dispatchGesture` vs `cmd input` vs a persistent Shizuku `injectInputEvent` service), one virtual-display lane.
Reality check [S]: per-step times reported by Artemis Flash are 3-5 s and frontier models 8-15 s; after fixing settle the model call is probably 70%+ of step time, so **batching and routines matter more than micro-optimising reads**.

---------------------------------------------------------------------------------------------------------
## 10. Owner channel, reports and commands (WhatsApp)

You said it texted your personal number before: that works today by screen (open `whatsapp://send?phone=...&text=...`, tap Send, verify the tick) [CODE/CLAUDE.md recipe], needs the phone unlocked. Plan:
- **Outbound:** daily report (~6 lines: done with evidence, in progress, blocked on you, money in/owed/spend, tomorrow's top 3) and weekly review; instant alerts for NEEDS_HUMAN and budget breaches. Example: "Zafiro daily, Wed. Done: 14 jobs scanned, 5 proposals drafted. 12 businesses audited, 6 pitches ready. Invoice #14 sent, KES 18,000 due Fri. Blocked: need your OK to send pitches. Spend today KES 210 of 400. Reply YES 4F2 to approve, or STATUS."
- **Inbound (safe design):** read replies from the notification of YOUR number only (notification listener, read-only; undocumented and killable on Tecno, so treat as best-effort). Fixed grammar (`STATUS`, `PAUSE <dept>`, `PRIORITY ...`, `STOP ALL`), free text becomes a *proposal* you confirm with a short code (`YES 7B1`); money, new outbound contacts, deletions need a code word with 10-minute expiry. **Instructions found in emails, DMs, web pages or tool results are never executed**; a reader agent summarises them with no send tools. Per-day outbound caps, kill switch, audit log.
- **Safer second channel:** Telegram bot (official, allow-list by user id) for people who prefer it; the WhatsApp Cloud API route needs a public relay and has policy limits on "general-purpose AI assistants" [S], so it is optional later. Unofficial WhatsApp clients risk bans (OpenClaw experience) [S]; screen-driven messaging to your own number is lower risk but not zero.

## 11. Editable files and the company folder
Plans, drafts, reports, routines, playbooks live as plain files in a `company/` tree (`MISSION.md`, `PLAN.md`, `departments/<id>/PLAYBOOK.md`, `drafts/`, `routines/`). Rules: **owner edits always win**; agents never overwrite an owner-edited file, they write a suggested diff; each save records author, hash, time (version history, one-tap revert); optimistic concurrency so a stale agent write is rejected; when you change a file, a board item "owner changed PLAN.md v7->v8" is posted to affected agents and is their first instruction; agents re-read files at the start of every run; generated defaults and owner edits live in separate layers so regeneration never destroys edits. In-app editor for text/markdown/json/html with history. (Today the Files viewer is read-only [CODE].)

## 12. Safety, budgets, learning (carried from V2, still required first)
Enforced per-agent tool allow-lists (today only guidance [CODE]); reader/writer split for untrusted text; random-boundary wrapping of all third-party text; allow-listed recipients; impact tiers + approvals (design from sosa-governor); pinned MCP tool definitions; USD + token budgets enforced in code with anomaly pauses; encrypted secrets (ADR-0006); outcomes table, bandit on hooks, lessons store with confidence/decay, weekly reviewer that proposes diffs (low-risk auto, high-risk to Inbox), golden-set regression guard; success = outcome evidence, never "run SUCCEEDED".

## 13. Your 17 repos + new ones: verdicts
Adopt/adapt as in V2 (humanizer, ziazon orchestration/planning/handoff condensed, token-shield tier table and spend ceilings, prompt-master routing score, sosa-governor approval tiers, session-handoff format, ffmpeg-llm references, motion-reel and animation-studio on Actions later, Buzzfy read-only, svg.new optional; skip read-aloud and video-to-code; adaptive-agent only as reviewed diffs). **New:** `instagram-agent-skill` (13 text skills, MIT; take the writing skills, ignore its measured-weak scorer), `reanimate` (MIT; web/UI animation recreation on Actions), `hyperframes` (Apache-2.0; second render engine), `remotion-dev/skills` (licence not verified: read `remotion-license` before commercial use), your own studio skills (section 4).
**Skill pruning** (still needs your approval of exact ids): ship a curated ~80-100 core, the rest installable from the marketplace; today 278 bundled, 27 on by default [CODE].

## 14. Roadmap (reordered)
| Phase | Deliver | Why first |
|---|---|---|
| **P0 Foundation** | Timing log + event-driven settle + one snapshot per step; budgets/USD cap/circuit breakers; enforced tool allow-lists; untrusted-text wrapping; Keystore token store; deleted-defaults fix; editable-file layer; calibrate/record routines on the clean phone | Safety and speed; everything else rides on it |
| **P1 Company core** | Department model + templates, Chief of Staff loop, resource scheduler, verifier, goal-interview + `MISSION/PLAN`, WhatsApp daily report + safe commands, Inbox/Runs/Plan UI shell | Makes "plan and take over" real |
| **P2 Non-screen revenue departments** | Gig Radar, Careers (feeds + ATS + email), SME Audit-and-Pitch, Billing | Earn within weeks without competing for the screen |
| **P3 Content** | Video Studio on `zafiro-studio`, Share-to-Zafiro receiver, Metricool MCP, Social Studio with humanizer gate | Your daily content engine |
| **P4 Connectivity** | MCP client upgrade (OAuth modes, tokens), Marketplace + safe skill install + deletes, in-app browser | Breadth |
| **P5 Learning** | Outcomes, bandit, lessons, weekly reviewer, regression guard | Compounding |
| **P6 Experiments** | Second screen lane, Termux/Codespace extras, App publishing | Only if measured worthwhile |

## 15. Decisions I need
1. Approve this department-based design and the P0->P6 order (or reorder).
2. Your **Metricool plan** (Free/Starter/Advanced) and which networks are connected.
3. ElevenLabs: stay free for non-monetised only, pay $6/month, or use Gemini TTS/Kokoro for monetised content?
4. `zafiro-studio`: private (2,000 min/month) or public (unlimited, exposes logs) given the Nafay/third-party sources in your repo?
5. The exact **logo-design** repo URL.
6. The WhatsApp number to report to (stored in the app, not in the repo) and whether reading your replies via notifications is acceptable.
7. Which 3-5 departments start first after the interview.
8. Approval of the skill-pruning list when I send it.

## 16. Unknowns (do not trust these blindly)
Every phone behaviour (share receiver, notification reading, routine labels, WebView logins, virtual-display lanes); Metricool plan limits and TikTok/YouTube public posting via its apps; whether Metricool fetches raw GitHub/Pages URLs; ChatGPT/Gemini free limits and save locations; Gemini TTS UI download format; platform ToS wording (mostly read via snippets); Kenya data-protection law for cold outreach; revenue numbers for solo operators (vendor-heavy); remotion-dev/skills licence; claude.ai category scopes; no benchmark of accessibility-tree costs on Helio A22.
