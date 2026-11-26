# CI result

**Gate: FAIL: assembleDebug failed; only 1609 tests ran (minimum 1860) - a module probably failed to compile**

- RUN_URL: https://github.com/TricretA/my-zafiro/actions/runs/45772513847
- GITHUB_SHA: f90cc88ff8183eb028e1b290e4bac7ea1176ff33
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
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/mcp/McpSettingsContent.kt:79:34 Unresolved reference 'Action'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/mcp/McpSettingsContent.kt:80:28 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/mcp/McpSettingsContent.kt:176:17 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/mcp/McpSettingsContent.kt:176:17 Not enough information to infer type argument for 'T'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:45:73 Conflicting import: imported name 'RuntimeToolValidationOrigin' is ambiguous.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:46:56 Conflicting import: imported name 'AndroidMcpSecretStore' is ambiguous.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:304:21 Unresolved reference 'AndroidMcpSecretStore'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1553:27 Unresolved reference 'AndroidMcpSecretStore'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1564:52 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1564:59 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1564:80 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1564:80 Not enough information to infer type argument for 'T'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1564:80 Not enough information to infer type argument for 'R'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1564:84 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1567:42 None of the following candidates is applicable:
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1576:9 Unresolved reference 'AndroidMcpSecretStore'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1590:13 Unresolved reference 'AndroidMcpSecretStore'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1592:9 Unresolved reference 'AndroidMcpSecretStore'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1610:9 Unresolved reference 'AndroidMcpSecretStore'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1866:26 None of the following candidates is applicable:
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1866:47 Unresolved reference 'Internal'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1873:26 None of the following candidates is applicable:
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1873:47 Unresolved reference 'Code'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1882:26 None of the following candidates is applicable:
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1882:47 Unresolved reference 'Internal'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1891:26 None of the following candidates is applicable:
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1891:47 Unresolved reference 'Code'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1951:17 Overload resolution ambiguity between candidates:
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1951:40 None of the following candidates is applicable:
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1951:61 Unresolved reference 'Code'.
* What went wrong:
Execution failed for task ':app:compileDebugKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:compileDebugKotlin'.
BUILD FAILED in 6m 23s
```
## unit_tests: key error lines
```
> Task :app:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/mcp/McpSettingsContent.kt:79:34 Unresolved reference 'Action'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/mcp/McpSettingsContent.kt:80:28 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/mcp/McpSettingsContent.kt:176:17 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/mcp/McpSettingsContent.kt:176:17 Not enough information to infer type argument for 'T'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:45:73 Conflicting import: imported name 'RuntimeToolValidationOrigin' is ambiguous.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:46:56 Conflicting import: imported name 'AndroidMcpSecretStore' is ambiguous.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:304:21 Unresolved reference 'AndroidMcpSecretStore'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1553:27 Unresolved reference 'AndroidMcpSecretStore'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1564:52 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1564:59 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1564:80 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1564:80 Not enough information to infer type argument for 'T'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1564:80 Not enough information to infer type argument for 'R'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1564:84 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1567:42 None of the following candidates is applicable:
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1576:9 Unresolved reference 'AndroidMcpSecretStore'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1590:13 Unresolved reference 'AndroidMcpSecretStore'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1592:9 Unresolved reference 'AndroidMcpSecretStore'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1610:9 Unresolved reference 'AndroidMcpSecretStore'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1866:26 None of the following candidates is applicable:
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1866:47 Unresolved reference 'Internal'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1873:26 None of the following candidates is applicable:
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1873:47 Unresolved reference 'Code'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1882:26 None of the following candidates is applicable:
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1882:47 Unresolved reference 'Internal'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1891:26 None of the following candidates is applicable:
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1891:47 Unresolved reference 'Code'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1951:17 Overload resolution ambiguity between candidates:
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1951:40 None of the following candidates is applicable:
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1951:61 Unresolved reference 'Code'.
* What went wrong:
Execution failed for task ':app:compileDebugKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:compileDebugKotlin'.
BUILD FAILED in 4m 33s
```
## lint: key error lines
```
> Task :agent-runtime:lintDebug FAILED
> Task :app:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/mcp/McpSettingsContent.kt:79:34 Unresolved reference 'Action'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/mcp/McpSettingsContent.kt:80:28 Unresolved reference 'id'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/mcp/McpSettingsContent.kt:176:17 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/mcp/McpSettingsContent.kt:176:17 Not enough information to infer type argument for 'T'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:45:73 Conflicting import: imported name 'RuntimeToolValidationOrigin' is ambiguous.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:46:56 Conflicting import: imported name 'AndroidMcpSecretStore' is ambiguous.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:304:21 Unresolved reference 'AndroidMcpSecretStore'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1553:27 Unresolved reference 'AndroidMcpSecretStore'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1564:52 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1564:59 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1564:80 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1564:80 Not enough information to infer type argument for 'T'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1564:80 Not enough information to infer type argument for 'R'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1564:84 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1567:42 None of the following candidates is applicable:
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1576:9 Unresolved reference 'AndroidMcpSecretStore'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1590:13 Unresolved reference 'AndroidMcpSecretStore'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1592:9 Unresolved reference 'AndroidMcpSecretStore'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1610:9 Unresolved reference 'AndroidMcpSecretStore'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1866:26 None of the following candidates is applicable:
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1866:47 Unresolved reference 'Internal'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1873:26 None of the following candidates is applicable:
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1873:47 Unresolved reference 'Code'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1882:26 None of the following candidates is applicable:
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1882:47 Unresolved reference 'Internal'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1891:26 None of the following candidates is applicable:
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1891:47 Unresolved reference 'Code'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1951:17 Overload resolution ambiguity between candidates:
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1951:40 None of the following candidates is applicable:
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/XRepo.kt:1951:61 Unresolved reference 'Code'.
> Task :store:lintDebug FAILED
> Task :business:notification:lintDebug FAILED
> Task :libs:shizuku:lintDebug FAILED
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
* What went wrong:
Execution failed for task ':libs:shizuku:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':libs:shizuku:lintDebug'.
BUILD FAILED in 3m 18s
```
