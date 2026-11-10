# CI result

**Gate: FAIL: only 548 tests ran (minimum 1770) - a module probably failed to compile**

- RUN_URL: https://github.com/TricretA/my-zafiro/actions/runs/42786874935
- GITHUB_SHA: a6aeff9ebc2d056c6be7e1f4e1d967ce42658a6c
- GITHUB_REF_NAME: feat/foundation

| step | outcome |
|---|---|
| python_check | success |
| assemble | success |
| unit_tests | cancelled |
| lint | skipped |

Unit tests: 548 run, 0 failed (0 known upstream, 0 NEW), 0 skipped


## unit_tests: key error lines
```
ConversationPersisterTest > trackActivity_bumpsUpdatedAtOnApprovalAndTurnEnd FAILED
```
