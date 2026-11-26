# CI result

**Gate: FAIL: only 1759 tests ran (minimum 1860) - a module probably failed to compile**

- RUN_URL: https://github.com/TricretA/my-zafiro/actions/runs/45847080677
- GITHUB_SHA: b4690fb5f4af8398b2d642302dd7ea733230ee22
- GITHUB_REF_NAME: feat/video-studio-v2

| step | outcome |
|---|---|
| python_check | success |
| assemble | success |
| unit_tests | failure |
| lint | failure |

Unit tests: 1759 run, 7 failed (7 known upstream, 0 flaky upstream, 0 NEW), 0 skipped

## Known upstream failures (also fail on pristine upstream)
- `com.niki914.zafiro.app.ui.home.composer.suggestion.SlashFuzzyMatcherTest.match_builtinCommandsMatchedByPrefixAndKeywords`
- `com.niki914.zafiro.app.notification.TurnNotificationManagerTest.onTurnEvent_whenOtherConversationActive_postsNotification`
- `com.niki914.zafiro.app.notification.TurnNotificationManagerTest.onTurnEvent_whenDisabled_doesNotPostNotification`
- `com.niki914.zafiro.app.notification.TurnNotificationManagerTest.onTurnEvent_whenVisibleConversationMatchesButAppInBackground_postsNotification`
- `com.niki914.zafiro.app.notification.TurnNotificationManagerTest.onTurnEvent_whenTriggeredByHost_skipsNotification`
- `com.niki914.zafiro.app.notification.TurnNotificationManagerTest.onTurnEvent_whenVisibleInForeground_skipsNotification`
- `com.niki914.zafiro.app.conversation.ConversationPersisterTest.trackActivity_bumpsUpdatedAtOnApprovalAndTurnEnd`

## unit_tests: key error lines
```
> Task :agent-runtime:compileDebugUnitTestKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/test/java/com/niki914/zafiro/chat/agentic/buildin/impl/SaveFileBuiltinTest.kt:17:42 Argument type mismatch: actual type is 'SuspendFunction0<Result<PublicExportResult>>', but 'SuspendFunction3<@ParameterName(...) File, @ParameterName(...) String?, @ParameterName(...) String?, Result<PublicExportResult>>' was expected.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/test/java/com/niki914/zafiro/chat/agentic/buildin/impl/SaveFileBuiltinTest.kt:17:44 Return type mismatch: expected 'Result<PublicExportResult>', actual 'File'.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/test/java/com/niki914/zafiro/chat/agentic/buildin/impl/SaveFileBuiltinTest.kt:17:44 Type mismatch: inferred type is 'File', but 'Result<PublicExportResult>' was expected.
e: file:///home/runner/work/my-zafiro/my-zafiro/agent-runtime/src/test/java/com/niki914/zafiro/chat/agentic/buildin/impl/StudioRenderBuiltinTest.kt:35:48 Unresolved reference 'error'.
ConversationPersisterTest > trackActivity_bumpsUpdatedAtOnApprovalAndTurnEnd FAILED
TurnNotificationManagerTest > onTurnEvent_whenOtherConversationActive_postsNotification FAILED
TurnNotificationManagerTest > onTurnEvent_whenDisabled_doesNotPostNotification FAILED
TurnNotificationManagerTest > onTurnEvent_whenVisibleConversationMatchesButAppInBackground_postsNotification FAILED
TurnNotificationManagerTest > onTurnEvent_whenTriggeredByHost_skipsNotification FAILED
TurnNotificationManagerTest > onTurnEvent_whenVisibleInForeground_skipsNotification FAILED
SlashFuzzyMatcherTest > match_builtinCommandsMatchedByPrefixAndKeywords FAILED
> Task :app:testDebugUnitTest FAILED
* What went wrong:
Execution failed for task ':agent-runtime:compileDebugUnitTestKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':agent-runtime:compileDebugUnitTestKotlin'.
* What went wrong:
Execution failed for task ':app:testDebugUnitTest'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:testDebugUnitTest'.
BUILD FAILED in 7m 12s
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
BUILD FAILED in 2m 56s
```
