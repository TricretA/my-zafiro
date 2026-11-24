# CI result

**Gate: FAIL: assembleDebug failed; only 904 tests ran (minimum 1860) - a module probably failed to compile**

- RUN_URL: https://github.com/TricretA/my-zafiro/actions/runs/45389984978
- GITHUB_SHA: e7cf6bf8e6dac49256d3a18b61022da80757052b
- GITHUB_REF_NAME: sync/upstream-20261124

| step | outcome |
|---|---|
| python_check | success |
| assemble | failure |
| unit_tests | failure |
| lint | failure |

Unit tests: 904 run, 0 failed (0 known upstream, 0 flaky upstream, 0 NEW), 0 skipped


## assemble: key error lines
```
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/BuiltinToolRegistry.kt:15:53 Unresolved reference 'LaunchAppBuiltin'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/BuiltinToolRegistry.kt:27:53 Unresolved reference 'OpenUriBuiltin'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/BuiltinToolRegistry.kt:66:17 Unresolved reference 'OpenUriBuiltin'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/BuiltinToolRegistry.kt:67:17 Unresolved reference 'LaunchAppBuiltin'.
> Task :agent-runtime:compileDebugKotlin FAILED
* What went wrong:
Execution failed for task ':agent-runtime:compileDebugKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':agent-runtime:compileDebugKotlin'.
BUILD FAILED in 3m 41s
```
## unit_tests: key error lines
```
> Task :agent-runtime:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/BuiltinToolRegistry.kt:15:53 Unresolved reference 'LaunchAppBuiltin'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/BuiltinToolRegistry.kt:27:53 Unresolved reference 'OpenUriBuiltin'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/BuiltinToolRegistry.kt:66:17 Unresolved reference 'OpenUriBuiltin'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/BuiltinToolRegistry.kt:67:17 Unresolved reference 'LaunchAppBuiltin'.
* What went wrong:
Execution failed for task ':agent-runtime:compileDebugKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':agent-runtime:compileDebugKotlin'.
BUILD FAILED in 2m 7s
```
## lint: key error lines
```
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/BuiltinToolRegistry.kt:15:53 Unresolved reference 'LaunchAppBuiltin'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/BuiltinToolRegistry.kt:27:53 Unresolved reference 'OpenUriBuiltin'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/BuiltinToolRegistry.kt:66:17 Unresolved reference 'OpenUriBuiltin'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/BuiltinToolRegistry.kt:67:17 Unresolved reference 'LaunchAppBuiltin'.
> Task :agent-runtime:compileDebugKotlin FAILED
> Task :store:lintDebug FAILED
> Task :business:notification:lintDebug FAILED
* What went wrong:
Execution failed for task ':agent-runtime:compileDebugKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':agent-runtime:compileDebugKotlin'.
* What went wrong:
Execution failed for task ':store:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':store:lintDebug'.
* What went wrong:
Execution failed for task ':business:notification:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':business:notification:lintDebug'.
BUILD FAILED in 2m 41s
```
