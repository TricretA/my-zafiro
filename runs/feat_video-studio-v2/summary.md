# CI result

**Gate: FAIL: 7 NEW test failure(s)**

- RUN_URL: https://github.com/TricretA/my-zafiro/actions/runs/45840758382
- GITHUB_SHA: 00295754861f758b32036c9ca0eb9e8facefad23
- GITHUB_REF_NAME: feat/video-studio-v2

| step | outcome |
|---|---|
| python_check | success |
| assemble | success |
| unit_tests | failure |
| lint | failure |

Unit tests: 2210 run, 8 failed (1 known upstream, 0 flaky upstream, 7 NEW), 0 skipped

## NEW failing tests
- `com.niki914.zafiro.app.conversation.ConversationMigrationTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7
- `com.niki914.zafiro.app.notification.ResidentNotificationBuilderTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7
- `com.niki914.zafiro.app.conversation.ConversationSearchRepoTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7
- `com.niki914.zafiro.app.conversation.ConversationRepoTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7
- `com.niki914.zafiro.app.notification.TurnNotificationManagerTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7
- `com.niki914.zafiro.app.ui.model.home.HomeChatViewModelTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7
- `com.niki914.zafiro.app.conversation.ConversationPersisterTest.classMethod` — java.lang.AssertionError: Failed to fetch maven artifact org.robolectric:android-all-instrumented:14-robolectric-10818077-i7
## Known upstream failures (also fail on pristine upstream)
- `com.niki914.zafiro.app.ui.home.composer.suggestion.SlashFuzzyMatcherTest.match_builtinCommandsMatchedByPrefixAndKeywords`

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
> Task :app:testDebugUnitTest FAILED
* What went wrong:
Execution failed for task ':app:testDebugUnitTest'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:testDebugUnitTest'.
BUILD FAILED in 4m
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
BUILD FAILED in 3m 16s
```
