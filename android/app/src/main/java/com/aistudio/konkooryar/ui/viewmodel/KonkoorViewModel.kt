package com.aistudio.konkooryar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.konkooryar.data.model.*
import com.aistudio.konkooryar.data.repository.KonkoorRepository
import com.aistudio.konkooryar.util.Constants
import com.aistudio.konkooryar.util.JalaliCalendar
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class KonkoorViewModel(private val repository: KonkoorRepository) : ViewModel() {

    val sessions: StateFlow<List<SessionModel>> = repository.allSessions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val exams: StateFlow<List<ExamModel>> = repository.allExams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val mistakes: StateFlow<List<MistakeCardModel>> = repository.allMistakes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayIso = JalaliCalendar.studyDayKey()

    val dueMistakes: StateFlow<List<MistakeCardModel>> = repository.getDueMistakes(todayIso)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayPlan: StateFlow<DailyPlanModel?> = repository.getPlanForDay(todayIso)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // App Settings
    private val _settings = MutableStateFlow(AppSettingsModel())
    val settings: StateFlow<AppSettingsModel> = _settings.asStateFlow()

    fun updateSettings(newSettings: AppSettingsModel) {
        _settings.value = newSettings
    }

    // Timer State
    private val _activeSubject = MutableStateFlow("bio")
    val activeSubject: StateFlow<String> = _activeSubject.asStateFlow()

    private val _activeChapter = MutableStateFlow("")
    val activeChapter: StateFlow<String> = _activeChapter.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _isTimerPaused = MutableStateFlow(false)
    val isTimerPaused: StateFlow<Boolean> = _isTimerPaused.asStateFlow()

    private val _elapsedSeconds = MutableStateFlow(0)
    val elapsedSeconds: StateFlow<Int> = _elapsedSeconds.asStateFlow()

    private var timerJob: Job? = null
    private var startedAtMs: Long = 0L
    private var accumulatedMs: Long = 0L

    fun setSubject(subjectId: String) {
        _activeSubject.value = subjectId
        val chapters = Constants.getChaptersForSubject(subjectId)
        if (chapters.isNotEmpty() && _activeChapter.value.isEmpty()) {
            _activeChapter.value = chapters.first()
        }
    }

    fun setChapter(chapter: String) {
        _activeChapter.value = chapter
    }

    fun startTimer() {
        if (_isTimerRunning.value) return
        val now = System.currentTimeMillis()
        startedAtMs = now
        _isTimerRunning.value = true
        _isTimerPaused.value = false

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_isTimerRunning.value) {
                delay(1000)
                val totalMs = accumulatedMs + (System.currentTimeMillis() - startedAtMs)
                _elapsedSeconds.value = (totalMs / 1000).toInt()
            }
        }
    }

    fun pauseTimer() {
        if (!_isTimerRunning.value) return
        accumulatedMs += (System.currentTimeMillis() - startedAtMs)
        _isTimerRunning.value = false
        _isTimerPaused.value = true
        timerJob?.cancel()
    }

    fun resumeTimer() {
        if (_isTimerRunning.value) return
        startedAtMs = System.currentTimeMillis()
        _isTimerRunning.value = true
        _isTimerPaused.value = false

        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_isTimerRunning.value) {
                delay(1000)
                val totalMs = accumulatedMs + (System.currentTimeMillis() - startedAtMs)
                _elapsedSeconds.value = (totalMs / 1000).toInt()
            }
        }
    }

    fun resetTimer() {
        _isTimerRunning.value = false
        _isTimerPaused.value = false
        timerJob?.cancel()
        startedAtMs = 0L
        accumulatedMs = 0L
        _elapsedSeconds.value = 0
    }

    fun finishAndSaveSession(correct: Int, wrong: Int, blank: Int, note: String) {
        val secs = _elapsedSeconds.value
        if (secs >= 5) {
            val session = SessionModel(
                id = UUID.randomUUID().toString(),
                subject = _activeSubject.value,
                chapter = _activeChapter.value,
                seconds = secs,
                correct = correct,
                wrong = wrong,
                blank = blank,
                note = note,
                day = JalaliCalendar.studyDayKey()
            )
            viewModelScope.launch {
                repository.saveSession(session)
            }
        }
        resetTimer()
    }

    // Exam Actions
    fun saveExam(subject: String, correct: Int, wrong: Int, blank: Int, duration: Int, note: String) {
        val percent = JalaliCalendar.calcKonkoorPercent(correct, wrong, blank)
        val exam = ExamModel(
            id = UUID.randomUUID().toString(),
            subject = subject,
            day = JalaliCalendar.studyDayKey(),
            correct = correct,
            wrong = wrong,
            blank = blank,
            durationMinutes = duration,
            percent = percent,
            note = note
        )
        viewModelScope.launch {
            repository.saveExam(exam)
        }
    }

    fun deleteExam(id: String) {
        viewModelScope.launch {
            repository.deleteExam(id)
        }
    }

    // Mistakes Actions
    fun saveMistake(subject: String, chapter: String, cause: String, question: String, source: String, importance: Int) {
        val nextReview = JalaliCalendar.addDays(todayIso, Constants.LEITNER_DAYS[0])
        val card = MistakeCardModel(
            id = UUID.randomUUID().toString(),
            subject = subject,
            chapter = chapter,
            cause = cause,
            question = question,
            source = source,
            importance = importance,
            box = 1,
            nextReview = nextReview
        )
        viewModelScope.launch {
            repository.saveMistake(card)
        }
    }

    fun reviewMistake(card: MistakeCardModel, remembered: Boolean) {
        val nextBox = if (remembered) (card.box + 1).coerceAtMost(5) else 1
        val intervalDays = Constants.LEITNER_DAYS[nextBox - 1]
        val nextReview = JalaliCalendar.addDays(todayIso, intervalDays)

        viewModelScope.launch {
            repository.updateMistakeReview(card.id, nextBox, nextReview)
        }
    }

    fun deleteMistake(id: String) {
        viewModelScope.launch {
            repository.deleteMistake(id)
        }
    }

    // Plan Actions
    fun addPlanItem(text: String) {
        val curPlan = todayPlan.value ?: DailyPlanModel(
            id = UUID.randomUUID().toString(),
            day = todayIso,
            wakeTime = "06:00",
            items = emptyList()
        )
        val newItem = PlanItemModel(id = UUID.randomUUID().toString(), text = text, done = false)
        val updated = curPlan.copy(items = curPlan.items + newItem)
        viewModelScope.launch {
            repository.savePlan(updated)
        }
    }

    fun togglePlanItem(itemId: String) {
        val curPlan = todayPlan.value ?: return
        val updatedItems = curPlan.items.map {
            if (it.id == itemId) it.copy(done = !it.done) else it
        }
        viewModelScope.launch {
            repository.savePlan(curPlan.copy(items = updatedItems))
        }
    }
}
