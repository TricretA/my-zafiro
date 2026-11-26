---
name: studio-actions-render
description: How to render a video on GitHub Actions from a job file using the studio_render builtin tool: upload raw input videos (up to 2 GB) as release assets, commit job JSON, dispatch the workflow, poll, and download the finished MP4 directly into Downloads (or any user-requested folder). Use for EVERY code-rendered video — canvas, bang-motion, stickman Code Engine, Remotion, reel editing, slideshow, shorts clipping. Uses the encrypted GitHub token from Settings > General > Video Studio & GitHub. zafiro-studio repo at https://github.com/TricretA/zafiro-studio.
license: MIT
---

# Render on GitHub Actions (zafiro-studio repo)

Prerequisites:
- The owner has configured their GitHub fine-grained token (Contents:write + Actions:write on `TricretA/zafiro-studio`) in **Settings → General → Video Studio & GitHub**.
- NEVER ask the owner to paste a token into chat, and NEVER put tokens into job JSON files or logs.
- Use the `studio_render` builtin tool — it reads the encrypted token automatically.
- First call `studio_render` with `op="status"`. If `configured=false`, tell the owner to enter their GitHub token in **Settings → General → Video Studio & GitHub** and stop with `NEEDS_HUMAN`.

## Job types

| type | What it does | Tools used on Actions |
|---|---|---|
| `canvas` | Render a self-contained canvas HTML film | Playwright + ffmpeg |
| `slideshow` | Still images + voice + captions → 1080×1920 video | ffmpeg only |
| `reel` | Edit raw footage with EDL (or reframe to 9:16) | ffmpeg + Whisper |
| `clip` | Extract viral clips from long video | Whisper + ffmpeg + scoring |
| `remotion` | Render a React/Remotion composition | Node + Remotion CLI |
| `stickman` | Render a stickman Code Engine film | Node + canvas |
| `bang-motion` | Render GSAP+Three.js HTML motion graphic | Playwright + ffmpeg |

## Universal render steps (via `studio_render`)

1. **Check status**: `studio_render(op="status")` → confirms `repo` and `default_output_folder` (default `Download/Zafiro`).
2. **Upload large raw media (if any)** — videos/audio on the phone (even >20 MB, up to 2 GB):
   `studio_render(op="upload_input", id="<job-id>", local_path="<path on phone or outputs/...>")`
   Use the returned `api_asset_url` (works for both private and public repos!) or `download_url` as `"source"` / `"voice"` in the job JSON.
3. **Commit engine files (if needed)** — HTML films, EDLs, Remotion files under `engines/` or `jobs/`:
   `studio_render(op="commit_file", repo_path="engines/canvas/<id>.html", content="...")`
4. **Build and commit the job file**:
   Save locally if useful, then `studio_render(op="commit_file", repo_path="jobs/<id>.json", content="{...}")`.
5. **Dispatch**:
   `studio_render(op="dispatch", job_path="jobs/<id>.json")` → returns `run_id`.
6. **Poll**:
   Use `wait` (20 s) + `studio_render(op="poll", run_id=<run_id>)` until `status == "completed"`. If `conclusion != "success"`, report `html_url` and inspect/fix once. After 3 failures: stop and report.
7. **Download straight to Downloads (or user-requested folder)**:
   `studio_render(op="download", id="<job-id>", folder="<user folder if specified, else omit to use Settings default>")`
   - Always saves to user-visible storage (default `Download/Zafiro`), NEVER hidden in internal `Android/data`!
   - If the owner asked for a specific folder (e.g. "put it in Movies/Reels" or "Download/ClientA"), pass `folder="Movies/Reels"` or `folder="Download/ClientA"`.
   - The finished video ALSO remains on GitHub as release `job-<id>` (`release_url` is returned).
8. **Verify before use**: duration near target, correct dimensions, audio present, file size under platform limit; check file size, report exact `display_path` and `release_url` to the owner. Never use a render that failed these.

## Job schemas

### Canvas job schema
```json
{
  "id": "my-canvas-film",
  "type": "canvas",
  "html": "engines/canvas/my-film.html",
  "voice": "https://api.github.com/repos/TricretA/zafiro-studio/releases/assets/12345",
  "fps": 30,
  "width": 1080,
  "height": 1920
}
```

### Reel edit (from phone raw video)
```json
{
  "id": "my-reel",
  "type": "reel",
  "source": "https://api.github.com/repos/TricretA/zafiro-studio/releases/assets/12345",
  "outputFormat": "9:16"
}
```

### Slideshow job schema
```json
{
  "id": "my-slideshow",
  "type": "slideshow",
  "images": [
    {"src": "https://url/image1.jpg", "seconds": 3},
    {"src": "https://url/image2.jpg", "seconds": 4}
  ],
  "voice": "https://url/voice.mp3",
  "captions": "https://url/captions.srt",
  "music": "energetic"
}
```

### Stickman B-Engine job schema
```json
{
  "id": "my-stickman",
  "type": "stickman",
  "style": "B1",
  "html": "engines/stickman/B1/my-film.html",
  "voice": "https://url/voice.mp3",
  "fps": 30
}
```

### Remotion job schema
```json
{
  "id": "my-remotion-video",
  "type": "remotion",
  "project": "engines/remotion/my-video",
  "composition": "Main",
  "fps": 30,
  "durationInFrames": 300,
  "voice": "https://url/voice.mp3"
}
```

### Clip job schema (extract viral shorts)
```json
{
  "id": "clip-podcast-ep12",
  "type": "clip",
  "source": "https://api.github.com/repos/TricretA/zafiro-studio/releases/assets/12345",
  "maxClips": 5,
  "maxDurationSeconds": 60,
  "captionStyle": "bounce",
  "music": "energetic",
  "reframe": "talking-head"
}
```

## Rules
- Modest daily volume — this repo exists to build and publish video templates, not a generic render farm; do not spam empty jobs.
- Never put secrets or tokens in job files, commits or logs (this is a public repo and exposes logs); the encrypted token lives only in Settings and is read automatically by `studio_render`.
- Files up to ~20 MB can go straight into a job JSON's `content`; anything larger (raw phone video/audio) must go through `studio_render(op="upload_input", ...)` first.
- Never commit a rendered MP4 to the repo — download it from the release asset only.
- If a file was generated on-phone (e.g. by Media Operator via Veo/Kling/CapCut), export it to public storage with `save_file(op="export", path="...", folder="...")` so the owner can access it in Downloads or their chosen folder.
