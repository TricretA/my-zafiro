# CI result

**Gate: PASS**

- RUN_URL: https://github.com/TricretA/my-zafiro/actions/runs/42884397598
- GITHUB_SHA: 3e0ddf5ef16ff18d4935ff5eab4ed618fe032e73
- GITHUB_REF_NAME: feat/foundation

| step | outcome |
|---|---|
| python_check | success |
| assemble | success |
| unit_tests | failure |
| lint | failure |

Unit tests: 1881 run, 6 failed (6 known upstream, 0 flaky upstream, 0 NEW), 0 skipped

## Known upstream failures (also fail on pristine upstream)
- `com.niki914.zafiro.app.notification.TurnNotificationManagerTest.onTurnEvent_whenOtherConversationActive_postsNotification`
- `com.niki914.zafiro.app.notification.TurnNotificationManagerTest.onTurnEvent_whenDisabled_doesNotPostNotification`
- `com.niki914.zafiro.app.notification.TurnNotificationManagerTest.onTurnEvent_whenVisibleConversationMatchesButAppInBackground_postsNotification`
- `com.niki914.zafiro.app.notification.TurnNotificationManagerTest.onTurnEvent_whenTriggeredByHost_skipsNotification`
- `com.niki914.zafiro.app.notification.TurnNotificationManagerTest.onTurnEvent_whenVisibleInForeground_skipsNotification`
- `com.niki914.zafiro.app.conversation.ConversationPersisterTest.trackActivity_bumpsUpdatedAtOnApprovalAndTurnEnd`

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
BUILD FAILED in 9m 37s
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
BUILD FAILED in 3m 51s
```
