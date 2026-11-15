# CI result

**Gate: FAIL: assembleDebug failed; 7 NEW test failure(s); only 1440 tests ran (minimum 1860) - a module probably failed to compile**

- RUN_URL: https://github.com/TricretA/my-zafiro/actions/runs/43694413185
- GITHUB_SHA: b2df2aa7b8bf69c29c7723005bafb54e8a67548e
- GITHUB_REF_NAME: feat/workspaces

| step | outcome |
|---|---|
| python_check | success |
| assemble | failure |
| unit_tests | failure |
| lint | failure |

Unit tests: 1440 run, 7 failed (0 known upstream, 0 flaky upstream, 7 NEW), 0 skipped

## NEW failing tests
- `com.tricreta.zafiro.autonomy.BundledWorkspacesTest.everyBundledWorkspaceParsesAndReferencesRealSkills` — java.lang.AssertionError: whatsapp: skill community-management is not bundled
- `com.tricreta.zafiro.autonomy.WorkspaceTest.newerVersionUpdatesContentKeepsOnStateAndDropsRemovedTasks` — com.tricreta.zafiro.autonomy.workspace.WorkspaceDef$Invalid: Not valid JSON: Unterminated array at character 717 of {"id":"substack","version":2,"name":"Substack","platform":"substack","quotas":"comment=3",           "agents":[{"id":"director","name":"Director","instructions":"lead","tools":["agent"
- `com.tricreta.zafiro.autonomy.TeamServiceTest.ownThreadRepliesAreAutomaticAndRejectionDemotes` — org.json.JSONException: No value for id
- `com.niki914.zafiro.chat.agentic.routine.BundledAssetsTest.workspaceAgentsOnlyUseRealTools` — java.lang.AssertionError: whatsapp.json: unknown tool py_web_read
- `com.niki914.zafiro.chat.BuiltinToolTest.defaultRegistry_offersOneScreenSurface` — java.lang.AssertionError: expected:<[agent, board, clipboard, compose_message, create_skill, device_info, engage, examples, execute_python, find_installed_apps, http_request, launch_app, load_skill, lock_screen, memory, notify, open, open_uri, py_meta_tools, save_file, screen_operation, screen_routi
- `com.niki914.zafiro.chat.agentic.buildin.impl.SaveFileBuiltinTest.rejectsPathEscapes` — java.lang.AssertionError: /etc/passwd expected null, but was:</tmp/junit3787881781932555686/outputs/etc/passwd>
- `com.niki914.zafiro.chat.agentic.buildin.impl.SaveFileBuiltinTest.reportsMissingContentAndBadBase64` — java.lang.AssertionError

## assemble: key error lines
```
> Task :app:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/ConversationHistoryPageContent.kt:374:111 No value passed for parameter 'onClick'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/ConversationHistoryPageContent.kt:374:161 Argument type mismatch: actual type is 'Function0<Unit>', but 'Modifier' was expected.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/ConversationHistoryPageContent.kt:375:109 No value passed for parameter 'onClick'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/ConversationHistoryPageContent.kt:375:160 Argument type mismatch: actual type is 'Function0<Unit>', but 'Modifier' was expected.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/ConversationHistoryPageContent.kt:377:104 No value passed for parameter 'onClick'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/ConversationHistoryPageContent.kt:377:150 Argument type mismatch: actual type is 'Function0<Unit>', but 'Modifier' was expected.
* What went wrong:
Execution failed for task ':app:compileDebugKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:compileDebugKotlin'.
BUILD FAILED in 5m 52s
```
## unit_tests: key error lines
```
> Task :app:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/ConversationHistoryPageContent.kt:374:111 No value passed for parameter 'onClick'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/ConversationHistoryPageContent.kt:374:161 Argument type mismatch: actual type is 'Function0<Unit>', but 'Modifier' was expected.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/ConversationHistoryPageContent.kt:375:109 No value passed for parameter 'onClick'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/ConversationHistoryPageContent.kt:375:160 Argument type mismatch: actual type is 'Function0<Unit>', but 'Modifier' was expected.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/ConversationHistoryPageContent.kt:377:104 No value passed for parameter 'onClick'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/ConversationHistoryPageContent.kt:377:150 Argument type mismatch: actual type is 'Function0<Unit>', but 'Modifier' was expected.
BuiltinToolTest > defaultRegistry_offersOneScreenSurface FAILED
SaveFileBuiltinTest > rejectsPathEscapes FAILED
SaveFileBuiltinTest > reportsMissingContentAndBadBase64 FAILED
BundledAssetsTest > workspaceAgentsOnlyUseRealTools FAILED
> Task :agent-runtime:testDebugUnitTest FAILED
BundledWorkspacesTest > everyBundledWorkspaceParsesAndReferencesRealSkills FAILED
TeamServiceTest > ownThreadRepliesAreAutomaticAndRejectionDemotes FAILED
WorkspaceTest > newerVersionUpdatesContentKeepsOnStateAndDropsRemovedTasks FAILED
> Task :autonomy:android:testDebugUnitTest FAILED
* What went wrong:
Execution failed for task ':app:compileDebugKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:compileDebugKotlin'.
* What went wrong:
Execution failed for task ':agent-runtime:testDebugUnitTest'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':agent-runtime:testDebugUnitTest'.
* What went wrong:
Execution failed for task ':autonomy:android:testDebugUnitTest'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':autonomy:android:testDebugUnitTest'.
BUILD FAILED in 4m 17s
```
## lint: key error lines
```
> Task :app:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/ConversationHistoryPageContent.kt:374:111 No value passed for parameter 'onClick'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/ConversationHistoryPageContent.kt:374:161 Argument type mismatch: actual type is 'Function0<Unit>', but 'Modifier' was expected.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/ConversationHistoryPageContent.kt:375:109 No value passed for parameter 'onClick'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/ConversationHistoryPageContent.kt:375:160 Argument type mismatch: actual type is 'Function0<Unit>', but 'Modifier' was expected.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/ConversationHistoryPageContent.kt:377:104 No value passed for parameter 'onClick'.
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/app/ui/content/ConversationHistoryPageContent.kt:377:150 Argument type mismatch: actual type is 'Function0<Unit>', but 'Modifier' was expected.
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
BUILD FAILED in 2m 53s
```
