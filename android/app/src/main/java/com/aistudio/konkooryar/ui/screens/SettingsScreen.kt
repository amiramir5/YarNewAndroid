package com.aistudio.konkooryar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.konkooryar.ui.theme.BluePrimary
import com.aistudio.konkooryar.ui.viewmodel.KonkoorViewModel
import com.aistudio.konkooryar.util.JalaliCalendar

@Composable
fun SettingsScreen(
    viewModel: KonkoorViewModel
) {
    val settings by viewModel.settings.collectAsState()
    var goalMinutes by remember(settings) { mutableStateOf(settings.dailyGoalMinutes.toString()) }
    var goalTests by remember(settings) { mutableStateOf(settings.dailyGoalTests.toString()) }
    var examTitle by remember(settings) { mutableStateOf(settings.examTitle) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp)
    ) {
        item {
            Text(
                text = "تنظیمات برنامه کنکوریار",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BluePrimary
            )
            Text(
                text = "تنظیم اهداف روزانه و تاریخ کنکور",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = examTitle,
                        onValueChange = { examTitle = it },
                        label = { Text("عنوان آزمون هدف") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = goalMinutes,
                        onValueChange = { goalMinutes = it },
                        label = { Text("هدف مطالعه روزانه (دقیقه)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = goalTests,
                        onValueChange = { goalTests = it },
                        label = { Text("هدف حل تست روزانه (تعداد)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "پخش صدا و هشدارها", fontSize = 13.sp)
                        Switch(
                            checked = settings.soundEnabled,
                            onCheckedChange = {
                                viewModel.updateSettings(settings.copy(soundEnabled = it))
                            }
                        )
                    }

                    Button(
                        onClick = {
                            val gm = goalMinutes.toIntOrNull() ?: 420
                            val gt = goalTests.toIntOrNull() ?: 100
                            viewModel.updateSettings(
                                settings.copy(
                                    dailyGoalMinutes = gm,
                                    dailyGoalTests = gt,
                                    examTitle = examTitle
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("ذخیره تنظیمات", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "درباره کنکوریار ۲.۰ نیتیو اندروید",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = BluePrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "این اپلیکیشن با زبان مدرن Kotlin و رابط کاربری Jetpack Compose به همراه دیتابیس بومی Room برای داوطلبان کنکور سراسری تجربی پیاده‌سازی شده است.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
