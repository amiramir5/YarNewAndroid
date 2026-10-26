package com.aistudio.konkooryar.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.konkooryar.data.model.MistakeCardModel
import com.aistudio.konkooryar.ui.theme.BluePrimary
import com.aistudio.konkooryar.ui.viewmodel.KonkoorViewModel
import com.aistudio.konkooryar.util.Constants
import com.aistudio.konkooryar.util.JalaliCalendar

@Composable
fun MistakesScreen(
    viewModel: KonkoorViewModel
) {
    val mistakes by viewModel.mistakes.collectAsState()
    val dueMistakes by viewModel.dueMistakes.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: Due for review, 1: All cards
    var showAddDialog by remember { mutableStateOf(false) }

    var newSubject by remember { mutableStateOf("bio") }
    var newChapter by remember { mutableStateOf("") }
    var newQuestion by remember { mutableStateOf("") }
    var newCause by remember { mutableStateOf(Constants.MISTAKE_CAUSES.first()) }
    var newSource by remember { mutableStateOf("") }

    val displayedList = if (selectedTab == 0) dueMistakes else mistakes

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = BluePrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "افزودن کارت جدید")
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

            // Leitner 5-Box Distribution Summary
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "جعبه لایتنر هوشمند (مرور ۵ مرحله‌ای)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (boxNum in 1..5) {
                            val count = mistakes.count { it.box == boxNum }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "خانه ${JalaliCalendar.faNum(boxNum)}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = JalaliCalendar.faNum(count),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = BluePrimary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tab Switcher: Due for Review vs All Cards
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "نیازمند مرور (${JalaliCalendar.faNum(dueMistakes.size)})",
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "همه کارت‌ها (${JalaliCalendar.faNum(mistakes.size)})",
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Flashcards List
            if (displayedList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (selectedTab == 0) "عالیه! کارت نیازمند مرور برای امروز ندارید. 🎉" else "هنوز نکته یا تستی در لایتنر ثبت نشده است.",
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
                    items(displayedList, key = { it.id }) { card ->
                        MistakeCardItem(
                            card = card,
                            onRemembered = { viewModel.reviewMistake(card, true) },
                            onForgotten = { viewModel.reviewMistake(card, false) },
                            onDelete = { viewModel.deleteMistake(card.id) }
                        )
                    }
                }
            }
        }
    }

    // Add Mistake Card Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("افزودن تست و نکته به لایتنر", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = newQuestion,
                        onValueChange = { newQuestion = it },
                        label = { Text("صورت تست یا نکته کلیدی") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newSource,
                        onValueChange = { newSource = it },
                        label = { Text("منبع سوال (مثلاً قلم‌چی ۱۴۰۵ یا کتاب تست)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newChapter,
                        onValueChange = { newChapter = it },
                        label = { Text("فصل یا مبحث") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newQuestion.isNotBlank()) {
                            viewModel.saveMistake(
                                subject = newSubject,
                                chapter = newChapter,
                                cause = newCause,
                                question = newQuestion,
                                source = newSource,
                                importance = 3
                            )
                            newQuestion = ""
                            newSource = ""
                            newChapter = ""
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BluePrimary)
                ) {
                    Text("افزودن کارت")
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
fun MistakeCardItem(
    card: MistakeCardModel,
    onRemembered: () -> Unit,
    onForgotten: () -> Unit,
    onDelete: () -> Unit
) {
    val subj = Constants.getSubject(card.subject)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${subj.iconEmoji} ${subj.shortName}",
                        fontWeight = FontWeight.Bold,
                        color = BluePrimary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "خانه ${JalaliCalendar.faNum(card.box)}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = card.question,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 20.sp
            )

            if (card.source.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "منبع: ${card.source}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onRemembered,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("یادم بود (خانه بعد)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onForgotten,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("فراموش کردم (خانه ۱)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
