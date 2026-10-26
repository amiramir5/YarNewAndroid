package com.aistudio.konkooryar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.konkooryar.ui.theme.BluePrimary
import com.aistudio.konkooryar.ui.viewmodel.KonkoorViewModel
import com.aistudio.konkooryar.util.Constants
import com.aistudio.konkooryar.util.JalaliCalendar

@Composable
fun StatsScreen(
    viewModel: KonkoorViewModel
) {
    val sessions by viewModel.sessions.collectAsState()
    val totalSeconds = sessions.sumOf { it.seconds }
    val totalTests = sessions.sumOf { it.correct + it.wrong + it.blank }

    val bySubject = sessions.groupBy { it.subject }
    val sortedSubjects = Constants.SUBJECTS.map { subj ->
        val list = bySubject[subj.id] ?: emptyList()
        val secs = list.sumOf { it.seconds }
        val tests = list.sumOf { it.correct + it.wrong + it.blank }
        Triple(subj, secs, tests)
    }.sortedByDescending { it.second }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        item {
            Text(
                text = "گزارش و آمار تحلیلی مطالعه",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BluePrimary
            )
            Text(
                text = "تفکیک ساعات مطالعه و حجم تست بر اساس دروس",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Aggregate Totals Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = JalaliCalendar.fmtHM(totalSeconds / 60),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = BluePrimary
                        )
                        Text(
                            text = "مجموع ساعت مطالعه",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${JalaliCalendar.faNum(totalTests)} تست",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF10B981)
                        )
                        Text(
                            text = "کل تست‌های حل‌شده",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "تفکیک دروس اختصاصی و عمومی",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(sortedSubjects) { (subj, secs, tests) ->
            val ratio = if (totalSeconds > 0) secs.toFloat() / totalSeconds else 0f
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${subj.iconEmoji} ${subj.name}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "${JalaliCalendar.fmtHM(secs / 60)} · ${JalaliCalendar.faNum(tests)} تست",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { ratio },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp),
                        color = BluePrimary,
                        trackColor = Color.LightGray.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}
