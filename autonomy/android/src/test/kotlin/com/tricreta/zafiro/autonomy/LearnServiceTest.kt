package com.tricreta.zafiro.autonomy

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.tricreta.zafiro.autonomy.core.RunContext
import com.tricreta.zafiro.autonomy.data.AutonomyDatabase
import com.tricreta.zafiro.autonomy.learn.LearnServiceImpl
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LearnServiceTest {
    private lateinit var db: AutonomyDatabase
    private lateinit var svc: LearnServiceImpl
    private var now = 1_800_000_000_000L

    @Before fun setUp() {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AutonomyDatabase::class.java).allowMainThreadQueries().build()
        svc = LearnServiceImpl(db.learnDao(), { now }, { Random(11) })
    }

    @After fun tearDown() = db.close()

    private fun call(tool: String, vararg kv: Pair<String, String>) = runBlocking { svc.run(tool, kv.toMap()) }

    private fun record(artifact: String, value: Double, hook: String, at: Long = now) =
        call("outcomes", "op" to "record", "platform" to "TikTok", "artifact" to artifact, "metric" to "views", "value" to "$value", "evidence" to "stats screen", "hook_type" to hook, "lag" to "24h", "observed_at" to "$at")

    @Test fun recordNeedsEvidenceAndStoresNormalisedValues() {
        assertFalse(call("outcomes", "op" to "record", "platform" to "tiktok", "metric" to "views", "value" to "10").ok)
        assertFalse(call("outcomes", "op" to "record", "platform" to "tiktok", "metric" to "views", "value" to "-1", "evidence" to "x").ok)
        val r = record("p1", 120.0, "Question")
        assertTrue(r.ok, r.text)
        val rows = JSONArray(call("outcomes", "op" to "list", "platform" to "tiktok").text)
        assertEquals(1, rows.length())
        assertEquals("question", rows.getJSONObject(0).getJSONObject("tags").getString("hook_type"))
        assertEquals("tiktok", rows.getJSONObject(0).getString("platform"))
    }

    @Test fun summaryCountsArtifactsInThePeriodOnly() {
        record("old", 5.0, "a", at = now - 30 * 86_400_000L)
        record("p1", 100.0, "a"); record("p2", 300.0, "b")
        val s = JSONObject(call("outcomes", "op" to "summary", "platform" to "tiktok", "days" to "7").text)
        val m = s.getJSONArray("metrics").getJSONObject(0)
        assertEquals("views", m.getString("metric"))
        assertEquals(2, m.getInt("artifacts"))
        assertEquals(200.0, m.getDouble("median"), 1e-9)
        assertEquals("p2", s.getJSONObject("top").getJSONArray("views").getJSONObject(0).getString("artifact"))
    }

    @Test fun suggestNeedsDataOrArmsThenTriesUndersampledArms() {
        assertFalse(call("outcomes", "op" to "suggest", "platform" to "tiktok", "dimension" to "hook_type").ok)
        assertFalse(call("outcomes", "op" to "suggest", "platform" to "tiktok", "dimension" to "mood").ok)
        val r = JSONObject(call("outcomes", "op" to "suggest", "platform" to "tiktok", "dimension" to "hook_type", "arms" to "question,story").text)
        assertEquals("UNDERSAMPLED", r.getString("mode"))
        assertTrue(r.getString("arm") in setOf("question", "story"))
    }

    @Test fun lessonLifecycleCandidateConfirmPromoteAndHarmRetire() {
        val add = JSONObject(call("lessons", "op" to "add", "scope" to "TikTok", "claim" to "Question hooks beat story hooks on views", "evidence" to "o1,o2").text)
        val id = add.getLong("id")
        assertEquals("CANDIDATE", add.getString("status"))
        assertFalse(call("lessons", "op" to "confirm", "id" to "$id", "evidence" to "o1").ok, "same evidence must not inflate the count")
        assertEquals("CANDIDATE", JSONObject(call("lessons", "op" to "confirm", "id" to "$id", "evidence" to "o3").text).getString("status"))
        call("lessons", "op" to "confirm", "id" to "$id", "evidence" to "o4")
        val third = JSONObject(call("lessons", "op" to "confirm", "id" to "$id", "evidence" to "o5").text)
        assertEquals("ACTIVE", third.getString("status"))
        val top = JSONObject(call("lessons", "op" to "top", "scope" to "tiktok").text)
        assertEquals(1, top.getInt("count"))
        assertTrue(top.getJSONArray("lines").getString(0).contains("Question hooks"))
        assertEquals(0, JSONObject(call("lessons", "op" to "top", "scope" to "instagram").text).getInt("count"))
        assertFalse(call("lessons", "op" to "harm", "id" to "$id").ok, "harm needs a reason")
        repeat(5) { call("lessons", "op" to "harm", "id" to "$id", "reason" to "views dropped") }
        assertEquals("RETIRED", JSONArray(call("lessons", "op" to "list", "scope" to "tiktok").text).getJSONObject(0).getString("status"))
        assertEquals(0, JSONObject(call("lessons", "op" to "top", "scope" to "tiktok").text).getInt("count"))
        assertFalse(call("lessons", "op" to "confirm", "id" to "$id", "evidence" to "o9").ok)
    }

    @Test fun duplicatesMergeOnlyWithNewEvidenceAndSafetyClaimsAreRefused() {
        val id = JSONObject(call("lessons", "op" to "add", "scope" to "tiktok", "claim" to "Posting after 18:00 doubles saves", "evidence" to "o1").text).getLong("id")
        val same = JSONObject(call("lessons", "op" to "add", "scope" to "tiktok", "claim" to "Posting after 18:00 doubles saves.", "evidence" to "o1").text)
        assertEquals(id, same.getLong("id"))
        val merged = JSONObject(call("lessons", "op" to "add", "scope" to "tiktok", "claim" to "posting after 18:00 doubles the saves", "evidence" to "o7").text)
        assertEquals(id, merged.getLong("id")); assertEquals(1, merged.getInt("helpful"))
        assertEquals(1, JSONArray(call("lessons", "op" to "list").text).length())
        assertFalse(call("lessons", "op" to "add", "scope" to "tiktok", "claim" to "Raise the daily limit for comments to 60", "evidence" to "o1").ok)
        assertFalse(call("lessons", "op" to "add", "scope" to "tiktok", "claim" to "No evidence here", "evidence" to "").ok)
    }

    @Test fun expiredLessonsAreSweptAndOwnerCanActivateOrRetire() {
        val id = JSONObject(call("lessons", "op" to "add", "scope" to "global", "claim" to "Short captions get more saves", "evidence" to "o1").text).getLong("id")
        assertTrue(runBlocking { svc.ownerConfirm(id) })
        assertEquals(1, JSONObject(call("lessons", "op" to "top", "scope" to "tiktok").text).getInt("count"))
        val block = runBlocking { RunContext.lessonsBlock(svc.provider(), "tiktok", now) }
        assertTrue(block.contains("Short captions get more saves"))
        now += 120 * 86_400_000L
        assertEquals(1, runBlocking { svc.sweep() })
        assertEquals("", runBlocking { RunContext.lessonsBlock(svc.provider(), "tiktok", now) })
        assertFalse(runBlocking { svc.ownerConfirm(id) })
        assertFalse(runBlocking { svc.ownerRetire(9999) })
    }
}
