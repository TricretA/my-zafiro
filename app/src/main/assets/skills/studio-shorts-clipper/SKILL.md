---
name: studio-shorts-clipper
description: Turn a long video (podcast, talk, tutorial, gaming VOD, live stream) into ranked, reframed, captioned vertical 9:16 Shorts, Reels, or TikToks. All heavy processing (Whisper, ffmpeg, viral scoring) runs on GitHub Actions via zafiro-studio. The Media Operator handles phone-side upload. Use whenever the owner provides a long video or stream link and wants viral clips extracted.
---

# Studio Shorts Clipper — 5-Signal Viral Clipping Pipeline

Long-video → viral 9:16 clips. Processing runs on GitHub Actions (zafiro-studio). Dispatch via studio-actions-render.

## 5-Signal Scoring Engine
Clips are ranked by combining:
1. **Acoustic jumps** — sudden loudness spikes (laugh, gasp, applause, exclamation)
2. **Transcript moments** — high-energy phrases, questions, surprising facts
3. **Scenario curiosity gaps** — quiet moments where someone reveals/makes/tries something
4. **Viral hook match** — similarity to 3,481-Short hook database structures
5. **3-layer dedup** — no clip reuses >40% timestamp overlap, >70% transcript similarity, or >40% segment footage

## Hook Structures (empirical multipliers)
| Hook type | Click multiplier |
|---|---|
| Number promise ("I made $X in Y days") | 2.46x |
| Shock fact | 1.68x |
| Relatable ("When you...") | 1.48x |
| Withheld / negation ("Not what you think") | 1.41x |
| Question | 1.24x |
| Vague teaser ("Wait for it...") | 0.67x ❌ |
| Bare label ("Day 1") | 0.88x ❌ |

Always draft 3–4 hook variants per clip. Pick the highest-scoring structure.

## 10-Step Pipeline
1. **Preflight** — probe resolution, fps, duration, audio streams. Never modify source.
2. **Transcribe** — Whisper (faster-whisper) or ElevenLabs Scribe with word timestamps. Mark `(LOUD)` spikes.
3. **Detect & rank** — acoustic + transcript + curiosity gap + 3-layer dedup + sentence-boundary snapping.
4. **Hook engineering** — 3–4 hook variants per strong clip (structures above).
5. **Owner checkpoint** — present ranked clip table (timestamps, duration, hook, why it works). Render top picks autonomously if no reply in 24 h.
6. **Snap cuts** — natural pauses/silences, 30 ms afade at joins, never cut mid-word.
7. **Reframe to 9:16** — talking head: speaker-centered 1080×1920 crop or blur-pad. Screen recording: blur-pad or black stack with hook banner. Gaming: split (facecam top, gameplay bottom).
8. **Captions & music** — Bounce (Poppins ExtraBold), Hype (Anton), or Clean captions. CC0 music bed ducked under speech. SFX (boom/hit/whoosh) only on payoffs/cuts.
9. **Export** — H.264 BT.709, -14 LUFS, ≤ -1.5 dBTP, 1080×1920.
10. **QA** — studio-qa-gates: no frozen frames, no clipped words, captions readable in safe zones.

## Clip job schema (for studio-actions-render)
```json
{
  "id": "clip-podcast-ep12",
  "type": "clip",
  "source": "https://url/to/source.mp4",
  "maxClips": 5,
  "maxDurationSeconds": 60,
  "captionStyle": "bounce",
  "music": "energetic",
  "reframe": "talking-head"
}
```

## Caption styles
- **Bounce** — Poppins ExtraBold white, 2-word groups, word-level highlight, bouncy reveal
- **Hype** — Anton uppercase, full line, high contrast
- **Clean** — regular weight, 1–2 lines, subtitle position
- **Boxed** — Bebas Neue, coloured background box

## On-phone work (Media Operator)
After render downloads: open CapCut on phone, import clip, add final polish (trending sound, platform hashtags), publish via video-publish skill.
