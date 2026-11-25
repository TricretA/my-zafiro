---
name: studio-editing-styles
description: The owner's growing library of editing styles, techniques and pacing numbers learned from reference videos (stored in the repo folder studio-editing-styles/). Use at the start of ANY video plan to pick a primary style and borrowed techniques, and after the owner uploads new reference videos to study and add them. Learn editing, never content.
---
# Editing styles library
Location in repo: `studio-editing-styles/` (README, CATALOG, styles/S1-S12, TECHNIQUES, BUILD_RECIPES, PACING_TABLE, styles.json, LOG).
## When planning a video
1. Read `studio-editing-styles/CATALOG.md`; choose ONE primary style and at most two borrowed techniques (IDs in TECHNIQUES.md). State them in the plan.
2. Build using `BUILD_RECIPES.md` (my engine functions and numbers).
3. QA against `PACING_TABLE.md` numbers: cuts per 10 s, motion median, sound event every ~0.35 s, -14 LUFS, one caption style that never moves, hard cut on a sound hit plus an entry animation.
## When the owner gives new reference videos (he uploads to `sample videos/`)
Run `python3 -I studio/tools/style_lab.py "sample videos/<file>" studio-editing-styles/analysis/vN`, read the contact sheet, look at transitions with `video_eyes.py sheet --every 0.3`, then add/extend a style file, append new technique IDs, update `styles.json`, `CATALOG.md`, `LOG.md`, commit and push. Do not store other creators' handles, characters or scripts as assets.
