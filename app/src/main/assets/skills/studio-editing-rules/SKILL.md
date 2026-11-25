---
name: studio-editing-rules
description: Rules for editing real footage and transcripts (rough cut, silence/mistake cutting, B-roll, captions cleaning, music/voice, export) learned from real edits in video-editor-skills, video-edit-skill and OpenClip. Use whenever the owner supplies raw footage, a recording or a transcript to cut, caption or polish.
---

# Editing rules (from real edits)

## Principle
The agent plans and decides WHAT; deterministic scripts (ffmpeg, whisper, my tools) do the media work; every stage writes a readable file (`transcript.json`, `edl.json`, `plan.md`, `qa/report.md`); originals are never modified; re-render under a new name (same name can show stale frames).

## Cutting
- Cut from WORDS, not volume (volume-only cut: ~800 pieces on a 34-min class; transcript cut: 120 and loved).
- Keep the best complete take (usually the last); cut false starts plus their restart cue and spoken notes to the editor. Unsure? Keep and flag; a wrong cut is invisible, a flagged keep costs 5 seconds.
- Pacing by content: classes/tutorials tight (max pause 0.5, keep 0.3), vlogs looser (1.0 / 0.6). Cut between phrases at real silence, never inside a word or a breath; keep natural beats after a comma or "First,".
- Scenes need room: start ~0.5 s before the first word, end ~0.6 s after the last (more on endings).
- Zoom/punch-in only on jump cuts; a punch-in lasts one line; a scene change opens at 100%.
- Verify sync after cuts by comparing cut frames against the raw.

## B-roll
Zero-word clips are B-roll (a transcript cut drops them). Watch clips at ~2 fps, not 5 frames. One visual per noun phrase; real footage/screen recording beats AI stills; every shot moves. Use each scene once.

## Captions (for people on mute)
Clean, not verbatim: remove stutters, cut-off words, fillers, self-corrections; write correct spelling of names (check the web), keep the voice but fix slang if asked. Read every cue (a filler rule once deleted the real word "um"=a). 1-3 words per chunk; same box style on everything; never white on light; never over the mouth/chin; reserve the caption band so overlays do not fight it; no lone punctuation line. Burn with libass/ASS (deterministic, per-word colour) or my canvas/HyperFrames captions. Subtitle files start at 00:00.

## Sound
Music louder where nobody talks (about -16 LUFS on B-roll, ducked ~11 dB under voice with a slow rise starting in the speaker's tail). One soft untuned swell into B-roll beats literal foley (zippers and swooshes sound cheap). Check music ends where intended. Do not normalise silent clips (+30 dB hiss). A muffled voice: measure the spectrum (little above 3 kHz = covered mic), fix with EQ/isolation.

## Layout and story
16:9 layouts cover faces in 4:3: scale panels into free space. Get the story right: ask when unsure of facts (the outro once said "see you in a new country" while flying home); never name places the owner wants private.

## Export
H.264 about 50 Mb/s (or H.265 30 Mb/s) is plenty for YouTube; "automatic" made 25 GB for 11 minutes. Match colour tags (Rec.709) so Apple devices do not look faded. Vertical: 1080x1920, 30 fps, loudness -14 LUFS, true peak <= -1.5 dBTP.

## video-use hard rules (browser-use/video-use, installed at ~/Developer/video-use, registered as skill `video-use`)
1 subtitles applied LAST in the filter chain (overlays hide captions otherwise); 2 per-segment extract then lossless `-c copy` concat (no double encode); 3 30 ms audio fades at every segment boundary (afade in/out 0.03) to avoid pops; 4 overlays use `setpts=PTS-STARTPTS+T/TB` so frame 0 sits at the overlay window start; 5 master SRT uses output-timeline offsets (`word.start - segment_start + segment_offset`); 6 never cut inside a word, snap to Scribe word boundaries; 7 pad every cut edge 30-200 ms (ASR drifts 50-100 ms); 8 word-level verbatim ASR only (keep fillers/gaps as signals); 9 cache transcripts per source; 10 build multiple animations with parallel sub-agents; 11 get the owner's OK on a 4-8 sentence plain-English strategy before touching the cut; 12 write session outputs to `<videos_dir>/edit/` (project.md memory, takes_packed.md, edl.json, transcripts/, animations/slot_N/, master.srt, preview.mp4, final.mp4).
Cut craft: audio-first; silences >= 400 ms are the cleanest cuts, 150-400 ms usable with a visual check, < 150 ms unsafe; preserve peaks (laughs, punchlines, reactions); speaker handoffs 400-600 ms air; audio events like (laughs) mark beats; never reason audio and video separately.
Self-eval before showing: filmstrip+waveform at EVERY cut boundary of the RENDERED output (+-1.5 s): flash/jump, waveform spike, hidden subtitle, misaligned overlay; sample first/last 2 s and mid-points; ffprobe duration matches the EDL; measure loudness (ebur128) and RMS per section; max 3 fix passes then flag. For publishable pieces spawn one critic sub-agent that roasts: verdict, ranked problems with timecodes/evidence, top 5 fixes.
Helpers to use: transcribe.py (Scribe, cached), transcribe_batch.py (4 workers), pack_transcripts.py (phrase-level takes_packed.md, break on silence >= 0.5 s), timeline_view.py (filmstrip + waveform at decision points only), render.py (EDL -> preview 720p / final), grade.py (ASC CDL-style grading; look at a frame, change one thing, look again). Needs ELEVENLABS_API_KEY in ~/Developer/video-use/.env (ask the owner; not set yet).
