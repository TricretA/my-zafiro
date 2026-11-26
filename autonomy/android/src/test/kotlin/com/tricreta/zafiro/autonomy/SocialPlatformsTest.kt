package com.tricreta.zafiro.autonomy

import com.tricreta.zafiro.autonomy.workspace.SocialCatalog
import com.tricreta.zafiro.autonomy.workspace.SocialAccount
import com.tricreta.zafiro.autonomy.workspace.SocialBrief
import com.tricreta.zafiro.autonomy.workspace.SocialPlatforms
import com.tricreta.zafiro.autonomy.workspace.SocialSettings
import org.json.JSONObject
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// 保护：平台开关、每平台日上限合并、agent 读取的 PLATFORMS.json、旧工作区迁移。改坏任何一个，agent 会在被关闭的平台上行动或超出上限。
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SocialPlatformsTest {
    private val catalog = SocialCatalog.parse(File("../../app/src/main/assets/social/platforms.json").readText())

    @Test fun quotasContainOnlyEnabledPlatformsAndOwnerOverridesWinOverDefaults() {
        var s = SocialSettings()
        s = SocialPlatforms.setEnabled(catalog, s, "instagram", true, "2026-11-26")
        s = SocialPlatforms.setEnabled(catalog, s, "reddit", true, "2026-11-26")
        s = s.copy(accounts = mapOf(
            "instagram" to SocialAccount("@brand", "Grow the brand", "VERIFIED", setOf("draft", "post")),
            "reddit" to SocialAccount("u/brand", "Help the community", "VERIFIED", setOf("draft", "comment")),
        ))
        s = SocialPlatforms.withCaps(s, "instagram", mapOf("post" to 3, "comment" to 0))
        val q = SocialPlatforms.quotas(catalog, s).split(',')
        assertTrue("instagram.post=3" in q && "instagram.comment=0" in q)
        assertTrue("reddit.post=1" in q)
        assertTrue(q.none { it.startsWith("tiktok.") }, "a platform that is off has no keys")
        assertTrue("post=0" in q && "comment=0" in q, "a missing platform prefix cannot bypass account consent")
    }

    @Test fun overrideCannotInventActionsThePlatformDoesNotHave() {
        val twitch = catalog.platform("twitch")!!
        val caps = SocialPlatforms.caps(twitch, SocialSettings(capOverrides = mapOf("twitch" to "follow=50,post=2")))
        assertEquals(2, caps["post"]); assertFalse("follow" in caps)
    }

    @Test fun platformsJsonListsEnabledOnlyAndIsEmptyWhenNothingIsPicked() {
        val none = JSONObject(SocialPlatforms.platformsJson(catalog, SocialSettings()))
        assertEquals(0, none.getJSONArray("enabled").length())
        val s = SocialPlatforms.setEnabled(catalog, SocialSettings(), "telegram", true, "2026-11-26")
        val one = JSONObject(SocialPlatforms.platformsJson(catalog, s))
        assertEquals("telegram", one.getJSONArray("enabled").getString(0))
        assertEquals("2026-11-26", one.getJSONObject("enabled_since").getString("telegram"))
        assertEquals(20, one.getJSONObject("metricool").getInt("monthly_limit"))
    }

    @Test fun enablingKeepsTheFirstDayAndUnknownIdsAreIgnored() {
        var s = SocialPlatforms.setEnabled(catalog, SocialSettings(), "x", true, "2026-11-01")
        s = SocialPlatforms.setEnabled(catalog, s, "x", true, "2026-11-20")
        s = SocialPlatforms.setEnabled(catalog, s, "myspace", true, "2026-11-20")
        assertEquals(mapOf("x" to "2026-11-01"), s.enabledSince)
        assertEquals(SocialSettings.parse(s.toJson()), s)
    }

    @Test fun turningAPlatformOffKeepsSkillsAnotherEnabledPlatformStillNeeds() {
        val off = SocialPlatforms.exclusiveSkills(catalog, "instagram", listOf("instagram", "facebook"), listOf("humanizer"))
        assertTrue("instagram-playbook" in off)
        val shared = SocialPlatforms.skillsOf(catalog, "instagram").intersect(SocialPlatforms.skillsOf(catalog, "facebook"))
        assertTrue(shared.isNotEmpty() && shared.none { it in off }, "skills of the platform that stays on are kept")
    }

    @Test fun onlyOldWorkspacesThatWereOnMigrateToPlatforms() {
        assertEquals(listOf("instagram", "substack"), SocialPlatforms.migratedPlatforms(catalog, listOf("substack", "instagram", "email", "jobhunt")))
        assertTrue(SocialPlatforms.migratedPlatforms(catalog, emptyList()).isEmpty())
    }

    @Test fun socialCannotArmUntilTheOwnerDefinesItsBusinessAccountAndPlan() {
        var settings = SocialPlatforms.setEnabled(catalog, SocialSettings(), "facebook", true, "2026-11-26")
        assertTrue(SocialPlatforms.configurationIssue(catalog, settings) != null)

        settings = settings.copy(
            brief = SocialBrief("Bingwa", "Teach local businesses", "Kenyan founders"),
            accounts = mapOf("facebook" to SocialAccount("Bingwa", "Build a founder community", "VERIFIED", setOf("draft", "post"))),
            scheduledTaskKeys = setOf("produce"),
        )
        assertEquals(null, SocialPlatforms.configurationIssue(catalog, settings))
    }
}
