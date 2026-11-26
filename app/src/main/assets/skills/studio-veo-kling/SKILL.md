---
name: studio-veo-kling
description: Generate video clips using AI video generation tools (Veo 3, Kling, Luma Dream Machine, Runway Gen-4) on-phone via the Gemini or web apps, then assemble in CapCut or download for GitHub Actions render. Use when the brief needs live-motion AI video (a person walking, a product in motion, a scene from a prompt). Media Operator handles phone-side generation. Routes to the right tool per the prompt type and cost.
---

# Studio Veo & Kling — AI Video Generation (On-Phone)

This job runs on the phone via the Media Operator. No GitHub Actions needed for generation (only for assembly/captioning).

## Tool routing
| Prompt type | Best tool | Why |
|---|---|---|
| Photorealistic, natural motion, lip-sync to audio | **Veo 3** (Gemini app) | Highest quality, native audio generation |
| Stylised, anime, fantasy, game-like | **Kling** (kling.ai) | Strong stylistic control |
| Cinematic, smooth camera moves | **Luma Dream Machine** (lumalabs.ai) | Best camera rigs and shot types |
| Short viral clips, rapid iteration | **Runway Gen-4** (runwayml.com) | Fast turnaround, motion brush |
| Image-to-video (still → motion) | **Kling** or **Luma** | Both excel at animating a reference still |

## Workflow
1. **Write prompt** — describe: subject, action, background, camera move, lighting, style, duration (max 8 s per clip). Be specific. Never say "make it look good" — say the exact motion.
2. **Hand to Media Operator** — provide prompts list. Media Operator opens the app on phone, generates each clip, downloads to gallery or Zafiro workspace.
3. **Assembly** — if >1 clip, use CapCut on-phone (cut, music, captions) OR commit clips to zafiro-studio and dispatch a slideshow/reel job via studio-actions-render.
4. **QA** — studio-qa-gates (visual check on phone + probe after Actions render).

## Prompt templates
**Person doing action** (Veo 3):
```
[Subject]: [Person description]. [Action]. [Environment]. [Camera]: [shot type], [movement]. [Lighting]. [Style]. 8 seconds, 9:16, cinematic.
```

**Product in motion** (Kling or Luma):
```
[Product name], [material and colour]. [Motion: slowly rotating / floating / being held by hand]. [Background]. [Camera: macro / product shot]. [Lighting: studio / natural]. No text, clean composition. 6 seconds, 9:16.
```

**Scene / story beat** (any tool):
```
[Scene description]. [Character action]. [Dialogue or sound if Veo 3]. [Camera move: push in / pull back / tracking shot]. [Mood]. [Style: cinematic / anime / watercolour]. 8 seconds, 9:16.
```

## Clip budget guide
- Veo 3: 2–5 clips per video (high credit cost — use for hero shots only)
- Kling: 4–8 clips per video (moderate cost)
- Luma: 4–10 clips (cheapest good-quality option)
- Runway: fast iteration — good for testing concepts

## Rules
- Never use a tool that requires a login the owner hasn't set up. Stop with NEEDS_HUMAN.
- Never generate real people's faces without explicit owner instruction.
- Stop with take_over on any CAPTCHA, login wall, or age verification.
- Download and verify each clip before assembly (not just the thumbnail).
- Save clips to `outputs/video/<project>/clips/` on Zafiro filesystem.
