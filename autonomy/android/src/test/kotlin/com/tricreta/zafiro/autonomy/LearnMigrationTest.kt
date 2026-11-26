package com.tricreta.zafiro.autonomy

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.test.core.app.ApplicationProvider
import com.tricreta.zafiro.autonomy.data.AutonomyDatabase
import com.tricreta.zafiro.autonomy.data.LessonEntity
import com.tricreta.zafiro.autonomy.data.OutcomeEntity
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals

/** A hand-built v1 file (exact DDL Room generated then) must open at v5 and the new learning tables must work, keeping old rows. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class LearnMigrationTest {
    private val v1Ddl = listOf(
        "CREATE TABLE IF NOT EXISTS `task` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `prompt` TEXT NOT NULL, `enabled` INTEGER NOT NULL, " +
            "`triggerType` TEXT NOT NULL, `onceAt` TEXT, `intervalMinutes` INTEGER, `anchorMs` INTEGER, `timeOfDay` TEXT, " +
            "`daysMask` INTEGER NOT NULL, `dayOfMonth` INTEGER NOT NULL, `zoneId` TEXT NOT NULL, `createdAtMs` INTEGER NOT NULL, " +
            "`updatedAtMs` INTEGER NOT NULL, `misfireGraceMs` INTEGER NOT NULL, `overlap` TEXT NOT NULL, `maxSteps` INTEGER NOT NULL, " +
            "`maxDurationMs` INTEGER NOT NULL, `configId` TEXT NOT NULL, `approvalTimeoutMs` INTEGER NOT NULL, PRIMARY KEY(`id`))",
        "CREATE TABLE IF NOT EXISTS `run` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `taskId` TEXT NOT NULL, `taskName` TEXT NOT NULL, " +
            "`scheduledForMs` INTEGER NOT NULL, `state` TEXT NOT NULL, `reason` TEXT, `missedCount` INTEGER NOT NULL, `createdAtMs` INTEGER NOT NULL, " +
            "`startedAtMs` INTEGER, `finishedAtMs` INTEGER, `summary` TEXT, `error` TEXT, `conversationId` TEXT, `steps` INTEGER NOT NULL, " +
            "`heartbeatMs` INTEGER, `manual` INTEGER NOT NULL)",
        "CREATE UNIQUE INDEX IF NOT EXISTS `index_run_taskId_scheduledForMs` ON `run` (`taskId`, `scheduledForMs`)",
        "CREATE INDEX IF NOT EXISTS `index_run_state` ON `run` (`state`)",
        "CREATE TABLE IF NOT EXISTS `cursor` (`taskId` TEXT NOT NULL, `lastHandledMs` INTEGER, PRIMARY KEY(`taskId`))",
        "CREATE TABLE IF NOT EXISTS `run_event` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `runId` INTEGER NOT NULL, `tsMs` INTEGER NOT NULL, " +
            "`kind` TEXT NOT NULL, `text` TEXT NOT NULL)",
        "CREATE INDEX IF NOT EXISTS `index_run_event_runId` ON `run_event` (`runId`)",
    )

    @Test fun `old file opens at v5 and outcomes and lessons can be stored and read`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val file = context.getDatabasePath(AutonomyDatabase.NAME)
        file.parentFile?.mkdirs(); file.delete()
        val raw = SQLiteDatabase.openOrCreateDatabase(file, null)
        v1Ddl.forEach { raw.execSQL(it) }
        raw.execSQL("INSERT INTO task VALUES ('t1','Morning brief','x',1,'DAILY',NULL,NULL,NULL,'08:00',0,1,'',1000,1000,7200000,'SKIP',30,600000,'cfg',300000)")
        raw.version = 1
        raw.close()

        val db = AutonomyDatabase.builder(context).allowMainThreadQueries().build()
        try {
            runBlocking {
                assertEquals("Morning brief", db.dao().task("t1")!!.name)
                val dao = db.learnDao()
                assertEquals(0, dao.lessons().size)
                assertEquals(0, dao.outcomes("", 0, 10).size)
                val oid = dao.insertOutcome(OutcomeEntity(workspaceId = "tiktok", platform = "tiktok", artifactId = "p1", artifactKind = "post", hookType = "question", format = "", lengthBucket = "", timeBucket = "", topic = "", metric = "views", value = 12.5, observedAtMs = 5000, lagLabel = "24h", evidence = "screen"))
                val lid = dao.insertLesson(LessonEntity(scope = "tiktok", claim = "c", evidenceIds = "o$oid", helpful = 1, harmful = 0, ttlDays = 90, status = "CANDIDATE", createdAtMs = 1, lastConfirmedMs = 1))
                assertEquals(12.5, dao.outcomes("tiktok", 0, 10).single().value, 1e-9)
                assertEquals("CANDIDATE", dao.lesson(lid)!!.status)
                assertEquals(1, dao.lessonsWithStatus("CANDIDATE").size)
            }
        } finally {
            db.close(); file.delete()
        }
    }
}
