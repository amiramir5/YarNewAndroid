package com.aistudio.konkooryar.util

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor
import kotlin.math.roundToInt

object JalaliCalendar {
    private val G_DAYS_IN_MONTH = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    private val J_MONTH_NAMES = arrayOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    )
    private val FA_DIGITS = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')

    fun faNum(number: Any?): String {
        if (number == null) return "—"
        val str = number.toString()
        val sb = java.lang.StringBuilder()
        for (ch in str) {
            if (ch in '0'..'9') {
                sb.append(FA_DIGITS[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun g2j(gy: Int, gm: Int, gd: Int): Triple<Int, Int, Int> {
        val gY = gy - 1600
        val gM = gm - 1
        val gD = gd - 1

        var gDayNo = 365 * gY + floor((gY + 3.0) / 4.0).toInt() -
                floor((gY + 99.0) / 100.0).toInt() + floor((gY + 399.0) / 400.0).toInt()

        for (i in 0 until gM) gDayNo += G_DAYS_IN_MONTH[i]
        if (gM > 1 && ((gy % 4 == 0 && gy % 100 != 0) || gy % 400 == 0)) {
            gDayNo++
        }
        gDayNo += gD

        var jDayNo = gDayNo - 79
        val jNp = floor(jDayNo / 12053.0).toInt()
        jDayNo %= 12053

        var jy = 979 + 33 * jNp + 4 * floor(jDayNo / 1461.0).toInt()
        jDayNo %= 1461

        if (jDayNo >= 366) {
            jy += floor((jDayNo - 1.0) / 365.0).toInt()
            jDayNo = (jDayNo - 1) % 365
        }

        val jm = if (jDayNo < 186) 1 + floor(jDayNo / 31.0).toInt() else 7 + floor((jDayNo - 186.0) / 30.0).toInt()
        val jd = 1 + if (jDayNo < 186) (jDayNo % 31) else ((jDayNo - 186) % 30)

        return Triple(jy, jm, jd)
    }

    fun studyDayKey(date: Date = Date()): String {
        val cal = Calendar.getInstance()
        cal.time = date
        if (cal.get(Calendar.HOUR_OF_DAY) < 5) {
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(cal.time)
    }

    fun addDays(iso: String, days: Int): String {
        return try {
            val parts = iso.split("-").map { it.toInt() }
            val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            cal.set(parts[0], parts[1] - 1, parts[2], 0, 0, 0)
            cal.add(Calendar.DAY_OF_MONTH, days)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            sdf.format(cal.time)
        } catch (_: Exception) {
            iso
        }
    }

    fun formatPersianDate(iso: String, style: String = "long"): String {
        return try {
            val parts = iso.split("-").map { it.toInt() }
            val (jy, jm, jd) = g2j(parts[0], parts[1], parts[2])
            val monthName = J_MONTH_NAMES[jm - 1]

            if (style == "short") {
                return "${faNum(jy)}/${faNum(jm.toString().padStart(2, '0'))}/${faNum(jd.toString().padStart(2, '0'))}"
            }

            val cal = Calendar.getInstance()
            cal.set(parts[0], parts[1] - 1, parts[2])
            val weekdays = arrayOf("یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنجشنبه", "جمعه", "شنبه")
            val weekdayName = weekdays[cal.get(Calendar.DAY_OF_WEEK) - 1]

            if (style == "weekday") {
                "$weekdayName، ${faNum(jd)} $monthName ${faNum(jy)}"
            } else {
                "${faNum(jd)} $monthName ${faNum(jy)}"
            }
        } catch (_: Exception) {
            "—"
        }
    }

    fun calcKonkoorPercent(correct: Int, wrong: Int, blank: Int): Double {
        val total = correct + wrong + blank
        if (total <= 0) return 0.0
        val score = ((3.0 * correct - wrong) / (3.0 * total)) * 100.0
        return (score * 10.0).roundToInt() / 10.0
    }

    fun calcDaysLeft(targetIso: String): Int {
        return try {
            val current = studyDayKey()
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val curDate = sdf.parse(current) ?: return 0
            val targetDate = sdf.parse(targetIso) ?: return 0
            val diff = (targetDate.time - curDate.time) / (1000 * 60 * 60 * 24)
            diff.coerceAtLeast(0).toInt()
        } catch (_: Exception) {
            0
        }
    }

    fun fmtClock(totalSeconds: Int): String {
        val s = totalSeconds.coerceAtLeast(0)
        val h = s / 3600
        val m = (s % 3600) / 60
        val sec = s % 60
        return "${faNum(h.toString().padStart(2, '0'))}:${faNum(m.toString().padStart(2, '0'))}:${faNum(sec.toString().padStart(2, '0'))}"
    }

    fun fmtHM(minutes: Int): String {
        val m = minutes.coerceAtLeast(0)
        val h = m / 60
        val min = m % 60
        return "${faNum(h.toString().padStart(2, '0'))}:${faNum(min.toString().padStart(2, '0'))}"
    }

    fun computeStreak(daysWithStudy: Set<String>): Int {
        if (daysWithStudy.isEmpty()) return 0
        var cursor = studyDayKey()
        if (!daysWithStudy.contains(cursor)) {
            cursor = addDays(cursor, -1)
        }
        var count = 0
        var guard = 0
        while (daysWithStudy.contains(cursor) && guard < 1000) {
            count++
            cursor = addDays(cursor, -1)
            guard++
        }
        return count
    }
}
