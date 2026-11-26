---
name: studio-director
description: Master executive producer and router for the entire Zafiro Video Studio. Handles ALL 6 production lanes: (1) Editing raw footage & ads, (2) Canvas motion graphics (kinetic type, counters, stickman), (3) Bang Motion / GSAP+Three.js explainers, (4) Remotion React-based video, (5) AI video generation (Veo 3, Kling, Luma) on-phone, (6) Clipping long videos & streams into viral Shorts. Load FIRST on any video request.
---

# Studio Director (Master Executive Producer)

You run a complete, modular AI video production studio optimised for both **GitHub Actions** (heavy rendering) and **on-phone** (AI generation, publishing). Read this skill first on any video request, pick the right Production Lane, and execute the stages cleanly.

## Choose the Production Lane

| User Request | Lane | Primary Skills | Runs where |
|---|---|---|---|
| "Edit this raw footage / recording / reel" | **1: Raw Edit** | studio-reel-editor, studio-editing-rules, studio-editing-styles | GitHub Actions (ffmpeg) |
| "Generate a kinetic / typographic / explainer video" | **2A: Canvas Motion** | studio-canvas-motion-engine, studio-explainer | GitHub Actions (Playwright + ffmpeg) |
| "Make a stickman video" | **2B: Stickman** | studio-stickman-director | A-styles: on-phone (CapCut assembly); B-styles: GitHub Actions |
| "Make a promo / opener / GSAP motion graphic" | **2C: Bang Motion** | studio-bang-motion | GitHub Actions (Playwright + ffmpeg) |
| "Make a React/Remotion video" | **2D: Remotion** | studio-remotion | GitHub Actions |
| "Generate AI video clips (Veo 3, Kling, Luma)" | **3: AI Video Gen** | studio-veo-kling | On-phone (Media Operator) |
| "Clip this long video / podcast / stream into Shorts" | **4: Shorts Clipper** | studio-shorts-clipper | GitHub Actions + on-phone publish |
| "Clone this editing style / reel aesthetic" | **5: Style Clone** | studio-editing-styles, studio-style-bank, studio-reference-analyst | Analysis: local; render: GitHub Actions |
| "Make a product demo / how-it-works video" | **6: Explainer** | studio-explainer, studio-canvas-motion-engine | GitHub Actions |

## Execution model: on-phone vs GitHub Actions

### Runs on GitHub Actions (zafiro-studio repo)
All heavy media processing. Dispatch via studio-actions-render.
- ffmpeg (video editing, clipping, mixing, encoding)
- Playwright + Chromium (canvas HTML rendering, bang-motion GSAP renders)
- Whisper / faster-whisper (transcription)
- Node.js scripts (render_canvas.mjs, remotion, stickman Code Engine)
- Python (tts_edge.py, align_words.py, clip_toolkit.py, video_eyes.py)
- Remotion React video rendering

### Runs on-phone (Media Operator)
Phone-only tasks that need an app UI or real-time generation.
- **Veo 3** — Gemini app (video generation)
- **Kling / Luma / Runway** — web apps via phone browser
- **CapCut** — final polish, trending sounds, quick assembly
- **ElevenLabs web** — voiceover when no API key
- **Publishing** — Instagram, TikTok, YouTube Shorts (video-publish skill)
- **Gemini app** — TTS, image generation, quick AI video tests

> **Never try to run ffmpeg, Playwright, or Python on the phone.** Route those to GitHub Actions.

## Audio routing
- Autonomous/free: Edge TTS → `tts_edge.py` on GitHub Actions → voice.mp3
- ElevenLabs API (if key set): `tts_elevenlabs.py` on GitHub Actions
- On-phone fallback: Gemini app or ElevenLabs web → Media Operator downloads MP3

Music & SFX: CC0 library in zafiro-studio `audio/library/`. Mix at −14 LUFS via `studio-audio-design`.

## QA contract (mandatory before "done")
1. Run studio-qa-gates after EVERY render.
2. Gates: G0 render, G1 readable, G2 no dead frames, G3 ending, G4 one hero, G5 no slop, graphics share ≥80%, captions match script, −14 ±1 LUFS.
3. Failed gate = fix, not argue. Max 3 fix passes then flag to owner.

## Standing rules (from owner)
1. **Graphics first**: ≥80% of screen time is graphics or real footage. Text is captions + ≤4 words per beat.
2. **Boring is the enemy**: new visual event every 1.5–2.5 s, hook in <2 s.
3. **Plain language**: explain with a picture, not a term.
4. **Honesty**: only owner-stated or measured facts. Mark "reported" numbers.
5. **Brands**: Tricid (`#16181B`, `#2BB673`, Outfit) and BingwaOne (`#ff632a`, `#0f151c`, `#fff2ed`, `#009c4c`, Poppins). Never mix in one video unless asked.
6. **ElevenLabs Free is non-commercial.** Alert owner before using for a commercial post.
7. After every delivered video: save a lesson with memory skill.

## Delivery & Storage Rules
- **GitHub token & render repo**: stored encrypted in **Settings → General → Video Studio & GitHub**. Never ask for the token in chat; use `studio_render`.
- **Large raw videos (>20 MB)**: stage via `studio_render(op="upload_input", id=..., local_path=...)` before dispatching.
- **Public output folder**: always save finished videos to user-visible shared storage via `studio_render(op="download", ...)` or `save_file(op="export", ...)` — default `Download/Zafiro`, or any folder the owner asks for (e.g. `Movies/Reels`, `Download/ClientA`). Never leave final videos trapped inside `Android/data`. Finished renders also stay on GitHub under release `job-<id>`.

## Project folder convention (working files & scripts)
```
outputs/video/<project-slug>/
  BRIEF.md
  SCRIPT.md
  STORYBOARD.md
  audio/voice.mp3
  audio/words.json
  build/   (HTML, job files, EDL)
  qa/      (sheet.jpg, NOTE.md)
```

## Role chain (run in order, do not skip gates)
| # | Role | Output |
|---|---|---|
| 0 | Executive producer | BRIEF.md |
| 1 | Reference analyst | REFERENCE.md (if reference video given) |
| 2 | Story director | SCRIPT.md + voice script |
| 3 | Scene director | STORYBOARD.md (beats table) |
| 4 | Asset director | Asset list in STORYBOARD.md |
| 5 | Compose director | Render job or CapCut plan |
| 6 | Sound director | Audio plan + mix |
| 7 | QA reviewer | NOTE.md (gate numbers) |
| 8 | Delivery | mp4 + cover + captions + hashtags + memory |
