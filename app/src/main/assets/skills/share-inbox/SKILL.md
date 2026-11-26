---
name: share-inbox
description: Get a generated image, video, audio or text out of ChatGPT, Gemini, Flow, ElevenLabs or Chrome into Zafiro with the Android Share sheet, verify it, and use it. Use whenever a file made in another app is needed.
license: MIT
---

# Share to Zafiro (the inbox)
Zafiro appears in the Android share sheet as **Zafiro inbox**. Anything shared to it is copied into `outputs/inbox/` (no storage permission, no guessing which file is newest) and listed by the `inbox` tool. Phone behaviour of this path is UNVERIFIED until the owner confirms it: if a step does not match the screen, say so, use the fallback below, and record the real label as a lesson.
## Steps
1. **Mark the time.** `inbox op=list limit=1`: remember the newest `time` (use 0 if the inbox is empty). Never rely on "the latest file".
2. **Generate** in the app (ChatGPT, Gemini, Flow, ElevenLabs) and wait until the result is fully shown.
3. **Open the share sheet** on the result:
   - ChatGPT / Gemini image: tap the image to open it full screen, then the share icon (arrow out of a box or three dots, then Share). Long-press on the image also offers Share in many versions.
   - Flow (video): open the finished clip, then the download or share icon, then Share.
   - ElevenLabs / audio: the audio file's share or download menu, then Share.
   - Chrome: long-press an image, choose Share image. For a page or text: three dots menu, Share, or select text and tap Share.
   Use `screen_operation` read to find the share icon by its label; do not tap by position.
4. **Pick the target.** In the share sheet choose **Zafiro inbox**. If it is not in the first row, scroll the app row or tap More. A short "Saved to the Zafiro inbox" message appears; the screen returns to the source app.
5. **Verify.** `inbox op=list since=<time from step 1> kind=image|video|audio|text`. Require: the item exists, `kind` is right, `size` is plausible (an image under 10 KB or a video under 100 KB is probably a thumbnail or an error page), and for images open it with `view_image` on its `path`. Several items: take the one whose `source` is the app you used.
6. **Use it.** `inbox op=take id=<id> to=outputs/<workspace>/` moves it where you need it (stays inside Zafiro's files folder) and returns the new path. Then continue (edit, `share_file`, publish). `inbox op=delete id=<id>` for rejects. Never publish before you have looked at the file.
## Rules
- Share only content you generated or the owner owns. Respect each app's terms: ChatGPT's terms ban programmatic extraction of output, so keep human pacing, one account, and prefer Gemini first. Generated media carries provenance marks: never strip them and set the AI flag when posting.
- Stop with `take_over` on CAPTCHA, login or consent screens.
- Text shared to the inbox becomes a .txt file you can read with `inbox op=list kind=text` then `take`.
## Fallback (if the share sheet has no Zafiro inbox or the app blocks sharing)
Use the MediaStore or shell route from `content-factory-screen`: note the time before generating, then list images or videos added after that time (`content query --uri content://media/external/images/media --projection _display_name:date_added --sort "date_added DESC"` or `ls -t /sdcard/Pictures /sdcard/Download`), copy the first new one into `outputs/`, and verify size and that it decodes.
