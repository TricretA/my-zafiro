# CI result

**Gate: FAIL: assembleDebug failed; only 904 tests ran (minimum 1860) - a module probably failed to compile**

- RUN_URL: https://github.com/TricretA/my-zafiro/actions/runs/45391496313
- GITHUB_SHA: 4d09c72617d4a41460c92d8990bcdaee20c892dd
- GITHUB_REF_NAME: feat/workspaces

| step | outcome |
|---|---|
| python_check | success |
| assemble | failure |
| unit_tests | failure |
| lint | failure |

Unit tests: 904 run, 0 failed (0 known upstream, 0 flaky upstream, 0 NEW), 0 skipped


## assemble: key error lines
```
> Task :agent-runtime:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/LegacyOpenBuiltins.kt:30:83 Return type mismatch: expected 'BuiltinToolResult', actual 'TextToolResult'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/LegacyOpenBuiltins.kt:41:68 Return type mismatch: expected 'BuiltinToolResult', actual 'TextToolResult'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/LegacyOpenBuiltins.kt:43:58 Unresolved reference 'AppName'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/LegacyOpenBuiltins.kt:44:16 Return type mismatch: expected 'BuiltinToolResult', actual 'TextToolResult'.
* What went wrong:
Execution failed for task ':agent-runtime:compileDebugKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':agent-runtime:compileDebugKotlin'.
BUILD FAILED in 3m 4s
```
## unit_tests: key error lines
```
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/LegacyOpenBuiltins.kt:30:83 Return type mismatch: expected 'BuiltinToolResult', actual 'TextToolResult'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/LegacyOpenBuiltins.kt:41:68 Return type mismatch: expected 'BuiltinToolResult', actual 'TextToolResult'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/LegacyOpenBuiltins.kt:43:58 Unresolved reference 'AppName'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/LegacyOpenBuiltins.kt:44:16 Return type mismatch: expected 'BuiltinToolResult', actual 'TextToolResult'.
> Task :agent-runtime:compileDebugKotlin FAILED
* What went wrong:
Execution failed for task ':agent-runtime:compileDebugKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':agent-runtime:compileDebugKotlin'.
BUILD FAILED in 1m 47s
```
## lint: key error lines
```
> Task :agent-runtime:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/LegacyOpenBuiltins.kt:30:83 Return type mismatch: expected 'BuiltinToolResult', actual 'TextToolResult'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/LegacyOpenBuiltins.kt:41:68 Return type mismatch: expected 'BuiltinToolResult', actual 'TextToolResult'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/LegacyOpenBuiltins.kt:43:58 Unresolved reference 'AppName'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/LegacyOpenBuiltins.kt:44:16 Return type mismatch: expected 'BuiltinToolResult', actual 'TextToolResult'.
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
BUILD FAILED in 2m 1s
```
