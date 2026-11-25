---
name: job-eligibility-gate
description: Run on EVERY job before drafting: country/timezone eligibility, ghost-job and scam checks, fit tier. Load whenever evaluating a job.
license: MIT
---

# Eligibility and scam gate (run before any draft)
1. **Country and timezone.** The owner is in Nairobi, Kenya, UTC+3, with no US/EU work authorization. Reject on any of: structured fields (Himalayas locationRestrictions without Kenya/Africa, WWR region "USA Only", RemoteOK "US Only"); text matching `must (reside|be (located|based)) in`, `(US|U\.S\.) (work )?authori[sz]ation`, `right to work`, `no (visa )?sponsorship`, `(EU|EEA|UK)[- ]only`, `W-?2`, `overlap (with|of)` for a timezone that leaves under 4 hours with UTC+3, `legal entit`. "EMEA" alone is NOT enough: read the posting. "We hire through an EOR (Deel, Remote.com)" is fine: both support Kenya.
2. **Ghost job.** Skip if older than 30 days, if the same title is not on the employer's own ATS, if the detail URL is closed (410/404), or if it is an evergreen "talent pool". Prefer the employer's own posting over an aggregator copy.
3. **Scam = hard fail, report once and move on:** any fee, registration or "processing" payment (especially M-Pesa), equipment cheque, bank/ID details before an offer, interview only over WhatsApp/Telegram, a free-mail recruiter claiming a big company, "task release" jobs, account-rental offers.
4. **Fit tier.** A (strong fit and eligibility confirmed, at most about 10 a day): full tailoring and an email to a named person if one exists; B (decent fit): templated tailoring; C: skip. Write the verdict and reason in the tracker either way.
5. Ask the owner (board item with owner set) when location eligibility is ambiguous or the form asks salary, visa, attestations or anything not in the profile file.
