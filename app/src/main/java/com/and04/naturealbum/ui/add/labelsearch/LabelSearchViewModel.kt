package com.and04.naturealbum.ui.add.labelsearch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.repository.local.LabelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LabelSearchViewModel @Inject constructor(
    private val labelRepository: LabelRepository,
) : ViewModel() {
    private val _queryLabel = MutableStateFlow(QueryLabel.empty())
    val queryLabel = _queryLabel.asStateFlow()

    val uiState = flow {
        emit(labelRepository.getLabels())
    }
        .map {
            it.toLabelSearchUiState()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LabelSearchUiState.Loading
        )

    fun updateQuery(text: String) {
        _queryLabel.value = _queryLabel.value.copy(text = text)
        if (text.isEmpty()) refreshChipColor()
    }

    private fun refreshChipColor() {
        _queryLabel.value = _queryLabel.value.copy(color = getRandomColor())
    }
}

data class QueryLabel(val text: String, val color: String) {
    companion object {
        fun empty() = QueryLabel("", getRandomColor())
    }
}
