package com.and04.naturealbum.ui.album.labelphotos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.and04.naturealbum.data.localdata.datastore.DataStoreManager
import com.and04.naturealbum.data.localdata.room.PhotoDetail
import com.and04.naturealbum.data.model.AlbumFolderData
import com.and04.naturealbum.data.repository.firebase.AlbumRepository
import com.and04.naturealbum.data.repository.local.LabelRepository
import com.and04.naturealbum.data.repository.local.PhotoDetailRepository
import com.and04.naturealbum.ui.utils.UiState
import com.and04.naturealbum.ui.utils.UserManager
import com.and04.naturealbum.utils.network.NetworkState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AlbumFolderViewModel @Inject constructor(
    private val photoDetailRepository: PhotoDetailRepository,
    private val syncDataStore: DataStoreManager,
    private val albumRepository: AlbumRepository,
    private val labelRepository: LabelRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<AlbumFolderData>>(UiState.Idle)
    val uiState: StateFlow<UiState<AlbumFolderData>> = _uiState

    fun loadFolderData(labelId: Int) {
        viewModelScope.launch {
            _uiState.emit(UiState.Loading)

            val labelJob = async {
                labelRepository.getLabelById(id = labelId)
            }

            val photoDetailsJob = async {
                photoDetailRepository.getPhotoDetailsUriByLabelId(labelId = labelId).reversed()
            }

            val labelData = labelJob.await()
            val photoDetailsData = photoDetailsJob.await()

            _uiState.emit(UiState.Success(AlbumFolderData(labelData, photoDetailsData)))
        }
    }

    fun deletePhotos(photoDetails: Set<PhotoDetail>) {
        viewModelScope.launch {
            val currentData = (_uiState.value as? UiState.Success)?.data ?: return@launch
            val updatedPhotoDetails = currentData.photoDetails.toMutableList()
            photoDetails.forEach { photoDetail ->
                photoDetailRepository.deleteImage(photoDetail) // Room에서 삭제
                syncDataStore.setDeletedFileName(photoDetail.fileName) // 삭제 정보를 DataStore에 저장
                launch(Dispatchers.IO) { deleteFile(photoDetail.fileName) } //file에서 이미지 삭제
                launch(Dispatchers.IO) {
                    val uid = UserManager.getUser()?.uid
                    if (NetworkState.getNetWorkCode() != NetworkState.DISCONNECTED && !uid.isNullOrEmpty()) {
                        val label = labelRepository.getLabelById(photoDetail.labelId)
                        albumRepository.deleteImageFile(
                            uid = uid,
                            label = label,
                            fileName = photoDetail.fileName,
                        )
                    }
                }
                updatedPhotoDetails.remove(photoDetail)
            }
            if (updatedPhotoDetails.isEmpty()) {
                _uiState.emit(UiState.Error("empty"))
            } else {
                _uiState.emit(UiState.Success(currentData.copy(photoDetails = updatedPhotoDetails)))
            }
        }
    }

    private fun deleteFile(fileName: String) {
        val file = File("$filePath${fileName}")
        if (file.exists()) {
            file.delete()
        }
    }

    companion object {
        val filePath = "/data/user/0/com.and04.naturealbum/files/"
    }
}
