# CI result

- RUN_URL: https://github.com/TricretA/my-zafiro/actions/runs/42781760098
- GITHUB_SHA: b6742a5a4657d7b7db1b90207424d520fbafbf38
- GITHUB_REF_NAME: feat/foundation

| step | outcome |
|---|---|
| python_check | success |
| assemble | success |
| unit_tests | failure |
| lint | failure |

Unit tests: 1740 run, 6 failed, 0 skipped

## Failing tests
- `com.niki914.zafiro.app.notification.TurnNotificationManagerTest.onTurnEvent_whenOtherConversationActive_postsNotification` — kotlinx.coroutines.test.UncompletedCoroutinesError: After waiting for 1m, the test body did not run to completion
- `com.niki914.zafiro.app.notification.TurnNotificationManagerTest.onTurnEvent_whenDisabled_doesNotPostNotification` — kotlinx.coroutines.test.UncompletedCoroutinesError: After waiting for 1m, the test body did not run to completion
- `com.niki914.zafiro.app.notification.TurnNotificationManagerTest.onTurnEvent_whenVisibleConversationMatchesButAppInBackground_postsNotification` — kotlinx.coroutines.test.UncompletedCoroutinesError: After waiting for 1m, the test body did not run to completion
- `com.niki914.zafiro.app.notification.TurnNotificationManagerTest.onTurnEvent_whenTriggeredByHost_skipsNotification` — kotlinx.coroutines.test.UncompletedCoroutinesError: After waiting for 1m, the test body did not run to completion
- `com.niki914.zafiro.app.notification.TurnNotificationManagerTest.onTurnEvent_whenVisibleInForeground_skipsNotification` — kotlinx.coroutines.test.UncompletedCoroutinesError: After waiting for 1m, the test body did not run to completion
- `com.niki914.zafiro.app.conversation.ConversationPersisterTest.trackActivity_bumpsUpdatedAtOnApprovalAndTurnEnd` — java.lang.AssertionError: expected:<2> but was:<1>

## unit_tests: key error lines
```
ConversationPersisterTest > trackActivity_bumpsUpdatedAtOnApprovalAndTurnEnd FAILED
TurnNotificationManagerTest > onTurnEvent_whenOtherConversationActive_postsNotification FAILED
TurnNotificationManagerTest > onTurnEvent_whenDisabled_doesNotPostNotification FAILED
TurnNotificationManagerTest > onTurnEvent_whenVisibleConversationMatchesButAppInBackground_postsNotification FAILED
TurnNotificationManagerTest > onTurnEvent_whenTriggeredByHost_skipsNotification FAILED
TurnNotificationManagerTest > onTurnEvent_whenVisibleInForeground_skipsNotification FAILED
> Task :app:testDebugUnitTest FAILED
* What went wrong:
Execution failed for task ':app:testDebugUnitTest'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:testDebugUnitTest'.
BUILD FAILED in 9m 9s
```
## lint: key error lines
```
> Task :store:lintDebug FAILED
> Task :business:notification:lintDebug FAILED
> Task :app:lintDebug FAILED
* What went wrong:
Execution failed for task ':store:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':store:lintDebug'.
* What went wrong:
Execution failed for task ':business:notification:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':business:notification:lintDebug'.
* What went wrong:
Execution failed for task ':app:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:lintDebug'.
BUILD FAILED in 3m 46s
```
