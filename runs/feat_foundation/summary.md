# CI result

- RUN_URL: https://github.com/TricretA/my-zafiro/actions/runs/42776650636
- GITHUB_SHA: 90eeb90a23c3bf35268baad76d77939a3e45b5e6
- GITHUB_REF_NAME: feat/foundation

| step | outcome |
|---|---|
| python_check | success |
| assemble | failure |
| unit_tests | failure |
| lint | failure |

Unit tests: 1253 run, 0 failed, 0 skipped


## assemble: key error lines
```
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:7:28 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:8:28 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:9:28 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:23:73 Unresolved reference 'ApprovalDecision'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:25:51 Unresolved reference 'ApprovalDecision'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:26:29 Unresolved reference 'ApprovalDecision'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:26:49 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:26:49 Not enough information to infer type argument for 'T'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:26:54 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:28:39 Unresolved reference 'ApprovalDecision'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:46:5 Unresolved reference 'Approver'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:48:5 'decide' overrides nothing.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:48:42 Unresolved reference 'ApprovalRequest'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:48:60 Unresolved reference 'ApprovalDecision'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:55:28 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:55:81 Unresolved reference 'ApprovalDecision'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:57:87 Unresolved reference 'ApprovalDecision'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:66:35 Unresolved reference 'ApprovalRequest'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:66:62 'when' expression must be exhaustive. Add an 'else' branch.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:67:12 Unresolved reference 'ApprovalRequest'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:68:30 Unresolved reference 'toolName'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:68:65 Unresolved reference 'command'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:68:101 Unresolved reference 'ruleName'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:69:12 Unresolved reference 'ApprovalRequest'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:76:59 Unresolved reference 'ApprovalDecision'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:76:87 Unresolved reference 'ApprovalDecision'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:4:28 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:5:28 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:6:28 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:7:28 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:8:28 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:9:28 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:18:28 Unresolved reference 'service'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:68:27 Unresolved reference 'AgentManager'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:68:42 Unresolved reference 'requireService'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:69:34 Unresolved reference 'createTaskAgent'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:90:50 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:91:47 Unresolved reference 'turns'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:91:67 Unresolved reference 'blocks'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:91:83 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:91:89 Unresolved reference 'TurnBlock'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:95:37 Unresolved reference 'Draft'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:97:21 Unresolved reference 'TurnStart'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:109:34 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:109:40 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:109:45 Unresolved reference 'isRunning'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:109:65 Unresolved reference 'AgentState'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:109:87 Unresolved reference 'lastOutcome'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:110:46 Unresolved reference 'isRunning'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:123:27 Unresolved reference 'blocks'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:123:43 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:123:49 Unresolved reference 'TurnBlock'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:124:34 Unresolved reference 'blocks'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:124:59 Unresolved reference 'TurnBlock'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:124:92 Unresolved reference 'text'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:125:33 Unresolved reference 'blocks'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:125:58 Unresolved reference 'TurnBlock'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:126:51 Unresolved reference 'AgentState'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:126:69 Unresolved reference 'lastOutcome'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:135:67 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:137:79 Argument type mismatch: actual type is 'R? (of fun <T, R> T.let)', but 'K? (of fun <K> ELVIS_CALL)' was expected.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:137:88 Unresolved reference 'message'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:137:97 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:137:101 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:137:117 Argument type mismatch: actual type is 'String', but 'K (of fun <K> ELVIS_CALL)' was expected.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:140:48 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:160:17 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:161:17 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:161:17 Not enough information to infer type argument for 'R'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:163:21 Not enough information to infer type argument for 'K'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:163:50 Unresolved reference 'destroy'.
> Task :autonomy:android:compileDebugKotlin FAILED
* What went wrong:
Execution failed for task ':autonomy:android:compileDebugKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':autonomy:android:compileDebugKotlin'.
BUILD FAILED in 3m 24s
```
## unit_tests: key error lines
```
> Task :autonomy:android:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:7:28 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:8:28 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:9:28 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:23:73 Unresolved reference 'ApprovalDecision'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:25:51 Unresolved reference 'ApprovalDecision'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:26:29 Unresolved reference 'ApprovalDecision'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:26:49 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:26:49 Not enough information to infer type argument for 'T'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:26:54 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:28:39 Unresolved reference 'ApprovalDecision'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:46:5 Unresolved reference 'Approver'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:48:5 'decide' overrides nothing.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:48:42 Unresolved reference 'ApprovalRequest'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:48:60 Unresolved reference 'ApprovalDecision'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:55:28 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:55:81 Unresolved reference 'ApprovalDecision'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:57:87 Unresolved reference 'ApprovalDecision'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:66:35 Unresolved reference 'ApprovalRequest'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:66:62 'when' expression must be exhaustive. Add an 'else' branch.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:67:12 Unresolved reference 'ApprovalRequest'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:68:30 Unresolved reference 'toolName'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:68:65 Unresolved reference 'command'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:68:101 Unresolved reference 'ruleName'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:69:12 Unresolved reference 'ApprovalRequest'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:76:59 Unresolved reference 'ApprovalDecision'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:76:87 Unresolved reference 'ApprovalDecision'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:4:28 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:5:28 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:6:28 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:7:28 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:8:28 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:9:28 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:18:28 Unresolved reference 'service'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:68:27 Unresolved reference 'AgentManager'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:68:42 Unresolved reference 'requireService'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:69:34 Unresolved reference 'createTaskAgent'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:90:50 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:91:47 Unresolved reference 'turns'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:91:67 Unresolved reference 'blocks'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:91:83 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:91:89 Unresolved reference 'TurnBlock'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:95:37 Unresolved reference 'Draft'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:97:21 Unresolved reference 'TurnStart'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:109:34 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:109:40 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:109:45 Unresolved reference 'isRunning'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:109:65 Unresolved reference 'AgentState'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:109:87 Unresolved reference 'lastOutcome'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:110:46 Unresolved reference 'isRunning'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:123:27 Unresolved reference 'blocks'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:123:43 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:123:49 Unresolved reference 'TurnBlock'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:124:34 Unresolved reference 'blocks'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:124:59 Unresolved reference 'TurnBlock'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:124:92 Unresolved reference 'text'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:125:33 Unresolved reference 'blocks'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:125:58 Unresolved reference 'TurnBlock'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:126:51 Unresolved reference 'AgentState'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:126:69 Unresolved reference 'lastOutcome'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:135:67 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:137:79 Argument type mismatch: actual type is 'R? (of fun <T, R> T.let)', but 'K? (of fun <K> ELVIS_CALL)' was expected.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:137:88 Unresolved reference 'message'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:137:97 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:137:101 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:137:117 Argument type mismatch: actual type is 'String', but 'K (of fun <K> ELVIS_CALL)' was expected.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:140:48 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:160:17 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:161:17 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:161:17 Not enough information to infer type argument for 'R'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:163:21 Not enough information to infer type argument for 'K'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:163:50 Unresolved reference 'destroy'.
* What went wrong:
Execution failed for task ':autonomy:android:compileDebugKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':autonomy:android:compileDebugKotlin'.
BUILD FAILED in 3m 36s
```
## lint: key error lines
```
> Task :autonomy:android:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:7:28 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:8:28 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:9:28 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:23:73 Unresolved reference 'ApprovalDecision'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:25:51 Unresolved reference 'ApprovalDecision'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:26:29 Unresolved reference 'ApprovalDecision'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:26:49 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:26:49 Not enough information to infer type argument for 'T'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:26:54 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:28:39 Unresolved reference 'ApprovalDecision'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:46:5 Unresolved reference 'Approver'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:48:5 'decide' overrides nothing.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:48:42 Unresolved reference 'ApprovalRequest'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:48:60 Unresolved reference 'ApprovalDecision'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:55:28 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:55:81 Unresolved reference 'ApprovalDecision'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:57:87 Unresolved reference 'ApprovalDecision'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:66:35 Unresolved reference 'ApprovalRequest'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:66:62 'when' expression must be exhaustive. Add an 'else' branch.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:67:12 Unresolved reference 'ApprovalRequest'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:68:30 Unresolved reference 'toolName'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:68:65 Unresolved reference 'command'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:68:101 Unresolved reference 'ruleName'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:69:12 Unresolved reference 'ApprovalRequest'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:76:59 Unresolved reference 'ApprovalDecision'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/Approvals.kt:76:87 Unresolved reference 'ApprovalDecision'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:4:28 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:5:28 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:6:28 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:7:28 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:8:28 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:9:28 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:18:28 Unresolved reference 'service'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:68:27 Unresolved reference 'AgentManager'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:68:42 Unresolved reference 'requireService'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:69:34 Unresolved reference 'createTaskAgent'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:90:50 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:91:47 Unresolved reference 'turns'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:91:67 Unresolved reference 'blocks'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:91:83 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:91:89 Unresolved reference 'TurnBlock'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:95:37 Unresolved reference 'Draft'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:97:21 Unresolved reference 'TurnStart'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:109:34 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:109:40 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:109:45 Unresolved reference 'isRunning'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:109:65 Unresolved reference 'AgentState'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:109:87 Unresolved reference 'lastOutcome'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:110:46 Unresolved reference 'isRunning'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:123:27 Unresolved reference 'blocks'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:123:43 Unresolved reference 'it'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:123:49 Unresolved reference 'TurnBlock'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:124:34 Unresolved reference 'blocks'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:124:59 Unresolved reference 'TurnBlock'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:124:92 Unresolved reference 'text'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:125:33 Unresolved reference 'blocks'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:125:58 Unresolved reference 'TurnBlock'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:126:51 Unresolved reference 'AgentState'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:126:69 Unresolved reference 'lastOutcome'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:135:67 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:137:79 Argument type mismatch: actual type is 'R? (of fun <T, R> T.let)', but 'K? (of fun <K> ELVIS_CALL)' was expected.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:137:88 Unresolved reference 'message'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:137:97 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:137:101 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:137:117 Argument type mismatch: actual type is 'String', but 'K (of fun <K> ELVIS_CALL)' was expected.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:140:48 Unresolved reference 'api'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:160:17 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:161:17 Cannot infer type for this parameter. Specify it explicitly.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:161:17 Not enough information to infer type argument for 'R'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:163:21 Not enough information to infer type argument for 'K'.
e: file:///home/runner/work/my-zafiro/my-zafiro/autonomy/android/src/main/kotlin/com/tricreta/zafiro/autonomy/run/RunExecutor.kt:163:50 Unresolved reference 'destroy'.
> Task :store:lintDebug FAILED
> Task :business:notification:lintDebug FAILED
* What went wrong:
Execution failed for task ':autonomy:android:compileDebugKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':autonomy:android:compileDebugKotlin'.
* What went wrong:
Execution failed for task ':store:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':store:lintDebug'.
```
