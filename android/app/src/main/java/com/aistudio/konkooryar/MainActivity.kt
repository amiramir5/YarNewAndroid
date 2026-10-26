package com.aistudio.konkooryar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aistudio.konkooryar.ui.screens.*
import com.aistudio.konkooryar.ui.theme.BluePrimary
import com.aistudio.konkooryar.ui.theme.KonkoorYarTheme
import com.aistudio.konkooryar.ui.viewmodel.KonkoorViewModel
import com.aistudio.konkooryar.util.JalaliCalendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as KonkoorYarApp
        val repository = app.repository

        setContent {
            KonkoorYarTheme {
                val viewModel: KonkoorViewModel = remember {
                    KonkoorViewModel(repository)
                }

                KonkoorYarAppContent(viewModel)
            }
        }
    }
}

enum class Screen(val title: String) {
    HOME("خانه"),
    TIMER("تایمر"),
    MISTAKES("لایتنر"),
    EXAMS("آزمون‌ها"),
    PLAN("برنامه‌ها"),
    STATS("آمار"),
    SETTINGS("تنظیمات")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KonkoorYarAppContent(viewModel: KonkoorViewModel) {
    var currentScreen by remember { mutableStateOf(Screen.HOME) }
    val sessions by viewModel.sessions.collectAsState()
    val dueMistakes by viewModel.dueMistakes.collectAsState()
    val streakDays = JalaliCalendar.computeStreak(sessions.map { it.day }.toSet())

    // Android Hardware Back button handling
    if (currentScreen != Screen.HOME) {
        BackHandler {
            currentScreen = Screen.HOME
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "کنکوریار",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = BluePrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "۲.۰",
                                color = BluePrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                },
                actions = {
                    // Streak badge
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = Color(0xFFFEF3C7),
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${JalaliCalendar.faNum(streakDays)} روز",
                                color = Color(0xFFB45309),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars
            ) {
                NavigationBarItem(
                    selected = currentScreen == Screen.HOME,
                    onClick = { currentScreen = Screen.HOME },
                    icon = { Icon(Icons.Default.Home, contentDescription = "خانه") },
                    label = { Text("خانه", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.TIMER,
                    onClick = { currentScreen = Screen.TIMER },
                    icon = { Icon(Icons.Default.Timer, contentDescription = "تایمر") },
                    label = { Text("تایمر", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.MISTAKES,
                    onClick = { currentScreen = Screen.MISTAKES },
                    icon = {
                        BadgedBox(badge = {
                            if (dueMistakes.isNotEmpty()) {
                                Badge { Text(JalaliCalendar.faNum(dueMistakes.size)) }
                            }
                        }) {
                            Icon(Icons.Default.School, contentDescription = "لایتنر")
                        }
                    },
                    label = { Text("لایتنر", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.EXAMS,
                    onClick = { currentScreen = Screen.EXAMS },
                    icon = { Icon(Icons.Default.Assignment, contentDescription = "آزمون‌ها") },
                    label = { Text("آزمون‌ها", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.PLAN,
                    onClick = { currentScreen = Screen.PLAN },
                    icon = { Icon(Icons.Default.Checklist, contentDescription = "برنامه‌ها") },
                    label = { Text("برنامه‌ها", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.STATS,
                    onClick = { currentScreen = Screen.STATS },
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "آمار") },
                    label = { Text("آمار", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.SETTINGS,
                    onClick = { currentScreen = Screen.SETTINGS },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "تنظیمات") },
                    label = { Text("تنظیمات", fontSize = 10.sp) }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentScreen) {
                Screen.HOME -> HomeScreen(
                    viewModel = viewModel,
                    onNavigateToTimer = { currentScreen = Screen.TIMER },
                    onNavigateToLeitner = { currentScreen = Screen.MISTAKES },
                    onNavigateToPlan = { currentScreen = Screen.PLAN }
                )
                Screen.TIMER -> TimerScreen(viewModel = viewModel)
                Screen.MISTAKES -> MistakesScreen(viewModel = viewModel)
                Screen.EXAMS -> ExamsScreen(viewModel = viewModel)
                Screen.PLAN -> PlanScreen(viewModel = viewModel)
                Screen.STATS -> StatsScreen(viewModel = viewModel)
                Screen.SETTINGS -> SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
