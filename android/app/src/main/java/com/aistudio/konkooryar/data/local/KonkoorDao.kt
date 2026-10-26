package com.aistudio.konkooryar.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface KonkoorDao {
    // Sessions
    @Query("SELECT * FROM sessions WHERE deletedAt IS NULL ORDER BY createdAt DESC")
    fun getAllSessions(): Flow<List<SessionEntity>>

    @Query("SELECT * FROM sessions WHERE day = :day AND deletedAt IS NULL")
    fun getSessionsForDay(day: String): Flow<List<SessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: SessionEntity)

    // Exams
    @Query("SELECT * FROM exams WHERE deletedAt IS NULL ORDER BY createdAt DESC")
    fun getAllExams(): Flow<List<ExamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: ExamEntity)

    @Query("UPDATE exams SET deletedAt = :deletedAt WHERE id = :id")
    suspend fun deleteExam(id: String, deletedAt: Long = System.currentTimeMillis())

    // Mistakes (Leitner)
    @Query("SELECT * FROM mistakes WHERE deletedAt IS NULL ORDER BY createdAt DESC")
    fun getAllMistakes(): Flow<List<MistakeEntity>>

    @Query("SELECT * FROM mistakes WHERE nextReview <= :today AND deletedAt IS NULL")
    fun getDueMistakes(today: String): Flow<List<MistakeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMistake(mistake: MistakeEntity)

    @Query("UPDATE mistakes SET box = :box, nextReview = :nextReview WHERE id = :id")
    suspend fun updateMistakeReview(id: String, box: Int, nextReview: String)

    @Query("UPDATE mistakes SET deletedAt = :deletedAt WHERE id = :id")
    suspend fun deleteMistake(id: String, deletedAt: Long = System.currentTimeMillis())

    // Plans
    @Query("SELECT * FROM daily_plans WHERE deletedAt IS NULL ORDER BY day DESC")
    fun getAllPlans(): Flow<List<DailyPlanEntity>>

    @Query("SELECT * FROM daily_plans WHERE day = :day AND deletedAt IS NULL LIMIT 1")
    fun getPlanForDay(day: String): Flow<DailyPlanEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: DailyPlanEntity)
}
