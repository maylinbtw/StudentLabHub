package com.slh.studentlabhub.ui.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.slh.studentlabhub.SlhApplication
import com.slh.studentlabhub.domain.model.SubjectSummary
import com.slh.studentlabhub.domain.repository.LabRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class SubjectScore(
    val id: Long,
    val shortTitle: String,
    val averageScore: Double?,
)

data class StatisticsUiState(
    val isLoading: Boolean = true,
    val subjects: List<SubjectScore> = emptyList(),
    val overallAverage: Double? = null,
    val totalWorks: Int = 0,
    val doneWorks: Int = 0,
    val defendedWorks: Int = 0,
) {
    val remainingWorks: Int
        get() = totalWorks - doneWorks
}

class StatisticsViewModel(
    repository: LabRepository,
) : ViewModel() {

    val uiState: StateFlow<StatisticsUiState> = repository.observeSubjects()
        .map { subjects -> subjects.toStatistics() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = StatisticsUiState(),
        )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SlhApplication
                StatisticsViewModel(app.container.labRepository)
            }
        }
    }
}

private fun List<SubjectSummary>.toStatistics(): StatisticsUiState {
    val averages = mapNotNull { it.averageScore }
    return StatisticsUiState(
        isLoading = false,
        subjects = map { subject ->
            SubjectScore(
                id = subject.id,
                shortTitle = shortTitle(subject.title),
                averageScore = subject.averageScore,
            )
        },
        overallAverage = if (averages.isEmpty()) null else averages.average(),
        totalWorks = sumOf { it.totalLabs },
        doneWorks = sumOf { it.doneLabs },
        defendedWorks = sumOf { it.defendedLabs },
    )
}

private fun shortTitle(title: String): String {
    val trimmed = title.trim()
    if (trimmed.length <= 6) return trimmed

    val words = trimmed
        .split(' ', '-')
        .map { word -> word.filter { it.isLetterOrDigit() } }
        .filter { it.isNotEmpty() }

    val latinWord = words.firstOrNull { it.first() in 'A'..'Z' || it.first() in 'a'..'z' }
    if (latinWord != null) return latinWord.take(8)

    return if (words.size > 1) {
        words.joinToString(separator = "") { it.first().uppercase() }.take(5)
    } else {
        trimmed.take(6)
    }
}