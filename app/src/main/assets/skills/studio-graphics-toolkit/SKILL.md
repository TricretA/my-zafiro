---
name: studio-graphics-toolkit
description: Everything for making rich animated graphics fast: the HyperFrames registry (456 ready blocks/components, installable offline), Shotcraft's 157 shot cards, my own layer-DSL generator, icon sets, idea-to-visual mapping and asset sources. Use when planning or building the visuals of any video, so I never search from scratch.
---

# Graphics toolkit

## 1. HyperFrames registry (use this first)
`cd <hf-project> && npx -y hyperframes@0.9.26 add <name> --no-clipboard --json` installs a component (`compositions/components/<name>.html`) or block (full scene) from the registry; it works in this container (verified 17 Nov). Read the installed file, paste its markup/`<template>` into a scene, set its variables. Catalog with one-line descriptions: `references/registry-catalog.md` (grep it: `grep -i "cursor\|caption\|count" references/registry-catalog.md`). Useful families:
- Captions: caption-kinetic-slam, caption-pill-karaoke, caption-highlight/gradient-fill/glitch-rgb/neon-glow/particle-burst/clip-wipe, headline-slam, per-word-rise/crossfade, text-stagger, scramble-reveal, typewriter.
- Transitions: whip-pan-cut, zoom-through-transition, flash-through-white, directional-wipe, colorama-wipe, iris-reveal, curtain-reveal, shutter-slam, torn-paper-rip, halftone-dissolve, match-cut, cross-warp-morph, grid-pixelate-wipe.
- UI/product: device-frame-stage, browser-device-stage, multi-device-splay, parallax-device-dive, filmed-screen, agent-cursor/cursor-* (annotate, drag-drop, marquee, zoom-follow), chat-thread/chat-message/typing-indicator, notification-stack/pileup, signup-flow, toggle-flip, success-check, store-badge-lockup.
- Data: count-up, number-wheel, slot-machine-roll, animated-bar-chart, data-chart, bar-chart-race, conic-progress-ring, decline-chart, star-rating-fill.
- Hand-drawn/annotation: hw-arrow, hw-callout-circle, hw-underline, hw-box-label, marker-highlight, outline-draw, whiteboard-ink, vox-annotate, yt-circle-pointer, stitched-text-draw.
- Camera/world: camera-shake, push-in, parallax-zoom, pull-back-reveal, dynamic-grid, grain-overlay, vignette, godrays, mesh-gradient-bg, aurora-drift.
- Logo/CTA: logo-sting, logo-wall, logo-brand-close, cta-lockup, titlecard-lockup, wordmark-tiles.
Blocks (210) include chat/AI exchange reveals, code-diff/typing/scroll, bar-chart-race, 3D extrude logo, glass hero, glitch, flowchart, carousel layouts, lower thirds.

## 2. Shotcraft shot cards (idea bank)
`references/shotcraft-cards.md` lists 157 shot ideas by category (camera, data, effects, interaction, opening, outro, rhythm, transition, typography, ui-entrance) with the intent line (Chinese). Full cards + demo code: clone `windyinwind/hyper-videocraft` (`references/shots/<category>/<name>.md`). Use for "what shot makes this beat feel physical".

## 3. My layer-DSL generator (`series_v2/_gen`)
`build2.py` turns a `spec.json` (scenes of layers anchored to spoken phrases) into a HyperFrames project. Helpers in `specs_v2.py`: `T` text, `I` lucide icon, `M` image/logo, `N` counter, `P` pill, `ST` stamp, `B` meter bar, `S` poster conveyor, `GR` tile grid (53 tiles, hi/dim), `sc` scene (bg dark/paper/navy), `hook`. Anims: pop, sl, sr, su, sd (drop), fade, stamp (+shake), grow, none; loops fl (float), spin. Add new layer types when a beat needs them. It is a fast baseline; beats that matter get hand-written scenes (see `series_v2` BingwaOne `bw.py`: chat bubbles + calendar crossing out + confetti + stamp, phone mockup playing a real screen recording).

## 4. Idea -> visual device
money/price -> bill, receipt, counter ticking, price tag stamp; waiting/delay -> clock, calendar tiles crossed off, loading dots; fake/broken -> stamp "FAKE", red X, cracked UI; leak/security -> door, lock, key, shield, watermark stamp; limit/overload -> meter bar filling red, memory bar, laptop; many things -> grid of tiles (53 posters), conveyor of posters; compare -> split screen, before/after wipe; steps -> numbered stack cards, tick marks; proof -> real capture inside a device frame with cursor; scale -> counter 0->N with boom; team/agents -> robot icons colliding, folder ownership map; time pressure -> night clock, 3 a.m.; success -> check burst + confetti; honest limit -> amber "not yet" pill, empty calendar ticks.

## 5. Asset sources
Lucide icons (npm `lucide-static`, 1544 SVGs; `series_v2/_gen/icons.json` holds ~85); demo posters (`demo-pack/` 38 story + 40 square, `tpl2_assets/img/p01-p18`); logos (Tricid `brand/logo-kit/dist`, BingwaOne `bingwaone.png`, Placid `placid-logo.png`, simple-icons for others); real captures via Playwright with cursor overlay (local Tricid stack: `pnpm start`-like restart recipe in memory); owner screen recordings (crop system bars: BingwaOne rec crop y 92-1528); Mixkit/CC0 SFX + m474 music in `video-pipeline/_audio/raw`. Hero 3D moments: `viettranx/3dviz-pro-max` (three.js scene skill) is available but needs a GPU; software GL is slow, use sparingly.
