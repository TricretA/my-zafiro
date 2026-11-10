# CI result

- RUN_URL: https://github.com/TricretA/my-zafiro/actions/runs/42781765950
- GITHUB_SHA: 2561bd51fd28c9c0dbb3cab98052d95181a58bd8
- GITHUB_REF_NAME: fix/baseline-upstream-tests

| step | outcome |
|---|---|
| python_check | skipped |
| assemble | success |
| unit_tests | failure |
| lint | failure |

Unit tests: 1715 run, 6 failed, 0 skipped

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
BUILD FAILED in 8m 41s
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
BUILD FAILED in 2m 56s
```
