---
name: studio-stickman-director
description: Turn a topic, script or idea into a stickman/stick-figure animated video in one of 16 styles (A1–A8 Model Engine, B1–B8 Code Engine). Code Engine styles render on GitHub Actions (zafiro-studio). Model Engine styles produce prompt packs for Veo 3, Kling, Luma or any generative video tool, then the Media Operator assembles them on-phone via CapCut. Use for faceless explainers, shorts, TikToks, Reels.
---

# Studio Stickman Director

16 stickman styles, two engines. Pick one engine + one style, then follow the lane.

## Engine A — Model Engine (AI-generated frames, assembled on phone)
AI image/video tools generate each shot; Media Operator assembles in CapCut.

| ID | Style | Look | Best for |
|---|---|---|---|
| A1 | Classic B&W | Bold ink outlines, white fill, clean shadow | Explainers, tutorials |
| A2 | Modern Beanie | Coloured beanie, casual poses, flat pastel bg | Relatable social content |
| A3 | Prehistoric | Cave-painting texture, ochre palette | Humour, origin stories |
| A4 | Cozy | Warm tones, rounded limbs, soft lighting | Lifestyle, wellness |
| A5 | Historical | Sepia, vintage newspaper grain | Historical facts, timelines |
| A6 | Amber Noir | Deep amber + black, dramatic shadows | Mystery, thriller hooks |
| A7 | Rubber Hose | 1930s cartoon, bouncy limbs, no sharp joints | Entertainment, nostalgia |
| A8 | Studio Ghibli | Soft watercolour, nature background, lush detail | Inspirational, calm explainers |

### A-Engine workflow
1. **Storyboard**: 6–10 beats (time, dialogue, action, visual device, emotion)
2. **Prompt pack**: one image prompt per scene (style, pose, background, expression, camera angle). Include reference style keywords from the ID above.
3. **Hand-off to Media Operator**: prompt list + voice script. Media Operator generates frames in Gemini / Ideogram / Kling / Veo 3, downloads, assembles in CapCut with beat sync and captions.
4. **QA**: check studio-qa-gates gates before publish.

---

## Engine B — Code Engine (HTML canvas, renders on GitHub Actions)
Pure canvas/JS animation; no AI image generation needed. Render via studio-actions-render.

| ID | Style | Look | Best for |
|---|---|---|---|
| B1 | Minimal Line | Thin black lines, white bg, one accent colour | Clean explainers |
| B2 | Colorful Hand-Drawn | Crayon strokes, saturated fills, wobbly lines | Kids, education |
| B3 | Retro TV | CRT scanlines, 4:3 vignette, phosphor glow | Nostalgia, retro brands |
| B4 | Survival Thriller | Dark bg, neon outlines, tension music beats | Thriller hooks, game content |
| B5 | Avatar Explainer | Rounded hero avatar, speech bubbles, clean cards | Product explainers |
| B6 | Dynamic Meme | Bold impact text overlay, reaction faces | Viral memes, commentary |
| B7 | Pixel Theater | Pixel-art canvas, 8-bit SFX, sprite animation | Gaming, retro culture |
| B8 | Greek Mythology | Attic black-figure art, terracotta bg, border | Epic stories, mythology |

### B-Engine workflow
1. **Script**: narration text, word timings (use tts_edge or ElevenLabs scribe).
2. **Build canvas HTML**: self-contained file exposing `window.seek(t)`, `DURATION`, `__ready`. Stickman is drawn in canvas JS; use `drawStickman(ctx, pose, x, y, scale, color)` helpers. Animate poses frame-by-frame via keyframe interpolation.
3. **Commit HTML to zafiro-studio** at `engines/stickman/<style>/<project>.html` and add fonts to `engines/fonts/` if needed.
4. **Dispatch render** via studio-actions-render (canvas job type, attach voice.mp3 URL if available).
5. **QA** with studio-qa-gates after download.

---

## Standing rules (both engines)
- One style per video. Borrow at most 2 visual techniques from another style.
- Hook visible in first 2 s — stickman does something surprising.
- Captions are mandatory (Poppins ExtraBold white + black outline, 9:16 safe zone).
- Voice is narration only; keep mouth shapes or speech-bubble sync honest.
- Never invent facts. Mark any numbers as examples.
