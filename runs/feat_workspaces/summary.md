# CI result

**Gate: FAIL: 8 NEW test failure(s)**

- RUN_URL: https://github.com/TricretA/my-zafiro/actions/runs/45575528343
- GITHUB_SHA: 1e16309f60698741901f10bab294a649e9d35988
- GITHUB_REF_NAME: feat/workspaces

| step | outcome |
|---|---|
| python_check | success |
| assemble | success |
| unit_tests | failure |
| lint | failure |

Unit tests: 2145 run, 14 failed (6 known upstream, 0 flaky upstream, 8 NEW), 0 skipped

## NEW failing tests
- `com.niki914.zafiro.app.conversation.ConversationMigrationTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7
- `com.niki914.zafiro.app.conversation.ConversationPersisterTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7
- `com.tricreta.zafiro.autonomy.BundledWorkspacesTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7
- `com.tricreta.zafiro.autonomy.WorkspaceTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7
- `com.tricreta.zafiro.autonomy.TeamServiceTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7
- `com.tricreta.zafiro.autonomy.MigrationTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7
- `com.tricreta.zafiro.autonomy.AutonomyRepositoryTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7
- `com.niki914.zafiro.chat.compaction.HistoryScrubberTest.keepsLastSixSkillsInFullAndStubsOlderOnes` — org.junit.ComparisonFailure: expected:<...ne line line line li[ne line line line line line line line line line line line line line line line line line line line line line line line line line line line line line line line line line line line line line line line line line line line line line line line
## Known upstream failures (also fail on pristine upstream)
- `com.niki914.zafiro.app.notification.TurnNotificationManagerTest.onTurnEvent_whenOtherConversationActive_postsNotification`
- `com.niki914.zafiro.app.notification.TurnNotificationManagerTest.onTurnEvent_whenDisabled_doesNotPostNotification`
- `com.niki914.zafiro.app.notification.TurnNotificationManagerTest.onTurnEvent_whenVisibleConversationMatchesButAppInBackground_postsNotification`
- `com.niki914.zafiro.app.notification.TurnNotificationManagerTest.onTurnEvent_whenTriggeredByHost_skipsNotification`
- `com.niki914.zafiro.app.notification.TurnNotificationManagerTest.onTurnEvent_whenVisibleInForeground_skipsNotification`
- `com.niki914.zafiro.app.ui.home.composer.suggestion.SlashFuzzyMatcherTest.match_builtinCommandsMatchedByPrefixAndKeywords`

## unit_tests: key error lines
```
AutonomyRepositoryTest > classMethod FAILED
BundledWorkspacesTest > classMethod FAILED
MigrationTest > classMethod FAILED
TeamServiceTest > classMethod FAILED
WorkspaceTest > classMethod FAILED
> Task :autonomy:android:testDebugUnitTest FAILED
HistoryScrubberTest > keepsLastSixSkillsInFullAndStubsOlderOnes FAILED
> Task :agent-runtime:testDebugUnitTest FAILED
ConversationMigrationTest > classMethod FAILED
ConversationPersisterTest > classMethod FAILED
TurnNotificationManagerTest > onTurnEvent_whenOtherConversationActive_postsNotification FAILED
TurnNotificationManagerTest > onTurnEvent_whenDisabled_doesNotPostNotification FAILED
TurnNotificationManagerTest > onTurnEvent_whenVisibleConversationMatchesButAppInBackground_postsNotification FAILED
TurnNotificationManagerTest > onTurnEvent_whenTriggeredByHost_skipsNotification FAILED
TurnNotificationManagerTest > onTurnEvent_whenVisibleInForeground_skipsNotification FAILED
SlashFuzzyMatcherTest > match_builtinCommandsMatchedByPrefixAndKeywords FAILED
> Task :app:testDebugUnitTest FAILED
* What went wrong:
Execution failed for task ':autonomy:android:testDebugUnitTest'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':autonomy:android:testDebugUnitTest'.
* What went wrong:
Execution failed for task ':agent-runtime:testDebugUnitTest'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':agent-runtime:testDebugUnitTest'.
* What went wrong:
Execution failed for task ':app:testDebugUnitTest'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:testDebugUnitTest'.
BUILD FAILED in 8m 50s
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
BUILD FAILED in 3m 2s
```
