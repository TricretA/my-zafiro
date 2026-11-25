---
name: studio-canvas-motion-engine
description: Build high-end motion-graphics films as one self-contained HTML canvas file (every frame a pure function of t) with synthesized sound, then render to MP4 with headless Chrome + ffmpeg. Based on the 7 reference engines from the Nafay3D Style Library (Creator Motion Graphics, Chalk Talk, Clean Tech, Software Walkthrough, Data Story, Warm Canvas, Pop Info). Use for kinetic-type explainers, ads and story videos where timing must lock to a voiceover; use HyperFrames instead when the shot is built from real footage/screen recordings.
---

# Canvas motion engine (the Nafay method)

Files: `studio/engines/nafay/<style>.html` (7 complete working references, MIT-like "free to use for your own videos"), `studio/engines/vertical-kinetic-template.html` (1080x1920 Creator-Motion engine ready for my content), `studio/engines/fonts/` (offline woff2 for every font the styles use), `studio/tools/render_canvas.mjs` (renderer), `studio/tools/video_eyes.py` (QA).

## Why it is better than my generator
- Every frame is a pure function of t (`window.seek(t)`), randomness is seeded (`CM.rng`, `CM.noise1`), so renders are exact and previews scrub.
- One timeline object (TL / `at('phrase')` from word times) drives BOTH picture and sound (`scoreSound`), so hits land on spoken words.
- Sound is synthesized in code (kit: slam, boom, pop, tick, click, whoosh, riser, glitch, ping, send, keys, marker, strike, tapeStop, stamp, shimmer, sparkle + music(): half-time beat, drone()). `voiceover:true` lowers music and plays `soft` sounds at 55%. Booms auto-duck the music. No SFX files needed.
- Looks pro by default: background glows + drifting grid + vignette + film grain stepped at 24 fps + light leaks + camera push/punch/shake + whip/glitch/flash cuts.
- Measured on the Creator Motion reference (21.5 s): motion median 2.55 (IQR 1.2-7.1), 0 s flat run, 0 s end hold, 1.9 hard cuts/10 s. My HyperFrames episodes: median ~0.3, 2.4 s flat end card. Treat the reference as the bar.

## Render
`NODE_PATH=/opt/node22/lib/node_modules node studio/tools/render_canvas.mjs film.html out.mp4 [--vo voice.mp3] [--fps 30] [--no-audio]` (about 35 s for 21 s; fonts served offline; output -14 LUFS). With `--vo`, effects are rendered in voiceover mode and mixed under the voice. Always run `video_eyes.py report out.mp4` and look at the sheet.

## Build a film (follow the Nafay prompt discipline)
1. Intake: topic/script + word-level timing (ElevenLabs scribe, or `align.py` pause-snapped). Duration = last word end + ~1.2 s.
2. STORYBOARD FIRST: 6-10 beats, each with start/end, exact on-screen words (2-6 key words per sentence, never the full transcript), emphasis move, how it connects to the previous beat (same scene continuing or which cut), key sounds. Draw stills with the engine's own helpers before animating when the owner wants approval; otherwise build and review the frame sheet.
3. Copy `vertical-kinetic-template.html` (9:16) or the matching reference (16:9). Replace the story block: WORDS (the word list), CUTS, PUNCH, SHAKE, FLASH, LEAK, scene functions, SCENES, SCENE_RANGES and `scoreSound()`. Keep CM, SFX, the helpers, the compositor and the player (play/pause/scrub/sound/loop/Load voiceover). Keep `window.seek/DURATION/scoreSound/SFX/__ready` exposed.
4. Anchor every on-screen word to `at('spoken phrase')` (within 0.03 s). Cut only at idea boundaries; finish each cut just before the next idea's first spoken word. Emphasis on stressed words; counters land exactly when the number is spoken.
5. Rules: change the story, words and props, not the medium. Statement text 110-260 px on 1080x1920 (80 px+ on 1280x720), labels >= 40 px (28 px on 720p), key numbers >= 160 px. Fill the frame. Each frame well under 50 ms. Proof shots are stylized mockups with generic names; never real people; label illustrative numbers as examples. Hook < 2 s; something new every 0.5-1.5 s; hold the takeaway ~1.5 s.
6. Brand: swap the palette object `C` and fonts (Tricid: Outfit green #2BB673 on graphite; BingwaOne: Poppins orange #ff632a on navy). Add fonts to `studio/engines/fonts` if a new family is needed (npm `@fontsource/<name>`).
7. QA with studio-qa-gates. Deliver mp4 + cover + captions + hashtags.

## Style signatures (what each reference teaches)
- Creator Motion (Kinetic Type): `slam` (blur+oversize->expo settle), `swipe`/`slamMarked` highlighter turns word dark, `strike`, `scribble` hand-drawn loop, `handArrow`, `drawYaw` perspective browser mockup, `bubble` chat/phone mockup with typing dots, `stampLayer`, rolling counter with boom+flash+light leak+spark streaks, whip/glitch/flash cuts. Yellow serif accent once per line; green only money; red only the wrong idea.
- Chalk Talk (Hand-Drawn): `chalkLine/chalkText` grainy strokes (`tooth`, `noise2`), visible stick + dust, camera tracks along one long board (`camAt`), counters re-chalked, `eraser` wipe, pink ring on key moments.
- Clean Tech (Kinetic Type, light): `rise/riseLine` type rises into place, `softCard`, `pillBehind`, `typed`, `cursorArrow`, `checkMark`, `node/promptCard`, dot grid, one electric-blue accent, push and zoom-through cuts, pad bed.
- Software Walkthrough (Screen/UI): `appWindow` on soft gradient desktop, `sidebar/header/modal/nodeCard/editor`, real-feeling `cursor` with click ripples, `spotlight` cutout, `stepCallout`, `notification`, camera zooms into what matters.
- Data Story: deep-ink editorial, `countAt`, line chart that draws through real milestones (`sceneLine`, `SLOPES`), bars that grow and are sonified (`sceneBars`, `barT`), comparison card, source always on screen.
- Warm Canvas (Talking head): two modes only: full-frame A-roll with overlays, or warm canvas with picture-in-picture speaker + white cards; serif two-tone headlines, `chapterCard`, `caption`, `wordSprite`, pastel chips. Needs the speaker's footage.
- Pop Info (Illustrated): flat vector scenes in code (`drawRoom/Shelf/Product/Shopper/Bubble`), one character with a big feeling, one counter, one dotted path (`drawPath`), `wobble/blink`, iris wipe in/out (`drawWipe`), Fredoka.

## Choosing engine vs HyperFrames
Canvas engine: typographic stories, counters, mockups drawn in code, illustrated explainers, anything where sound must hit exact words. HyperFrames: real screen recordings, device-frame footage, video clips, registry components. Combine by rendering each separately and joining with ffmpeg, or place stills (screenshots/posters) with `drawImage` inside the canvas engine.
