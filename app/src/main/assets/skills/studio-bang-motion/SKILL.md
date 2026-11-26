---
name: studio-bang-motion
description: Build cinematic HTML+GSAP+Three.js motion graphics (promo video, opener, kinetic typography, explainer) that render to MP4 on GitHub Actions via zafiro-studio. 6 explainer engines, 10 brand style directions, 19 opener concepts. Use when studio-canvas-motion-engine is too code-heavy or when the brief needs a persistent #world camera-rig, ThreeJS 3D, or one of the 6 explainer flavours.
license: MIT (Bang Tutorial, MIT)
---

# Bang Motion — Cinematic Web Motion Graphics

Renders on GitHub Actions (zafiro-studio). NOT an on-phone job. Dispatch via studio-actions-render.

## 6 Explainer Engines (pick one per video)
| Engine | Look | Use when |
|---|---|---|
| Continuous Action | One continuous shot, objects fly in/out | Fast product demos |
| Cartoon Collage | Cut-out layered characters, paper texture | Playful explainers |
| Visual Journalism | Dark bg, data lines, editorial typography | Stats and investigation |
| White Catalog | Clean white, minimal product cards | E-commerce, SaaS |
| Vintage Sketch | Pencil texture, hatching, sepia | Artisanal, heritage brands |
| Cartoon Stage | Character on a coloured stage, speech bubble | Story-driven explainers |

## 10 Brand Style Directions
Cinematic Dark · Poster Color-Block · Editorial Light · Paper & Print · Retro/Analog · Brutalist Mono · Pastel Playful · Luxury Dark · Flat Pop with Photos · Grainy Gradient

## Anti-PPT Rules (mandatory)
1. Never fade `<section>` slide decks — use `#world` camera movement, whip-pans, or object wipes.
2. Keep a persistent visual thread (one hero subject or continuous world).
3. Max 2 text tiers per shot.
4. Background must move — not static Ken Burns alone.
5. Never repeat the same transition on every cut.
6. Start from the matching style starter, never from scratch.
7. Camera zoom >= 1.08 inside stage frames.
8. Never repeat the generic opener skeleton (zoom text → feature tiles → search bar → CTA).

## Determinism Law
Pure function of `tl.time()`. Zero `Math.random()` or `Date.now()` per render loop. Use `?clean=1`, CSS `opacity:0` initial + `immediateRender:false`.

## Build workflow
1. Write a **Style Brief**: palette hex + source, display font, motion signature, background motion, surface, object style, transitions.
2. Copy the matching `engines/motion-web/starters/starter-explainer-<engine>.html` from zafiro-studio.
3. Replace story content (never the engine scaffolding).
4. QA snaps: `node engines/motion-web/render_motion_web.mjs index.html qa/snaps --snap 0,2,5,8,12`.
5. Dispatch render via studio-actions-render (canvas job, html path in zafiro-studio repo).

## Starter files in zafiro-studio
`engines/motion-web/starters/` — committed by the owner or seeded from Claude-Video-Editor source. If missing, build from scratch following the Anti-PPT rules and expose `window.seek/DURATION/__ready`.

## Brand palette
Tricid: `#16181B` graphite, `#2BB673` green, Outfit font.
BingwaOne: `#0f151c` navy, `#ff632a` orange, `#fff2ed` cream, `#009c4c` green, Poppins font.
