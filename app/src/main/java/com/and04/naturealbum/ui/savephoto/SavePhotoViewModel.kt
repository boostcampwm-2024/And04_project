package com.and04.naturealbum.ui.savephoto

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.repository.DataRepository
import com.and04.naturealbum.data.room.Album
import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.data.room.PhotoDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

sealed class UiState {
    data object Idle : UiState()
    data object Loading : UiState()
    data object Success : UiState()
}

@HiltViewModel
class SavePhotoViewModel @Inject constructor(
    private val repository: DataRepository,
) : ViewModel() {

    private val _photoLoadingUiState = MutableStateFlow<UiState>(UiState.Idle)
    val photoLoadingUiState: StateFlow<UiState> = _photoLoadingUiState

    private val _photoSaveState = MutableStateFlow<UiState>(UiState.Idle)
    val photoSaveState: StateFlow<UiState> = _photoSaveState

    fun setPhotoLoadingUiSate(uiState: UiState) {
        _photoLoadingUiState.value = uiState
    }

    fun savePhoto(
        uri: String,
        label: Label,
        location: Location,
        description: String,
        isRepresented: Boolean,
    ) {
        _photoSaveState.value = UiState.Loading
        viewModelScope.launch {
            val labelId =
                if (label.id == 0) repository.insertLabel(label).toInt()
                else label.id

            val album = async { repository.getAlbumByLabelId(labelId) }
            val photoDetailId = async {
                repository.insertPhoto(
                    PhotoDetail(
                        labelId = labelId,
                        photoUri = uri,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        description = description,
                        datetime = LocalDateTime.now(ZoneId.of("UTC")),
                    )
                )
            }
            album.await().run {
                if (isEmpty()) repository.insertPhotoInAlbum(
                    Album(
                        labelId = labelId,
                        photoDetailId = photoDetailId.await().toInt()
                    )
                )
                else if (isRepresented)
                    repository.updateAlbum(
                        first().copy(
                            photoDetailId = photoDetailId.await().toInt()
                        )
                    )
                else {
                }
            }
            _photoSaveState.emit(UiState.Success)
        }
    }

}
