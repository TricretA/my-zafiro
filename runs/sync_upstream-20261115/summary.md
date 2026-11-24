# CI result

**Gate: FAIL: 4 NEW test failure(s)**

- RUN_URL: https://github.com/TricretA/my-zafiro/actions/runs/45402199222
- GITHUB_SHA: 32b2c80e1ef8723c3a0d8e734f3fe9538bfbb63e
- GITHUB_REF_NAME: sync/upstream-20261115

| step | outcome |
|---|---|
| python_check | success |
| assemble | success |
| unit_tests | failure |
| lint | failure |

Unit tests: 1961 run, 10 failed (6 known upstream, 0 flaky upstream, 4 NEW), 0 skipped

## NEW failing tests
- `com.niki914.zafiro.app.ui.model.SettingsViewModelTest.buildSettingsUiState_excludesHiddenGroupsAndDropsEmptySections` — java.lang.AssertionError: expected:<[Theme, GeneralSettings, Backup]> but was:<[Theme, Backup, GeneralSettings]>
- `com.niki914.zafiro.repo.XRepoDomainSettingsTest.builtinGroupsReferenceRegisteredToolsWithoutOverlap` — java.lang.ExceptionInInitializerError
- `com.niki914.zafiro.repo.XRepoDomainSettingsTest.builtinGroupWriteThroughTogglesAllMembersAtomically` — java.lang.NoClassDefFoundError: Could not initialize class com.niki914.zafiro.repo.BuiltinToolGroups
- `com.niki914.zafiro.chat.BuiltinToolTest.defaultRegistry_offersOneScreenSurface` — java.lang.AssertionError: expected:<[agent, clipboard, compose_message, device_info, execute_python, find_installed_apps, http_request, launch_app, load_skill, lock_screen, memory, notify, open, open_uri, py_meta_tools, screen_operation, screens, screenshot, shell, ssh, take_over, view_image, wait]>
## Known upstream failures (also fail on pristine upstream)
- `com.niki914.zafiro.app.notification.TurnNotificationManagerTest.onTurnEvent_whenOtherConversationActive_postsNotification`
- `com.niki914.zafiro.app.notification.TurnNotificationManagerTest.onTurnEvent_whenDisabled_doesNotPostNotification`
- `com.niki914.zafiro.app.notification.TurnNotificationManagerTest.onTurnEvent_whenVisibleConversationMatchesButAppInBackground_postsNotification`
- `com.niki914.zafiro.app.notification.TurnNotificationManagerTest.onTurnEvent_whenTriggeredByHost_skipsNotification`
- `com.niki914.zafiro.app.notification.TurnNotificationManagerTest.onTurnEvent_whenVisibleInForeground_skipsNotification`
- `com.niki914.zafiro.app.conversation.ConversationPersisterTest.trackActivity_bumpsUpdatedAtOnApprovalAndTurnEnd`

## unit_tests: key error lines
```
BuiltinToolTest > defaultRegistry_offersOneScreenSurface FAILED
> Task :agent-runtime:testDebugUnitTest FAILED
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
* What went wrong:
Execution failed for task ':agent-runtime:testDebugUnitTest'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':agent-runtime:testDebugUnitTest'.
* What went wrong:
Execution failed for task ':app:testDebugUnitTest'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:testDebugUnitTest'.
BUILD FAILED in 8m 58s
```
## lint: key error lines
```
> Task :store:lintDebug FAILED
> Task :agent-runtime:lintDebug FAILED
> Task :business:notification:lintDebug FAILED
> Task :app:lintDebug FAILED
* What went wrong:
Execution failed for task ':store:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':store:lintDebug'.
* What went wrong:
Execution failed for task ':agent-runtime:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':agent-runtime:lintDebug'.
* What went wrong:
Execution failed for task ':business:notification:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':business:notification:lintDebug'.
* What went wrong:
Execution failed for task ':app:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:lintDebug'.
BUILD FAILED in 2m 46s
```
