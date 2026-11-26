# CI result

**Gate: FAIL: assembleDebug failed; 1 NEW test failure(s); only 1073 tests ran (minimum 1860) - a module probably failed to compile**

- RUN_URL: https://github.com/TricretA/my-zafiro/actions/runs/45848691691
- GITHUB_SHA: bd362234df9acdcd6ab508012ee8671eace0fc4f
- GITHUB_REF_NAME: feat/social-workspace-control

| step | outcome |
|---|---|
| python_check | success |
| assemble | failure |
| unit_tests | failure |
| lint | failure |

Unit tests: 1073 run, 1 failed (0 known upstream, 0 flaky upstream, 1 NEW), 0 skipped

## NEW failing tests
- `com.tricreta.zafiro.autonomy.SocialPlatformsTest.quotasContainOnlyEnabledPlatformsAndOwnerOverridesWinOverDefaults` — java.lang.AssertionError: Expected value to be true.

## assemble: key error lines
```
> Task :app:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/WorkspacesVisual.kt:11:43 Cannot access 'val RowColumnParentData?.weight: Float': it is internal in file.
* What went wrong:
Execution failed for task ':app:compileDebugKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:compileDebugKotlin'.
BUILD FAILED in 4m 25s
```
## unit_tests: key error lines
```
> Task :agent-runtime:compileDebugUnitTestKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/test/java/com/niki914/zafiro/chat/agentic/buildin/impl/SaveFileBuiltinTest.kt:17:42 Argument type mismatch: actual type is 'SuspendFunction0<Result<PublicExportResult>>', but 'SuspendFunction3<@ParameterName(...) File, @ParameterName(...) String?, @ParameterName(...) String?, Result<PublicExportResult>>' was expected.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/test/java/com/niki914/zafiro/chat/agentic/buildin/impl/SaveFileBuiltinTest.kt:17:44 Return type mismatch: expected 'Result<PublicExportResult>', actual 'File'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/test/java/com/niki914/zafiro/chat/agentic/buildin/impl/SaveFileBuiltinTest.kt:17:44 Type mismatch: inferred type is 'File', but 'Result<PublicExportResult>' was expected.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/test/java/com/niki914/zafiro/chat/agentic/buildin/impl/StudioRenderBuiltinTest.kt:35:48 Unresolved reference 'error'.
> Task :app:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/WorkspacesVisual.kt:11:43 Cannot access 'val RowColumnParentData?.weight: Float': it is internal in file.
SocialPlatformsTest > quotasContainOnlyEnabledPlatformsAndOwnerOverridesWinOverDefaults FAILED
> Task :autonomy:android:testDebugUnitTest FAILED
* What went wrong:
Execution failed for task ':agent-runtime:compileDebugUnitTestKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':agent-runtime:compileDebugUnitTestKotlin'.
* What went wrong:
Execution failed for task ':app:compileDebugKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:compileDebugKotlin'.
* What went wrong:
Execution failed for task ':autonomy:android:testDebugUnitTest'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':autonomy:android:testDebugUnitTest'.
BUILD FAILED in 2m 2s
```
## lint: key error lines
```
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/WorkspacesVisual.kt:11:43 Cannot access 'val RowColumnParentData?.weight: Float': it is internal in file.
> Task :app:compileDebugKotlin FAILED
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
BUILD FAILED in 2m 20s
```
