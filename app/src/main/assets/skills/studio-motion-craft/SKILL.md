---
name: studio-motion-craft
description: The motion-design laws, timing numbers, never-ship list and HyperFrames gotchas distilled from motionmaxxing, Herk Edit Kit, Shotcraft and OpenMontage. Use whenever designing or building animated scenes, transitions, kinetic type, UI demos, counters or hero moments.
---

# Motion craft

## Judgment (what to make)
1. The idea belongs to the brand/story: cover the logo, it should stop working for a competitor.
2. The viewer watches something happen: perform the claim (a bill drains, a door locks, 53 tiles light up), do not caption it. Show it, then say it.
3. Every beat buys one belief. Write "Buys ___: the viewer ___" or cut the beat. Roles: hook, turn, proof, cta; proof is the biggest share.
4. Every beat causes the next: the last frame of beat N is the first of beat N+1 (carried object, matching shape, transition that starts in A and ends in B). Rule of threes (3 benefits, 3 steps).
5. Emphasis is spent, not spread: at most a quarter of the beats are "the point"; one hero per frame (max 3); the ending gets smaller than the opening.
6. Time breathes: hold before a payoff (0.6-1.5 s designed breath), give reading its time, stop before the idea is spent, end once and quieter.
7. One world: one logic of light, material, lens; a ground is a surface (gradient, grid, texture), not a flat swatch. One accent colour = one meaning. One frame clock, one blur grammar.
8. Ambition on the hero moment: it gets the hardest work (3D, shader, dense comp) and should be worth a screenshot.

## Timing numbers (measured on 53 pro moments, 30 fps)
- Median move 10 f (0.33 s). Translate/scale entries 12 f (0.4 s); opacity 6 f; blur 7 f; exits 13-14 f. 6 f and 24 f are the p25/p75 edges.
- Land decelerates, leave accelerates: most travel in the first 4-8 f then a long tail; no bounce. At most one visible spring per film.
- Arrival decelerates from oversize (logos crash in from ~3.5x, cursors 2-5x); nothing grows from zero. Spend scale once.
- Almost nothing fades; things arrive by moving, cutting, unmasking, focusing. Cut on motion, not on rest; scenes start already moving.
- Words arrive whole, 2-12 f apart (2-4 fast hook, 4-6 headline, 8-12 voice-led). Letter typing only inside a real input field.
- No dead frame: a held frame keeps one living thing (drift, caret, light, grain). Human motion band median ~6.8 (IQR 4.4-9.8) in motionmaxxing's metric; my `video_eyes.py` median-diff should be >= 0.6 with no flat run over 0.8 s.
- Counters step per frame; land on a round hero value for a claim, an un-round value for data; cut before the release.
- Demo physics: cursor parks before acting; press squashes 2-3 f and the target answers a frame later.
- Fast sizzle (avg shot 1.1-1.5 s, none over 2.8 s) is for reels; give explanation room (2-3.5 s) but never a static scene.

## Never ship (delete, do not justify)
Corner/edge labels, tracked-caps kickers, counters like "02 / STEP", timecodes, progress bars, corner brackets; deck layouts (left headline + right device); generic UI cards (status dot + label + value, "Good morning"); the name's first association (rocket for "launch"); invented stats or proof; slogan copy ("Not just X. It's Y.", triplets of one-word sentences, "Unlock", "the future of"); the same house style every time (giant cropped word, colour-flood wipe, dot-becomes-logo); flat swatch grounds; a pinned object across 3+ shots; a web-footer ending (logo + tagline + button + URL) held over 1.4 s; claiming gates without numbers; a film you did not look at.

## Type
Whole words, big (statements 80 px+, labels 28 px+, key numbers 120 px+). One typeface family per film (+ one accent). Never white on light: measure the pixels behind captions. Keep 90 px side margins, 140 top, 380 bottom clear. Words land on their spoken word within 0.03-0.1 s.

## Camera and depth
Slow push 1.06-1.13x over every scene; whip pan with motion blur and a streak between ideas; parallax layers; perspective grid floor + vignette + grain on dark scenes; light is the brand (halos, beams, chrome gradient text) on dark styles.

## HyperFrames gotchas (learned the hard way)
- A scene composition id must never equal an element id inside it (hides the whole scene). Use fromTo with `immediateRender:false` for effects that must start hidden (flash, confetti). Use real elements, not ::after, for GSAP targets. Absolute-position anything you move. Cold-seek lint: fromTo must also set destination opacity.
- Timelines are paused and deterministic; no Math.random at render (seed a list at build time); finite `repeat` counts computed from scene length.
- Every scene's timeline should reach its slot length (`tl.to({}, {duration: SLOT}, 0)` if shorter) so the sub-composition is not hidden early (Herk law 11).
- Look at frames before claiming anything. `npx hyperframes lint` then `check --json`, snapshot or sheet at 0.5-1 s steps.
