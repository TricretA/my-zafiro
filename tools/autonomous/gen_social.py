"""Generates the single "social" workspace (Social Media Manager): shared team, shared tasks, per-platform playbooks,
routines, the platform catalogue asset and the shared social skills. Called from gen_workspaces.py."""
import json
from social_platforms import PLATFORMS

METRICOOL_FREE_LIMIT = 20
WARMUP_DAYS = 28
FALLBACK_CAPS = "post=3,comment=15,reply=30,like=30,follow=8,dm=3,note=6,restack=10"
SHARED = ["team-protocol", "toolbox", "skill-router", "fast-screen", "engagement-loop", "reviewer-rubric", "owner-corrections", "human-pacing", "screen-routines", "video-publish",
          "social-ops", "social-account-safety", "social-voice-and-slop", "metricool-posting", "humanizer", "stop-slop"]

ROLE_MAP = dict(
    director=[("plan the day or week", "social-strategy,content-calendar,batch-content-plan,goals-and-kpis,campaign-and-launch-planning,cross-platform-repurposing"),
              ("every draft gate and every posting decision", "social-voice-and-slop,humanizer,social-account-safety,metricool-posting"),
              ("before you say a day is done", "sp-verification-before-completion")],
    researcher=[("trends, competitors, audience", "audience-research,competitor-analysis,content-research-and-sourcing,deep-research,viral-reverse-engineering,trend-jacking")],
    strategist=[("pillars, calendar, experiments", "content-pillars,content-calendar,social-strategy,experimentation-and-ab-testing,campaign-and-launch-planning,idea-generation-and-ideation"),
                ("one idea on many platforms", "cross-platform-repurposing,platform-specs-and-validation")],
    writer=[("any caption, post or script", "caption-writer,hook-writer,writing-style-and-tone,voice-builder,social-voice-and-slop,humanizer,stop-slop"),
            ("platform formats", "short-form-video-script,scripting-and-storyboarding,thread-writer,carousel-writer,linkedin-post-writer,tiktok-script,reels-script,threads-post,text-post-and-microblog,email-and-newsletter,thumbnail-design,quote-cards-and-text-graphics")],
    reviewer=[("every review", "reviewer-rubric,social-voice-and-slop,humanizer,stop-slop,platform-specs-and-validation")],
    engager=[("every loop", "engagement-loop,reply-and-comment-writer,community-management,human-pacing,social-account-safety"), ("hostile or tricky comments", "crisis-and-moderation"),
             ("direct messages and leads", "social-selling-and-dm")],
    executor=[("any screen job", "fast-screen,screen-routines,human-pacing,social-account-safety"), ("post a video or image", "video-publish,platform-specs-and-validation,scheduling-and-queue"),
              ("Metricool route", "metricool-posting")],
    analyst=[("numbers and reporting", "analytics-and-reporting,goals-and-kpis,mkt-analytics,metricool-posting"), ("experiments", "experimentation-and-ab-testing,content-audit")],
)

EXTRA_TOOLS = dict(executor=",notify", engager=",notify")


def build(skill, w, A, ROLES, ROLE_TITLE, TOOLS, skill_exists):
    ids = {p["id"] for p in PLATFORMS}
    for p in PLATFORMS:
        p["playbook"] = f"{p['id']}-playbook"
        p["skills"] = [s for s in dict.fromkeys(p["skills"]) if skill_exists(s)]

    # ------------------------------------------------------------------ shared skills
    skill("social-ops", "How the Social Media Manager team works: the live platform file, per-platform action keys, rotation, the file layout and the run loop. Load first in every social run.", f"""
# Social ops (read first)
One team serves ALL the owner's platforms, but only the ones he switched on. The app writes the live configuration to `social/PLATFORMS.json` (read it with `save_file op=read path=social/PLATFORMS.json`):
```
{{"enabled":["instagram","tiktok"],"enabled_since":{{"instagram":"2026-11-26"}},"caps":{{"instagram":{{"post":1,"comment":15}}}},
 "apps":{{"instagram":"com.instagram.android"}},"metricool":{{"plan":"free","monthly_limit":{METRICOOL_FREE_LIMIT},"support":{{"instagram":"free","linkedin":"paid"}}}},"warmup_days":{WARMUP_DAYS}}}
```
## Rules for every run
1. Read the file FIRST. Work ONLY on platforms in `enabled`. A platform not listed is off: do not research it, draft for it, engage on it or post to it, even if an old file or memory mentions it.
2. `enabled` empty or the file missing: `notify` the owner once a day (title "Pick your platforms", content "Open Workspaces > Social Media Manager and switch on the platforms you use.") and end with `STATUS: DONE` and the result SKIPPED - no platforms enabled. Do nothing else.
3. For each platform you touch, `load_skill` `<platform>-playbook` (facts, specs, routes, ramp, caps, AI rules, never-list) and follow it. Playbooks list the platform's extra skills.
4. **Caps and the gate use per-platform keys**: `engage op=plan platform=<p> action=<p>.<action> ...` (e.g. `action=instagram.comment`). The caps are in `caps` and in `engage op=today` (keys look like `instagram.post`). A missing key falls back to the generic cap. Never exceed a cap. Serious actions (dm, email_send, payment, delete, apply) still wait for the owner: pass `serious=true` for them.
5. **Warm-up**: a platform enabled less than `warmup_days` days ago, or whose `social/accounts/<platform>.md` does not say `established`, follows the ramp in its playbook and half its caps. Record the account state, handle and start date in `social/accounts/<platform>.md` the first time you touch it. The owner creates accounts by hand: you never create accounts or verify them.
6. **Rotation**: a run covers at most 3 platforms (a posting run, 2). Keep `social/rotation.json` (`{{"last":{{"instagram":"2026-11-26T10:00"}}}}`), read it, pick the enabled platforms served longest ago, update it at the end. Over several runs every enabled platform gets its turn.
7. One screen job at a time (the phone is shared). Prefer non-screen work (research, drafting, Metricool analytics) in parallel.
## File layout (all under the Files page, `save_file` paths)
`social/PLATFORMS.json` (app-written, read-only for you) · `social/experience_bank.md` (owner's real anecdotes, prices, places; see social-voice-and-slop) · `social/metricool_count.txt` (monthly counter, see metricool-posting) · `social/rotation.json` · `social/accounts/<platform>.md` · `social/plans/<date>.md` · `social/drafts/<platform>/<date>-<slug>.md` · `social/numbers/<date>.md` · `social/RULES.md` (owner corrections, newest first: they override everything).
## Posting routes (capability ladder, see each playbook)
Owner on a PAID Metricool plan: 1 Metricool MCP, 2 `share_file` + `<platform>.finish_share`, 3 the app on screen. Owner on the FREE plan (default; `metricool.plan` says): 2 and 3 first, Metricool is the safety net (see metricool-posting). Never claim a post is live without proof (a post id, a success screen, or the post on the profile).
## Digest
The owner digest goes by notify and, if `whatsapp` is enabled, by the digest recipe in `whatsapp-playbook`.
""")

    skill("social-account-safety", "New-account warm-up ramps, daily caps, human pacing and what to do on any block prompt. Load before any outward action on a social platform.", """
# Social account safety
Platform policies prohibit automation that imitates humans. Engagement automation on a screen is where bans concentrate, so the team works like a careful person on the owner's own phone, with low caps and no tricks. These numbers are community heuristics, not platform rules (unverified): the analyst checks them against the owner's own results and the owner can change caps in the app.
1. **Accounts are created and verified by the owner by hand.** Never create accounts, solve CAPTCHAs, bypass phone/email verification, 2FA, age gates or consent screens: `take_over` or NEEDS_HUMAN.
2. **Warm-up ramp for a NEW account (first 28 days):** days 1-3 browse and react only (about 10-20 likes a day, no posts, no follows); week 1 finish the profile, 1 post every other day, a few comments; week 2 3-4 posts, 5-10 comments, up to 20 follows on follow-friendly apps; weeks 3-4 a post a day and 10-15 comments; after that the caps. Use half of the daily caps during warm-up. The exact ramp is in the platform's playbook.
3. **Pace like a person.** `engage op=pace` before every action, then `wait`. Vary the order (a like, a scroll, a comment, a long pause). No bursts, no fixed minute marks, no night activity beyond the schedule, one device and one network.
4. **Stop on any warning.** "Action blocked", "try again later", "confirm you are human", a CAPTCHA, a login challenge, a temporary restriction, a policy strike: stop ALL actions on that platform for 24-48 hours, write it in `social/accounts/<platform>.md`, `notify` the owner once, and continue with the other platforms. Never retry the blocked action and never "test" whether it still works.
5. **Never**: follow/unfollow churn, mass DMs, identical text twice, link drops in comments, engagement pods, buying followers or views, posting the same asset on a platform twice, impersonation, scraped contact lists, a second account to dodge a limit.
6. **Honesty**: no invented experience, results, clients or testimonials. Label realistic AI media where the platform requires it (see playbook) and keep metadata.
7. When a cap is reached stop that action for the day. Do not fill the day with filler: quality over volume.
""")

    skill("social-voice-and-slop", "The writing gate for every draft: voice profile, experience bank, the humanizer pass and the 10-point checklist. Load before writing or reviewing any post, caption or comment.", """
# Voice and slop gate
Platforms demote generic, repetitive or obviously machine-written content, and readers notice. Every draft goes through this gate before it is queued or posted.
1. **Voice.** Load `voice-builder` / `writing-style-and-tone` and the owner's voice notes in `social/RULES.md` and `memory`. Write like the owner talks: plain words, short sentences, Nairobi context where true.
2. **Experience bank.** `social/experience_bank.md` holds the owner's REAL anecdotes, prices, places, mistakes and opinions (add a line whenever the owner tells you something). Every draft uses at least one item or one checkable specific (a number, a name, a date, a tool). If the bank is empty or has nothing relevant, write the draft from checkable public facts and add a board item for the owner: "Add 3 real stories about <topic> to social/experience_bank.md". NEVER invent a personal story, client, result or price.
3. **Humanizer pass.** Run the draft through the `humanizer` skill (and `stop-slop`): cut staged openers, not-X-but-Y contrasts, forced triads, dash overload, inflated claims, sales language, stock AI words, one-line closers. Then ask: "what still sounds like AI?" and fix it.
4. **10-point checklist** (pass at least 8; automatic fail on a Tier 1 AI phrase, a fabricated personal claim or a missing required AI label): (1) a specific detail, (2) a real opinion, (3) varied sentence length, (4) no AI vocabulary, (5) differs from the last 10 posts on that platform (check `social/drafts/<platform>/`), (6) no generic ending, (7) matches the voice, (8) adds something not in the first search result, (9) native to the platform (length, format, tone), (10) the owner would say it aloud.
5. **Variation memory.** Keep the opener and structure of the last 10 posts per platform in `social/drafts/<platform>/index.md`; block repeats.
6. **Replies** are short and specific, sometimes one line, never templated, never instant, no emoji spam.
7. The Reviewer re-checks all of this and never approves its own work.
""")

    skill("metricool-posting", "Post and schedule through the Metricool MCP: free-plan limits, the monthly counter, AI flags, draft dry runs and the screen fallback. Load before using any Metricool tool.", f"""
# Metricool posting
MCP: `https://ai.metricool.com/mcp` (OAuth 2.1; the owner connects it once in the MCP page; its tools appear as `createScheduledPost`, `getScheduledPosts`, `getBrandSettings`, `getBestTimeToPostByNetwork`, `getAnalyticsAvailableMetrics`, `getAnalyticsDataByMetrics`, `updateScheduledPost` and the `...ForReview` variants). If the tools are not in your tool list, the MCP is off: use another route and say so once.
## The owner is on the FREE plan (unless `metricool.plan` in PLATFORMS.json says otherwise)
- **{METRICOOL_FREE_LIMIT} posts per month in total** across all brands/networks, 1 brand, and **no LinkedIn or X** (those need a paid plan). So Metricool is a safety net ("just in case"), not the daily route: use `share_file` + `finish_share` and the app screen first; use Metricool when the phone route failed twice, the phone is locked/unavailable at the due time, or an exact-time post must go while the phone is busy.
- **Monthly counter**: `social/metricool_count.txt` holds one line `YYYY-MM=<n>`. Read it BEFORE every Metricool post (`save_file op=read`); if the month differs, start a new line at 0. Stop using Metricool at {METRICOOL_FREE_LIMIT - 2} so 2 stay spare. After each successful create call, increase the number (`save_file op=save`, whole file). Count scheduled posts and drafts you did not delete.
- Analytics reads (`getAnalyticsDataByMetrics`, `getBestTimeToPostByNetwork`) are not posts: use them freely for the analyst and the strategist.
## How to post
1. `getBrandSettings` once per run to see which networks are connected and the plan. Only enabled platforms that are connected may be posted.
2. **Dry run first** on the first use for a platform: `createScheduledPost` with `draft:true`; check the result; delete or reuse the draft. A real post only after the dry run looked right.
3. **Media must be public URLs** (Metricool has no upload tool). Host files on the owner's public media repo / GitHub Pages and pass the https URL. A URL that redirects to a short-lived signed link may be rejected (unverified): test with a 3-second clip.
4. **AI flags**: for AI-generated or AI-edited realistic media set the network flag: `isAiGenerated` (Meta networks), `isAigc` (TikTok), `isAiGeneratedContent` (YouTube) (field names as of the last schema read; if the schema differs, follow the schema). Do not strip metadata.
5. `autoPublish:false` turns the post into a push notification in the Metricool phone app: use it as a screen fallback (the executor finishes the post on screen with the notification) and when the owner wants to approve first. The `...ForReview` variants make a post wait for approval.
6. TikTok/YouTube posts from third-party tools may stay private until the vendor audits the app (unverified): after the first real post, check on the platform that it is public and record the result in `social/accounts/<platform>.md`.
7. Verify: `getScheduledPosts` shows the post and its status; record the id in the board result. Never report DONE from the create call alone for a post that is due now: check the platform.
8. Errors (429, auth expired, network not connected): stop using Metricool this run, fall back to the phone route, and tell the owner once if re-authorisation is needed.
## Never
Exceed the monthly counter, post to LinkedIn/X on the free plan, post unreviewed drafts, publish without the AI flag on realistic AI media, put secrets in a prompt or file.
""")

    # ------------------------------------------------------------------ per-platform playbooks
    def ladder(p):
        m = p["metricool"]
        support = {"free": "supported on the free plan", "paid": "supported only on a PAID Metricool plan (not on the owner's free plan)", "none": "NOT supported by Metricool"}[m]
        return ("Capability ladder for this platform. Metricool: " + support + ". Order on a PAID plan: 1 Metricool (skill `metricool-posting`), 2 `share_file` + `" + p["id"] + ".finish_share`" + (" (video/image)" if p["share"] else " (not available here)") + ", 3 the app on screen" + (" (`" + p["id"] + ".post_text`)" if p["text"] else "") + ". "
                "On the FREE plan (default) the order is 2, 3, then Metricool only as the fallback and only while the monthly counter allows (20 posts a month in total, stop at 18)." if m == "free" else
                "Capability ladder for this platform. Metricool: " + support + ". Order: 1 `share_file` + `" + p["id"] + ".finish_share`" + (" (video/image)" if p["share"] else " (not available here)") + ", 2 the app on screen" + (" (`" + p["id"] + ".post_text`)" if p["text"] else "") + (", 3 Metricool only if the owner has a paid plan that includes it (check `getBrandSettings`)." if m == "paid" else ". Metricool is never used for this platform."))

    for p in PLATFORMS:
        pid = p["id"]
        routines = [f"`{pid}.open_home`", f"`{pid}.read_activity`", f"`{pid}.scan_feed`", f"`{pid}.comment`", f"`{pid}.like`"] + ([f"`{pid}.post_text`"] if p["text"] else []) + ([f"`{pid}.finish_share` (after `share_file package={p['app']}`)"] if p["share"] else [])
        caps_keys = ", ".join(f"{pid}.{c.replace('=', '=')}" for c in p["caps"].split(","))
        opt = " (optional extra platform)" if p["tier"] == "optional" else ""
        skill(p["playbook"], f"{p['name']} playbook: facts and specs, posting routes, engagement tactics, new-account warm-up, caps, AI-disclosure and never-list. Load when working on {p['name']}.", f"""
# {p['name']} playbook{opt}
App package: `{p['app']}`. Area: {p['kind']}. Only use this when `{pid}` is in `enabled` of `social/PLATFORMS.json`.
Facts marked (unverified) or (heuristic) are hypotheses: test them against the owner's own numbers and never state them to the owner as certain.

## Facts, formats and specs
{p['facts']}

## Posting routes
{ladder(p)}
Platform specifics: {p['route']}

## Engagement tactics
{p['engage']}
Always through the engagement loop (skill `engagement-loop`) and the gate: `engage op=plan platform={pid} action={pid}.<action>`.

## Warm-up ramp for a NEW account
{p['warmup']}
During warm-up use half of the caps below and keep the ramp. Record the start date in `social/accounts/{pid}.md`.

## Daily caps (gate keys; the owner can change them in the app; `engage op=today` shows live counts)
{caps_keys}

## AI-disclosure rules
{p['disclosure']}

## Research and tools
{p['research']}
Platform skills to load for specific jobs: {', '.join(p['skills']) if p['skills'] else 'none'}. Also `social-voice-and-slop` for every draft and `social-account-safety` before outward actions.

## Screen routines (`screen_routine routine=list`)
{', '.join(routines)}.
Labels are best-effort guesses for the app's English UI: on the first run a routine may fail with a clear reason. Read the screen once, add the real label to `files/routines/{pid}.<name>.json`, and record the fix as a lesson. Do not loop.

## Never
{p['never']} Also: invent facts or results, bypass locks/CAPTCHAs/verification, exceed caps, act while the platform shows a warning.
""")

    # ------------------------------------------------------------------ routines
    for p in PLATFORMS:
        pid, Lb = p["id"], p["labels"]
        def R(name, desc, params, steps, pid=pid):
            w(f"{A}/routines/{pid}.{name}.json", json.dumps(dict(id=f"{pid}.{name}", version=1, platform=pid, description=desc, params=params, steps=steps), indent=1) + "\n")
        n = p["name"]
        openapp = dict(op="open_app", package=p["app"])
        home = dict(op="tap", labels=Lb["home"], optional=True, timeout_s=6)
        R("open_home", f"Open {n} and go to the home screen.", [], [openapp, home])
        R("read_activity", f"Open {n} notifications/activity and return what is shown (replies, likes, mentions).", [],
          [openapp, dict(op="tap", labels=Lb["activity"], optional=True), dict(op="sleep", ms=1500), dict(op="collect", count=3)])
        R("scan_feed", f"Scroll the {n} feed and collect post texts to judge for engagement.", [],
          [openapp, home, dict(op="tap", labels=Lb["notes"], optional=True), dict(op="sleep", ms=1200), dict(op="collect", count=5)])
        R("comment", f"On the post (or chat) currently open in {n}, write a comment or reply and send it. Only after an engage gate of AUTO or owner approval.", [dict(name="text", required=True)],
          [dict(op="tap", labels=Lb["comment"], timeout_s=6), dict(op="type", labels=Lb["field"], text="{text}"), dict(op="tap", labels=Lb["send"], timeout_s=8)])
        R("like", f"Like or react to the post currently open in {n}.", [], [dict(op="tap", labels=Lb["like"])])
        if p["text"]:
            R("post_text", f"Create and publish a text post in {n}. Only after an engage gate of AUTO or owner approval.", [dict(name="text", required=True)],
              [openapp, home, dict(op="tap", labels=Lb["compose"], timeout_s=6), dict(op="type", labels=Lb["field"], text="{text}"), dict(op="tap", labels=Lb["post"], timeout_s=8)])
        if p["share"]:
            R("finish_share", f"After share_file opened {n} with a video or image attached: continue through Next/Continue, put the caption in, and post. Labels are best-effort for the English UI.", [dict(name="text", required=True)],
              [dict(op="wait", labels=["Next", "Post", "Share", "Send", "Upload", "Continue", "Publish", "Create"], timeout_s=15),
               dict(op="tap", labels=["Next", "Continue"], optional=True, timeout_s=3),
               dict(op="tap", labels=["Next", "Continue"], optional=True, timeout_s=3),
               dict(op="type", labels=Lb["field"] + ["Caption", "Description", "Add a caption", "Write a caption", "Title"], text="{text}", optional=True),
               dict(op="tap", labels=Lb["post"] + ["Share", "Publish", "Upload", "Post", "Send"], timeout_s=10)])
        if pid == "substack":
            R("reply_in_thread", "Reply to the note or comment currently open in Substack.", [dict(name="text", required=True)],
              [dict(op="tap", labels=["Reply"], timeout_s=6), dict(op="type", labels=["Reply", "Write a reply", "Add a reply"], text="{text}"), dict(op="tap", labels=["Post", "Send", "Reply"], timeout_s=8)])
            R("restack", "Restack the note currently open in Substack.", [], [dict(op="tap", labels=["Restack"]), dict(op="tap", labels=["Restack", "Share"], optional=True)])
            R("open_profile", "Open a profile in Substack by name via search.", [dict(name="name", required=True)],
              [openapp, dict(op="tap", labels=["Search", "Explore"], timeout_s=6), dict(op="type", labels=["Search"], text="{name}"), dict(op="key", code=66), dict(op="sleep", ms=1500), dict(op="tap", labels=["{name}"], timeout_s=8)])
            R("read_stats", "Open Substack stats and return the visible numbers (subscribers, views).", [],
              [openapp, dict(op="tap", labels=["Dashboard", "Stats", "Profile"], optional=True), dict(op="tap", labels=["Stats", "Subscribers"], optional=True), dict(op="sleep", ms=2000), dict(op="collect", count=2)])

    # ------------------------------------------------------------------ catalogue asset (parsed by the app and tests)
    cat = dict(version=1, metricoolFreeLimit=METRICOOL_FREE_LIMIT, warmupDays=WARMUP_DAYS, fallbackCaps=FALLBACK_CAPS,
               platforms=[dict(id=p["id"], name=p["name"], app=p["app"], optional=p["tier"] == "optional", metricool=p["metricool"], caps=p["caps"],
                               skills=[p["playbook"]] + p["skills"]) for p in PLATFORMS])
    w(f"{A}/social/platforms.json", json.dumps(cat, indent=1, ensure_ascii=False) + "\n")

    # ------------------------------------------------------------------ the workspace
    def skill_map(role):
        return "\n".join(f"- {job}: {', '.join(i for i in ids_.split(',') if skill_exists(i))}" for job, ids_ in ROLE_MAP.get(role, []))

    names = ", ".join(p["id"] for p in PLATFORMS)
    common = ("You are the {title} of the Social Media Manager team (workspace \"social\"). FIRST load skills team-protocol, social-ops and social-account-safety and follow them. "
              "THEN read `save_file op=read path=social/PLATFORMS.json`: it lists the platforms the owner switched ON (possible ids: " + names + "). Work ONLY on those; for each one load `<platform>-playbook`. "
              "If the file is missing or `enabled` is empty: `notify` the owner once a day to pick platforms (Workspaces > Social Media Manager) and end with STATUS: DONE (SKIPPED - no platforms enabled). "
              "Corrections from the owner are in `social/RULES.md` and override everything older. Success is evidence, not effort. Keep replies short; save long output with save_file.")
    persist = (" PERSISTENCE: do not stop after one job. When an item is done take the next one (`board op=next`). When the board is empty create the next three highest-value items toward today's targets (the caps in each playbook are ceilings, not targets: fill them only with GOOD work, never filler). "
               "You are judged on outcomes per day (posts that landed, real conversations, results measured). If blocked on one platform, switch to another enabled platform and report the block once. Keep a one-line progress note on the board item every few steps.")
    M = dict(
        director="MISSION: run the whole social operation across the enabled platforms and keep it moving. Each run: `board op=resume`; read PLATFORMS.json, `social/rotation.json` and `engage op=today`; pick the next platforms by rotation; create board items with clear owners (title starts with the platform id); call specialists with the `agent` tool (depth 0 for you) in this order when work exists: researcher > strategist > writer > reviewer > engager/executor > analyst; wait for each answer and hand the next step on. GATES you enforce: no draft is posted before the Reviewer passed it; every draft went through `social-voice-and-slop` (experience bank + `humanizer` + the 10-point checklist); realistic AI media carries the platform's AI label (and the Metricool AI flags when Metricool is used); posting follows each playbook's capability ladder, and on the FREE Metricool plan Metricool is only the fallback (counter in `social/metricool_count.txt`, stop at 18 of 20 a month). Never do the specialists' work yourself. Weekly: write the plan as Markdown (save_file) and send the link.",
        researcher="MISSION: for each platform you are given, find what its audience cares about now: trending topics, questions people ask, accounts and posts worth engaging with, competitors' winning formats. Use web_search/web_fetch/rss_read, an Apify or Buzzfy MCP when on, and the scan routines. Output a research brief file (`social/plans/research-<date>.md`) with a section per platform and board items for the strategist and engager with concrete targets (platform, author, post, why). Cite sources; never invent numbers; mark unverified claims.",
        strategist="MISSION: turn research and results into one plan across the enabled platforms: content pillars, a week calendar (which idea goes where, in each platform's native format, via `cross-platform-repurposing`), warm-up status per platform, experiments (one change at a time), and engagement targets. Respect each playbook's caps and ramp. Output a plan file (`social/plans/<date>.md`) and board items for the writer. Use `examples op=list` and the analyst's numbers.",
        writer="MISSION: write native drafts per platform from board items (a caption is not a script is not a thread: follow the playbook's formats and `platform-specs-and-validation`). Every draft goes through `social-voice-and-slop`: use at least one real item from `social/experience_bank.md` or a checkable specific, run `humanizer` and `stop-slop`, tick the 10-point checklist. Save each draft (`social/drafts/<platform>/<date>-<slug>.md`, with caption, tags, alt text, AI-label need and the media file path) and hand it to the reviewer. Revise exactly what the reviewer asked, at most twice. Never invent personal stories.",
        reviewer="MISSION: quality gate. Score every draft with `reviewer-rubric` against the owner's examples AND the `social-voice-and-slop` 10-point checklist. Check the platform's specs, caps and warm-up state, that nothing is an ad or a false claim, that required AI labels are present and that the draft differs from the last 10 posts. Output PASS/REVISE/REJECT with scores and the exact fix in the board item. You never post and never rewrite from scratch.",
        engager="MISSION: run the engagement loop (skill `engagement-loop`) on the platforms you are given, within each platform's caps and warm-up ramp: reply to the owner's own threads, find relevant conversations, draft comments through `social-voice-and-slop`, get them reviewed, and have the executor act. Gate every action with `engage op=plan platform=<p> action=<p>.<action>`. Do not tap the screen yourself except scan/collect routines; delegate actions to the executor one job per call. Stop a platform for 24-48 h on any block prompt (social-account-safety).",
        executor="MISSION: operate the phone apps exactly as asked, one job per call. Load `screen-routines`, `fast-screen` and `human-pacing`. Use `screen_routine` routines (`<platform>.*`) instead of step-by-step taps. POSTING ROUTE: follow the platform's playbook ladder. On the FREE Metricool plan use `share_file` + `<platform>.finish_share`, then the screen composer; use Metricool (skill `metricool-posting`, counter file, AI flags, draft:true dry run first, `autoPublish:false` screen fallback) only as the fallback. Before any outward action make sure it has an `engage op=plan` id with gate AUTO or an owner approval; afterwards `engage op=done` with evidence from on_screen_now. Locked/black screen, a foreign app or a verification prompt: NEEDS_HUMAN. Never loop on a failing step.",
        analyst="MISSION: measure and learn per enabled platform. Read stats and results (stats routine, notifications, Metricool analytics `getAnalyticsDataByMetrics` when connected: reads do not count against the 20-post limit), count actions with `engage op=today`, save `social/numbers/<date>.md`, compare with the plan, and write 1-3 specific lessons with the memory tool. Check the heuristics in the playbooks (caps, ramps, best times) against the owner's own numbers and say when one looks wrong. Propose one experiment. Never make up metrics: write 'not available' when the screen did not show them.",
    )
    agents = []
    for r in ROLES:
        title = ROLE_TITLE[r]
        instr = (common.format(title=title) + persist + "\n" + M[r] + "\nSKILL MAP. Before each job `load_skill` the matching ones (1-3; use skill-router for anything not listed; each playbook lists its platform skills):\n" + skill_map(r) +
                 "\nSPEED: act fast. Prefer routines and share_file over step-by-step taps; never re-read a screen after an action; use the cheapest tool that gives evidence.")
        extra = {"engager": ["engagement-loop", "human-pacing", "fast-screen", "reply-and-comment-writer"], "executor": ["fast-screen", "screen-routines", "human-pacing", "video-publish", "metricool-posting"],
                 "reviewer": ["reviewer-rubric", "social-voice-and-slop", "humanizer"], "director": ["owner-corrections", "social-voice-and-slop", "metricool-posting"],
                 "writer": ["toolbox", "social-voice-and-slop", "humanizer", "stop-slop"], "analyst": ["toolbox", "metricool-posting"]}.get(r, [])
        agents.append(dict(id=f"social-{r}", name=f"Social {title}", instructions=instr, tools=(TOOLS[r] + EXTRA_TOOLS.get(r, "")).split(","),
                           skills=list(dict.fromkeys(["team-protocol", "skill-router", "social-ops", "social-account-safety"] + extra)), usesScreen=r == "executor"))

    head = "Workspace \"social\" (Social Media Manager). Load skills team-protocol and social-ops, read `social/PLATFORMS.json`, and work ONLY on the platforms in `enabled` (none enabled: notify the owner once to pick platforms, then STATUS: DONE as SKIPPED). Loop over the enabled platforms by rotation (max 3 per run), loading `<platform>-playbook` for each. "
    D = "social-director"

    def t(key, name, agent, trig, maxSteps, maxMin, prompt, criteria, time=None, interval=None, days=None):
        d = dict(key=key, name=name, agent=agent, trigger=trig, maxSteps=maxSteps, maxMinutes=maxMin, prompt=head + prompt, criteria=criteria)
        if time: d["time"] = time
        if interval: d["intervalMinutes"] = interval
        if days: d["days"] = days
        return d
    tasks = [
        t("heartbeat", "Social: team heartbeat", D, "INTERVAL", 100, 45, "Heartbeat: `board op=resume`, then `board op=list open_only=true`; do every item created by owner first. Then work through the open items by calling the right specialists until the board is empty or the caps of the platforms in rotation are reached. If the board is empty, create the next valuable items (engagement targets, a due draft, an overdue follow-up, a platform not served for a day). Do not start a screen job if one is already running.",
          "Open board items were advanced or the board was empty, and the result lists what was done per platform with evidence, or SKIPPED with a reason.", interval=30),
        t("prebrief", "Social: morning plan", D, "DAILY", 60, 30, "Morning plan: read yesterday's numbers, lessons, `examples op=list`, `engage op=today` and the board; write today's plan (`social/plans/<date>.md`) with 3 priorities per active platform group; create board items for the researcher, writer and engager and call them.",
          "A plan file exists for today covering every enabled platform and board items were created.", time="05:30"),
        t("research", "Social: research", "social-researcher", "DAILY", 60, 30, "Research for the platforms in rotation: today's best topics, questions and conversation targets. Save the brief and create board items for the strategist and engager.",
          "A research brief exists with a section per platform covered and board items were created.", time="07:15"),
        t("produce", "Social: production", D, "DAILY", 80, 40, "Production cycle: take the plan's due pieces for the platforms in rotation, have the writer draft each (voice gate), the reviewer score it, and queue PASSed drafts for the owner or (gate AUTO) for the executor to publish by the playbook's route. Respect the caps and the warm-up ramps. Save drafts as files.",
          "At least one reviewed draft file exists, or the run says why nothing was due.", time="09:00"),
    ]
    for hh, mm in ((10, 0), (14, 30), (19, 50), (21, 30)):
        tasks.append(t(f"engage{hh}", f"Social: engagement {hh:02d}:{mm:02d}", "social-engager", "DAILY", 100, 45, "Run one full engagement loop (skill engagement-loop) on the platforms whose turn it is, within each platform's caps and warm-up ramp. Use the executor for every screen action, one job per call.",
                       "Counts of replies, comments and likes per platform are reported with evidence, or the result says SKIPPED with the reason (cap reached, phone locked, app blocked, platform paused).", time=f"{hh:02d}:{mm:02d}"))
    tasks += [
        t("stats", "Social: daily stats and learning", "social-analyst", "DAILY", 60, 30, "Read today's results per enabled platform and the action log, save `social/numbers/<date>.md`, write lessons, propose one experiment, and pass the summary to the director.",
          "A numbers file exists for today and at least one lesson was saved (or 'nothing new' is justified).", time="20:35"),
        t("digest", "Social: owner digest", D, "DAILY", 50, 25, "Write today's digest (per platform: what was posted or done, who replied, what needs the owner, what was learned; Metricool count this month) as a file, `notify` the owner, and if whatsapp is enabled send it with the digest recipe in `whatsapp-playbook`. If nothing happened say so honestly.",
          "A digest file exists and the owner was notified.", time="21:10"),
        t("overnight", "Social: overnight drafting", "social-strategist", "DAILY", 60, 40, "Overnight, no screen: prepare tomorrow's drafts and engagement targets for the platforms in rotation from the plan and research, hand them on through the board.",
          "Drafts or targets were created, or the result says why not.", time="23:35"),
        t("weekly", "Social: weekly review and plan", D, "WEEKLY", 80, 45, "Weekly review across the enabled platforms: what worked, what did not, numbers vs plan, ramp status per platform, Metricool use vs the monthly limit, lessons. Write next week's plan (`social/plans/week-<n>.md`) and show the link. Do not start implementing until the owner says so, except the normal daily tasks.",
          "A weekly plan file exists and the review names at least one change.", time="18:05", days=["SUN"]),
    ]
    all_ids = sorted(set(SHARED) | {s for a in agents for s in a["skills"]} | {i for r in ROLE_MAP.values() for _, lst in r for i in lst.split(",") if skill_exists(i)})
    d = dict(id="social", version=1, name="Social Media Manager", platform="social", app="",
             summary="One team for all your social platforms: switch on only the ones you use. 8 agents, shared schedule, a playbook, routines and posting routes per platform, daily caps you can edit.",
             quotas=FALLBACK_CAPS, agents=agents, tasks=tasks, skills=all_ids, mcps=["Metricool"],
             memory=["Platform list: read social/PLATFORMS.json at the start of every run; work only on enabled platforms. Metricool is on the FREE plan (20 posts a month, no LinkedIn/X): fallback only. The owner digest recipe lives in whatsapp-playbook."])
    w(f"{A}/workspaces/social.json", json.dumps(d, indent=1, ensure_ascii=False) + "\n")
    return [p["playbook"] for p in PLATFORMS]
