# CI result

**Gate: FAIL: 16 NEW test failure(s)**

- RUN_URL: https://github.com/TricretA/my-zafiro/actions/runs/45781506139
- GITHUB_SHA: f358a2e06ad761d9acb2d874ec638a2c33f45990
- GITHUB_REF_NAME: feat/mcp

| step | outcome |
|---|---|
| python_check | success |
| assemble | success |
| unit_tests | failure |
| lint | failure |

Unit tests: 2296 run, 23 failed (7 known upstream, 0 flaky upstream, 16 NEW), 0 skipped

## NEW failing tests
- `com.niki914.zafiro.repo.XRepoDomainSettingsTest.mcpSaveRecoversBrokenServersJsonAndKeepsDottedName` — java.lang.RuntimeException: Method getSharedPreferences in android.content.ContextWrapper not mocked. See https://developer.android.com/r/studio-ui/build/not-mocked for details.
- `com.niki914.zafiro.mcp.McpCoreTest.refreshUsesSixtySecondSkew` — java.lang.AssertionError
- `com.niki914.zafiro.mcp.McpCoreTest.shippedCatalogParsesAndContainsCategoryCoverage` — java.io.FileNotFoundException: app/src/main/assets/mcp_catalog.json (No such file or directory)
- `com.niki914.zafiro.app.ui.model.SettingsViewModelTest.buildSettingsUiState_excludesHiddenGroupsAndDropsEmptySections` — java.lang.AssertionError: expected:<[Tools, Skills, QuickPrompts, Actions]> but was:<[Tools, Skills, QuickPrompts, Actions, McpMarketplace]>
- `com.niki914.zafiro.app.ui.model.McpSettingsViewModelTest.startEdit_formatsHeadersInputAsSortedMultilineJson` — org.junit.ComparisonFailure: expected:<[{   "Authorization": "Bearer xxx",   "X-Trace-Id": "trace-1" }]> but was:<[]>
- `com.niki914.zafiro.app.ui.model.McpSettingsViewModelTest.save_rejectsDuplicateName` — java.lang.AssertionError: expected:<1> but was:<0>
- `com.niki914.zafiro.app.ui.model.McpSettingsViewModelTest.requestDelete_refreshesOtherLoadedMcpSettingsViewModels` — java.lang.AssertionError: expected:<1> but was:<0>
- `com.niki914.zafiro.app.ui.model.McpSettingsViewModelTest.load_readsSavedServersIntoUiState` — java.lang.AssertionError: expected:<2> but was:<0>
- `com.niki914.zafiro.app.ui.model.McpSettingsViewModelTest.save_withInvalidHeadersJson_setsHeadersErrorAndSkipsPersistence` — java.lang.RuntimeException: Method getSharedPreferences in android.content.ContextWrapper not mocked. See https://developer.android.com/r/studio-ui/build/not-mocked for details.
- `com.niki914.zafiro.app.ui.model.McpSettingsViewModelTest.save_renameFailureDoesNotDeleteExistingServer` — java.lang.RuntimeException: Method getSharedPreferences in android.content.ContextWrapper not mocked. See https://developer.android.com/r/studio-ui/build/not-mocked for details.
- `com.niki914.zafiro.app.ui.model.McpSettingsViewModelTest.save_withHeaders_persistsHeadersAndEmitsExitDetail` — java.lang.RuntimeException: Method getSharedPreferences in android.content.ContextWrapper not mocked. See https://developer.android.com/r/studio-ui/build/not-mocked for details.
- `com.niki914.zafiro.app.ui.model.McpSettingsViewModelTest.formState_comparesValidHeadersByNormalizedMap` — java.lang.AssertionError
- `com.niki914.zafiro.app.ui.model.McpSettingsViewModelTest.save_withBlankName_setsNameErrorAndSkipsPersistence` — java.lang.RuntimeException: Method getSharedPreferences in android.content.ContextWrapper not mocked. See https://developer.android.com/r/studio-ui/build/not-mocked for details.
- `com.niki914.zafiro.app.ui.model.McpSettingsViewModelTest.save_persistsTrimmedServerAndUpdatesUiState` — java.lang.AssertionError: expected:<1> but was:<0>
- `com.niki914.zafiro.app.ui.model.McpSettingsViewModelTest.save_withNonHttpScheme_setsUrlErrorAndSkipsPersistence` — java.lang.RuntimeException: Method getSharedPreferences in android.content.ContextWrapper not mocked. See https://developer.android.com/r/studio-ui/build/not-mocked for details.
- `com.niki914.zafiro.app.ui.model.McpSettingsViewModelTest.requestDelete_deletesServerAndEmitsExitDetail` — java.lang.RuntimeException: Method getSharedPreferences in android.content.ContextWrapper not mocked. See https://developer.android.com/r/studio-ui/build/not-mocked for details.
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
McpSettingsViewModelTest > startEdit_formatsHeadersInputAsSortedMultilineJson FAILED
McpSettingsViewModelTest > save_rejectsDuplicateName FAILED
McpSettingsViewModelTest > requestDelete_refreshesOtherLoadedMcpSettingsViewModels FAILED
McpSettingsViewModelTest > load_readsSavedServersIntoUiState FAILED
McpSettingsViewModelTest > save_withInvalidHeadersJson_setsHeadersErrorAndSkipsPersistence FAILED
McpSettingsViewModelTest > save_renameFailureDoesNotDeleteExistingServer FAILED
McpSettingsViewModelTest > save_withHeaders_persistsHeadersAndEmitsExitDetail FAILED
McpSettingsViewModelTest > formState_comparesValidHeadersByNormalizedMap FAILED
McpSettingsViewModelTest > save_withBlankName_setsNameErrorAndSkipsPersistence FAILED
McpSettingsViewModelTest > save_persistsTrimmedServerAndUpdatesUiState FAILED
McpSettingsViewModelTest > save_withNonHttpScheme_setsUrlErrorAndSkipsPersistence FAILED
McpSettingsViewModelTest > requestDelete_deletesServerAndEmitsExitDetail FAILED
SettingsViewModelTest > buildSettingsUiState_excludesHiddenGroupsAndDropsEmptySections FAILED
McpCoreTest > refreshUsesSixtySecondSkew FAILED
McpCoreTest > shippedCatalogParsesAndContainsCategoryCoverage FAILED
XRepoDomainSettingsTest > mcpSaveRecoversBrokenServersJsonAndKeepsDottedName FAILED
> Task :app:testDebugUnitTest FAILED
* What went wrong:
Execution failed for task ':app:testDebugUnitTest'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:testDebugUnitTest'.
BUILD FAILED in 9m 42s
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
BUILD FAILED in 4m 21s
```
