package com.aistudio.konkooryar.data.repository

import com.aistudio.konkooryar.data.local.*
import com.aistudio.konkooryar.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class KonkoorRepository(private val dao: KonkoorDao) {

    val allSessions: Flow<List<SessionModel>> = dao.getAllSessions().map { list ->
        list.map { it.toModel() }
    }

    val allExams: Flow<List<ExamModel>> = dao.getAllExams().map { list ->
        list.map { it.toModel() }
    }

    val allMistakes: Flow<List<MistakeCardModel>> = dao.getAllMistakes().map { list ->
        list.map { it.toModel() }
    }

    fun getDueMistakes(today: String): Flow<List<MistakeCardModel>> =
        dao.getDueMistakes(today).map { list -> list.map { it.toModel() } }

    fun getPlanForDay(day: String): Flow<DailyPlanModel?> =
        dao.getPlanForDay(day).map { it?.toModel() }

    suspend fun saveSession(session: SessionModel) {
        dao.insertSession(session.toEntity())
    }

    suspend fun saveExam(exam: ExamModel) {
        dao.insertExam(exam.toEntity())
    }

    suspend fun deleteExam(id: String) {
        dao.deleteExam(id)
    }

    suspend fun saveMistake(mistake: MistakeCardModel) {
        dao.insertMistake(mistake.toEntity())
    }

    suspend fun updateMistakeReview(id: String, box: Int, nextReview: String) {
        dao.updateMistakeReview(id, box, nextReview)
    }

    suspend fun deleteMistake(id: String) {
        dao.deleteMistake(id)
    }

    suspend fun savePlan(plan: DailyPlanModel) {
        dao.insertPlan(plan.toEntity())
    }

    // Mapping functions
    private fun SessionEntity.toModel() = SessionModel(
        id = id,
        subject = subject,
        book = book,
        chapter = chapter,
        goftar = goftar,
        seconds = seconds,
        correct = correct,
        wrong = wrong,
        blank = blank,
        note = note,
        day = day,
        type = type,
        createdAt = createdAt
    )

    private fun SessionModel.toEntity() = SessionEntity(
        id = id,
        subject = subject,
        book = book,
        chapter = chapter,
        goftar = goftar,
        seconds = seconds,
        correct = correct,
        wrong = wrong,
        blank = blank,
        note = note,
        day = day,
        type = type,
        createdAt = createdAt
    )

    private fun ExamEntity.toModel() = ExamModel(
        id = id,
        subject = subject,
        day = day,
        correct = correct,
        wrong = wrong,
        blank = blank,
        durationMinutes = durationMinutes,
        percent = percent,
        note = note,
        createdAt = createdAt
    )

    private fun ExamModel.toEntity() = ExamEntity(
        id = id,
        subject = subject,
        day = day,
        correct = correct,
        wrong = wrong,
        blank = blank,
        durationMinutes = durationMinutes,
        percent = percent,
        note = note,
        createdAt = createdAt
    )

    private fun MistakeEntity.toModel() = MistakeCardModel(
        id = id,
        subject = subject,
        chapter = chapter,
        cause = cause,
        question = question,
        source = source,
        importance = importance,
        box = box,
        nextReview = nextReview,
        createdAt = createdAt
    )

    private fun MistakeCardModel.toEntity() = MistakeEntity(
        id = id,
        subject = subject,
        chapter = chapter,
        cause = cause,
        question = question,
        source = source,
        importance = importance,
        box = box,
        nextReview = nextReview,
        createdAt = createdAt
    )

    private fun DailyPlanEntity.toModel(): DailyPlanModel {
        val itemList = mutableListOf<PlanItemModel>()
        try {
            val jsonArray = JSONArray(itemsJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                itemList.add(
                    PlanItemModel(
                        id = obj.optString("id", i.toString()),
                        text = obj.optString("text", ""),
                        done = obj.optBoolean("done", false)
                    )
                )
            }
        } catch (_: Exception) {}

        return DailyPlanModel(
            id = id,
            day = day,
            wakeTime = wakeTime,
            items = itemList,
            createdAt = createdAt
        )
    }

    private fun DailyPlanModel.toEntity(): DailyPlanEntity {
        val jsonArray = JSONArray()
        items.forEach { item ->
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("text", item.text)
            obj.put("done", item.done)
            jsonArray.put(obj)
        }
        return DailyPlanEntity(
            id = id,
            day = day,
            wakeTime = wakeTime,
            itemsJson = jsonArray.toString(),
            createdAt = createdAt
        )
    }
}
