---
name: studio-shorts-clipper
description: Turn a long video (talk, podcast, tutorial, stream) into ranked vertical short clips: transcribe, score segments, snap to clean cut points, reframe to 9:16, animate captions, export per platform. Use when the owner gives long footage or asks for Shorts/Reels/TikToks from an existing video.
---

# Shorts clipper (claude-shorts + OpenClip method)

Pipeline (10 steps, interactive only at the choice step): 1 preflight (duration, resolution, GPU) -> 2 transcribe with word timestamps (ElevenLabs scribe_v1 or local whisper; keep `segments[]` for reading and `captions[] {text,startMs,endMs}` for rendering) -> 3 detect content type (talking head: face-tracked centre crop; screen recording: letterboxed framed layout; podcast: speaker tracking/centre crop) -> 4 score 8-12 candidate segments of 15-55 s -> 5 present ranked candidates with a suggested hook line and one-sentence reason; owner picks -> 6 snap boundaries to natural audio cut points (silence/breath, never mid-word) -> 7 reframe -> 8 clean captions and render (bold / bounce / clean styles; one accent word) -> 9 export per platform (YouTube Shorts, TikTok, Reels) -> 10 validate (length, loudness, caption sync, safe zones).

Scoring rubric (0-100): hook strength 0.30 (bold claim, curiosity gap, pattern interrupt), standalone coherence 0.25 (makes sense with no context), emotional intensity 0.20, value density 0.15, payoff quality 0.10. Reject clips that need earlier context. Title the first 3 seconds with a hook overlay.

Rules: the first frame is already interesting; start on the charged word; one idea per clip; end on the payoff, not mid-sentence; add a looping or callback end when possible. Add moving B-roll (see studio-editing-rules and studio-graphics-toolkit) on beats where the speaker is static for more than ~3 s. Thumbnails: branded cover frame with 3-5 words. Content must be the owner's own or licensed. Tools: ffmpeg `silencedetect`, `video_eyes.py sheet`, scribe/whisper, `render_canvas.mjs` or HyperFrames for animated captions.
