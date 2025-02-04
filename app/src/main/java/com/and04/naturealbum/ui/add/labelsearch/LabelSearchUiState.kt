package com.and04.naturealbum.ui.add.labelsearch

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import com.and04.naturealbum.data.localdata.room.Label

@Stable
sealed interface LabelSearchUiState {

    @Immutable
    data object Loading : LabelSearchUiState

    @Immutable
    data class RegisteredLabels(
        val labels: List<Label>
    ) : LabelSearchUiState
}

fun List<Label>.toLabelSearchUiState(): LabelSearchUiState =
    LabelSearchUiState.RegisteredLabels(
        labels = this
    )
