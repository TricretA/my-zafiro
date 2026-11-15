# CI result

**Gate: FAIL: assembleDebug failed; only 832 tests ran (minimum 1860) - a module probably failed to compile**

- RUN_URL: https://github.com/TricretA/my-zafiro/actions/runs/43693801066
- GITHUB_SHA: 5e71093452316ec24e34da43088b597fb087619c
- GITHUB_REF_NAME: feat/workspaces

| step | outcome |
|---|---|
| python_check | success |
| assemble | failure |
| unit_tests | failure |
| lint | failure |

Unit tests: 832 run, 0 failed (0 known upstream, 0 flaky upstream, 0 NEW), 0 skipped


## assemble: key error lines
```
> Task :agent-runtime:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:11:48 Unresolved reference 'RoutineStore'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:40:22 Unresolved reference 'RoutineStore'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:47:52 Unresolved reference 'steps'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:49:77 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:58:23 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:58:44 Unresolved reference 'params'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:58:61 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:58:83 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:58:117 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:59:130 Unresolved reference 'joinToString'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:67:65 Unresolved reference 'RoutineStore'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:68:24 Unresolved reference 'routines'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:71:19 Unresolved reference 'routines'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:71:45 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:73:37 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:73:60 Unresolved reference 'platform'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:73:92 Unresolved reference 'description'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:74:41 Unresolved reference 'params'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:74:67 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:74:80 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:74:93 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:78:19 Unresolved reference 'errors'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:78:62 Unresolved reference 'errors'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:78:101 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:78:112 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/routine/RoutineStore.kt:23:1 Syntax error: Unclosed comment.
* What went wrong:
Execution failed for task ':agent-runtime:compileDebugKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':agent-runtime:compileDebugKotlin'.
BUILD FAILED in 2m 56s
```
## unit_tests: key error lines
```
> Task :agent-runtime:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:11:48 Unresolved reference 'RoutineStore'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:40:22 Unresolved reference 'RoutineStore'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:47:52 Unresolved reference 'steps'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:49:77 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:58:23 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:58:44 Unresolved reference 'params'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:58:61 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:58:83 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:58:117 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:59:130 Unresolved reference 'joinToString'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:67:65 Unresolved reference 'RoutineStore'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:68:24 Unresolved reference 'routines'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:71:19 Unresolved reference 'routines'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:71:45 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:73:37 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:73:60 Unresolved reference 'platform'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:73:92 Unresolved reference 'description'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:74:41 Unresolved reference 'params'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:74:67 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:74:80 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:74:93 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:78:19 Unresolved reference 'errors'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:78:62 Unresolved reference 'errors'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:78:101 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:78:112 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/routine/RoutineStore.kt:23:1 Syntax error: Unclosed comment.
> Task :autonomy:android:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceDef.kt:66:1 Syntax error: Unclosed comment.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:24:30 Unresolved reference 'WorkspaceDef'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:25:42 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:26:66 Unresolved reference 'version'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:28:28 Unresolved reference 'agents'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:28:41 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:32:94 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:35:13 Unresolved reference 'agents'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:35:30 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:36:27 Suspension functions can only be called within coroutine body.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:36:35 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:37:17 Suspension functions can only be called within coroutine body.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:39:28 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:39:41 Unresolved reference 'name'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:39:64 Unresolved reference 'instructions'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:39:124 Unresolved reference 'tools'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:40:32 Unresolved reference 'skills'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:40:73 Unresolved reference 'usesScreen'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:41:97 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:46:24 Unresolved reference 'tasks'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:46:47 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:46:51 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:47:34 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:48:13 Unresolved reference 'tasks'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:48:29 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:49:33 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:49:39 Unresolved reference 'key'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:50:27 Suspension functions can only be called within coroutine body.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:51:17 Suspension functions can only be called within coroutine body.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:51:70 Unresolved reference 'defaultEnabled'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:52:17 Suspension functions can only be called within coroutine body.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:54:49 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:54:64 Unresolved reference 'version'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:54:82 Unresolved reference 'quotas'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:55:34 Unresolved reference 'agents'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:55:51 Unresolved reference 'tasks'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:59:33 Unresolved reference 'WorkspaceDef'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:61:36 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:61:66 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:63:43 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:65:13 Unresolved reference 'tasks'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:65:29 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:66:33 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:66:39 Unresolved reference 'key'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:67:27 Suspension functions can only be called within coroutine body.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:67:39 Return type mismatch: expected 'ERROR CLASS: Cannot infer type variable TypeVariable(_L)', actual 'Unit'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:68:35 Unresolved reference 'defaultEnabled'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:69:17 Suspension functions can only be called within coroutine body.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:70:27 Suspension functions can only be called within coroutine body.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:75:31 Unresolved reference 'WorkspaceDef'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:85:80 Unresolved reference 'id'.
* What went wrong:
```
## lint: key error lines
```
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:11:48 Unresolved reference 'RoutineStore'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:40:22 Unresolved reference 'RoutineStore'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:47:52 Unresolved reference 'steps'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:49:77 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:58:23 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:58:44 Unresolved reference 'params'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:58:61 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:58:83 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:58:117 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:59:130 Unresolved reference 'joinToString'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:67:65 Unresolved reference 'RoutineStore'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:68:24 Unresolved reference 'routines'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:71:19 Unresolved reference 'routines'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:71:45 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:73:37 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:73:60 Unresolved reference 'platform'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:73:92 Unresolved reference 'description'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:74:41 Unresolved reference 'params'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:74:67 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:74:80 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:74:93 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:78:19 Unresolved reference 'errors'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:78:62 Unresolved reference 'errors'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:78:101 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/buildin/impl/ScreenRoutineBuiltin.kt:78:112 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/main/java/com/niki914/zafiro/chat/agentic/routine/RoutineStore.kt:23:1 Syntax error: Unclosed comment.
> Task :agent-runtime:compileDebugKotlin FAILED
> Task :autonomy:android:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceDef.kt:66:1 Syntax error: Unclosed comment.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:24:30 Unresolved reference 'WorkspaceDef'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:25:42 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:26:66 Unresolved reference 'version'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:28:28 Unresolved reference 'agents'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:28:41 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:32:94 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:35:13 Unresolved reference 'agents'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:35:30 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:36:27 Suspension functions can only be called within coroutine body.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:36:35 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:37:17 Suspension functions can only be called within coroutine body.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:39:28 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:39:41 Unresolved reference 'name'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:39:64 Unresolved reference 'instructions'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:39:124 Unresolved reference 'tools'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:40:32 Unresolved reference 'skills'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:40:73 Unresolved reference 'usesScreen'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:41:97 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:46:24 Unresolved reference 'tasks'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:46:47 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:46:51 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:47:34 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:48:13 Unresolved reference 'tasks'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:48:29 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:49:33 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:49:39 Unresolved reference 'key'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:50:27 Suspension functions can only be called within coroutine body.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:51:17 Suspension functions can only be called within coroutine body.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:51:70 Unresolved reference 'defaultEnabled'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:52:17 Suspension functions can only be called within coroutine body.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:54:49 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:54:64 Unresolved reference 'version'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:54:82 Unresolved reference 'quotas'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:55:34 Unresolved reference 'agents'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:55:51 Unresolved reference 'tasks'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:59:33 Unresolved reference 'WorkspaceDef'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:61:36 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:61:66 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:63:43 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:65:13 Unresolved reference 'tasks'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:65:29 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:66:33 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:66:39 Unresolved reference 'key'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:67:27 Suspension functions can only be called within coroutine body.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:67:39 Return type mismatch: expected 'ERROR CLASS: Cannot infer type variable TypeVariable(_L)', actual 'Unit'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:68:35 Unresolved reference 'defaultEnabled'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:69:17 Suspension functions can only be called within coroutine body.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:70:27 Suspension functions can only be called within coroutine body.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:75:31 Unresolved reference 'WorkspaceDef'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/workspace/WorkspaceInstaller.kt:85:80 Unresolved reference 'id'.
> Task :store:lintDebug FAILED
```
