---
name: skill-router
description: Pick the right skills before any job: a map from job type to the bundled skills that do it best. Load when you start work or when unsure which skill fits.
license: MIT
---

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
