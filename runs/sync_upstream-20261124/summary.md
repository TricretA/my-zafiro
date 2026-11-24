# CI result

**Gate: FAIL: 15 NEW test failure(s)**

- RUN_URL: https://github.com/TricretA/my-zafiro/actions/runs/45402206387
- GITHUB_SHA: 82561910035af83082c712bff92121c15a6e96b6
- GITHUB_REF_NAME: sync/upstream-20261124

| step | outcome |
|---|---|
| python_check | success |
| assemble | success |
| unit_tests | failure |
| lint | failure |

Unit tests: 2023 run, 15 failed (0 known upstream, 0 flaky upstream, 15 NEW), 0 skipped

## NEW failing tests
- `com.niki914.zafiro.app.conversation.ConversationMigrationTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7
- `com.niki914.zafiro.app.notification.ResidentNotificationBuilderTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7
- `com.niki914.zafiro.app.conversation.ConversationSearchRepoTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7
- `com.niki914.zafiro.app.conversation.ConversationRepoTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7
- `com.niki914.zafiro.runtime.service.PreemptPreviousTurnTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7
- `com.niki914.zafiro.repo.XRepoTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7
- `com.niki914.zafiro.app.notification.TurnNotificationManagerTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7
- `com.niki914.zafiro.app.ui.home.composer.suggestion.SlashFuzzyMatcherTest.match_builtinCommandsMatchedByPrefixAndKeywords` — java.lang.AssertionError: expected:<[new]> but was:<[new, vd]>
- `com.niki914.zafiro.app.ui.model.home.HomeChatViewModelTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7
- `com.niki914.zafiro.app.conversation.ConversationPersisterTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7
- `com.tricreta.zafiro.autonomy.BundledWorkspacesTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7
- `com.tricreta.zafiro.autonomy.WorkspaceTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7
- `com.tricreta.zafiro.autonomy.TeamServiceTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7
- `com.tricreta.zafiro.autonomy.MigrationTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7
- `com.tricreta.zafiro.autonomy.AutonomyRepositoryTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7

## unit_tests: key error lines
```
ConversationMigrationTest > classMethod FAILED
ConversationPersisterTest > classMethod FAILED
ConversationRepoTest > classMethod FAILED
ConversationSearchRepoTest > classMethod FAILED
ResidentNotificationBuilderTest > classMethod FAILED
TurnNotificationManagerTest > classMethod FAILED
SlashFuzzyMatcherTest > match_builtinCommandsMatchedByPrefixAndKeywords FAILED
HomeChatViewModelTest > classMethod FAILED
XRepoTest > classMethod FAILED
PreemptPreviousTurnTest > classMethod FAILED
> Task :app:testDebugUnitTest FAILED
> Task :autonomy:android:testDebugUnitTest FAILED
AutonomyRepositoryTest > classMethod FAILED
BundledWorkspacesTest > classMethod FAILED
MigrationTest > classMethod FAILED
TeamServiceTest > classMethod FAILED
WorkspaceTest > classMethod FAILED
* What went wrong:
Execution failed for task ':app:testDebugUnitTest'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:testDebugUnitTest'.
* What went wrong:
Execution failed for task ':autonomy:android:testDebugUnitTest'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':autonomy:android:testDebugUnitTest'.
BUILD FAILED in 3m 46s
```
## lint: key error lines
```
> Task :store:lintDebug FAILED
> Task :business:notification:lintDebug FAILED
> Task :agent-runtime:lintDebug FAILED
> Task :app:lintDebug FAILED
* What went wrong:
Execution failed for task ':store:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':store:lintDebug'.
* What went wrong:
Execution failed for task ':business:notification:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':business:notification:lintDebug'.
* What went wrong:
Execution failed for task ':agent-runtime:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':agent-runtime:lintDebug'.
* What went wrong:
Execution failed for task ':app:lintDebug'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:lintDebug'.
BUILD FAILED in 3m
```
