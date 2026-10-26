package com.aistudio.konkooryar.data.model

data class SubjectInfo(
    val id: String,
    val name: String,
    val shortName: String,
    val colorHex: String,
    val category: String = "konkoor",
    val iconEmoji: String = "📚"
)

data class SessionModel(
    val id: String,
    val subject: String,
    val book: String = "all",
    val chapter: String = "",
    val goftar: String = "",
    val seconds: Int,
    val correct: Int = 0,
    val wrong: Int = 0,
    val blank: Int = 0,
    val note: String = "",
    val day: String, // YYYY-MM-DD
    val type: String = "آموزش و مطالعه",
    val createdAt: Long = System.currentTimeMillis()
)

data class ExamModel(
    val id: String,
    val subject: String,
    val day: String,
    val correct: Int,
    val wrong: Int,
    val blank: Int,
    val durationMinutes: Int,
    val percent: Double,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class MistakeCardModel(
    val id: String,
    val subject: String,
    val chapter: String = "",
    val cause: String = "مفهومی و ضعف علمی",
    val question: String,
    val source: String = "",
    val importance: Int = 3, // 1 to 5
    val box: Int = 1, // 1 to 5
    val nextReview: String, // YYYY-MM-DD
    val createdAt: Long = System.currentTimeMillis()
)

data class PlanItemModel(
    val id: String,
    val text: String,
    val done: Boolean = false
)

data class DailyPlanModel(
    val id: String,
    val day: String,
    val wakeTime: String = "06:00",
    val items: List<PlanItemModel> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
)

data class AppSettingsModel(
    val theme: String = "light",
    val examDate: String = "2027-07-01",
    val examTitle: String = "کنکور سراسری تجربی",
    val dailyGoalMinutes: Int = 420, // 7 hours
    val dailyGoalTests: Int = 100,
    val soundEnabled: Boolean = true
)
