---
name: studio-remotion
description: Build React/Remotion motion graphics compositions that render to MP4 on GitHub Actions (zafiro-studio). Use for component-based, data-driven, or Figma-imported videos where timing derives from fps and spring physics. Covers Remotion 4, FigmaKit, spring/5-layer primitives, and the studio's motion.py audio/QA CLI.
---

# Studio Remotion — React-Based Video Rendering

Runs on GitHub Actions. Dispatch via studio-actions-render (remotion job type). Do NOT attempt on-phone.

## Non-negotiable rules
1. **Never use linear interpolation.** Every `interpolate()` gets an easing curve; every entrance prefers `spring()`. Always `extrapolateLeft/Right: "clamp"`.
2. **Entrances animate 2–3 properties together** (opacity + translateY + scale). A lone fade is forbidden.
3. **Stagger everything.** Lists, words, rays, cards: 3–6 frame offsets. Nothing enters simultaneously.
4. **Exits exist and are faster than entrances** (~10 frames vs ~20).
5. **Five-layer stack in every scene**: background mesh → assets → graphics/type → color grade → grain + vignette.
6. **Every still image gets Ken Burns** (slow scale 1→1.08 + pan). Every video asset uses `<OffthreadVideo>`, never `<Video>`.
7. **Idle elements breathe**: anything on screen >2s gets sin-wave micro-motion.
8. **All timing derives from fps** via `useVideoConfig()`. No magic frame numbers.
9. **One theme object** at the top (colors, easings, spring presets, fonts). Never inline a hex or easing.
10. **Render, extract frames, look at them, fix, re-render.** Never deliver unverified.

## Project structure
```
src/
  index.ts        (registerRoot)
  Root.tsx        (Composition: duration/fps/size)
  scenes/*.tsx
  components/*.tsx
  theme.ts        (single source of truth for brand)
```

## Setup in zafiro-studio
Remotion projects live under `engines/remotion/<project>/`. The render workflow installs deps, runs `npx remotion render`, and publishes the MP4.

## Remotion job schema (for studio-actions-render)
```json
{
  "id": "my-video",
  "type": "remotion",
  "project": "engines/remotion/my-video",
  "composition": "Main",
  "fps": 30,
  "width": 1080,
  "height": 1920,
  "durationInFrames": 300,
  "voice": "https://url/to/voice.mp3"
}
```

## Workflow
1. Scope: duration, fps, dimensions, assets, brand.
2. Create `src/theme.ts` with the brand palette and spring presets.
3. Write `Root.tsx` and scene components. Apply all 10 rules above.
4. Commit to `engines/remotion/<project>/` in zafiro-studio.
5. Dispatch via studio-actions-render.
6. QA with studio-qa-gates after download.

## FigmaKit
If a Figma design exists, use `engines/remotion/figmakit/` to crop and export frames as assets, then import via `staticFile()` in Remotion.
