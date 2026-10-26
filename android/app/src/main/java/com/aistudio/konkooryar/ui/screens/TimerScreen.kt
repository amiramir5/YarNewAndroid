package com.aistudio.konkooryar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.aistudio.konkooryar.util.Constants
import com.aistudio.konkooryar.util.JalaliCalendar

@Composable
fun TimerScreen(
    viewModel: KonkoorViewModel
) {
    val activeSubject by viewModel.activeSubject.collectAsState()
    val activeChapter by viewModel.activeChapter.collectAsState()
    val isRunning by viewModel.isTimerRunning.collectAsState()
    val isPaused by viewModel.isTimerPaused.collectAsState()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsState()

    var showFinishDialog by remember { mutableStateOf(false) }
    var correctTests by remember { mutableStateOf("") }
    var wrongTests by remember { mutableStateOf("") }
    var blankTests by remember { mutableStateOf("") }
    var sessionNote by remember { mutableStateOf("") }

    val currentSubj = Constants.getSubject(activeSubject)
    val chapters = Constants.getChaptersForSubject(activeSubject)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 96.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Subject Selector Horizontal Chips
        Text(
            text = "انتخاب درس برای مطالعه",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Right
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Constants.SUBJECTS.forEach { subj ->
                val isSelected = subj.id == activeSubject
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setSubject(subj.id) },
                    label = {
                        Text(
                            text = "${subj.iconEmoji} ${subj.shortName}",
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BluePrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selected Subject & Chapter Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "${currentSubj.iconEmoji} ${currentSubj.name}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = BluePrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (activeChapter.isNotEmpty()) activeChapter else "انتخاب فصل و مبحث",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Large Yeolpumta Stopwatch Clock Display
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
                .background(if (isRunning) BluePrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = JalaliCalendar.fmtClock(elapsedSeconds),
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    color = if (isRunning) BluePrimary else MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (isRunning) "در حال ثبت زمان مطالعه..." else if (isPaused) "زمان موقتاً متوقف شد" else "آماده برای شروع",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // Controls: Start, Pause, Resume, Reset, Finish
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!isRunning && !isPaused) {
                Button(
                    onClick = { viewModel.startTimer() },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(56.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("شروع مطالعه", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            } else if (isRunning) {
                // Pause button
                Button(
                    onClick = { viewModel.pauseTimer() },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B)),
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                ) {
                    Icon(Icons.Default.Pause, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("توقف موقت", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Finish button
                Button(
                    onClick = { showFinishDialog = true },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("اتمام و ثبت", fontWeight = FontWeight.Bold)
                }
            } else if (isPaused) {
                // Resume button
                Button(
                    onClick = { viewModel.resumeTimer() },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ادامه", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Finish button
                Button(
                    onClick = { showFinishDialog = true },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("اتمام و ثبت", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Reset button
                IconButton(
                    onClick = { viewModel.resetTimer() },
                    modifier = Modifier.size(54.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "تنظیم مجدد", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }

    // Finish Session Dialog with test counts (درست، غلط، نزده)
    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = {
                Text(
                    text = "ثبت گزارش جلسه مطالعه",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "زمان ثبت‌شده: ${JalaliCalendar.fmtClock(elapsedSeconds)}",
                        fontWeight = FontWeight.Bold,
                        color = BluePrimary,
                        fontSize = 14.sp
                    )

                    OutlinedTextField(
                        value = correctTests,
                        onValueChange = { correctTests = it },
                        label = { Text("تعداد تست درست") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = wrongTests,
                        onValueChange = { wrongTests = it },
                        label = { Text("تعداد تست نادرست") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = blankTests,
                        onValueChange = { blankTests = it },
                        label = { Text("تعداد تست نزده") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = sessionNote,
                        onValueChange = { sessionNote = it },
                        label = { Text("یادداشت جلسه (اختیاری)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val c = correctTests.toIntOrNull() ?: 0
                        val w = wrongTests.toIntOrNull() ?: 0
                        val b = blankTests.toIntOrNull() ?: 0
                        viewModel.finishAndSaveSession(c, w, b, sessionNote)
                        showFinishDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Text("ذخیره نهایی")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }
}
