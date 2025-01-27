package com.and04.naturealbum.ui.add.savephoto

import android.graphics.Bitmap
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.localdata.room.Album
import com.and04.naturealbum.data.localdata.room.HazardAnalyzeStatus
import com.and04.naturealbum.data.localdata.room.Label
import com.and04.naturealbum.data.localdata.room.Label.Companion.NEW_LABEL
import com.and04.naturealbum.data.localdata.room.PhotoDetail
import com.and04.naturealbum.data.repository.RetrofitRepository
import com.and04.naturealbum.data.repository.local.LabelRepository
import com.and04.naturealbum.data.repository.local.LocalAlbumRepository
import com.and04.naturealbum.data.repository.local.PhotoDetailRepository
import com.and04.naturealbum.ui.utils.UiState
import com.and04.naturealbum.utils.network.NetworkState
import com.google.firebase.Firebase
import com.google.firebase.vertexai.type.content
import com.google.firebase.vertexai.vertexAI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class SavePhotoViewModel @Inject constructor(
    private val retrofitRepository: RetrofitRepository,
    private val photoDetailRepository: PhotoDetailRepository,
    private val localAlbumRepository: LocalAlbumRepository,
    private val labelRepository: LabelRepository,
) : ViewModel() {
    private val _photoSaveState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val photoSaveState: StateFlow<UiState<Unit>> = _photoSaveState

    private val _uiState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val uiState: StateFlow<UiState<String>> = _uiState

    fun getGeneratedContent(bitmap: Bitmap?) = viewModelScope.launch {
        try {
            bitmap?.let { nonNullBitmap ->
                _uiState.emit(UiState.Loading)
                val model = Firebase.vertexAI.generativeModel(GEMINI_MODEL)
                val content = content {
                    image(nonNullBitmap)
                    text(GEMINI_PROMPT)
                }

                val result = model.generateContent(content)
                _uiState.emit(UiState.Success(result.text ?: ""))
            }
        } catch (e: Exception) {
            Log.e("Error", e.message.toString())
            _uiState.emit(UiState.Error(e.message.toString()))
        }
    }

    fun savePhoto(
        uri: String,
        fileName: String,
        label: Label,
        location: Location,
        description: String,
        isRepresented: Boolean,
        time: LocalDateTime,
    ) {
        _photoSaveState.value = UiState.Loading // 로딩 시작
        viewModelScope.launch {
            try {
                val labelId =
                    if (label.id == NEW_LABEL) labelRepository.insertLabel(label).toInt()
                    else label.id

                val album = async { localAlbumRepository.getAlbumByLabelId(labelId) }
                val address = async {
                    if (NetworkState.getNetWorkCode() != NetworkState.DISCONNECTED) {
                        retrofitRepository.convertCoordsToAddress(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                    } else {
                        EMPTY_ADDRESS
                    }
                }
                val photoDetailId = async {
                    photoDetailRepository.insertPhoto(
                        PhotoDetail(
                            labelId = labelId,
                            photoUri = uri,
                            fileName = fileName,
                            latitude = location.latitude,
                            longitude = location.longitude,
                            description = description,
                            hazardCheckResult = HazardAnalyzeStatus.NOT_CHECKED,
                            datetime = time,
                            address = address.await()
                        )
                    )
                }

                launch {
                    photoDetailRepository.updateAddressByPhotoDetailId(
                        address = address.await(),
                        photoDetailId = photoDetailId.await().toInt()
                    )
                }

                album.await().run {
                    if (isEmpty()) {
                        localAlbumRepository.insertPhotoInAlbum(
                            Album(
                                labelId = labelId,
                                photoDetailId = photoDetailId.await().toInt()
                            )
                        )
                    } else if (isRepresented) {
                        localAlbumRepository.updateAlbum(
                            first().copy(
                                photoDetailId = photoDetailId.await().toInt()
                            )
                        )
                    } else {
                    }
                }
                _photoSaveState.emit(UiState.Success(Unit)) // 저장 완료
            } catch (e: Exception) {
                Log.e("SavePhotoViewModel", "Error saving photo: ${e.message}")
                _photoSaveState.emit(UiState.Idle)
            }
        }
    }

    companion object {
        private const val GEMINI_MODEL = "gemini-1.5-flash"
        private const val GEMINI_PROMPT =
            "이 이미지를 보고 어떤 생물인지 생물 도감의 이름(학명 또는 일반명)으로 가장 유사한 하나의 단어를 답해주세요. 학명이 있다면 학명을 우선 사용하세요. 추가 설명은 하지 마세요. 한국어를 사용하세요."
        private const val EMPTY_ADDRESS = ""
    }
}
