#!/usr/bin/env python3
"""Generates the bundled workspaces, their playbook skills and screen routines into app/src/main/assets.
Run from the repo root: python3 tools/autonomous/gen_workspaces.py   (output is committed; CI tests parse every file)."""
import json, os, textwrap

A = "app/src/main/assets"
os.makedirs(f"{A}/workspaces", exist_ok=True); os.makedirs(f"{A}/routines", exist_ok=True)
DISABLED = []  # skill ids that start OFF (a workspace turns them on)

def w(path, text):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    open(path, "w").write(text)

def skill(sid, desc, body):
    DISABLED.append(sid)
    w(f"{A}/skills/{sid}/SKILL.md", f"---\nname: {sid}\ndescription: {desc}\nlicense: MIT\n---\n\n{body.strip()}\n")

# ------------------------------------------------------------------ shared skills
skill("team-protocol", "How the agent team works together: board, handoffs, status lines, evidence. Load first in every team run.", """
# Team protocol
You belong to a workspace team. Work is passed on the shared board, never by memory or guesswork.
0. Pick your skills first: load `skill-router` if unsure, then `load_skill` the 1-3 skills that fit this job (see your SKILL MAP). Work fast: for any screen work load `fast-screen`. Do not improvise what a skill already explains.
1. Start: `board op=resume` (re-opens work a dead run left half done), then `board op=next owner=<your name>`; if nothing is waiting, check `board op=list open_only=true`.
2. Items created by `owner` are replies or instructions from the owner: do them FIRST, exactly as written, and record the result. Hand work on with `board op=add` (title, detail, owner = the next agent, workspace, depth). Give the full context in `detail`: the next agent has not seen your conversation.
3. Finish every item you touch: `board op=update id=<id> status=DONE|FAILED|BLOCKED result=<what happened and where the output is>`. A result is required. Never leave an item DOING.
4. Save long outputs (plans, drafts, reports) with `save_file` (path like `<workspace>/drafts/2026-11-16-note.md`) and put the path in the result.
5. Delegation depth: the director is depth 0. Pass `depth=N` to the `agent` tool when your brief says delegation depth N. Chains stop at 3.
6. Success means evidence. DONE needs a result that names what was produced or done (a file path, an action id, counts). If nothing needed doing, say SKIPPED and why. Never claim an action happened that you did not see on screen or in a tool result.
7. Last line of your reply is exactly one of: `STATUS: DONE`, `STATUS: NEEDS_HUMAN - <what exactly>`, `STATUS: FAILED`.
8. Files the owner may have edited (plans, drafts, playbooks, routines, rules): read the CURRENT file before changing it. If it differs from what you last wrote, the owner's version wins: do not overwrite it, write your change as a new file with `-suggested` in the name and post a board item for the owner. Plans and lessons the owner edited are instructions.\n9. Never bypass a lock screen, CAPTCHA, login, 2FA, consent screen or payment: `take_over` or NEEDS_HUMAN.
""")

skill("toolbox", "Which tool to use for what: web, files, dates, images, scheduling, screen. Load when unsure how to do something.", """
# Toolbox
- **Find information:** `web_search` (no key) then `web_fetch` on the best results; `rss_read` for a blog/Substack/subreddit feed (Substack: https://NAME.substack.com/feed). If a site blocks you, use an MCP search server if one is on, or the screen.
- **Read any public page cleanly:** `web_fetch` on `https://r.jina.ai/<full url>` returns readable text for many pages that plain fetch cannot parse (articles, profiles, videos with transcripts). Public pages only.
- **Scrapers without code:** if the Apify MCP is on, search its actors for the platform (YouTube, TikTok, Instagram, Reddit, LinkedIn) and run one; confirm the actor exists before relying on it.
- **Dates:** never guess. `datetime` (now, add, diff, next_weekday) in the owner's zone.
- **Files the owner can open:** `save_file` (save, append, read, delete, list). Always show the returned link. Plans and reports: Markdown or HTML.
- **Images:** `make_card` makes a quote/announcement card PNG (square 1080x1080, portrait 1080x1350, pin 1000x1500). Photos and video need an MCP such as Higgsfield or fal.ai if the owner switched one on.
- **Scheduling:** `schedule` creates follow-ups and recurring jobs (min 15 minutes) with a self-contained prompt; `board` is for handoffs between agents, `schedule` is for later.
- **Post media fast:** `share_file` opens another app's share screen with the video/image and caption ready (skill `video-publish`). **Phone:** `screen_routine` first, `screen_operation` for one-offs, `open`/`open_uri`/`launch_app` to start apps, `notify` to tell the owner, `take_over` when a human is needed.
- **Data and code:** `http_request` for APIs, `execute_python` for calculations and file processing, `memory` for lasting lessons.
- **Skills:** `load_skill` only when needed; `create_skill` to save a repeatable method.
Prefer the cheapest tool that gives evidence. Report what you actually saw.
""")

skill("engagement-loop", "The daily engagement loop that builds an audience: find conversations, judge, comment, like, reply, within caps. Use for any engagement task.", """
# Engagement loop
Posting alone does not build an audience; conversation does. Each engagement run does ALL of these, in order, within today's caps:
1. **Own threads first.** Open notifications/activity, collect replies and comments on the owner's own content. Reply to every real one (thank-you-only replies are not enough: add a thought or a question). Own-thread replies and likes need no approval; neither do normal comments and posts.
2. **Find 15-25 relevant posts** (niche keywords from the workspace playbook, people the owner interacted with, accounts in `memory`). Use the platform's collect/scan routine. Record each candidate on the board (title = author + first words).
3. **Judge each**: is there something specific and true the owner can add? If not, skip it. Prefer posts less than a day old with room to be seen.
4. **Draft the comment** (use `reply-and-comment-writer` if available): 1-4 sentences, specific to the post, no pitch, no link, no flattery-only. Hand it to the Reviewer via the board. The Reviewer scores it against `examples list` and the rubric.
5. **Gate**: `engage op=plan` for every outward action (pass `serious=true` only for something irreversible, expensive or legally sensitive). The owner switched this workspace on, so you work alone: AUTO = act now. DENIED = daily cap reached, stop that action type for today. NEEDS_APPROVAL happens only for serious actions (direct messages, sending email, payments, deletions, job applications): leave those for the owner, who sees them as a pop-up and on the Dashboard, and carry on with other work.
6. **Act like a person**: one action at a time, then `engage op=pace` and wait that many seconds (use the `wait` tool). Then `engage op=done id=<id> note=<evidence>`.
7. **Like / restack** a few genuinely good posts (within caps). Never like or follow in bursts.
8. End with counts: replied N, commented N, liked N, drafts waiting N, skipped N and why.
""")

skill("reviewer-rubric", "Scores drafts for quality, truth and fit before anything is posted. Used by the Reviewer agent.", """
# Reviewer rubric
Run `examples op=list platform=<platform>` first. Compare the draft with GOOD and BAD examples and the owner's reasons.
Score 1-5 on each: **Specific** (a concrete detail, not a generality) · **Useful or interesting** to the target reader · **Voice** (sounds like the owner, no AI-isms, no hype) · **True** (every claim verifiable; nothing invented) · **Not an ad** (no hidden pitch, no link dump, no engagement bait) · **Fit** (the platform's norms and length).
Verdict: PASS (all >= 4), REVISE (any 3; say exactly what to change), REJECT (any <= 2, or a false/invented claim, or an ad). Write the verdict, the scores and the fix into the board item result. Never approve your own work. Never soften a REJECT.
""")

skill("owner-corrections", "How to apply a correction from the owner everywhere in one step so the same mistake does not repeat.", """
# Owner corrections
When the owner says something was wrong, bad, off-brand or "do X instead", do all of these in the same turn:
1. `examples op=correct platform=<p> text=<the bad output> reason=<their words>`.
2. Add a one-line standing rule to the workspace rulebook file with `save_file` (path `<workspace>/RULES.md`, keep the old rules, add the new line at the top).
3. Save a lesson with the `memory` tool (specific and reusable).
4. If a skill or agent instruction caused it, say which one so the owner can approve a change.
Reply with what you changed in each place. Do not just apologise.
""")

skill("human-pacing", "Rules for acting on a platform like a person so accounts are not flagged.", """
# Human pacing
Daily caps are in `engage op=today`. Never exceed them. Between actions wait `engage op=pace` seconds (default 20-90 s); between sessions leave hours. Vary what you do (a like, then a comment, then a scroll). Do not act in bursts or at machine-regular times. Stop for the day on any warning, rate-limit message, verification prompt or odd screen: report NEEDS_HUMAN. Stay logged in and in the app; never use other tools to post.
""")

skill("screen-routines", "How to drive the phone screen reliably with screen_routine and verified taps instead of step-by-step tapping.", """
# Screen routines
Prefer `screen_routine` over many `screen_operation` calls: `routine=list` shows what exists; `routine=<id> params={...}` does a whole job in one call with every tap verified (the screen must change; one retry; then a clear failure).
After every call read `on_screen_now`: success is what is on screen, not `ok`. If a routine fails: do NOT repeat it. Read the screen once with `screen_operation read`, then (a) fix the wrong label in the routine file `files/routines/<id>.json` (labels are plain text lists; add the real label), or (b) finish the job with `steps=[...]`, or (c) report FAILED with the screen text. The screen lags: when something should have appeared, use `wait` ops rather than tapping again.
The phone must be awake and unlocked. If the screen is black or shows a lock screen, wake it once (`screen_operation key code=224`), and if it stays locked report NEEDS_HUMAN.
""")

skill("skill-from-source", "Turns a GitHub repo, a document, a web page or pasted notes into a Zafiro skill and installs it.", """
# Skill from source
Goal: a skill is a short, specific instruction file the agent loads only when needed.
1. Get the source: for a GitHub repo use `http_request` on raw.githubusercontent.com (README, docs, any SKILL.md); for a page use `py_web_read`; for an uploaded document use its text.
2. Check the license (MIT/Apache/CC-BY ok; if unclear or restrictive, write the skill from the ideas in your own words and say so).
3. Distil: when to use it, required inputs, numbered steps, decision rules, what never to do. 80-250 lines at most. Keep examples short.
4. Install with the `create_skill` tool (id in lowercase-with-dashes, description <= 200 chars that says WHEN to use it, body). If the repo already contains skills (folders with SKILL.md) install each with their text, adding an `adapted_from` line.
5. Tell the owner the skill id and how to switch it on (Skills page). Do not enable skills that you did not test.
""")

skill("skill-router", "Pick the right skills before any job: a map from job type to the bundled skills that do it best. Load when you start work or when unsure which skill fits.", """
# Skill router
Skills are expert method files: using one is faster and better than working from memory. At the START of every job do this (30 seconds):
1. Name the job in a few words (write a hook, plan a week, find trends, reply to a comment, make a thumbnail, edit a video...).
2. Find the matching row below, or scan `<available_skills>` for the closest description. Load the 1-3 best skills with `load_skill` and follow their steps. Do not load more than you need; do not skip loading because you "know how".
3. If no skill fits, say so in one line and continue. If you keep repeating a method that no skill covers, save it with `create_skill`.
| Job | Load |
|---|---|
| write a caption / post / note | caption-writer, hook-writer, writing-style-and-tone, stop-slop, then the platform writer skill (tiktok-script, reels-script, linkedin-post-writer, thread-writer, text-post-and-microblog, email-and-newsletter, carousel-writer, youtube-shorts) |
| first line / hook / title | hook-writer, viral-reverse-engineering, trend-jacking |
| script a short video | short-form-video-script, scripting-and-storyboarding, tiktok-script / reels-script / youtube-shorts |
| make or edit a video / image | capcut, descript, opus-clip, captions-and-clipping, ai-video, veo-3, kling, runway, luma, heygen, synthesia, ai-voiceover, ai-music-and-sound, nano-banana, flux, ideogram, image-prompt, canva, design-and-templates, thumbnail-design, quote-cards-and-text-graphics, infographic-and-data-viz, ai-image-editing |
| publish / schedule | tiktok-video-publishing, instagram-reels-publishing, youtube-publishing-and-metadata, scheduling-and-queue, platform-specs-and-validation, video-publish |
| plan strategy / calendar / pillars | social-strategy, content-pillars, content-calendar, batch-content-plan, campaign-and-launch-planning, goals-and-kpis, idea-generation-and-ideation |
| research audience / competitors / trends | audience-research, competitor-analysis, content-research-and-sourcing, deep-research, viral-reverse-engineering, mkt-competitor-profiling |
| reply / comment / community | reply-and-comment-writer, engagement-routine, community-management, collabs-and-cross-promotion, crisis-and-moderation, ugc-and-influencer |
| search visibility | social-seo, instagram-seo, pinterest-seo, ai-search-optimization, hashtag-strategy, mkt-seo-audit |
| analytics / experiments | analytics-and-reporting, mkt-analytics, experimentation-and-ab-testing, content-audit, data-statistical-analysis, data-create-viz |
| grow an account on one platform | tiktok-growth, instagram-growth, x-growth, linkedin-growth, threads-growth, pinterest-growth, reddit-marketing, facebook-strategy, facebook-groups, youtube-long-form |
| outreach / sales / DMs | social-selling-and-dm, mkt-cold-email, mkt-prospecting, draft-outreach style skills, mkt-sales-enablement |
| monetise | creator-monetization, lead-magnets-and-funnels, link-in-bio-and-traffic, mkt-offers, mkt-pricing |
| brand voice | brand-profile, voice-builder, writing-style-and-tone, smc-voice-builder, smc-newsletter-voice |
| operate the phone | fast-screen, screen-routines, human-pacing, phone-use |
| build a plan or check your own work | sp-writing-plans, sp-executing-plans, sp-verification-before-completion, sp-systematic-debugging |
| make a new skill from a repo or doc | skill-from-source, install-skill-from-github, skill-creator |
Also check any MCP tools that are switched on (their names appear in your tool list): an MCP that does the job (search, scrape, image/video generation, scheduling, CRM) is better than doing it by hand on the screen.
""")

skill("fast-screen", "Speed rules for driving the phone screen: compact reads, bare-number taps, no re-reads, routines, share_file. Load before any screen work.", """
# Fast screen work
Every screen step costs time and money, and old steps are re-billed on every later step. Work like this:
1. **One call, one change.** Each `screen_operation` tap/type/scroll already returns the NEW screen. Never call `read` right after an action. `read` only for the first look.
2. **Tap by number.** Use the number in [brackets] as `token` (`token:"12"`). Lines look like `[12]btn "Post" tap`. A line ending `edit` is a text field (use `set_text`). `format:"full"` exists but costs 5x more: only if the compact list hides what you need.
3. **Long screens:** if it says "N more nodes hidden", use `operation:"search"` with keywords, or scroll. Do not read the same screen twice.
4. **If the result says "screen content unchanged"** your action did nothing: do not repeat it; try another element, scroll, wait 1-2 s, or report.
5. **Routines first.** `screen_routine routine=list`. A routine does 5-30 steps in one call and verifies each tap. For a new multi-step job write the steps once with `screen_routine steps=[...] save_as="<platform>.<job>" params={...}`: if it succeeds it is saved and next time costs almost nothing.
6. **Media goes by sharing, not by browsing.** To post a video or image use `share_file` (opens the target app's share screen with the file and caption ready), then only tap Next/Post. Never scroll a gallery to find a file.
7. **Open apps directly** with `open`/`launch_app`/`open_uri` (deep links such as whatsapp://send?phone=..&text=..). Never navigate the launcher.
8. **Do not screenshot** unless the compact list is empty or the screen is a canvas/game. Screenshots are expensive; the tree is enough.
9. **Wait smartly.** Use a routine `wait` op with labels (returns as soon as the label appears) instead of fixed sleeps.
10. **Stop loops.** The same failing action twice means change approach; three failed attempts means report FAILED with the screen text.
""")

skill("video-publish", "Post a video (or image) with a caption to TikTok, Instagram, YouTube, Facebook, Reddit and others fast: prepare once, share_file, finish with a routine, verify. Use for any 'post this video' job.", """
# Publish a video fast
Goal: one finished video + per-platform captions posted to several apps in minutes, with minimal tokens.
**Prepare once (no screen):**
1. The video must be a file under Zafiro's files folder, e.g. `outputs/clip.mp4` (save or download it first). If you also need a thumbnail or cover, make it now (`make_card`).
2. Write each platform's caption separately (load `caption-writer`, `hashtag-strategy`, `platform-specs-and-validation`). Save them as `outputs/captions.json` so they survive a failure. Keep them native: different hook, length, tags per platform; Reddit needs a title and a subreddit that allows the post (read its rules first with `web_fetch` on r.jina.ai).
3. `engage op=plan` for each post (action `post`). Respect the answer (AUTO / DENIED / NEEDS_APPROVAL).
**For each platform (one at a time, the screen is shared):**
4. `share_file package=<app package> path=outputs/clip.mp4 text=<that platform's caption>`. The app opens on its share/create screen with the video attached and the caption copied to the clipboard.
5. `screen_routine routine=<platform>.finish_share params={text:<caption>}` taps Next/Continue until the caption screen, puts the caption in the caption field (paste or type), and taps Post/Share. If the routine does not exist or fails, read the screen ONCE and continue by hand with fast-screen rules, then save what worked with `save_as`.
6. Verify from the result's `on_screen_now`: a success banner, the post in the profile, or "posting"/"uploading" progress. If it says uploading, `wait` for completion before leaving the app. Then `engage op=done id=<id> note=<evidence>`.
7. Pace: `engage op=pace`, then the next platform.
**App packages:** TikTok `com.zhiliaoapp.musically` (some regions `com.ss.android.ugc.trill`), Instagram `com.instagram.android`, YouTube `com.google.android.youtube`, Facebook `com.facebook.katana`, Reddit `com.reddit.frontpage`, X `com.twitter.android`, LinkedIn `com.linkedin.android`, Threads `com.instagram.barcelona`, Pinterest `com.pinterest`.
**Honest limits:** not every app accepts a shared video straight into its composer (Instagram asks Reel/Story/Feed; Reddit asks for a community and title; Substack's app has no video share, so for Substack post text with a link to the video). The routine labels are best-effort guesses for the English UI and need a first-run fix. Say plainly what actually appeared on screen. Never report a post as done without seeing it.
""")

# ------------------------------------------------------------------ platform data
ROLES = ["director", "researcher", "strategist", "writer", "reviewer", "engager", "executor", "analyst"]
ROLE_TITLE = dict(director="Director", researcher="Researcher", strategist="Strategist", writer="Writer", reviewer="Reviewer", engager="Engager", executor="Executor", analyst="Analyst")
SUBSTACK_IDS = dict(director=("growth_director", "Growth Director"), researcher=("audience_researcher", "Audience Researcher"), strategist=("editorial_strategist", "Editorial Strategist"),
                    writer=("content_producer", "Content Producer"), reviewer=("quality_reviewer", "Quality Reviewer"), engager=("community_builder", "Community Builder"),
                    executor=("android_assistant", "Android Execution Assistant"), analyst=("analytics_analyst", "Analytics Analyst"))

TOOLS = dict(
    director="agent,board,engage,examples,save_file,memory,load_skill,notify,device_info,wait,execute_python,http_request,datetime,schedule,web_search",
    researcher="web_search,web_fetch,rss_read,datetime,http_request,load_skill,memory,board,save_file,execute_python",
    strategist="load_skill,memory,board,save_file,examples,datetime,web_search,web_fetch,rss_read,http_request,execute_python",
    writer="load_skill,board,save_file,examples,memory,make_card,datetime,web_search,web_fetch",
    reviewer="examples,engage,board,load_skill,memory,web_fetch,save_file",
    engager="screen_routine,screen_operation,load_skill,engage,board,agent,memory,wait,web_search,web_fetch",
    executor="screen_routine,screen_operation,share_file,screenshot,open,screens,engage,board,wait,take_over,device_info,clipboard,load_skill,open_uri,launch_app,save_file,http_request,execute_python",
    analyst="screen_routine,save_file,memory,board,load_skill,execute_python,notify,engage,web_fetch,datetime",
)

P = [
 dict(id="substack", name="Substack", app="com.substack.app", kind="Notes and posts", can_post=True, off=0, enabled_default=True,
      quotas="note=6,post=1,comment=15,reply=40,like=30,restack=10,follow=10,dm=5",
      skills=["social-strategy", "reply-and-comment-writer", "engagement-routine", "email-and-newsletter", "text-post-and-microblog", "content-pillars", "content-calendar", "audience-research", "competitor-analysis", "writing-style-and-tone", "voice-builder", "stop-slop"],
      facts="Substack has two surfaces: Notes (short posts in a feed, replies, restacks, likes) and long posts (newsletter, comments). Audience growth on Substack comes mostly from Notes and from thoughtful comments on other writers' posts and notes, plus recommendations from other publications. Notes that are only links tend to travel less. A restack shares someone's note or post with your followers (add your own thought when you can). The Activity tab shows replies, likes, new subscribers and restacks. The owner's publication: tricreta.substack.com (Android developer; websites, AI features, dashboards, design/video). Content pillars come from the owner's content plan; WhatsApp/M-Pesa/bot topics are at most 1 in 6 pieces.",
      look="Substack",
      labels=dict(home=["Home"], notes=["Notes"], compose=["New note", "Write a note", "Create", "What's on your mind", "Start a note"], field=["What's on your mind", "Say something", "Write a note", "Start writing"], post=["Post", "Publish"], comment=["Comment", "Reply"], send=["Post", "Send", "Reply"], like=["Like"], restack=["Restack"], activity=["Activity", "Notifications", "Inbox"], profile=["Profile"])),
 dict(id="x", name="X (Twitter)", app="com.twitter.android", kind="posts and replies", can_post=True, off=7, enabled_default=False,
      quotas="post=4,comment=15,reply=30,like=30,follow=8,dm=3",
      skills=["x-growth", "thread-writer", "reply-and-comment-writer", "engagement-routine", "hook-writer", "trend-jacking", "social-strategy", "writing-style-and-tone"],
      facts="WARNING: the owner's @tricreta account was permanently suspended (read-only banner seen 2026-11-13). Do not appeal or create accounts unasked. Keep this workspace off until the owner says the account is restored. When active: short posts, threads, replies to larger accounts in the niche are the main growth lever.",
      look="X",
      labels=dict(home=["Home"], notes=["For you", "Following"], compose=["Post", "Compose", "What is happening", "What's happening"], field=["What is happening", "What's happening", "Post text"], post=["Post"], comment=["Reply", "Post your reply"], send=["Reply", "Post"], like=["Like"], restack=["Repost"], activity=["Notifications"], profile=["Profile"])),
 dict(id="linkedin", name="LinkedIn", app="com.linkedin.android", kind="posts, comments and connection messages", can_post=True, off=13, enabled_default=True,
      quotas="post=1,comment=12,reply=25,like=25,follow=5,dm=5",
      skills=["linkedin-growth", "linkedin-post-writer", "linkedin-company-pages", "profile-optimization", "reply-and-comment-writer", "social-selling-and-dm", "engagement-routine", "social-strategy"],
      facts="LinkedIn rewards useful comments on other people's posts and consistent posting with a clear point of view. LinkedIn's terms prohibit third-party automation tools; this workspace only drives the official app on the owner's phone at human pace with low caps. Connection requests and DMs are always drafts for the owner to approve. No fake claims about experience.",
      look="LinkedIn",
      labels=dict(home=["Home"], notes=["Home"], compose=["Start a post", "Post", "Create a post"], field=["What do you want to talk about", "Share your thoughts", "Start a post"], post=["Post"], comment=["Comment"], send=["Post", "Send"], like=["Like"], restack=["Repost"], activity=["Notifications"], profile=["Me", "Profile"])),
 dict(id="instagram", name="Instagram", app="com.instagram.android", kind="reels, carousels, stories and comments", can_post=False, off=19, enabled_default=True,
      quotas="post=1,comment=15,reply=30,like=30,follow=8,dm=4",
      skills=["instagram-growth", "instagram-reels-publishing", "instagram-seo", "reels-script", "carousel-writer", "caption-writer", "hashtag-strategy", "reply-and-comment-writer", "canva", "capcut", "engagement-routine"],
      facts="Instagram growth comes from Reels (reach), carousels (saves and shares), consistent Stories, and real comments on accounts in the niche. Publishing: prepare the video, caption and hashtags as files, then use share_file (skill video-publish) to open Instagram's share screen with the video attached and finish with the instagram.finish_share routine (its labels may need a first-run fix: look at the screen once and correct the routine file). Instagram asks Reel/Story/Feed after sharing: pick Reel for video. Comments and replies run through the engagement loop.",
      look="Instagram",
      labels=dict(home=["Home"], notes=["Home"], compose=["Create", "New post", "+"], field=["Write a caption", "Add a caption"], post=["Share", "Post"], comment=["Comment", "Add a comment"], send=["Post", "Send"], like=["Like"], restack=["Share"], activity=["Activity", "Notifications"], profile=["Profile"])),
 dict(id="facebook", name="Facebook", app="com.facebook.katana", kind="page posts, group posts and comments", can_post=True, off=25, enabled_default=True,
      quotas="post=2,comment=10,reply=25,like=25,dm=3",
      skills=["facebook-strategy", "facebook-groups", "caption-writer", "reply-and-comment-writer", "community-management", "engagement-routine", "social-strategy"],
      facts="Facebook rewards native posts, video, and active groups. Group posting must follow each group's rules and admin approval. Never spam groups; one genuinely helpful comment beats a drop of links. Page management may be in the separate Pages Manager app (com.facebook.pages.app).",
      look="Facebook",
      labels=dict(home=["Home"], notes=["Home"], compose=["What's on your mind", "Create post", "Write something"], field=["What's on your mind", "Write something"], post=["POST", "Post"], comment=["Comment", "Write a comment"], send=["Send", "Post"], like=["Like"], restack=["Share"], activity=["Notifications"], profile=["Menu", "Profile"])),
 dict(id="tiktok", name="TikTok", app="com.zhiliaoapp.musically", kind="videos and comments", can_post=False, off=31, enabled_default=True,
      quotas="post=1,comment=15,reply=25,like=30,follow=8",
      skills=["tiktok-growth", "tiktok-script", "tiktok-video-publishing", "tiktok-photo-mode", "short-form-video-script", "hook-writer", "capcut", "hashtag-strategy", "trend-jacking", "engagement-routine"],
      facts="TikTok growth depends on the first 2 seconds, watch-through rate and trend/sound fit. The team writes scripts, shot lists, captions and hashtags and queues them as files; video creation uses the owner's tools (CapCut, generators); publish with share_file (skill video-publish) and the tiktok.finish_share routine (labels may need a first-run fix). Never claim a post went live without seeing it on screen. Some regions use the package com.ss.android.ugc.trill.",
      look="TikTok",
      labels=dict(home=["Home"], notes=["For You", "Following"], compose=["Create", "+"], field=["Describe your post", "Add description"], post=["Post"], comment=["Add comment", "Comment"], send=["Post", "Send"], like=["Like"], restack=["Share"], activity=["Inbox", "Activity"], profile=["Profile"])),
 dict(id="youtube", name="YouTube", app="com.google.android.youtube", kind="shorts, videos and comment replies", can_post=False, off=37, enabled_default=True,
      quotas="post=1,comment=10,reply=30,like=20",
      skills=["youtube-long-form", "youtube-shorts", "youtube-publishing-and-metadata", "thumbnail-design", "scripting-and-storyboarding", "reply-and-comment-writer", "community-management", "seo-for-youtube" if False else "social-seo"],
      facts="YouTube growth depends on titles, thumbnails and retention. The team plans topics, writes scripts, titles, descriptions, chapters and thumbnail briefs, and replies to comments on the owner's videos. Uploading uses YouTube Studio (com.google.android.apps.youtube.creator); the final publish stays with the owner until a verified routine exists.",
      look="YouTube",
      labels=dict(home=["Home"], notes=["Shorts", "Subscriptions"], compose=["Create", "+"], field=["Add a comment", "Title"], post=["Upload", "Post"], comment=["Comments", "Add a comment"], send=["Send", "Reply"], like=["like this video", "Like"], restack=["Share"], activity=["Notifications"], profile=["You"])),
 dict(id="reddit", name="Reddit", app="com.reddit.frontpage", kind="comments and posts in relevant subreddits", can_post=True, off=43, enabled_default=True,
      quotas="post=1,comment=8,reply=15,like=20",
      skills=["reddit-marketing", "reply-and-comment-writer", "community-management", "engagement-routine", "audience-research"],
      facts="Reddit punishes self-promotion. Contribute first (answers, insight), follow each subreddit's rules (read them before posting), keep promotion under 10%, never post the same text in several subreddits, and expect new or low-karma accounts to be filtered. Reddit has no web fallback from the phone (reddit.com JSON/RSS return 403), so rounds drive the app.",
      look="Reddit",
      labels=dict(home=["Home"], notes=["Home", "Popular"], compose=["Create", "Create post", "+"], field=["Title", "Body text", "Add a comment"], post=["Post"], comment=["Add a comment", "Comment"], send=["Reply", "Comment", "Post"], like=["Upvote"], restack=["Share"], activity=["Inbox"], profile=["Profile"])),
 dict(id="threads", name="Threads", app="com.instagram.barcelona", kind="short posts and replies", can_post=True, off=49, enabled_default=True,
      quotas="post=4,comment=15,reply=30,like=30,follow=8",
      skills=["threads-growth", "threads-post", "text-post-and-microblog", "reply-and-comment-writer", "engagement-routine", "hook-writer"],
      facts="Threads rewards conversational short posts and replies to active accounts. Treat it like a stream of small conversations: ask a real question, add a real detail, reply to every reply.",
      look="Threads",
      labels=dict(home=["Home"], notes=["For you", "Following"], compose=["New thread", "Start a thread", "What's new", "Create"], field=["What's new", "Start a thread"], post=["Post"], comment=["Reply"], send=["Post", "Reply"], like=["Like"], restack=["Repost"], activity=["Activity"], profile=["Profile"])),
 dict(id="pinterest", name="Pinterest", app="com.pinterest", kind="pins and boards", can_post=False, off=55, enabled_default=True,
      quotas="post=5,like=20,follow=10",
      skills=["pinterest-growth", "pinterest-pin-design", "pinterest-seo", "canva", "caption-writer", "social-seo"],
      facts="Pinterest is a search engine: pin titles, descriptions and board names carry keywords; fresh pins to the same URL beat repeats. The team prepares pin images (files), titles, descriptions and target boards; publishing is the owner's tap until a routine is verified.",
      look="Pinterest",
      labels=dict(home=["Home"], notes=["Home"], compose=["Create", "Pin", "+"], field=["Add a title", "Tell everyone what your Pin is about"], post=["Publish", "Save"], comment=["Add a comment"], send=["Post", "Send"], like=["React", "Like"], restack=["Save"], activity=["Notifications", "Updates"], profile=["Saved", "Profile"])),
 dict(id="email", name="Email (Gmail)", app="com.google.android.gm", kind="inbox triage, drafts and follow-ups", can_post=False, off=61, enabled_default=True,
      quotas="reply=30,dm=10",
      skills=["mkt-cold-email", "mkt-emails", "email-and-newsletter", "mkt-email-sequence" if False else "marketing-email-sequence", "social-selling-and-dm", "customer-support-draft-response", "inbox-triage" if False else "productivity-task-management"],
      facts="Email work: triage the inbox, draft replies, follow up on leads and applications, and read notifications. The owner's rule: drafts are typed but NOT sent unless a message type is pre-approved; cold outreach is never bulk. Gmail search on this phone: set_text works in the search field but IME Enter does not submit, tap the keyboard search key. When the phone is locked, `dumpsys notification` via rish can read Gmail notification text.",
      look="Gmail",
      labels=dict(home=["Inbox"], notes=["Inbox", "Primary"], compose=["Compose"], field=["Compose email", "Subject", "To"], post=["Send"], comment=["Reply"], send=["Send"], like=["Star"], restack=["Forward"], activity=["Inbox"], profile=["Search in mail"])),
 dict(id="whatsapp", name="WhatsApp", app="com.whatsapp.w4b", kind="the daily digest and message drafts", can_post=False, off=67, enabled_default=True,
      quotas="dm=10,reply=20",
      skills=["social-selling-and-dm", "customer-support-draft-response", "community-management"],
      facts="WhatsApp Business (com.whatsapp.w4b) is the owner's channel. Daily digest recipe that works when the phone is unlocked: write the report file, open_uri whatsapp://send?phone=<number>&text=<urlencoded>, wait about 10 s, find and tap Send via the accessibility tree, verify the Sent tick. Reports must be well formatted: bold title (*...*), numbered bold sections (What ran / failed and why / done with counts / fixed / plan / needs from you), '- ' bullets, blank lines, newlines as %0A. Lead messages are typed as drafts and NOT sent: the owner reviews and taps send. Never message strangers in bulk.",
      look="WhatsApp",
      labels=dict(home=["Chats"], notes=["Chats"], compose=["New chat", "Message"], field=["Message", "Type a message"], post=["Send"], comment=["Reply"], send=["Send"], like=["React"], restack=["Forward"], activity=["Chats"], profile=["Settings"])),
 dict(id="jobhunt", name="Careers (Kenya-first)", app="com.google.android.gm", kind="applications, outreach drafts and the tracker", can_post=False, off=73, enabled_default=True,
      quotas="post=15,dm=10,reply=20",
      skills=["job-sources-kenya", "job-eligibility-gate", "job-application-pipeline", "humanizer", "mkt-cold-email", "social-selling-and-dm", "linkedin-growth", "profile-optimization", "deep-research", "productivity-task-management"],
      facts="Mission: remote and contract work, and Kenyan employers, for an Android developer who also does full-stack web and AI automation, based in Nairobi (UTC+3). Only channels that genuinely hire Kenyans (skill job-sources-kenya). EVERY job passes the eligibility gate BEFORE a draft is written (skill job-eligibility-gate): remote boards overstate who can apply (a RemoteOK sample was 64% US-only). Realistic yield is a few genuinely eligible roles a day: scan continuously, apply fast to every eligible match, spend the rest of the effort on high-touch applications; never pad with ineligible ones. NEVER auto-submit: drafts queue to the owner (first applications and anything for a Tier A employer need approval). NEVER fabricate experience, clients, metrics or credentials; use only the owner's approved profile file. Any fee, processing payment, M-Pesa registration, WhatsApp-only task or cheque offer is a scam: hard fail. Log every source, eligibility verdict and outcome in applications/tracker.csv so real response rates exist after a month.",
      look="Gmail",
      labels=dict(home=["Inbox"], notes=["Inbox"], compose=["Compose"], field=["Compose email", "Subject"], post=["Send"], comment=["Reply"], send=["Send"], like=["Star"], restack=["Forward"], activity=["Inbox"], profile=["Search in mail"])),
]


# ------------------------------------------------------------------ share inbox and learning loop (shared skills)
skill("share-inbox", "Get a generated image, video, audio or text out of ChatGPT, Gemini, Flow, ElevenLabs or Chrome into Zafiro with the Android Share sheet, verify it, and use it. Use whenever a file made in another app is needed.", """
# Share to Zafiro (the inbox)
Zafiro appears in the Android share sheet as **Zafiro inbox**. Anything shared to it is copied into `outputs/inbox/` (no storage permission, no guessing which file is newest) and listed by the `inbox` tool. Phone behaviour of this path is UNVERIFIED until the owner confirms it: if a step does not match the screen, say so, use the fallback below, and record the real label as a lesson.
## Steps
1. **Mark the time.** `inbox op=list limit=1`: remember the newest `time` (use 0 if the inbox is empty). Never rely on "the latest file".
2. **Generate** in the app (ChatGPT, Gemini, Flow, ElevenLabs) and wait until the result is fully shown.
3. **Open the share sheet** on the result:
   - ChatGPT / Gemini image: tap the image to open it full screen, then the share icon (arrow out of a box or three dots, then Share). Long-press on the image also offers Share in many versions.
   - Flow (video): open the finished clip, then the download or share icon, then Share.
   - ElevenLabs / audio: the audio file's share or download menu, then Share.
   - Chrome: long-press an image, choose Share image. For a page or text: three dots menu, Share, or select text and tap Share.
   Use `screen_operation` read to find the share icon by its label; do not tap by position.
4. **Pick the target.** In the share sheet choose **Zafiro inbox**. If it is not in the first row, scroll the app row or tap More. A short "Saved to the Zafiro inbox" message appears; the screen returns to the source app.
5. **Verify.** `inbox op=list since=<time from step 1> kind=image|video|audio|text`. Require: the item exists, `kind` is right, `size` is plausible (an image under 10 KB or a video under 100 KB is probably a thumbnail or an error page), and for images open it with `view_image` on its `path`. Several items: take the one whose `source` is the app you used.
6. **Use it.** `inbox op=take id=<id> to=outputs/<workspace>/` moves it where you need it (stays inside Zafiro's files folder) and returns the new path. Then continue (edit, `share_file`, publish). `inbox op=delete id=<id>` for rejects. Never publish before you have looked at the file.
## Rules
- Share only content you generated or the owner owns. Respect each app's terms: ChatGPT's terms ban programmatic extraction of output, so keep human pacing, one account, and prefer Gemini first. Generated media carries provenance marks: never strip them and set the AI flag when posting.
- Stop with `take_over` on CAPTCHA, login or consent screens.
- Text shared to the inbox becomes a .txt file you can read with `inbox op=list kind=text` then `take`.
## Fallback (if the share sheet has no Zafiro inbox or the app blocks sharing)
Use the MediaStore or shell route from `content-factory-screen`: note the time before generating, then list images or videos added after that time (`content query --uri content://media/external/images/media --projection _display_name:date_added --sort "date_added DESC"` or `ls -t /sdcard/Pictures /sdcard/Download`), copy the first new one into `outputs/`, and verify size and that it decodes.
""")

skill("learning-loop", "How the team learns from measured results: when to record outcomes, how to ask the bandit for the next experiment, how to add and use lessons, and the evidence rule. Load before any post, application or weekly review.", """
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
""")

for _r in ("director", "strategist", "writer", "engager", "analyst"):
    TOOLS[_r] += ",outcomes,lessons"
TOOLS["executor"] += ",inbox"

BASE_MAP = dict(
    director=[("plan the day or week", "social-strategy,content-calendar,batch-content-plan,goals-and-kpis"), ("before you say a day is done", "sp-verification-before-completion")],
    researcher=[("trends, competitors, audience", "audience-research,competitor-analysis,content-research-and-sourcing,deep-research,viral-reverse-engineering")],
    strategist=[("pillars, calendar, experiments", "content-pillars,content-calendar,social-strategy,experimentation-and-ab-testing,campaign-and-launch-planning,idea-generation-and-ideation")],
    writer=[("any caption, post or script", "caption-writer,hook-writer,writing-style-and-tone,stop-slop,voice-builder"), ("video or image copy", "short-form-video-script,scripting-and-storyboarding,thumbnail-design,quote-cards-and-text-graphics")],
    reviewer=[("every review", "reviewer-rubric,stop-slop,platform-specs-and-validation")],
    engager=[("every loop", "engagement-loop,reply-and-comment-writer,community-management,human-pacing"), ("hostile or tricky comments", "crisis-and-moderation")],
    executor=[("any screen job", "fast-screen,screen-routines,human-pacing"), ("post a video or image", "video-publish,platform-specs-and-validation")],
    analyst=[("numbers and reporting", "analytics-and-reporting,goals-and-kpis,mkt-analytics"), ("experiments", "experimentation-and-ab-testing,content-audit")],
)
for _r in ("director", "strategist", "writer", "engager", "analyst"):
    BASE_MAP[_r].append(("record results, pick the next experiment, use lessons", "learning-loop"))
BASE_MAP["executor"].append(("fetch a file made in another app (share sheet, then verify)", "share-inbox"))
PLATFORM_KEYS = dict(
    writer=("writer", "script", "caption", "carousel", "thread", "hook", "story", "newsletter", "microblog", "post", "copy", "email-and"),
    strategist=("growth", "strategy", "seo", "calendar", "pillars", "trend", "audience", "profile"),
    researcher=("seo", "trend", "competitor", "audience", "research"),
    engager=("comment", "reply", "community", "engagement", "groups", "dm", "selling", "support"),
    executor=("publishing", "scheduling", "specs", "capcut", "canva", "photo"),
    director=("growth", "strategy"),
)

def skill_exists(sid):
    return os.path.isdir(f"{A}/skills/{sid}")

def skill_map(p, role):
    rows = []
    for job, ids in BASE_MAP.get(role, []):
        rows.append((job, [i for i in ids.split(",") if skill_exists(i)]))
    keys = PLATFORM_KEYS.get(role, ())
    extra = [i for i in p["skills"] if any(k in i for k in keys) and skill_exists(i)]
    if extra:
        rows.append((f"{p['name'].split(' (')[0]} specifics", extra))
    return rows

def map_ids(p):
    out = set()
    for role in ROLES:
        for _, ids in skill_map(p, role):
            out.update(ids)
    return out

def render_map(p, role):
    rows = skill_map(p, role)
    lines = "\n".join(f"- {job}: {', '.join(ids)}" for job, ids in rows if ids)
    return ("SKILL MAP. Before each job, `load_skill` the matching ones (1-3, do not skip, do not improvise what a skill explains; use skill-router for anything not listed):\n" + lines) if lines else ""

# ------------------------------------------------------------------ roles
def instr(p, role, ids):
    n, app, kind = p["name"], p["app"], p["kind"]
    ws = p["id"]
    common = (f"You are the {ROLE_TITLE[role]} of the {n} team (workspace \"{ws}\"). Working area: {kind}. FIRST load skill team-protocol and the skill {ws}-playbook; follow them. "
              f"Read the workspace rules in `{ws}/RULES.md` (use `save_file op=list dir={ws}` and execute_python to read it) if it exists: it overrides older notes. "
              f"Success is evidence, not effort. Keep replies short; save long output with save_file.")
    d = dict(
        director=f"MISSION: run the whole {n} operation and keep it moving until the owner stops it. Each run: `board op=resume`; read the board and today's counts (`engage op=today`); decide the next most valuable actions toward audience growth and results; create board items with clear owners; call the specialists with the `agent` tool (depth 0 for you) in this order when work exists: researcher > strategist > writer > reviewer > engager/executor > analyst. Wait for each answer, read its result, and hand the next step on. Run background calls in parallel only for non-screen work. Never do the specialists' work yourself. Stop only when the board has no open items or a daily cap is reached; then write the day's summary and notify the owner if something needs them. Weekly: write the plan as a Markdown file with save_file and send the link.",
        researcher=f"MISSION: find what the {n} audience cares about now: trending topics, questions people ask, accounts and posts worth engaging with, competitors' winning formats. Use py_web_read/http_request and, on screen, the scan routine. Output: a research brief file (save_file) plus board items for the strategist and engager with concrete targets (author, post, why it matters). Cite sources; never invent numbers.",
        strategist=f"MISSION: turn research and results into a plan: content pillars, a week calendar with formats and times, experiments (one change at a time), and engagement targets. Output a plan file (save_file, Markdown or HTML) and board items for the writer. Use `examples op=list` and the analyst's last numbers. The owner reads the plan before big changes.",
        writer=f"MISSION: write {kind} in the owner's voice, from board items. One idea per piece, concrete detail, no hype, no AI-isms, no invented facts. Save each draft with save_file and hand it to the reviewer through the board. Revise exactly what the reviewer asked, at most twice.",
        reviewer=f"MISSION: quality gate. Score every draft with skill reviewer-rubric against the owner's examples. Output PASS/REVISE/REJECT with scores and the exact fix in the board item. Also check platform rules and that nothing is an ad or a false claim. You never post and never rewrite from scratch.",
        engager=f"MISSION: run the engagement loop on {n} (skill engagement-loop): reply to the owner's own threads, find relevant conversations, draft comments, get them reviewed, and have the executor act within caps. Do not tap the screen yourself except for scan/collect routines; delegate actions to the executor one job per call.",
        executor=f"MISSION: operate the {n} app ({app}) on the phone, exactly as asked, one job per call. Load skill screen-routines and human-pacing first. Use screen_routine routines ({ws}.*) rather than step-by-step taps. Before any outward action, make sure it has an `engage op=plan` id with gate AUTO or an owner approval. After acting, `engage op=done` with evidence taken from on_screen_now. If the screen is locked, black, in a foreign app, or a verification appears: report NEEDS_HUMAN. Never loop on a failing step.",
        analyst=f"MISSION: measure and learn. Read {n} stats and results (stats routine or notifications), count actions from the action log (`engage op=today`), save a daily numbers file, compare with the plan, and write 1-3 specific lessons with the memory tool. Propose one experiment. Send the daily digest summary to the director. Never make up metrics: write 'not available' when the screen did not show them.",
    )[role]
    persist = (" PERSISTENCE: do not stop after one job. When an item is done, immediately take the next one (`board op=next`). When the board is empty, create the next three highest-value items toward today's targets (the daily caps in the playbook are your targets: work toward filling them with GOOD work, never with filler), then do them. "
               "You are judged on outcomes per day (posts that landed, real conversations, results measured), not on the number of steps. If you are blocked, switch to another useful job instead of waiting, and report the block once. Keep a one-line progress note on the board item every few steps so the owner can watch you.")
    return common + persist + "\n" + d + "\n" + render_map(p, role) + "\nSPEED: act fast. Prefer routines and share_file over step-by-step taps; never re-read a screen after an action (the action already returns it); use the cheapest tool that gives evidence."

def tasks_for(p, ids):
    ws, n, off = p["id"], p["name"], p["off"]
    def t(h, m):
        m = (m + off) % 60; h = (h + (m < off % 60 and 0)) % 24
        return f"{h:02d}:{m:02d}"
    head = f"Workspace \"{ws}\". Load skill team-protocol and {ws}-playbook. "
    T = []
    T.append(dict(key="heartbeat", name=f"{n}: team heartbeat", agent=ids["director"][0], trigger="INTERVAL", intervalMinutes=30, maxSteps=100, maxMinutes=45,
                  prompt=head + "Heartbeat: first run `board op=resume`, then `board op=list open_only=true` and do every item created by owner before anything else (owner replies such as approvals or instructions). Then work through the open board items by calling the right specialists until the board is empty or today's caps are reached. If the board is empty, create the next valuable items (engagement targets, a draft that is due, an overdue follow-up) and run them. Do not start a screen job if one is already running; the screen is shared.",
                  criteria="Open board items were advanced or the board was empty, and the result lists what was done with evidence or says SKIPPED with a reason."))
    T.append(dict(key="prebrief", name=f"{n}: morning plan", agent=ids["director"][0], trigger="DAILY", time=t(5, 30), maxSteps=60, maxMinutes=30,
                  prompt=head + "Morning plan: read yesterday's numbers, lessons and `examples op=list`; check `engage op=today` and the board; write today's plan file (save_file path `" + ws + "/plans/<date>.md`) with 3 priorities; create board items for the researcher, writer and engager; call them.",
                  criteria="A plan file exists for today and board items were created."))
    T.append(dict(key="research", name=f"{n}: research", agent=ids["researcher"][0], trigger="DAILY", time=t(7, 15), maxSteps=60, maxMinutes=30,
                  prompt=head + "Research: find today's best topics, questions and conversation targets. Save a brief file and create board items for the strategist and engager.",
                  criteria="A research brief file exists and board items were created for the next agents."))
    if p["can_post"] or ws in ("instagram", "tiktok", "youtube", "pinterest"):
        T.append(dict(key="produce", name=f"{n}: production", agent=ids["director"][0], trigger="DAILY", time=t(9, 0), maxSteps=80, maxMinutes=40,
                      prompt=head + "Production cycle: take the plan's due pieces, have the writer draft each, the reviewer score it, and queue PASSed drafts for the owner or (if the engage gate is AUTO) for the executor to publish. Save drafts as files.",
                      criteria="At least one reviewed draft file exists, or the run says why nothing was due."))
    for hh, mm in ((10, 0), (14, 30), (19, 50), (21, 30)):
        T.append(dict(key=f"engage{hh}", name=f"{n}: engagement {hh:02d}:{mm:02d}", agent=ids["engager"][0], trigger="DAILY", time=t(hh, mm), maxSteps=100, maxMinutes=45,
                      prompt=head + "Run one full engagement loop (skill engagement-loop) within today's caps. Use the executor for every screen action, one job per call.",
                      criteria="Counts of replies, comments and likes are reported with evidence, or the result says SKIPPED with the reason (cap reached, phone locked, app blocked)."))
    T.append(dict(key="stats", name=f"{n}: daily stats and learning", agent=ids["analyst"][0], trigger="DAILY", time=t(20, 30), maxSteps=60, maxMinutes=30,
                  prompt=head + "Read today's results and the action log, save the numbers file, write lessons, propose one experiment, and pass the summary to the director.",
                  criteria="A numbers file exists for today and at least one lesson was saved (or 'nothing new' is justified)."))
    T.append(dict(key="digest", name=f"{n}: owner digest", agent=ids["director"][0], trigger="DAILY", time=t(21, 5), maxSteps=50, maxMinutes=25,
                  prompt=head + "Write today's digest (what was posted or done, who got replies, what needs the owner, what was learned) as a file, notify the owner, and if the whatsapp workspace is on, send it using its recipe. If nothing happened, say so honestly.",
                  criteria="A digest file exists and the owner was notified."))
    T.append(dict(key="overnight", name=f"{n}: overnight drafting", agent=ids["strategist"][0], trigger="DAILY", time=t(23, 30), maxSteps=60, maxMinutes=40,
                  prompt=head + "Overnight, no screen: prepare tomorrow's drafts and engagement targets from the plan and research, hand them on through the board.",
                  criteria="Drafts or targets were created, or the result says why not."))
    T.append(dict(key="weekly", name=f"{n}: weekly review and plan", agent=ids["director"][0], trigger="WEEKLY", days=["SUN"], time=t(18, 0), maxSteps=80, maxMinutes=45,
                  prompt=head + "Weekly review: what worked, what did not, numbers vs plan, lessons. Write next week's plan as a Markdown file with save_file (`" + ws + "/plans/week-<n>.md`) and show the link. Do not start implementing until the owner says so, except the normal daily tasks.",
                  criteria="A weekly plan file exists and the review names at least one change."))
    T.append(dict(key="review", name=f"{n}: weekly learning review", agent=ids["analyst"][0], trigger="WEEKLY", days=["SUN"], time=t(16, 30), maxSteps=60, maxMinutes=30,
                  prompt=head + "Load skill learning-loop and follow its section 5. Read `outcomes op=summary platform=" + ws + " days=7`, `lessons op=list scope=" + ws + "` and the " + ws + "-playbook skill. Write a PROPOSAL file with save_file at `" + ws + "/proposals/<date>.md` (diff style, each change with its evidence ids and a LOW or HIGH risk label) and add one board item for the owner (`board op=add owner=owner`) naming the file. Do NOT edit any playbook, rule, cap, approval or checklist yourself, and never write a lesson that touches them. With under about 10 recorded results, propose experiments only and say so.",
                  criteria="A proposal file exists for this week (or the result says there was too little data), and the owner has a board item pointing to it."))
    return T


PLATFORM_TOOLS = {
 "youtube": "Research: web_search + web_fetch on youtube.com/results?search_query=..., r.jina.ai for a video page, Apify actor 'streamers/youtube-scraper' if the Apify MCP is on. Transcripts of competitors' videos show hooks that work. Publishing: YouTube Studio app via screen routines; thumbnails with make_card.",
 "tiktok": "Research: Apify 'clockworks/tiktok-scraper' (if on) for trending sounds and top videos in the niche; web_fetch via r.jina.ai on tiktok.com/tag/<tag>. Higgsfield/fal.ai MCP for video clips if switched on; CapCut skill for editing steps.",
 "instagram": "Research: Apify 'apify/instagram-scraper' (if on) for top posts and hashtags; make_card for carousel slides; Canva MCP if switched on.",
 "linkedin": "Research: web_search 'site:linkedin.com/posts <topic>', r.jina.ai on public posts; screen routines for comments. Never use third-party automation tools.",
 "reddit": "Research: subreddit rules via web_fetch on https://r.jina.ai/https://www.reddit.com/r/<sub>/about/rules ; top posts via the app routines.",
 "substack": "Research: rss_read on https://<writer>.substack.com/feed for each writer in your niche; web_search for niche topics; Notes and comments via screen routines.",
 "x": "Research: web_search; X is suspended for the owner, keep this workspace off until restored.",
 "facebook": "Research: group rules via the app; Apify 'apify/facebook-posts-scraper' (if on) for page benchmarks.",
 "pinterest": "Research: pinterest search suggestions via the app; make_card for pin images in 1000x1500.",
 "email": "Use Gmail via screen routines; rss_read for newsletters; never bulk-send.",
 "whatsapp": "Use the digest recipe; drafts only for leads.",
 "jobhunt": "Research: web_search for remote roles, r.jina.ai for job pages; log every application in applications/tracker.csv with save_file append.",
}


VIDEO = {"tiktok": "com.zhiliaoapp.musically", "instagram": "com.instagram.android", "youtube": "com.google.android.youtube", "facebook": "com.facebook.katana",
         "reddit": "com.reddit.frontpage", "x": "com.twitter.android", "linkedin": "com.linkedin.android", "threads": "com.instagram.barcelona", "pinterest": "com.pinterest"}

# Heuristics from public creator guidance, NOT guarantees. The analyst checks them against the owner's own numbers.
ENGAGE_MORE = {
 "tiktok": "Hook inside the first 2 seconds; vertical 9:16; short caption with 3-5 specific tags. After posting, spend 15 minutes replying to every comment (a video reply to a good question is a second post) and leave useful comments on 10 niche videos. Comment on fresh videos (under 2 hours old) where the creator replies.",
 "instagram": "Reels drive reach; carousels drive saves and shares. First caption line is the hook. 3-5 relevant tags. After posting, reply to every comment in the first hour and leave real comments on 10 accounts in the niche. Add the Reel to Stories.",
 "youtube": "Shorts: vertical, hook in the first second, keyword in the title. Long form: title and thumbnail promise one clear outcome, deliver in the first 30 seconds. Pin a comment with a question, reply to every comment in the first day, and comment on videos of channels in the niche.",
 "reddit": "Read the subreddit rules before anything. Give value first: comment helpfully for days before posting. Reddit punishes self-promotion and obvious automation: low caps, no links in first comments, never the same text twice, no cross-posting spam. Titles carry the post.",
 "linkedin": "A clear opening line, short paragraphs, one point of view. Comment thoughtfully on 5-10 posts from people in the niche within the first hour of your post; links work better in the first comment than in the body (heuristic: test it).",
 "facebook": "Native video beats links. In groups follow each group's rules and admin approval; one genuinely helpful comment beats a link drop. Reply to every comment on your posts.",
 "pinterest": "Vertical 2:3 pins (1000x1500), keyword-rich title and description, pin to the right board, steady daily volume beats bursts.",
 "threads": "Short conversational posts and real replies; it rewards back-and-forth. Reply to accounts in the niche and to every reply you get.",
 "x": "Short posts, threads for depth, replies to larger accounts in the niche are the main lever. (Account suspended: keep off until restored.)",
}

PLAYBOOK_ENGAGE = {
 "substack": "Notes first: 3-6 Notes a day max. Comments on larger writers' posts in the niche are the best discovery lever: add one specific insight, no pitch. Reply to every comment on the owner's posts. Restack a few good notes with a line of your own.",
}

for p in P:
    ws = p["id"]
    ids = SUBSTACK_IDS if ws == "substack" else {r: (f"{ws}-{r}", f"{p['name'].split(' (')[0]} {ROLE_TITLE[r]}") for r in ROLES}
    agents = [dict(id=ids[r][0], name=ids[r][1], instructions=instr(p, r, ids), tools=TOOLS[r].split(","),
                   skills=["team-protocol", "skill-router", f"{ws}-playbook"] + {"engager": ["engagement-loop", "human-pacing", "fast-screen"], "executor": ["fast-screen", "screen-routines", "human-pacing"] + (["video-publish"] if ws in VIDEO else []), "reviewer": ["reviewer-rubric"], "director": ["owner-corrections"], "writer": ["toolbox"], "analyst": ["toolbox"]}.get(r, []),
                   usesScreen=r in ("executor",)) for r in ROLES]
    # engager also reads the screen for scan routines -> serialised with the executor via usesScreen
    for a in agents:
        if a["id"] == ids["engager"][0]: a["usesScreen"] = False
    skills = ["team-protocol", "toolbox", "skill-router", "fast-screen", "engagement-loop", "reviewer-rubric", "owner-corrections", "human-pacing", "screen-routines", f"{ws}-playbook"] + (["video-publish"] if ws in VIDEO else []) + p["skills"] + sorted(map_ids(p))
    d = dict(id=ws, version=2, name=p["name"], platform=ws, app=p["app"], summary=f"A ready-made {p['name']} team ({len(ROLES)} agents, {len(tasks_for(p, ids))} scheduled tasks): {p['kind']}, engagement and daily learning.",
             quotas=p["quotas"], agents=agents, tasks=tasks_for(p, ids), skills=sorted(set(skills)), mcps=[], memory=[p["facts"]])
    w(f"{A}/workspaces/{ws}.json", json.dumps(d, indent=1, ensure_ascii=False) + "\n")

    ENGAGE_TEXT = PLAYBOOK_ENGAGE.get(ws) or ENGAGE_MORE.get(ws) or ("Reply to every real reply on the owner's own content first, then add one specific, useful comment on a relevant post, then like a few genuinely good posts. Quality over volume; stop for the day at the caps.")
    if ws in ENGAGE_MORE and ws in PLAYBOOK_ENGAGE: ENGAGE_TEXT += " " + ENGAGE_MORE[ws]
    skill(f"{ws}-playbook", f"{p['name']} operating playbook: platform facts, formats, engagement tactics, caps and routines. Load when working on {p['name']}.", f"""
# {p['name']} playbook
App package: `{p['app']}`. Area: {p['kind']}.

## What to know
{p['facts']}

## Daily caps (owner-adjustable in the workspace; `engage op=today` shows live counts)
{p['quotas'].replace(',', ', ')}

## Engagement tactics
{ENGAGE_TEXT}

## Research and tools for this platform
{PLATFORM_TOOLS.get(ws, 'web_search and web_fetch.')}

## Screen routines (`screen_routine routine=list`)
`{ws}.open_home`, `{ws}.read_activity`, `{ws}.scan_feed`, `{ws}.comment`, `{ws}.like`""" + (f", `{ws}.post_text`" if p["can_post"] or ws == "substack" else "") + (f", `{ws}.finish_share` (after `share_file package={VIDEO[ws]}`)" if ws in VIDEO else "") + f""".
The labels in these routines are best-effort guesses for the app's English UI. On the first run each routine may fail with a clear reason: read the screen once, add the real label to `files/routines/{ws}.<name>.json`, and record the fix as a lesson. Do not loop.

## Never
Invent facts or results, post ads, spam, bypass locks/CAPTCHAs/verification, exceed caps, or act while the platform shows a warning.
""")

    # ---------------------------------------------------------------- routines
    L = p["labels"]
    def R(name, desc, params, steps):
        w(f"{A}/routines/{ws}.{name}.json", json.dumps(dict(id=f"{ws}.{name}", version=1, platform=ws, description=desc, params=params, steps=steps), indent=1) + "\n")
    openapp = dict(op="open_app", package=p["app"])
    home = dict(op="tap", labels=L["home"], optional=True, timeout_s=6)
    R("open_home", f"Open {p['name']} and go to the home screen.", [], [openapp, home])
    R("read_activity", f"Open {p['name']} notifications/activity and return what is shown (replies, likes, mentions).", [],
      [openapp, dict(op="tap", labels=L["activity"], optional=True), dict(op="sleep", ms=1500), dict(op="collect", count=3)])
    R("scan_feed", f"Scroll the {p['name']} feed and collect post texts to judge for engagement.", [],
      [openapp, home, dict(op="tap", labels=L["notes"], optional=True), dict(op="sleep", ms=1200), dict(op="collect", count=5)])
    R("comment", f"On the post currently open in {p['name']}, write a comment and send it. Only after an engage gate of AUTO or owner approval.", [dict(name="text", required=True)],
      [dict(op="tap", labels=L["comment"], timeout_s=6), dict(op="type", labels=L["field"], text="{text}"), dict(op="tap", labels=L["send"], timeout_s=8)])
    R("like", f"Like the post currently open in {p['name']}.", [], [dict(op="tap", labels=L["like"])])
    if p["can_post"] or ws == "substack":
        R("post_text", f"Create and publish a text post in {p['name']}. Only after an engage gate of AUTO or owner approval.", [dict(name="text", required=True)],
          [openapp, home, dict(op="tap", labels=L["compose"], timeout_s=6), dict(op="type", labels=L["field"], text="{text}"), dict(op="tap", labels=L["post"], timeout_s=8)])
    if ws in VIDEO:
        R("finish_share", f"After share_file opened {p['name']} with a video attached: continue through Next/Continue, put the caption in, and post. Labels are best-effort for the English UI.", [dict(name="text", required=True)],
          [dict(op="wait", labels=["Next", "Post", "Share", "Upload", "Continue", "Publish", "Create"], timeout_s=15),
           dict(op="tap", labels=["Next", "Continue"], optional=True, timeout_s=3),
           dict(op="tap", labels=["Next", "Continue"], optional=True, timeout_s=3),
           dict(op="type", labels=L["field"] + ["Caption", "Description", "Add a caption", "Write a caption", "Title"], text="{text}", optional=True),
           dict(op="tap", labels=L["post"] + ["Share", "Publish", "Upload", "Post"], timeout_s=10)])
    if ws == "substack":
        R("reply_in_thread", "Reply to the note or comment currently open in Substack.", [dict(name="text", required=True)],
          [dict(op="tap", labels=["Reply"], timeout_s=6), dict(op="type", labels=["Reply", "Write a reply", "Add a reply"], text="{text}"), dict(op="tap", labels=["Post", "Send", "Reply"], timeout_s=8)])
        R("restack", "Restack the note currently open in Substack.", [], [dict(op="tap", labels=["Restack"]), dict(op="tap", labels=["Restack", "Share"], optional=True)])
        R("open_profile", "Open a profile in Substack by name via search.", [dict(name="name", required=True)],
          [openapp, dict(op="tap", labels=["Search", "Explore"], timeout_s=6), dict(op="type", labels=["Search"], text="{name}"), dict(op="key", code=66), dict(op="sleep", ms=1500), dict(op="tap", labels=["{name}"], timeout_s=8)])
        R("read_stats", "Open Substack stats and return the visible numbers (subscribers, views).", [],
          [openapp, dict(op="tap", labels=["Dashboard", "Stats", "Profile"], optional=True), dict(op="tap", labels=["Stats", "Subscribers"], optional=True), dict(op="sleep", ms=2000), dict(op="collect", count=2)])

# A single-agent workspace: turns any repo, document or page into an installed skill.
w(f"{A}/workspaces/toolsmith.json", json.dumps(dict(
    id="toolsmith", version=1, name="Skill Builder", platform="toolsmith", app="", summary="One agent that turns a GitHub repo, a document, a web page or pasted notes into an installed Zafiro skill.", quotas="",
    agents=[dict(id="skill_builder", name="Skill Builder", tools=["create_skill", "http_request", "py_web_read", "execute_python", "load_skill", "save_file", "memory"], skills=["skill-from-source", "skill-creator"], usesScreen=False,
                 instructions="You are the Skill Builder. The owner gives you a GitHub repo, a document, a web page or notes. Load skill skill-from-source and follow it. Install each resulting skill with create_skill. For a repo that already contains skills, install each one (check the license first). Reply with the installed skill ids and one line on what each does. Never install a skill you could not read in full."),],
    tasks=[], skills=["skill-from-source", "skill-creator"], mcps=[], memory=[]), indent=1) + "\n")
DISABLED.append("skill-from-source")
# Phone health: read-only checks, notifies the owner at most once a day per problem.
skill("phone-health", "Checks that the phone can run the team: awake, unlocked, battery, Shizuku, network. Tells the owner once when something blocks work.", """
# Phone health
Run these checks with `device_info` and `execute_python` (rish helper in files/rish, RISH_APPLICATION_ID=com.niki914.zafiro.auto, stdin=DEVNULL):
1. Battery and charging (device_info). Below 25% and not charging: problem.
2. Screen state and lock: `dumpsys window | grep isKeyguardShowing` and wakefulness. Locked or asleep: try `input keyevent KEYCODE_WAKEUP` once. Still locked: problem (never try to bypass a lock).
3. Shizuku/rish alive: run `id` through rish. 'Server is not running': problem. You cannot restart Shizuku yourself without a computer or wireless-debugging pairing, so tell the owner exactly what to tap.
4. Network (device_info). Offline: problem.
5. Recent runs: count FAILED/MISSED/INTERRUPTED in the last 3 hours from the Dashboard data you can read with `board op=list`.
For each problem call `notify` ONCE per day per problem: remember it in files/outputs/health/alerts.json (date + problem) and skip if already sent today. If all is well, do nothing and say STATUS: DONE with 'all clear'.
""")
w(f"{A}/workspaces/health.json", json.dumps(dict(
    id="health", version=1, name="Phone health", platform="health", app="", summary="A watchdog that checks the phone every 30 minutes (awake, unlocked, battery, Shizuku, network) and tells you once if something blocks the team.", quotas="",
    agents=[dict(id="phone_doctor", name="Phone Doctor", tools=["device_info", "execute_python", "notify", "board", "save_file", "load_skill"], skills=["phone-health"], usesScreen=False,
                 instructions="You are the Phone Doctor. Load skill phone-health and run its checks. Be quiet when all is well. Never change settings, never bypass a lock, never uninstall anything.")],
    tasks=[dict(key="check", name="Phone health check", prompt="Run the phone-health checks now.", agent="phone_doctor", trigger="INTERVAL", intervalMinutes=30, maxSteps=25, maxMinutes=10, criteria="Checks ran; any problem was reported once to the owner.")],
    skills=["phone-health"], mcps=[], memory=[]), indent=1) + "\n")

# ================================================================== v1.19 additions (Careers Kenya, Video Studio)
skill("job-sources-kenya", "Verified places where Kenyans actually get hired, with feed URLs, how to read each and what to skip. Load before scanning for jobs.", """
# Job sources that hire Kenyans (checked 2026-11-25; re-check feeds that fail)
Read feeds with `http_request` / `rss_read` / `web_fetch`. Never scrape against robots.txt. Never auto-submit.
## Feeds and APIs (cheapest, no screen)
- **Himalayas** `https://himalayas.app/jobs/api` (cursor JSON). Has per-job `locationRestrictions` and `timezoneRestrictions`: keep jobs whose list contains Kenya or Africa, or is empty, and whose timezone allows UTC+3. Best eligibility signal. `applicationLink` is the apply URL. A single-job URL returning 410 = closed.
- **M-KOPA** `https://api.ashbyhq.com/posting-api/job-board/m-kopa` (Nairobi backend/engineering roles).
- **One Acre Fund / Tupande** `https://boards-api.greenhouse.io/v1/boards/oneacrefund/jobs` (AI and automation engineering leads in Nairobi).
- **Moniepoint** `https://boards-api.greenhouse.io/v1/boards/moniepoint/jobs` (Remote Kenya roles).
- Any company with a Greenhouse `boards-api.greenhouse.io/v1/boards/<token>/jobs`, Lever `api.lever.co/v0/postings/<co>?mode=json` or Ashby `api.ashbyhq.com/posting-api/job-board/<name>` board: read `location` and the published/updated date; skip anything older than 30 days.
- **ReliefWeb Kenya** `https://reliefweb.int/jobs/rss.xml?advanced-search=(C131)` (NGO/UN digital roles in Kenya).
- **JobWeb Kenya** `https://jobwebkenya.com/feed/`, **Career Point Kenya** `https://careerpointkenya.co.ke/feed/`, **VC4A** `https://vc4a.com/jobs/feed/` (slow).
- **We Work Remotely** RSS `https://weworkremotely.com/categories/remote-full-stack-programming-jobs.rss` and `remote-programming-jobs.rss` (use only items whose region says Anywhere in the World). **RemoteOK** `https://remoteok.com/api` (mostly US-only: keep only Worldwide). **Remotive** `https://remotive.com/api/remote-jobs?category=software-dev` (worldwide only, 24 h delay, at most ~4 calls a day). **Jobicy** `https://jobicy.com/api/v2/remote-jobs?count=50&geo=anywhere&industry=dev`.
- **Hacker News "Who is hiring"** (first of every month): find the thread with `https://hn.algolia.com/api/v1/search_by_date?query=Ask%20HN%3A%20Who%20is%20hiring&tags=story,author_whoishiring`, read `https://hn.algolia.com/api/v1/items/<id>`. Many posts apply by email to a human: keep ones saying worldwide/anywhere/EMEA/Africa and not US-only.
- **Telegram** public channels readable at `https://t.me/s/TechPsyche` and `https://t.me/s/africajobs` (filter Kenya/Africa/Worldwide).
## Email alerts (no feed exists): BrighterMonday, MyJobMag
Create alerts ("software developer", "Android", "AI") in the owner's job mailbox and read them with Gmail. Do not scrape these sites.
## High-touch, one-time or a few per week (draft, owner submits)
Upwork (M-Pesa payout available; bots that submit proposals are banned: draft only), Turing and Andela vetting (do deliberately: a failed test means months of waiting), Toptal, Crossover, Arc, Lemon.io, Contra, micro1 and OpenTrain (confirm Kenya eligibility first), local employers (Safaricom, Equity, KCB, Cellulant, Africa's Talking: check careers pages by hand), Public Service Commission and county posts (watch only; fake recruitment ads are common).
## Closed to Kenya or not worth it
Outlier, DataAnnotation, Remotasks, Mercor (reported closed to Kenya). Great Kenyan Jobs (stale). Never rent or buy accounts.
## Facebook / X / WhatsApp job posts
Cannot be read reliably. Treat any job group as hostile until the poster is verified; documented fake pages exist (fake Naivas, Rivatex, KPA, KeRRA, Kenya Railways ads).
""")

skill("job-eligibility-gate", "Run on EVERY job before drafting: country/timezone eligibility, ghost-job and scam checks, fit tier. Load whenever evaluating a job.", r"""
# Eligibility and scam gate (run before any draft)
1. **Country and timezone.** The owner is in Nairobi, Kenya, UTC+3, with no US/EU work authorization. Reject on any of: structured fields (Himalayas locationRestrictions without Kenya/Africa, WWR region "USA Only", RemoteOK "US Only"); text matching `must (reside|be (located|based)) in`, `(US|U\.S\.) (work )?authori[sz]ation`, `right to work`, `no (visa )?sponsorship`, `(EU|EEA|UK)[- ]only`, `W-?2`, `overlap (with|of)` for a timezone that leaves under 4 hours with UTC+3, `legal entit`. "EMEA" alone is NOT enough: read the posting. "We hire through an EOR (Deel, Remote.com)" is fine: both support Kenya.
2. **Ghost job.** Skip if older than 30 days, if the same title is not on the employer's own ATS, if the detail URL is closed (410/404), or if it is an evergreen "talent pool". Prefer the employer's own posting over an aggregator copy.
3. **Scam = hard fail, report once and move on:** any fee, registration or "processing" payment (especially M-Pesa), equipment cheque, bank/ID details before an offer, interview only over WhatsApp/Telegram, a free-mail recruiter claiming a big company, "task release" jobs, account-rental offers.
4. **Fit tier.** A (strong fit and eligibility confirmed, at most about 10 a day): full tailoring and an email to a named person if one exists; B (decent fit): templated tailoring; C: skip. Write the verdict and reason in the tracker either way.
5. Ask the owner (board item with owner set) when location eligibility is ambiguous or the form asks salary, visa, attestations or anything not in the profile file.
""")

skill("job-application-pipeline", "End-to-end application workflow: tailoring, email sending limits, tracker fields, follow-ups, interviews and payment basics for a Kenya-based developer.", """
# Application pipeline
**Résumé variants** (plain text and PDF; only facts from the owner's profile file): Android/Kotlin; full-stack TypeScript/Node; AI-automation/agents. Pick by keyword match. Line one states "Nairobi, UTC+3, N hours overlap with <their timezone>, contract-ready".
**Cover note (120-180 words):** a specific hook from the posting; one quantified project with a link; timezone and contract eligibility; one clear ask. Run it through the `humanizer` skill. No invented claims. Email subject: `Android/Kotlin engineer (Nairobi, UTC+3) - <Role>, <Company>`.
**Sending:** use a dedicated mailbox (not the main account), one recipient per message, vary the text, PDF under 200 KB or a portfolio link. Safe ramp: 25-40 cold emails a day per mailbox after a two-week ramp (Google publishes no cold-send limit; this is a judgement). Pause at once on any 4.7.x/5.7.x error or a bounce rate above 2%. Never email an address that appears only inside a job post body unless the post says to apply by email.
**ATS forms:** fill with screen routines or the in-app browser; stop with take_over on CAPTCHA/login; never submit a form asking for money.
**Tracker** (`applications/tracker.csv`, append with save_file): job_id, source, source_url, employer_url, company, title, posted_at, first_seen, eligibility, tier, resume_variant, channel, applied_at, contact, followups, status, reply_class, salary_posted, outcome_evidence.
**Follow-up:** one nudge on day 5-7 to a named person, a final one on day 14, then close. Never more than two.
**Replies and interviews:** classify each reply; interview invites and offers go to the owner immediately with notify and a board item.
**Money pointers (verify before relying; not tax advice):** Upwork can pay to M-Pesa; PayPal to M-Pesa and Payoneer to a Kenyan bank are common; keep a second payout route; Kenyan residents must declare income from any source (check kra.go.ke or an adviser).
**Positioning:** sell shipped Android and AI-automation outcomes at fixed scope, not the lowest rate.
""")

skill("studio-actions-render", "How to render a video on GitHub Actions from a job file: commit job JSON, dispatch the workflow, poll, download the release asset. Use for every code-rendered video.", """
# Render on GitHub Actions (zafiro-studio repo)
Prerequisites (owner-provided, never invent them): a repo named `zafiro-studio` with `.github/workflows/render.yml` (trigger `workflow_dispatch`, input `job`), the studio tools (render script, ffmpeg, Node 22, Chromium/Playwright), and a fine-grained token stored in the app's secrets with Actions:write and Contents:write on that one repo only. If any is missing, say so and stop with NEEDS_HUMAN.
1. Build the job file (scenes, script text, asset URLs or committed asset paths, voice file path, caption .srt/.ass, aspect ratio 9:16 1080x1920, fps 30) and save it locally with save_file.
2. Commit it: `PUT https://api.github.com/repos/<owner>/zafiro-studio/contents/jobs/<id>.json` (base64 content). Do not run several Contents writes at once.
3. Dispatch: `POST https://api.github.com/repos/<owner>/zafiro-studio/actions/workflows/render.yml/dispatches` body `{"ref":"main","inputs":{"job":"jobs/<id>.json"}}`. Keep the run id from the response if present, otherwise list runs and match the run name.
4. Poll `GET /repos/<owner>/zafiro-studio/actions/runs/<run_id>` every 15-20 s (use `wait`) until status=completed; on failure read the run summary and either fix the job file once or report.
5. Download the mp4 from the release the workflow publishes (`GET /repos/<owner>/zafiro-studio/releases/tags/job-<id>` then the asset with `Accept: application/octet-stream`) into outputs/.
6. Verify before use: duration near target, 1080x1920, audio present, file size under the platform limit. Never post a render that failed these.
Rules: modest daily volume (this repo exists to build and publish video templates; not a generic render farm), no secrets in job files or logs (public repos expose logs). Files up to about 20 MB can go in the Contents API; larger inputs should be URLs.
""")

skill("content-factory-screen", "Make voice, images and AI video through apps and websites on the phone screen (no paid APIs) and bring the files back. Use when a video or post needs voice or generated images.", """
# Content factory by screen (no paid AI APIs)
**Voice.** Prefer an option with no commercial limit: Gemini TTS in AI Studio (free; Google may use unpaid inputs for training, so send nothing sensitive) or a TTS step on the render runner. ElevenLabs Free forbids commercial use and the restriction sticks to content made on it: use it only for non-monetised tests unless the owner has a paid plan. Never create extra accounts to get more credits; stop on CAPTCHA (take_over).
**Images.** Open the Gemini or ChatGPT app with `launch_app`, prompt with the aspect ratio written in the prompt (4:5 for Instagram, 9:16 for stories/reel covers), generate, then save. Free limits are not published; on a limit message switch app or retry later. ChatGPT's terms ban programmatic extraction of output: keep human-like pacing, one real account, prefer Gemini first. Generated images carry provenance marks: assume platforms may label them AI and set the AI flag when posting; never strip metadata or claim AI work is human-made.
**AI video (only if the owner asks).** Google Flow (Gemini Pro): about 50 credits a day for free accounts, Veo Lite clips of 4-8 s cost 10 credits, peak-hour limits for free users (about 17:00-20:00 Nairobi). Prefer code-rendered video.
**Bringing the file back (preferred: Share to Zafiro).** Load `share-inbox`: after generating, use the app's Share button and pick **Zafiro inbox**; then `inbox op=list since=<time noted before>` and verify. If the share sheet has no Zafiro inbox or the app blocks sharing, use the MediaStore/shell fallback below. **Fallback.** Before pressing generate, note the time. Afterwards find the first image or video added after that time: `shell`/`execute_python` with `content query --uri content://media/external/images/media --projection _display_name:date_added --sort "date_added DESC"` (or `ls -t /sdcard/Pictures/* /sdcard/Download`), copy it into Zafiro's files outputs/ folder, verify it decodes and has the expected size. Never take "the latest file overall".
**Posting.** Use the Metricool MCP if connected (media must be public URLs; set the AI flags isAiGenerated / isAigc / isAiGeneratedContent for generated media); otherwise share_file and the platform's finish_share routine. Always run drafts through `humanizer` first.
""")

STUDIO = ["studio-director","studio-motion-craft","studio-shortform-playbook","studio-graphics-toolkit","studio-style-bank","studio-reference-analyst","studio-canvas-motion-engine","studio-qa-gates","studio-editing-rules","studio-editing-styles","studio-shorts-clipper"]
for sid in STUDIO + ["humanizer"]:
    DISABLED.append(sid)
vs_skills = STUDIO + ["humanizer", "studio-actions-render", "share-inbox", "content-factory-screen", "video-publish", "fast-screen", "skill-router", "caption-writer", "hook-writer", "short-form-video-script", "platform-specs-and-validation", "capcut", "ai-voiceover", "thumbnail-design", "team-protocol", "toolbox"]
w(f"{A}/workspaces/video-studio.json", json.dumps(dict(
    id="video-studio", version=2, name="Video Studio", platform="video-studio", app="", summary="Produces short videos, reels and ads: brief, script, storyboard, voice, render on GitHub Actions, QA gates, delivery. Built from the owner's own studio skills.", quotas="",
    agents=[
        dict(id="video-producer", name="Video Producer", tools=["board","save_file","execute_python","http_request","web_fetch","web_search","load_skill","memory","datetime","make_card","agent","notify","wait"], skills=["studio-director","studio-actions-render","skill-router"], usesScreen=False,
             instructions="You are the Video Producer: executive producer of a one-person studio. Load studio-director first and follow its role chain and standing rules (graphics first, a new visual event every 1.5-2.5 s, hook in 2 s, plain language, real assets, honesty). For code-rendered video load studio-actions-render and render on GitHub Actions; for typographic or kinetic stories use studio-canvas-motion-engine; for real footage see studio-editing-rules. Run every script and caption through the humanizer skill. After rendering apply the studio-qa-gates checks and fix failures before delivering. Save deliverables (mp4, cover, caption text, hashtags) under outputs/video/<project>/ and report the paths. Never post: hand finished items to the Media Operator. Save a lesson with memory after each delivered video. If the render repo, token or voice file is missing, ask the owner with a board item (owner set) and continue with other work."),
        dict(id="video-media-operator", name="Media Operator", tools=["screen_routine","screen_operation","share_file","launch_app","open_uri","screenshot","wait","take_over","shell","save_file","board","load_skill","clipboard","execute_python","inbox"], skills=["share-inbox","content-factory-screen","fast-screen","video-publish"], usesScreen=True,
             instructions="You operate apps on the phone for the Video Studio: generate images and voice in the Gemini or ChatGPT apps or web tools, bring the files back, and publish finished videos. Load share-inbox, content-factory-screen and fast-screen first. One job per call. Obey the rules in those skills on terms of service and provenance. Report the saved file path and verify the file exists before saying done. Stop with take_over on any CAPTCHA, login or consent screen."),
    ],
    tasks=[
        dict(key="heartbeat", name="Video Studio: heartbeat", agent="video-producer", trigger="INTERVAL", intervalMinutes=60, maxSteps=60, maxMinutes=30,
             prompt="Workspace video-studio. Run board op=resume, then do every open board item (owner items first). If a video brief is waiting, produce it with the studio-director role chain. If nothing is waiting, do nothing and say STATUS: DONE.", criteria="Open board items were advanced, or the board was empty."),
    ],
    skills=vs_skills, mcps=[], memory=["The studio skills come from the owner's own video repo. Standing rules: graphics first (80% graphics or real footage), a new visual event every 1.5-2.5 s, hook in 2 s, plain language, honest facts only. ElevenLabs Free is non-commercial."]), indent=1) + "\n")


dis = f"{A}/skills_disabled_by_default.txt"
cur = [l.strip() for l in open(dis).read().splitlines() if l.strip()]
open(dis, "w").write("\n".join(cur + [d for d in sorted(set(DISABLED)) if d not in cur]) + "\n")
missing = sorted({sk for p in P for sk in p["skills"] if not os.path.isdir(f"{A}/skills/{sk}")})
print("skill ids not bundled (ignored at enable time):", missing)
print("generated", len(P), "workspaces,", len(set(DISABLED)), "skills")
