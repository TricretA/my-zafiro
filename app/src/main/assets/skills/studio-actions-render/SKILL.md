---
name: studio-actions-render
description: How to render a video on GitHub Actions from a job file: commit job JSON, dispatch the workflow, poll, download the release asset. Use for every code-rendered video.
license: MIT
---

# Render on GitHub Actions (zafiro-studio repo)
Prerequisites (owner-provided, never invent them): a repo named `zafiro-studio` with `.github/workflows/render.yml` (trigger `workflow_dispatch`, input `job`), the studio tools (render script, ffmpeg, Node 22, Chromium/Playwright), and a fine-grained token stored in the app's secrets with Actions:write and Contents:write on that one repo only. If any is missing, say so and stop with NEEDS_HUMAN.
1. Build the job file (scenes, script text, asset URLs or committed asset paths, voice file path, caption .srt/.ass, aspect ratio 9:16 1080x1920, fps 30) and save it locally with save_file.
2. Commit it: `PUT https://api.github.com/repos/<owner>/zafiro-studio/contents/jobs/<id>.json` (base64 content). Do not run several Contents writes at once.
3. Dispatch: `POST https://api.github.com/repos/<owner>/zafiro-studio/actions/workflows/render.yml/dispatches` body `{"ref":"main","inputs":{"job":"jobs/<id>.json"}}`. Keep the run id from the response if present, otherwise list runs and match the run name.
4. Poll `GET /repos/<owner>/zafiro-studio/actions/runs/<run_id>` every 15-20 s (use `wait`) until status=completed; on failure read the run summary and either fix the job file once or report.
5. Download the mp4 from the release the workflow publishes (`GET /repos/<owner>/zafiro-studio/releases/tags/job-<id>` then the asset with `Accept: application/octet-stream`) into outputs/.
6. Verify before use: duration near target, 1080x1920, audio present, file size under the platform limit. Never post a render that failed these.
Rules: modest daily volume (this repo exists to build and publish video templates; not a generic render farm), no secrets in job files or logs (public repos expose logs). Files up to about 20 MB can go in the Contents API; larger inputs should be URLs.
