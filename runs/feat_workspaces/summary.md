# CI result

**Gate: FAIL: 4 NEW test failure(s)**

- RUN_URL: https://github.com/TricretA/my-zafiro/actions/runs/43695572778
- GITHUB_SHA: a49cb7c9d4209d3279eba8373dd390e2c16d1ebe
- GITHUB_REF_NAME: feat/workspaces

| step | outcome |
|---|---|
| python_check | success |
| assemble | success |
| unit_tests | failure |
| lint | failure |

Unit tests: 2003 run, 10 failed (6 known upstream, 0 flaky upstream, 4 NEW), 0 skipped

## NEW failing tests
- `com.tricreta.zafiro.autonomy.BundledWorkspacesTest.everyBundledWorkspaceParsesAndReferencesRealSkills` — com.tricreta.zafiro.autonomy.workspace.WorkspaceDef$Invalid: Workspace id "x" must be lowercase letters, digits, - or _.
- `com.niki914.zafiro.repo.XRepoDomainSettingsTest.builtinGroupsReferenceRegisteredToolsWithoutOverlap` — java.lang.ExceptionInInitializerError
- `com.niki914.zafiro.repo.XRepoDomainSettingsTest.builtinGroupWriteThroughTogglesAllMembersAtomically` — java.lang.NoClassDefFoundError: Could not initialize class com.niki914.zafiro.repo.BuiltinToolGroups
- `com.niki914.zafiro.app.ui.model.SettingsViewModelTest.buildSettingsUiState_excludesHiddenGroupsAndDropsEmptySections` — java.lang.AssertionError: expected:<[Theme, GeneralSettings, Backup]> but was:<[Theme, Backup, GeneralSettings]>
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
SettingsViewModelTest > buildSettingsUiState_excludesHiddenGroupsAndDropsEmptySections FAILED
XRepoDomainSettingsTest > builtinGroupsReferenceRegisteredToolsWithoutOverlap FAILED
XRepoDomainSettingsTest > builtinGroupWriteThroughTogglesAllMembersAtomically FAILED
> Task :app:testDebugUnitTest FAILED
BundledWorkspacesTest > everyBundledWorkspaceParsesAndReferencesRealSkills FAILED
> Task :autonomy:android:testDebugUnitTest FAILED
* What went wrong:
Execution failed for task ':app:testDebugUnitTest'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:testDebugUnitTest'.
* What went wrong:
Execution failed for task ':autonomy:android:testDebugUnitTest'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':autonomy:android:testDebugUnitTest'.
BUILD FAILED in 9m 9s
```
## lint: key error lines
```
> Task :agent-runtime:lintDebug FAILED
> Task :store:lintDebug FAILED
> Task :business:notification:lintDebug FAILED
> Task :app:lintDebug FAILED
* What went wrong:
Execution failed for task ':agent-runtime:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':agent-runtime:lintDebug'.
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
