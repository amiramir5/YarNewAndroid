package com.aistudio.konkooryar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey val id: String,
    val subject: String,
    val book: String,
    val chapter: String,
    val goftar: String,
    val seconds: Int,
    val correct: Int,
    val wrong: Int,
    val blank: Int,
    val note: String,
    val day: String,
    val type: String,
    val createdAt: Long,
    val deletedAt: Long? = null
)

@Entity(tableName = "exams")
data class ExamEntity(
    @PrimaryKey val id: String,
    val subject: String,
    val day: String,
    val correct: Int,
    val wrong: Int,
    val blank: Int,
    val durationMinutes: Int,
    val percent: Double,
    val note: String,
    val createdAt: Long,
    val deletedAt: Long? = null
)

@Entity(tableName = "mistakes")
data class MistakeEntity(
    @PrimaryKey val id: String,
    val subject: String,
    val chapter: String,
    val cause: String,
    val question: String,
    val source: String,
    val importance: Int,
    val box: Int,
    val nextReview: String,
    val createdAt: Long,
    val deletedAt: Long? = null
)

@Entity(tableName = "daily_plans")
data class DailyPlanEntity(
    @PrimaryKey val id: String,
    val day: String,
    val wakeTime: String,
    val itemsJson: String,
    val createdAt: Long,
    val deletedAt: Long? = null
)
