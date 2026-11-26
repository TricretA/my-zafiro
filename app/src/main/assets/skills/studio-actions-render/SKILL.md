---
name: studio-actions-render
description: How to render a video on GitHub Actions from a job file: commit job JSON, dispatch the workflow, poll, download the release asset. Use for EVERY code-rendered video — canvas, bang-motion, stickman Code Engine, Remotion, reel editing, slideshow, shorts clipping. zafiro-studio repo at https://github.com/TricretA/zafiro-studio.
license: MIT
---

# Render on GitHub Actions (zafiro-studio repo)

Prerequisites (owner-provided, never invent them):
- Repo `TricretA/zafiro-studio` exists with `.github/workflows/render.yml`
- Fine-grained GitHub token stored in app secrets with Actions:write and Contents:write on that repo only
- If any prerequisite is missing: say so and stop with NEEDS_HUMAN

## Job types

| type | What it does | Tools used |
|---|---|---|
| `canvas` | Render a self-contained canvas HTML film | Playwright + ffmpeg |
| `slideshow` | Still images + voice + captions → 1080×1920 video | ffmpeg only |
| `reel` | Edit raw footage with EDL | ffmpeg + Whisper |
| `clip` | Extract viral clips from long video | Whisper + ffmpeg + scoring |
| `remotion` | Render a React/Remotion composition | Node + Remotion CLI |
| `stickman` | Render a stickman Code Engine film | Node + canvas |

## Universal render steps

1. **Build the job file** and save it locally with save_file. Schema: `{id, type, ...type-specific fields, width?, height?, fps?}`. Default: 1080×1920, 30 fps.
2. **Commit it**: `PUT https://api.github.com/repos/TricretA/zafiro-studio/contents/jobs/<id>.json` (base64 content, via http_request). Do NOT run several Contents writes at once.
3. **If the job references engine files** (HTML, Python scripts, Remotion project): commit those first to the right path under `engines/` before dispatching.
4. **Dispatch**: `POST https://api.github.com/repos/TricretA/zafiro-studio/actions/workflows/render.yml/dispatches` body `{"ref":"main","inputs":{"job":"jobs/<id>.json"}}`. Use the `Authorization: Bearer <GITHUB_TOKEN>` header from the app secret.
5. **Poll** `GET /repos/TricretA/zafiro-studio/actions/runs` every 20 s (use `wait`) until `status=completed`; on failure read the run summary URL, fix the job file once, re-dispatch. After 3 failures: stop and report.
6. **Download** the mp4 from the release `job-<id>`: `GET /repos/TricretA/zafiro-studio/releases/tags/job-<id>` → find the .mp4 asset → download with `Accept: application/octet-stream` into `outputs/video/<project>/final/`.
7. **Verify before use**: duration near target, correct dimensions, audio present, file size under platform limit. Never use a render that failed these.

## Canvas job schema
```json
{
  "id": "my-canvas-film",
  "type": "canvas",
  "html": "engines/canvas/my-film.html",
  "voice": "https://url/to/voice.mp3",
  "fps": 30,
  "width": 1080,
  "height": 1920
}
```

## Slideshow job schema
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

## Stickman B-Engine job schema
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

## Remotion job schema
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

## Clip job schema
```json
{
  "id": "clip-podcast-ep12",
  "type": "clip",
  "source": "https://url/to/source.mp4",
  "maxClips": 5,
  "maxDurationSeconds": 60,
  "captionStyle": "bounce",
  "music": "energetic",
  "reframe": "talking-head"
}
```

## Rules
- Modest daily volume — this repo exists to build and publish video templates, not a generic render farm.
- No secrets in job files or logs (public repo exposes logs). Pass API keys via GitHub Secrets.
- Files up to ~20 MB can go in the Contents API; larger inputs should be URLs.
- Never commit a rendered MP4 — download from the release asset only.
