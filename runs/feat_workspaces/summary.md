# CI result

**Gate: FAIL: assembleDebug failed; only 1452 tests ran (minimum 1860) - a module probably failed to compile**

- RUN_URL: https://github.com/TricretA/my-zafiro/actions/runs/43710765898
- GITHUB_SHA: 179916fb877f22668e204a741a0c1473d91e7237
- GITHUB_REF_NAME: feat/workspaces

| step | outcome |
|---|---|
| python_check | success |
| assemble | failure |
| unit_tests | failure |
| lint | failure |

Unit tests: 1452 run, 0 failed (0 known upstream, 0 flaky upstream, 0 NEW), 0 skipped


## assemble: key error lines
```
> Task :app:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/SkillsSettingsContent.kt:145:15 Type 'MutableState<String>' has no method 'setValue(Nothing?, KMutableProperty0<*>, String)', so it cannot serve as a delegate for var (read-write property).
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/TeamPagesContent.kt:225:26 Unresolved reference 'fillMaxWidth'.
* What went wrong:
Execution failed for task ':app:compileDebugKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:compileDebugKotlin'.
BUILD FAILED in 5m 16s
```
## unit_tests: key error lines
```
> Task :app:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/SkillsSettingsContent.kt:145:15 Type 'MutableState<String>' has no method 'setValue(Nothing?, KMutableProperty0<*>, String)', so it cannot serve as a delegate for var (read-write property).
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/TeamPagesContent.kt:225:26 Unresolved reference 'fillMaxWidth'.
* What went wrong:
Execution failed for task ':app:compileDebugKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:compileDebugKotlin'.
BUILD FAILED in 4m 14s
```
## lint: key error lines
```
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/SkillsSettingsContent.kt:145:15 Type 'MutableState<String>' has no method 'setValue(Nothing?, KMutableProperty0<*>, String)', so it cannot serve as a delegate for var (read-write property).
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/TeamPagesContent.kt:225:26 Unresolved reference 'fillMaxWidth'.
> Task :app:compileDebugKotlin FAILED
> Task :agent-runtime:lintDebug FAILED
> Task :store:lintDebug FAILED
> Task :business:notification:lintDebug FAILED
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
BUILD FAILED in 2m 47s
```
