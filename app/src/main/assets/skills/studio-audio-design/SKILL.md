---
name: studio-audio-design
description: Audio production for Zafiro video studio — voiceover (Edge TTS free or ElevenLabs API), music (CC0 Mixkit beds from 7 mood categories), SFX (12 categorized CC0 FX packs), and beat-locked mixing at -14 LUFS. On GitHub Actions: runs tts_edge.py and ffmpeg mixing. On-phone: ElevenLabs web or Gemini voice if no Actions token. Routes to the right pipeline per budget and connectivity.
---

# Studio Audio Design

## Voiceover routing
| Scenario | Tool | Where it runs |
|---|---|---|
| Free, automated, no API key | Edge TTS (`tts_edge.py`) | GitHub Actions |
| Premium, natural, ElevenLabs API key present | ElevenLabs API (`tts_elevenlabs.py tts`) | GitHub Actions |
| Owner supplies MP3 (ElevenLabs or recorded) | Word-align (`align_words.py` + Whisper scribe) | GitHub Actions |
| Quick test on-phone | Gemini app text-to-speech | Phone (Media Operator) |

## Voice selection
- Owner voice ID: **Dan, XA3P1cj7XJexe6Zq0ATX** (ElevenLabs)
- Always write the voice script first and send it in the SCRIPT.md. Owner reads and corrects before TTS generation.
- ElevenLabs Free tier is non-commercial. For commercial use, upgrade or use Edge TTS.

## Music routing (CC0 / royalty-free)
7 moods available from `audio/library/music/` in zafiro-studio:
- **energetic** — uptempo, driving, 128+ BPM
- **motivational** — uplifting, rising chords
- **calm** — ambient, low BPM, gentle pads
- **corporate** — neutral, clean, professional
- **mystery** — dark, pulsing, minor key
- **playful** — quirky, staccato, bright
- **cinematic** — orchestral swells, wide dynamics

Specify mood in the render job. The Actions runner selects and mixes automatically.

## SFX routing (CC0)
12 categories: ui-clicks, notifications, whooshes, booms, hits, cash/success, error, nature, crowd, tech, retro-game, ambient-texture.

Use SFX only on **visible actions**: a counter incrementing, a button being clicked, a card flying in. Not on every cut.

## Mixing spec (mandatory)
- Voice: 0 dB relative reference
- Music bed: −9 to −14 dB under voice (duck harder during fast-talking sections)
- SFX: −3 to −6 dB, transient peaks allowed
- Master: **−14 LUFS ± 1**, true peak ≤ −1.5 dBTP
- afade 0.03 s at every audio boundary (no pops)
- Music ring-out clean (no abrupt cut at end)

## Audio job schema (adds to canvas/reel/clip jobs)
```json
{
  "voice": "https://url/to/voice.mp3",
  "music": "energetic",
  "sfx": true,
  "lufsTarget": -14
}
```

## On-phone fallback (Media Operator)
If GitHub Actions token is missing or quota exhausted:
1. Open Gemini app → "Voice" → paste script → generate → download MP3.
2. Or open ElevenLabs web → Speech Synthesis → paste script → download.
3. Upload MP3 to zafiro-studio via Contents API, then dispatch render.
