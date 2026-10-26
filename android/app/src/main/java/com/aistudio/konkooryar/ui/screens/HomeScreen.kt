package com.aistudio.konkooryar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.konkooryar.ui.theme.BluePrimary
import com.aistudio.konkooryar.ui.viewmodel.KonkoorViewModel
import com.aistudio.konkooryar.util.JalaliCalendar

@Composable
fun HomeScreen(
    viewModel: KonkoorViewModel,
    onNavigateToTimer: () -> Unit,
    onNavigateToLeitner: () -> Unit,
    onNavigateToPlan: () -> Unit
) {
    val sessions by viewModel.sessions.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val dueMistakes by viewModel.dueMistakes.collectAsState()
    val todayPlan by viewModel.todayPlan.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val liveSeconds by viewModel.elapsedSeconds.collectAsState()

    val todayIso = JalaliCalendar.studyDayKey()
    val todaySessions = sessions.filter { it.day == todayIso }
    val completedSecs = todaySessions.sumOf { it.seconds }
    val totalTodaySecs = completedSecs + if (isTimerRunning) liveSeconds else 0
    val totalTodayTests = todaySessions.sumOf { it.correct + it.wrong + it.blank }

    val daysLeft = JalaliCalendar.calcDaysLeft(settings.examDate)
    val streakDays = JalaliCalendar.computeStreak(sessions.map { it.day }.toSet())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        // Countdown Hero Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = BluePrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = settings.examTitle,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${JalaliCalendar.faNum(daysLeft)} روز",
                        color = Color.White,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "تا کنکور سراسری (تیرماه)",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Stats Row: Study Time, Tests, Streak
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Today study time
                Card(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = BluePrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = JalaliCalendar.fmtHM(totalTodaySecs / 60),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "مطالعه امروز",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }

                // Tests count
                Card(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${JalaliCalendar.faNum(totalTodayTests)} تست",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "تست‌های امروز",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }

                // Streak
                Card(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFF59E0B))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${JalaliCalendar.faNum(streakDays)} روز",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "تداوم مطالعه",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Quick Launch Timer Button
        item {
            Button(
                onClick = onNavigateToTimer,
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isTimerRunning) Color(0xFF10B981) else BluePrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Icon(if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isTimerRunning) "تایمر در حال اجرا — ورود به صفحه تایمر" else "شروع تایمر هوشمند مطالعه",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        // Due Leitner Review Notice
        if (dueMistakes.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToLeitner() }
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFFD97706))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "مرور جعبه لایتنر امروز",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                            Text(
                                text = "${JalaliCalendar.faNum(dueMistakes.size)} کارت تست و نکته نیازمند مرور است.",
                                fontSize = 12.sp,
                                color = Color(0xFFB45309)
                            )
                        }
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color(0xFFD97706))
                    }
                }
            }
        }

        // Today's Plan Tasks Preview
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "برنامه مطالعاتی امروز",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        TextButton(onClick = onNavigateToPlan) {
                            Text("مدیریت برنامه‌ها")
                        }
                    }

                    val items = todayPlan?.items ?: emptyList()
                    if (items.isEmpty()) {
                        Text(
                            text = "هنوز برنامه‌ای برای امروز ثبت نشده است. روی مدیریت برنامه‌ها بزنید.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    } else {
                        items.take(4).forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = item.done,
                                    onCheckedChange = { viewModel.togglePlanItem(item.id) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item.text,
                                    fontSize = 13.sp,
                                    color = if (item.done) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
