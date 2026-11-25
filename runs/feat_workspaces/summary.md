# CI result

**Gate: PASS**

- RUN_URL: https://github.com/TricretA/my-zafiro/actions/runs/45573077466
- GITHUB_SHA: 1b034143b3b215503a23d6504b092841bd2757da
- GITHUB_REF_NAME: feat/workspaces

| step | outcome |
|---|---|
| python_check | success |
| assemble | success |
| unit_tests | failure |
| lint | cancelled |

Unit tests: 2159 run, 7 failed (7 known upstream, 0 flaky upstream, 0 NEW), 0 skipped

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
ConversationPersisterTest > trackActivity_bumpsUpdatedAtOnApprovalAndTurnEnd FAILED
TurnNotificationManagerTest > onTurnEvent_whenOtherConversationActive_postsNotification FAILED
TurnNotificationManagerTest > onTurnEvent_whenDisabled_doesNotPostNotification FAILED
TurnNotificationManagerTest > onTurnEvent_whenVisibleConversationMatchesButAppInBackground_postsNotification FAILED
TurnNotificationManagerTest > onTurnEvent_whenTriggeredByHost_skipsNotification FAILED
TurnNotificationManagerTest > onTurnEvent_whenVisibleInForeground_skipsNotification FAILED
SlashFuzzyMatcherTest > match_builtinCommandsMatchedByPrefixAndKeywords FAILED
> Task :app:testDebugUnitTest FAILED
* What went wrong:
Execution failed for task ':app:testDebugUnitTest'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:testDebugUnitTest'.
BUILD FAILED in 9m 27s
```
## lint: key error lines
```
> Task :store:lintDebug FAILED
> Task :business:notification:lintDebug FAILED
> Task :libs:shizuku:lintDebug FAILED
> Task :agent-runtime:lintDebug FAILED
```
