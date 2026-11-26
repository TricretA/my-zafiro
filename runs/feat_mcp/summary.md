# CI result

**Gate: FAIL: assembleDebug failed; only 1609 tests ran (minimum 1860) - a module probably failed to compile**

- RUN_URL: https://github.com/TricretA/my-zafiro/actions/runs/45785504056
- GITHUB_SHA: 2b269813271d72b6c9231c89b1ff8fecb4398883
- GITHUB_REF_NAME: feat/mcp

| step | outcome |
|---|---|
| python_check | success |
| assemble | failure |
| unit_tests | failure |
| lint | failure |

Unit tests: 1609 run, 0 failed (0 known upstream, 0 flaky upstream, 0 NEW), 0 skipped


## assemble: key error lines
```
> Task :app:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1555:18 Cannot access 'var installedStoreForTest: Boolean': it is private in 'com/niki914/zafiro/repo/XRepo'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1579:19 Cannot access 'var installedStoreForTest: Boolean': it is private in 'com/niki914/zafiro/repo/XRepo'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1592:19 Cannot access 'var installedStoreForTest: Boolean': it is private in 'com/niki914/zafiro/repo/XRepo'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1595:19 Cannot access 'var installedStoreForTest: Boolean': it is private in 'com/niki914/zafiro/repo/XRepo'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1613:19 Cannot access 'var installedStoreForTest: Boolean': it is private in 'com/niki914/zafiro/repo/XRepo'.
* What went wrong:
Execution failed for task ':app:compileDebugKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:compileDebugKotlin'.
BUILD FAILED in 6m 16s
```
## unit_tests: key error lines
```
> Task :app:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1555:18 Cannot access 'var installedStoreForTest: Boolean': it is private in 'com/niki914/zafiro/repo/XRepo'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1579:19 Cannot access 'var installedStoreForTest: Boolean': it is private in 'com/niki914/zafiro/repo/XRepo'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1592:19 Cannot access 'var installedStoreForTest: Boolean': it is private in 'com/niki914/zafiro/repo/XRepo'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1595:19 Cannot access 'var installedStoreForTest: Boolean': it is private in 'com/niki914/zafiro/repo/XRepo'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1613:19 Cannot access 'var installedStoreForTest: Boolean': it is private in 'com/niki914/zafiro/repo/XRepo'.
* What went wrong:
Execution failed for task ':app:compileDebugKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:compileDebugKotlin'.
BUILD FAILED in 4m 31s
```
## lint: key error lines
```
> Task :app:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1555:18 Cannot access 'var installedStoreForTest: Boolean': it is private in 'com/niki914/zafiro/repo/XRepo'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1579:19 Cannot access 'var installedStoreForTest: Boolean': it is private in 'com/niki914/zafiro/repo/XRepo'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1592:19 Cannot access 'var installedStoreForTest: Boolean': it is private in 'com/niki914/zafiro/repo/XRepo'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1595:19 Cannot access 'var installedStoreForTest: Boolean': it is private in 'com/niki914/zafiro/repo/XRepo'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1613:19 Cannot access 'var installedStoreForTest: Boolean': it is private in 'com/niki914/zafiro/repo/XRepo'.
> Task :agent-runtime:lintDebug FAILED
> Task :store:lintDebug FAILED
> Task :business:notification:lintDebug FAILED
> Task :libs:shizuku:lintDebug FAILED
* What went wrong:
Execution failed for task ':app:compileDebugKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:compileDebugKotlin'.
* What went wrong:
Execution failed for task ':agent-runtime:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':agent-runtime:lintDebug'.
* What went wrong:
Execution failed for task ':store:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':store:lintDebug'.
* What went wrong:
Execution failed for task ':business:notification:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':business:notification:lintDebug'.
* What went wrong:
Execution failed for task ':libs:shizuku:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':libs:shizuku:lintDebug'.
BUILD FAILED in 3m 31s
```
