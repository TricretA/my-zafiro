---
name: social-voice-and-slop
description: The writing gate for every draft: voice profile, experience bank, the humanizer pass and the 10-point checklist. Load before writing or reviewing any post, caption or comment.
license: MIT
---

# Voice and slop gate
Platforms demote generic, repetitive or obviously machine-written content, and readers notice. Every draft goes through this gate before it is queued or posted.
1. **Voice.** Load `voice-builder` / `writing-style-and-tone` and the owner's voice notes in `social/RULES.md` and `memory`. Write like the owner talks: plain words, short sentences, Nairobi context where true.
2. **Experience bank.** `social/experience_bank.md` holds the owner's REAL anecdotes, prices, places, mistakes and opinions (add a line whenever the owner tells you something). Every draft uses at least one item or one checkable specific (a number, a name, a date, a tool). If the bank is empty or has nothing relevant, write the draft from checkable public facts and add a board item for the owner: "Add 3 real stories about <topic> to social/experience_bank.md". NEVER invent a personal story, client, result or price.
3. **Humanizer pass.** Run the draft through the `humanizer` skill (and `stop-slop`): cut staged openers, not-X-but-Y contrasts, forced triads, dash overload, inflated claims, sales language, stock AI words, one-line closers. Then ask: "what still sounds like AI?" and fix it.
4. **10-point checklist** (pass at least 8; automatic fail on a Tier 1 AI phrase, a fabricated personal claim or a missing required AI label): (1) a specific detail, (2) a real opinion, (3) varied sentence length, (4) no AI vocabulary, (5) differs from the last 10 posts on that platform (check `social/drafts/<platform>/`), (6) no generic ending, (7) matches the voice, (8) adds something not in the first search result, (9) native to the platform (length, format, tone), (10) the owner would say it aloud.
5. **Variation memory.** Keep the opener and structure of the last 10 posts per platform in `social/drafts/<platform>/index.md`; block repeats.
6. **Replies** are short and specific, sometimes one line, never templated, never instant, no emoji spam.
7. The Reviewer re-checks all of this and never approves its own work.
