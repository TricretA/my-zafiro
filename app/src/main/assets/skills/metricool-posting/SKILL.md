---
name: metricool-posting
description: Post and schedule through the Metricool MCP: free-plan limits, the monthly counter, AI flags, draft dry runs and the screen fallback. Load before using any Metricool tool.
license: MIT
---

# Metricool posting
MCP: `https://ai.metricool.com/mcp` (OAuth 2.1; the owner connects it once in the MCP page; its tools appear as `createScheduledPost`, `getScheduledPosts`, `getBrandSettings`, `getBestTimeToPostByNetwork`, `getAnalyticsAvailableMetrics`, `getAnalyticsDataByMetrics`, `updateScheduledPost` and the `...ForReview` variants). If the tools are not in your tool list, the MCP is off: use another route and say so once.
## The owner is on the FREE plan (unless `metricool.plan` in PLATFORMS.json says otherwise)
- **20 posts per month in total** across all brands/networks, 1 brand, and **no LinkedIn or X** (those need a paid plan). So Metricool is a safety net ("just in case"), not the daily route: use `share_file` + `finish_share` and the app screen first; use Metricool when the phone route failed twice, the phone is locked/unavailable at the due time, or an exact-time post must go while the phone is busy.
- **Monthly counter**: `social/metricool_count.txt` holds one line `YYYY-MM=<n>`. Read it BEFORE every Metricool post (`save_file op=read`); if the month differs, start a new line at 0. Stop using Metricool at 18 so 2 stay spare. After each successful create call, increase the number (`save_file op=save`, whole file). Count scheduled posts and drafts you did not delete.
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
