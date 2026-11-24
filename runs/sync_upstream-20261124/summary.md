# CI result

**Gate: FAIL: assembleDebug failed; only 1492 tests ran (minimum 1860) - a module probably failed to compile**

- RUN_URL: https://github.com/TricretA/my-zafiro/actions/runs/45393058814
- GITHUB_SHA: 1b0ed0c85c910679806bb4e0b21957b536b767f7
- GITHUB_REF_NAME: sync/upstream-20261124

| step | outcome |
|---|---|
| python_check | success |
| assemble | failure |
| unit_tests | failure |
| lint | failure |

Unit tests: 1492 run, 0 failed (0 known upstream, 0 flaky upstream, 0 NEW), 0 skipped


## assemble: key error lines
```
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/AboutSettingsContent.kt:156:21 Type 'MutableState<Boolean>' has no method 'setValue(Nothing?, KMutableProperty0<*>, Boolean)', so it cannot serve as a delegate for var (read-write property).
> Task :app:compileDebugKotlin FAILED
* What went wrong:
Execution failed for task ':app:compileDebugKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:compileDebugKotlin'.
BUILD FAILED in 6m 2s
```
## unit_tests: key error lines
```
> Task :app:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/AboutSettingsContent.kt:156:21 Type 'MutableState<Boolean>' has no method 'setValue(Nothing?, KMutableProperty0<*>, Boolean)', so it cannot serve as a delegate for var (read-write property).
* What went wrong:
Execution failed for task ':app:compileDebugKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:compileDebugKotlin'.
BUILD FAILED in 4m 24s
```
## lint: key error lines
```
> Task :agent-runtime:lintDebug FAILED
> Task :app:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/AboutSettingsContent.kt:156:21 Type 'MutableState<Boolean>' has no method 'setValue(Nothing?, KMutableProperty0<*>, Boolean)', so it cannot serve as a delegate for var (read-write property).
> Task :store:lintDebug FAILED
> Task :business:notification:lintDebug FAILED
* What went wrong:
Execution failed for task ':agent-runtime:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':agent-runtime:lintDebug'.
* What went wrong:
Execution failed for task ':app:compileDebugKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:compileDebugKotlin'.
* What went wrong:
Execution failed for task ':store:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':store:lintDebug'.
* What went wrong:
Execution failed for task ':business:notification:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':business:notification:lintDebug'.
BUILD FAILED in 3m 2s
```
