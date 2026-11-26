# CI result

**Gate: FAIL: only 1609 tests ran (minimum 1860) - a module probably failed to compile**

- RUN_URL: https://github.com/TricretA/my-zafiro/actions/runs/45779051864
- GITHUB_SHA: 18a429d982830c36048f5e55ee34d1ba668db144
- GITHUB_REF_NAME: feat/mcp

| step | outcome |
|---|---|
| python_check | success |
| assemble | success |
| unit_tests | failure |
| lint | failure |

Unit tests: 1609 run, 0 failed (0 known upstream, 0 flaky upstream, 0 NEW), 0 skipped


## unit_tests: key error lines
```
> Task :app:compileDebugUnitTestKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/test/java/com/niki914/zafiro/mcp/McpCoreTest.kt:5:15 Unresolved reference 'test'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/test/java/com/niki914/zafiro/mcp/McpCoreTest.kt:6:15 Unresolved reference 'test'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/test/java/com/niki914/zafiro/mcp/McpCoreTest.kt:7:15 Unresolved reference 'test'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/test/java/com/niki914/zafiro/mcp/McpCoreTest.kt:8:15 Unresolved reference 'test'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/test/java/com/niki914/zafiro/mcp/McpCoreTest.kt:11:6 Unresolved reference 'Test'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/test/java/com/niki914/zafiro/mcp/McpCoreTest.kt:16:9 Unresolved reference 'assertEquals'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/test/java/com/niki914/zafiro/mcp/McpCoreTest.kt:17:9 Unresolved reference 'assertEquals'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/test/java/com/niki914/zafiro/mcp/McpCoreTest.kt:18:9 Unresolved reference 'assertEquals'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/test/java/com/niki914/zafiro/mcp/McpCoreTest.kt:21:6 Unresolved reference 'Test'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/test/java/com/niki914/zafiro/mcp/McpCoreTest.kt:29:9 Unresolved reference 'assertTrue'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/test/java/com/niki914/zafiro/mcp/McpCoreTest.kt:30:9 Unresolved reference 'assertFalse'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/test/java/com/niki914/zafiro/mcp/McpCoreTest.kt:33:6 Unresolved reference 'Test'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/test/java/com/niki914/zafiro/mcp/McpCoreTest.kt:37:9 Unresolved reference 'assertTrue'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/test/java/com/niki914/zafiro/mcp/McpCoreTest.kt:38:9 Unresolved reference 'assertTrue'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/test/java/com/niki914/zafiro/mcp/McpCoreTest.kt:39:9 Unresolved reference 'assertTrue'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/test/java/com/niki914/zafiro/mcp/McpCoreTest.kt:40:9 Unresolved reference 'assertTrue'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/test/java/com/niki914/zafiro/mcp/McpCoreTest.kt:43:6 Unresolved reference 'Test'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/test/java/com/niki914/zafiro/mcp/McpCoreTest.kt:45:9 Unresolved reference 'assertEquals'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/test/java/com/niki914/zafiro/mcp/McpCoreTest.kt:49:9 Unresolved reference 'assertTrue'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/test/java/com/niki914/zafiro/mcp/McpCoreTest.kt:50:9 Unresolved reference 'assertTrue'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/test/java/com/niki914/zafiro/mcp/McpCoreTest.kt:54:9 Unresolved reference 'assertFalse'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/test/java/com/niki914/zafiro/mcp/McpCoreTest.kt:60:6 Unresolved reference 'Test'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/test/java/com/niki914/zafiro/mcp/McpCoreTest.kt:65:9 Unresolved reference 'assertEquals'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/test/java/com/niki914/zafiro/mcp/McpCoreTest.kt:66:9 Unresolved reference 'assertEquals'.
* What went wrong:
Execution failed for task ':app:compileDebugUnitTestKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:compileDebugUnitTestKotlin'.
BUILD FAILED in 4m 29s
```
## lint: key error lines
```
> Task :store:lintDebug FAILED
> Task :business:notification:lintDebug FAILED
> Task :libs:shizuku:lintDebug FAILED
> Task :agent-runtime:lintDebug FAILED
> Task :app:lintDebug FAILED
* What went wrong:
Execution failed for task ':store:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':store:lintDebug'.
* What went wrong:
Execution failed for task ':business:notification:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':business:notification:lintDebug'.
* What went wrong:
Execution failed for task ':libs:shizuku:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':libs:shizuku:lintDebug'.
* What went wrong:
Execution failed for task ':agent-runtime:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':agent-runtime:lintDebug'.
* What went wrong:
Execution failed for task ':app:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:lintDebug'.
BUILD FAILED in 4m 29s
```
