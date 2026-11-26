# CI result

**Gate: FAIL: assembleDebug failed; 1 NEW test failure(s); only 1616 tests ran (minimum 1860) - a module probably failed to compile**

- RUN_URL: https://github.com/TricretA/my-zafiro/actions/runs/45858588278
- GITHUB_SHA: 9b4fe2965fd6244d0c1752c14ccd3018f59fcba1
- GITHUB_REF_NAME: fix/video-studio-ci

| step | outcome |
|---|---|
| python_check | success |
| assemble | failure |
| unit_tests | failure |
| lint | failure |

Unit tests: 1616 run, 1 failed (0 known upstream, 0 flaky upstream, 1 NEW), 0 skipped

## NEW failing tests
- `com.tricreta.zafiro.autonomy.SocialPlatformsTest.quotasContainOnlyEnabledPlatformsAndOwnerOverridesWinOverDefaults` — java.lang.AssertionError: Expected value to be true.

## assemble: key error lines
```
> Task :app:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/WorkspacesVisual.kt:11:43 Cannot access 'val RowColumnParentData?.weight: Float': it is internal in file.
* What went wrong:
Execution failed for task ':app:compileDebugKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:compileDebugKotlin'.
BUILD FAILED in 6m 2s
```
## unit_tests: key error lines
```
> Task :app:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/WorkspacesVisual.kt:11:43 Cannot access 'val RowColumnParentData?.weight: Float': it is internal in file.
SocialPlatformsTest > quotasContainOnlyEnabledPlatformsAndOwnerOverridesWinOverDefaults FAILED
> Task :autonomy:android:testDebugUnitTest FAILED
* What went wrong:
Execution failed for task ':app:compileDebugKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:compileDebugKotlin'.
* What went wrong:
Execution failed for task ':autonomy:android:testDebugUnitTest'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':autonomy:android:testDebugUnitTest'.
BUILD FAILED in 4m 34s
```
## lint: key error lines
```
> Task :app:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/WorkspacesVisual.kt:11:43 Cannot access 'val RowColumnParentData?.weight: Float': it is internal in file.
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
BUILD FAILED in 3m 17s
```
