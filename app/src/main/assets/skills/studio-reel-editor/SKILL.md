---
name: studio-reel-editor
description: Edit raw footage (talking-head recordings, screen recordings, event recaps, ad clips) into a polished reel or short. All heavy processing runs on GitHub Actions (ffmpeg, Whisper). The agent plans cuts from a transcript, never from volume alone, and writes an EDL before touching any file. Use when the owner provides raw footage or a recording to cut, caption, or polish.
---

# Studio Reel Editor — Raw Footage Editing

Agent plans the cut. GitHub Actions does the ffmpeg work. Media Operator handles CapCut polish on-phone.

## Core principle
Cut from WORDS, not volume. Volume-only cut = ~800 pieces on a 34-min class. Transcript cut = 120, cleaner.

## Workflow
1. **Stage raw video to GitHub** — use `studio_render(op="upload_input", id="<job-id>", local_path="<phone path to raw video>")`. This streams files up to 2 GB cleanly without hitting the 20 MB limit.
2. **Transcribe** — Whisper (faster-whisper) with word timestamps on GitHub Actions. Cache transcript per source.
3. **Plan** — produce `edl.json` (array of `{start, end, reason, type}` objects). Get owner OK on the 4–8 sentence plain-English strategy before touching the cut. Mark `(KEEP)`, `(CUT)`, `(FLAG)` per segment.
4. **Rough cut rules**:
   - Keep the best complete take (usually last); cut false starts + restart cues.
   - Pacing: tutorials ≤ 0.5 s max pause; vlogs ≤ 1.0 s max pause.
   - Cut between phrases at real silence, never inside a word or a breath.
   - Start scene ≈ 0.5 s before first word, end ≈ 0.6 s after last.
   - Zoom/punch-in only on jump cuts (one line each). Scene change opens at 100%.
5. **B-roll** — zero-word clips are B-roll candidates. One visual per noun phrase. Real footage or screen recording beats AI stills. Every shot moves. Use each scene once.
6. **Captions** — clean, not verbatim. Remove um/uh unless character. 1–2 lines per card, in safe zone.
7. **Audio** — music bed 9–14 dB under voice. SFX on visible actions only. afade 0.03 at every segment boundary to avoid pops.
8. **Render on GitHub Actions** — commit job JSON via `studio_render(op="commit_file")` and dispatch via `studio_render(op="dispatch")`.
9. **Download to Downloads (or custom folder)** — `studio_render(op="download", id="<job-id>", folder="<user folder if specified, else default Download/Zafiro>")`.
10. **QA** — studio-qa-gates. Filmstrip + waveform at EVERY cut boundary (±1.5 s). Max 3 fix passes.

## Reel edit job schema (for studio-actions-render)
```json
{
  "id": "my-reel",
  "type": "reel",
  "source": "https://url/to/raw.mp4",
  "edl": "jobs/my-reel-edl.json",
  "captions": "jobs/my-reel.srt",
  "music": "energetic",
  "outputFormat": "9:16"
}
```

## Reframe rules
- Talking head → speaker-centered 1080×1920 crop.
- Screen recording landscape → blur-pad to 9:16 or black frame stack with top hook banner.
- Multi-speaker → portrait split (speaker A top, speaker B bottom).

## CapCut on-phone (Media Operator)
After download from GitHub Actions: open CapCut, import, add trending sound or final colour grade, add branded lower-third if needed, export, publish via video-publish.

## Never-do list
- Never modify the source file.
- Never cut inside a word.
- Never deliver without looking at the QA frame sheet.
- Never output a reel with a flat final 2+ s end card (cut to ≤ 1.4 s with a living element).
