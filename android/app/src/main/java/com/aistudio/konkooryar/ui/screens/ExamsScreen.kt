package com.aistudio.konkooryar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.konkooryar.data.model.ExamModel
import com.aistudio.konkooryar.ui.theme.BluePrimary
import com.aistudio.konkooryar.ui.viewmodel.KonkoorViewModel
import com.aistudio.konkooryar.util.Constants
import com.aistudio.konkooryar.util.JalaliCalendar

@Composable
fun ExamsScreen(
    viewModel: KonkoorViewModel
) {
    val exams by viewModel.exams.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    var selectedSubject by remember { mutableStateOf("bio") }
    var correctCount by remember { mutableStateOf("") }
    var wrongCount by remember { mutableStateOf("") }
    var blankCount by remember { mutableStateOf("") }
    var durationMinutes by remember { mutableStateOf("30") }
    var examNote by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = BluePrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "ثبت آزمون جدید")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "کارنامه‌ها و آزمون‌های سنجشی",
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = BluePrimary
            )
            Text(
                text = "محاسبه درصد با فرمول نمره منفی سازمان سنجش (۳ درست - ۱ غلط)",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (exams.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "هنوز آزمونی ثبت نشده است. با دکمه + کارنامه اضافه کنید.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 96.dp)
                ) {
                    items(exams, key = { it.id }) { exam ->
                        ExamCardItem(
                            exam = exam,
                            onDelete = { viewModel.deleteExam(exam.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("ثبت کارنامه آزمون جدید", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = correctCount,
                        onValueChange = { correctCount = it },
                        label = { Text("تعداد تست درست") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = wrongCount,
                        onValueChange = { wrongCount = it },
                        label = { Text("تعداد تست نادرست") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = blankCount,
                        onValueChange = { blankCount = it },
                        label = { Text("تعداد تست نزده") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = durationMinutes,
                        onValueChange = { durationMinutes = it },
                        label = { Text("مدت زمان (دقیقه)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = examNote,
                        onValueChange = { examNote = it },
                        label = { Text("نام آزمون / یادداشت (مثلا ماز ۲۶ مهر)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val c = correctCount.toIntOrNull() ?: 0
                        val w = wrongCount.toIntOrNull() ?: 0
                        val b = blankCount.toIntOrNull() ?: 0
                        val d = durationMinutes.toIntOrNull() ?: 30
                        viewModel.saveExam(selectedSubject, c, w, b, d, examNote)
                        showAddDialog = false
                        correctCount = ""
                        wrongCount = ""
                        blankCount = ""
                        examNote = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Text("محاسبه و ذخیره")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }
}

@Composable
fun ExamCardItem(
    exam: ExamModel,
    onDelete: () -> Unit
) {
    val subj = Constants.getSubject(exam.subject)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${subj.iconEmoji} ${subj.name}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                if (exam.note.isNotEmpty()) {
                    Text(
                        text = exam.note,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "درست: ${JalaliCalendar.faNum(exam.correct)}",
                        fontSize = 11.sp,
                        color = Color(0xFF10B981)
                    )
                    Text(
                        text = "غلط: ${JalaliCalendar.faNum(exam.wrong)}",
                        fontSize = 11.sp,
                        color = Color(0xFFEF4444)
                    )
                    Text(
                        text = "نزده: ${JalaliCalendar.faNum(exam.blank)}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${JalaliCalendar.faNum(String.format("%.1f", exam.percent))}٪",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = if (exam.percent >= 50.0) Color(0xFF10B981) else Color(0xFFEF4444)
                )
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
