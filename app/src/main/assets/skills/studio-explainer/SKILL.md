---
name: studio-explainer
description: Make a clean 45–90 second product demo or concept explainer video rendered to MP4 on GitHub Actions. Three tiers — Default (calm, premium), Engaging (story arc, more cuts), High Energy (fast pacing, bold typography, SFX on every beat). Routes to the right canvas engine and delivers a storyboard for approval before rendering.
---

# Studio Explainer — 3-Tier Explainer Engine

Pick ONE tier based on the content and audience energy.

## Tier comparison
| | Default | Engaging | High Energy |
|---|---|---|---|
| Pacing | 1 new visual / 2–3 s | 1 new visual / 1.5–2 s | 1 new visual / 0.5–1.5 s |
| Look | Airy, light canvas, restrained | Story arc, character movement | Bold type, SFX on every beat |
| Use | Homepage demo, investor deck | Social explainer, educational | TikTok / Reels / viral hooks |
| Engine | studio-canvas-motion-engine (Clean Tech style) | studio-canvas-motion-engine (Creator Motion or Pop Info) | studio-canvas-motion-engine (Creator Motion) or studio-bang-motion |
| Length | 45–90 s | 30–60 s | 15–45 s |

---

## Default Explainer

A calm, premium product demo. The camera pushes into one line of text, rides along with typing, pulls back to reveal the result, glides to the next feature.

**Brief (ask once, then go)**:
- Product: name + what it does. URL preferred.
- The one thing the viewer must remember.
- Brand: colours, fonts, logo — or "use the website."
- Format: 16:9 or 9:16. Default 9:16.
- Features or workflow steps (3–5). If a URL given, extract from the site.

**Rules**: Read the URL first. Never invent stats, counts, testimonials. Numbers only if stated by the user or the website. No real people's faces. No third-party logos unless supplied.

---

## Engaging Explainer

A story arc: problem → tension → solution → proof → CTA. Character or stickman carries through. 3–5 scene swaps.

**Story structure**:
1. Hook (problem statement, curiosity gap) — 0–5 s
2. Aggravate (why this hurts) — 5–15 s
3. Pivot (here's the solution) — 15–25 s
4. Proof (show it working, number) — 25–45 s
5. CTA — last 5 s

---

## High Energy Explainer

Fast cuts. Bold impact text. Sound-locked to words. Treat every sentence as a new shot.

**Rules**:
- Every on-screen word anchored to `at('spoken phrase')` within 0.03 s
- Cut only at idea boundaries
- Emphasis on stressed words; counters hit exactly when spoken
- Minimum: `slam` (blur+oversize→expo settle) on every new claim
- SFX: boom on a number reveal, tick on a list item, whoosh on every cut

---

## Common delivery contract (all tiers)
1. STORYBOARD first (beats table: time / on-screen words / visual device / motion / sound). Owner may review before render.
2. Voice: free Edge TTS (`tts_edge.py`) autonomous, or ElevenLabs if `ELEVENLABS_API_KEY` present.
3. Render on GitHub Actions via studio-actions-render.
4. QA: studio-qa-gates (G0–G5, audio -14 LUFS, captions diff).
5. Deliver: mp4 + cover frame + caption text + hashtags under `outputs/video/<project>/`.
