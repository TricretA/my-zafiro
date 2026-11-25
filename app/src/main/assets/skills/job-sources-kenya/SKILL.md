---
name: job-sources-kenya
description: Verified places where Kenyans actually get hired, with feed URLs, how to read each and what to skip. Load before scanning for jobs.
license: MIT
---

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
