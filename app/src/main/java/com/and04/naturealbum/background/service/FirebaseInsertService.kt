package com.and04.naturealbum.background.service

import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.net.toUri
import com.and04.naturealbum.data.dto.FirebaseLabel
import com.and04.naturealbum.data.dto.FirebasePhotoInfo
import com.and04.naturealbum.data.localdata.room.HazardAnalyzeStatus
import com.and04.naturealbum.data.localdata.room.Label
import com.and04.naturealbum.data.localdata.room.PhotoDetailDao
import com.and04.naturealbum.data.repository.RetrofitRepository
import com.and04.naturealbum.data.repository.firebase.AlbumRepository
import com.and04.naturealbum.utils.image.ImageConvert
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseInsertService : Service() {
    @Inject
    lateinit var albumRepository: AlbumRepository

    @Inject
    lateinit var retrofitRepository: RetrofitRepository

    @Inject
    lateinit var photoDetailDao: PhotoDetailDao
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var job: Job? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            val uid = Firebase.auth.currentUser!!.uid
            val uri = intent?.getStringExtra(SERVICE_URI) as String
            val fileName = intent.getStringExtra(SERVICE_FILENAME)!!
            val dateTime = intent.getStringExtra(SERVICE_DATETIME)!!
            val label = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(SERVICE_LABEL, Label::class.java)!!
            } else {
                intent.getParcelableExtra<Label>(SERVICE_LABEL)!!
            }
            val latitude = intent.getDoubleExtra(SERVICE_LOCATION_LATITUDE, 0.0)
            val longitude = intent.getDoubleExtra(SERVICE_LOCATION_LONGITUDE, 0.0)
            val description = intent.getStringExtra(SERVICE_DESCRIPTION) as String

            val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                Log.e("FirebaseInsertService-onStartCommand", "$throwable : 이미지 저장 오류")
                stopService(intent)
            }

            job = scope.launch(exceptionHandler) {
                val imgEncoding = ImageConvert.getBase64FromUri(applicationContext, uri)

                val hazardMapperResult =
                    retrofitRepository.analyzeHazardWithGreenEye(imgEncoding)
                if (hazardMapperResult == HazardAnalyzeStatus.FAIL) {
                    photoDetailDao.updateHazardCheckResultByFIleName(
                        HazardAnalyzeStatus.FAIL,
                        fileName
                    )
                    stopService(intent)
                    return@launch
                } else {
                    photoDetailDao.updateHazardCheckResultByFIleName(
                        HazardAnalyzeStatus.PASS,
                        fileName
                    )
                }

                val storageUriDeferred = async {
                    albumRepository.saveImageFile(
                        uid = uid,
                        label = label.name,
                        fileName = fileName,
                        uri = uri.toUri()
                    )
                }
                val serverNoLabelDeferred = async {
                    val serverLabels = albumRepository.getLabelsToList(uid).getOrThrow()
                    serverLabels.none { serverLabel ->
                        serverLabel.labelName == label.name
                    }
                }

                val storageUri = storageUriDeferred.await()
                val serverNoLabel = serverNoLabelDeferred.await()

                val insertLabelJob = launch {
                    if (serverNoLabel) {
                        albumRepository
                            .insertLabel(
                                uid = uid,
                                labelName = label.name,
                                labelData = FirebaseLabel(
                                    backgroundColor = label.backgroundColor,
                                    thumbnailUri = storageUri.toString(),
                                    fileName = fileName
                                )
                            )
                    }
                }

                val insertPhotoInfoJob = launch {
                    albumRepository.insertPhotoInfo(
                        uid = uid,
                        fileName = fileName,
                        photoData = FirebasePhotoInfo(
                            uri = storageUri.toString(),
                            label = label.name,
                            latitude = latitude,
                            longitude = longitude,
                            description = description,
                            datetime = dateTime
                        )
                    )
                }

                joinAll(insertLabelJob, insertPhotoInfoJob)
                stopService(intent)
            }

        } catch (e: NullPointerException) {
            Log.e("FirebaseInsertService", e.toString())
            stopService(intent)
        }

        return START_NOT_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

    companion object {
        const val SERVICE_URI = "service_uri"
        const val SERVICE_FILENAME = "service_filename"
        const val SERVICE_LABEL = "service_label"
        const val SERVICE_LOCATION_LATITUDE = "service_location_latitude"
        const val SERVICE_LOCATION_LONGITUDE = "service_location_longitude"
        const val SERVICE_DESCRIPTION = "service_location"
        const val SERVICE_DATETIME = "service_datetime"
    }
}
