package com.tricreta.zafiro.autonomy.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Index
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.tricreta.zafiro.autonomy.core.Lesson
import com.tricreta.zafiro.autonomy.core.LessonStatus
import com.tricreta.zafiro.autonomy.core.OutcomeObs
import kotlinx.coroutines.flow.Flow

/** One measured result of an artifact (post, application, reply, video). Added in schema v5. */
@Entity(tableName = "outcome", indices = [Index("platform"), Index("observedAtMs")])
data class OutcomeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workspaceId: String,
    /** Lower-case platform or department, e.g. tiktok, jobhunt. */
    val platform: String,
    /** Post id, url or application id; blank = a stand-alone observation. */
    val artifactId: String,
    /** post | application | reply | video */
    val artifactKind: String,
    val hookType: String,
    val format: String,
    val lengthBucket: String,
    val timeBucket: String,
    val topic: String,
    val metric: String,
    val value: Double,
    val observedAtMs: Long,
    /** 1h | 24h | 7d | 3d | 14d | now | blank */
    val lagLabel: String,
    /** Where the number was read. Mandatory, so a figure can be checked. */
    val evidence: String,
)

/** A short evidenced claim with counts; status is derived by the curator. Added in schema v5. */
@Entity(tableName = "lesson", indices = [Index("status")])
data class LessonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val scope: String,
    val claim: String,
    /** Comma-separated outcome ids, board ids or paths. */
    val evidenceIds: String,
    val helpful: Int,
    val harmful: Int,
    val ttlDays: Int,
    /** CANDIDATE | ACTIVE | RETIRED */
    val status: String,
    val createdAtMs: Long,
    val lastConfirmedMs: Long,
)

fun OutcomeEntity.toObs() = OutcomeObs(
    id = id, workspace = workspaceId, platform = platform, artifactId = artifactId, artifactKind = artifactKind,
    tags = mapOf("hook_type" to hookType, "format" to format, "length_bucket" to lengthBucket, "time_bucket" to timeBucket, "topic" to topic).filterValues { it.isNotBlank() },
    metric = metric, value = value, observedAtMs = observedAtMs, lag = lagLabel,
)

fun LessonEntity.toLesson() = Lesson(
    id = id, scope = scope, claim = claim, evidence = evidenceIds.split(',').map { it.trim() }.filter { it.isNotEmpty() },
    helpful = helpful, harmful = harmful, ttlDays = ttlDays, status = LessonStatus.parse(status) ?: LessonStatus.CANDIDATE,
    createdMs = createdAtMs, lastConfirmedMs = lastConfirmedMs,
)

fun Lesson.toEntity() = LessonEntity(
    id = id, scope = scope, claim = claim, evidenceIds = evidence.joinToString(","), helpful = helpful, harmful = harmful, ttlDays = ttlDays,
    status = status.name, createdAtMs = createdMs, lastConfirmedMs = lastConfirmedMs,
)

/** Outcomes and lessons. A separate DAO so the learning store stays self-contained inside the autonomy database. */
@Dao
interface LearnDao {
    @Insert
    suspend fun insertOutcome(o: OutcomeEntity): Long

    @Query("SELECT * FROM outcome WHERE observedAtMs >= :sinceMs AND (:platform = '' OR platform = :platform) ORDER BY observedAtMs DESC, id DESC LIMIT :limit")
    suspend fun outcomes(platform: String, sinceMs: Long, limit: Int): List<OutcomeEntity>

    @Query("SELECT * FROM outcome WHERE observedAtMs >= :sinceMs ORDER BY observedAtMs DESC, id DESC LIMIT :limit")
    fun observeOutcomes(sinceMs: Long, limit: Int): Flow<List<OutcomeEntity>>

    @Query("DELETE FROM outcome")
    suspend fun clearOutcomes()

    @Insert
    suspend fun insertLesson(l: LessonEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertLesson(l: LessonEntity)

    @Query("SELECT * FROM lesson WHERE id = :id")
    suspend fun lesson(id: Long): LessonEntity?

    @Query("SELECT * FROM lesson ORDER BY id")
    suspend fun lessons(): List<LessonEntity>

    @Query("SELECT * FROM lesson WHERE status = :status ORDER BY id")
    suspend fun lessonsWithStatus(status: String): List<LessonEntity>

    @Query("SELECT * FROM lesson ORDER BY CASE status WHEN 'ACTIVE' THEN 0 WHEN 'CANDIDATE' THEN 1 ELSE 2 END, lastConfirmedMs DESC")
    fun observeLessons(): Flow<List<LessonEntity>>

    @Query("DELETE FROM lesson")
    suspend fun clearLessons()
}
