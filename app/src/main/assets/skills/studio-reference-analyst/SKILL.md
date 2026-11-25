---
name: studio-reference-analyst
description: Study a video the owner supplies (an Instagram reel he screen-recorded or downloaded, or one of my own renders) and turn it into a style sheet and shot breakdown. Use when the owner uploads or names a reference video, or says "learn the style of this".
---

# Reference analyst (my eyes)

I cannot browse Instagram (Agent-Reach only lists profiles/posts, needs a login, no video content). I need the FILE: screen recording or download, uploaded to the chat.

## Protocol
1. Probe and sheet: `python3 -I studio/tools/video_eyes.py report ref.mp4 --out ref_eyes` (duration, fps, hard cuts, average shot length, cuts per 10 s, motion band, flat runs, end hold, loudness). Then read `ref_eyes/sheet.jpg`; for key moments pull full frames: `ffmpeg -ss T -i ref.mp4 -frames:v 1 f.png`.
2. Per shot or shot-group, fill 5 aspects: content (what is shown), composition (layout, scale, where the eye goes), motion (entry/exit, camera, speed, easing feel), type (font class, size, case, colour logic, caption chunking), colour and sound (palette, ground, music mood, SFX placement). Classify each shot: real footage, screen recording, animated UI, vector graphic, AI video, still.
3. Structure: hook device and timing, beats and their one belief each, list handling, payoff, CTA, loop/callback, length vs speech rate.
4. If narration matters and credits allow, transcribe (ElevenLabs scribe) to get words per second and hook wording; otherwise estimate from the picture.
5. Write `REFERENCE.md`: what makes it work (3 specific things), the style sheet (see studio-style-bank), what to borrow (structure, pacing, device types) and what not (content, branding, exact shots), and the 3-5 questions for the owner (own voice?, length?, topic?).
6. Feed the result to the story and scene directors; build one test frame before the full video.

## Notes
- Motion classification matters: never assume a reference uses video when it uses animated stills.
- Hash-tag/trend claims need data from the owner's analytics; do not invent.
