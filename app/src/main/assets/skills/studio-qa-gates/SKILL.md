---
name: studio-qa-gates
description: Quality gates to run on every render before delivery: frame sheet review, computed gates (not blank, no dead frames, end hold), captions vs script, loudness and sync, listen-check list. Use after each render and before telling the owner a video is done.
---

# QA gates

Run: `python3 -I studio/tools/video_eyes.py report out.mp4 --out qa` then LOOK at `qa/sheet.jpg` and 3-4 full frames (hook frame 0, a proof beat, the hero beat, the last frame).

| Gate | Pass | How |
| --- | --- | --- |
| G0 render | decodes, not blank, picture moves, length = plan, audio present and not silent | `video_eyes.py gates --expect S --expect-audio` |
| G1 readable | text >= 28 px (statements 80+), contrast >= 3:1, nothing in the bottom 380 px or over faces, frame 0 crisp (thumbnail) | by eye on stills |
| G2 no dead frames | longest flat run <= 0.5 s (designed breath <= 1.5 s with a reason) and motion median >= 1.0 (Creator Motion reference measures 2.55, IQR 1.2-7.1; my HyperFrames v2 episodes ~0.3) | gates |
| G3 ending | end hold <= 1.4 s; final shot <= 25% of length | gates |
| G4 one hero | one lit focal point per frame, max 3 | by eye |
| G5 no slop | scan the never-ship list in studio-motion-craft | by eye |
| Graphics share | >= 80% of the time a graphic or real footage is the subject; text <= 4 words per beat besides captions | storyboard check |
| Captions | caption text equals spoken words (script/STT diff); keyword colour only on one word | script |
| Audio | -14 +-1 LUFS, true peak <= -1.5 dBTP, bed 9-14 dB under voice, SFX on visible actions, music ring-out clean | ffmpeg ebur128; I cannot hear: give 5 listen-check timestamps |
| Facts | only owner-stated or measured; "reported" chips where unverified; no tokens, hostnames, private numbers visible | frame check |

Write the numbers into `NOTE.md` (never claim a gate without a number; G1, G4, G5 are judged on the sheet and say so). A failed gate is fixed, not argued. Baseline on 17 Nov 2026: all 12 series-v2 episodes FAIL G2/G3 (2.4 s flat end card, motion median ~0.3). Fix in the next build: cut the end card to <= 1.4 s with a living element, add background motion/grain to every scene, add continuous motion layers behind text.

Extra from video-use: after a render, inspect the rendered file at each cut with a filmstrip+waveform, cap fix passes at 3, and for anything publishable spawn a critic sub-agent briefed to roast (verdict, ranked problems with timecodes, top 5 fixes).
