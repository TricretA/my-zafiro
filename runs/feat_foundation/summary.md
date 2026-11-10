# CI result

**Gate: FAIL: assembleDebug failed; 1 NEW test failure(s); only 1402 tests ran (minimum 1860) - a module probably failed to compile**

- RUN_URL: https://github.com/TricretA/my-zafiro/actions/runs/42915295717
- GITHUB_SHA: 6a49f2ac09f29e31d347c8ea20e8ed8da976415b
- GITHUB_REF_NAME: feat/foundation

| step | outcome |
|---|---|
| python_check | success |
| assemble | failure |
| unit_tests | failure |
| lint | cancelled |

Unit tests: 1402 run, 1 failed (0 known upstream, 0 flaky upstream, 1 NEW), 0 skipped

## NEW failing tests
- `com.niki914.zafiro.business.agent.AgentManagerImplTest.destroy_removesAgentFromContainerAndForcesNewInstanceOnNextSwitch` — java.lang.AssertionError

## assemble: key error lines
```
> Task :app:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/SkillApi.kt:75:86 Cannot access 'companion object Companion : Any': it is private in 'com/niki914/zafiro/repo/SkillFileRepository'.
* What went wrong:
Execution failed for task ':app:compileDebugKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:compileDebugKotlin'.
BUILD FAILED in 5m 39s
```
## unit_tests: key error lines
```
> Task :app:compileDebugKotlin FAILED
e: file:///home/runner/work/my-zafiro/my-zafiro/app/src/main/java/com/niki914/zafiro/repo/SkillApi.kt:75:86 Cannot access 'companion object Companion : Any': it is private in 'com/niki914/zafiro/repo/SkillFileRepository'.
AgentManagerImplTest > destroy_removesAgentFromContainerAndForcesNewInstanceOnNextSwitch FAILED
> Task :business:agent:testDebugUnitTest FAILED
* What went wrong:
Execution failed for task ':app:compileDebugKotlin'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':app:compileDebugKotlin'.
* What went wrong:
Execution failed for task ':business:agent:testDebugUnitTest'.
org.gradle.api.tasks.TaskExecutionException: Execution failed for task ':business:agent:testDebugUnitTest'.
BUILD FAILED in 4m 20s
```
