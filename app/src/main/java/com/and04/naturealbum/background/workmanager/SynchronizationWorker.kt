package com.and04.naturealbum.background.workmanager

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.and04.naturealbum.data.datastore.DataStoreManager
import com.and04.naturealbum.data.dto.FirebaseLabel
import com.and04.naturealbum.data.dto.FirebaseLabelResponse
import com.and04.naturealbum.data.dto.FirebasePhotoInfo
import com.and04.naturealbum.data.dto.FirebasePhotoInfoResponse
import com.and04.naturealbum.data.dto.SyncAlbumsDto
import com.and04.naturealbum.data.dto.SyncPhotoDetailsDto
import com.and04.naturealbum.data.repository.DataRepository
import com.and04.naturealbum.data.repository.FireBaseRepository
import com.and04.naturealbum.data.room.Album
import com.and04.naturealbum.data.room.HazardAnalyzeStatus
import com.and04.naturealbum.data.room.Label
import com.and04.naturealbum.data.room.PhotoDetail
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

@HiltWorker
class SynchronizationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val roomRepository: DataRepository,
    private val fireBaseRepository: FireBaseRepository,
    private val syncDataStore: DataStoreManager
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        private var IS_RUNNING = false
        private const val WORKER_NAME = "WORKER_SYNCHRONIZATION"
        private const val HOUR = 0
        private const val MINUTE = 0

        fun isWorking() = IS_RUNNING

        fun runSync(context: Context) {
            val duration = getDurationTime()

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<SynchronizationWorker>()
                .setInitialDelay(duration.seconds, TimeUnit.SECONDS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    WORKER_NAME,
                    ExistingWorkPolicy.REPLACE,
                    workRequest
                )
        }

        fun runImmediately(context: Context) {
            if (!IS_RUNNING) {
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.UNMETERED) //Wifi 연결 시 실행
                    .build()

                val workRequest = OneTimeWorkRequestBuilder<SynchronizationWorker>()
                    .setConstraints(constraints)
                    .build()

                WorkManager.getInstance(context)
                    .enqueueUniqueWork(
                        WORKER_NAME,
                        ExistingWorkPolicy.REPLACE,
                        workRequest
                    )
            }
        }


        fun cancel(context: Context) {
            WorkManager.getInstance(context)
                .cancelUniqueWork(WORKER_NAME)
        }

        private fun getDurationTime(): Duration {
            val triggerHour = HOUR
            val triggerMinute = MINUTE

            val newSyncTime = LocalTime.of(triggerHour, triggerMinute)
            val now: LocalDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES)
            val nowTime: LocalTime = now.toLocalTime()

            val plusDay = if (nowTime == newSyncTime || nowTime.isAfter(newSyncTime)) 1 else 0

            val nextTriggerTime = now.plusDays(plusDay.toLong())
                .withHour(newSyncTime.hour)
                .withMinute(newSyncTime.minute)

            return Duration.between(LocalDateTime.now(), nextTriggerTime)
        }
    }

    override suspend fun doWork(): Result = coroutineScope {
        try {
            val currentUser = Firebase.auth.currentUser ?: return@coroutineScope Result.failure()
            val uid = currentUser.uid
            IS_RUNNING = true

            val unSynchronizedPhotoDetailsToLocal: MutableList<FirebasePhotoInfoResponse> =
                mutableListOf()
            val fileNameToLabelUid =
                HashMap<String, Pair<Int, String>>()// key LabelName, value (label_id to labelName)

            val label = async {
                val labels = fireBaseRepository.getLabels(uid)
                val allLocalLabels = roomRepository.getSyncCheckAlbums()

                val unSynchronizedLabelsToServer = allLocalLabels.filter { label ->
                    labels.none { firebaseLabel ->
                        firebaseLabel.labelName == label.labelName
                    }
                }

                val unSynchronizedLabelsToLocal = labels.filter { label ->
                    allLocalLabels.none { localLabel ->
                        localLabel.labelName == label.labelName
                    }
                }

                unSynchronizedLabelsToServer.forEach { label ->
                    launch {
                        insertLabelToServer(uid, label)
                    }
                }

                unSynchronizedLabelsToLocal.forEach { label ->
                    launch {
                        fileNameToLabelUid[label.labelName] =
                            insertLabelToLocal(label) to label.fileName
                    }
                }
            }

            val photoDetail = async {
                val photos = fireBaseRepository.getPhotos(uid)
                val allLocalPhotos = roomRepository.getSyncCheckPhotos()

                val unSynchronizedPhotoDetailsToServer = allLocalPhotos.filter { photo ->
                    photos.none { firebasePhoto ->
                        firebasePhoto.fileName == photo.fileName
                    }
                }

                unSynchronizedPhotoDetailsToServer.forEach { photo ->
                    launch {
                        insertPhotoDetail(uid, photo)
                    }
                }

                unSynchronizedPhotoDetailsToLocal.addAll(
                    photos.filter { photo ->
                        allLocalPhotos.none { localPhoto ->
                            localPhoto.fileName == photo.fileName
                        }
                    }
                )
            }

            label.await()
            photoDetail.await()

            async {
                unSynchronizedPhotoDetailsToLocal.forEach { photo ->
                    launch {
                        insertPhotoDetailAndAlbumToLocal(photo, fileNameToLabelUid)
                    }
                }
            }.await()

            syncDataStore.setSyncTime(
                LocalDateTime.now(ZoneId.of("Asia/Seoul"))
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            )
            Result.success()
        } catch (e: Exception) {
            //TODO FireStore와 LocalDB 비교 후 같이면 Result.success() 다르면 retry()
            Result.retry()
        } finally {
            IS_RUNNING = false
            runSync(applicationContext)
        }
    }

    private suspend fun insertLabelToServer(uid: String, label: SyncAlbumsDto) {
        val storageUri = fireBaseRepository
            .saveImageFile(
                uid = uid,
                label = label.labelName,
                fileName = label.fileName,
                uri = label.photoDetailUri.toUri(),
            )

        fireBaseRepository
            .insertLabel(
                uid = uid,
                labelName = label.labelName,
                labelData = FirebaseLabel(
                    backgroundColor = label.labelBackgroundColor,
                    thumbnailUri = storageUri.toString(),
                    fileName = label.fileName
                )
            )
    }

    private suspend fun insertLabelToLocal(label: FirebaseLabelResponse): Int =
        withContext(Dispatchers.IO + SupervisorJob()) {
            val localLabelData = Label(
                backgroundColor = label.backgroundColor,
                name = label.labelName
            )

            roomRepository.insertLabel(localLabelData).toInt()
        }

    private suspend fun insertPhotoDetailAndAlbumToLocal(
        photo: FirebasePhotoInfoResponse,
        fileNameToLabelUid: HashMap<String, Pair<Int, String>>
    ) = withContext(Dispatchers.IO + SupervisorJob()) {

        val valueList = fileNameToLabelUid.values.toList()
        val findAlbumData = valueList.find { value -> value.second == photo.fileName }
        val uri = makeFileToUri(photo.uri, photo.fileName)

        if (findAlbumData != null) {
            val labelId = findAlbumData.first
            val photoDetailId = insertPhotoDetailToLocal(photo, labelId, uri)

            insertAlbum(labelId, photoDetailId)
        } else {
            val labelId =
                fileNameToLabelUid[photo.label]?.first ?: roomRepository.getIdByName(photo.label)!!
            insertPhotoDetailToLocal(photo, labelId, uri)
        }
    }

    private suspend fun insertPhotoDetail(uid: String, photo: SyncPhotoDetailsDto) {
        //TODO 유해성 검사

        val storageUri = fireBaseRepository
            .saveImageFile(
                uid = uid,
                label = photo.labelName,
                fileName = photo.fileName,
                uri = photo.photoDetailUri.toUri(),
            )

        fireBaseRepository
            .insertPhotoInfo(
                uid = uid,
                fileName = photo.fileName,
                photoData = FirebasePhotoInfo(
                    uri = storageUri.toString(),
                    label = photo.labelName,
                    latitude = photo.latitude,
                    longitude = photo.longitude,
                    description = photo.description,
                    datetime = photo.datetime
                )
            )
    }

    private suspend fun insertPhotoDetailToLocal(
        photo: FirebasePhotoInfoResponse,
        labelId: Int,
        uri: String
    ): Int {
        return roomRepository.insertPhoto(
            PhotoDetail(
                labelId = labelId,
                photoUri = uri,
                fileName = photo.fileName,
                latitude = photo.latitude ?: 0.0, //FIXME 위치 NULL 해결 되면 삭제
                longitude = photo.longitude ?: 0.0,
                description = photo.description,
                datetime = LocalDateTime
                    .parse(photo.datetime, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    .atZone(ZoneId.of("UTC"))
                    .withZoneSameInstant(ZoneId.systemDefault())
                    .toLocalDateTime(),
                hazardCheckResult = HazardAnalyzeStatus.PASS
            )
        ).toInt()
    }

    private suspend fun insertAlbum(labelId: Int, photoDetailId: Int) {
        roomRepository.insertPhotoInAlbum(
            Album(
                labelId = labelId,
                photoDetailId = photoDetailId
            )
        )
    }

    private fun makeFileToUri(photoUri: String, fileName: String): String {
        val context = applicationContext
        val storage = context.filesDir
        val imageFile = File(storage, fileName)
        imageFile.createNewFile()

        FileOutputStream(imageFile).use { fos ->
            BitmapFactory.decodeStream(URL(photoUri).openStream()).apply {
                if (Build.VERSION.SDK_INT >= 30) {
                    compress(Bitmap.CompressFormat.WEBP_LOSSY, 100, fos)
                } else {
                    compress(Bitmap.CompressFormat.JPEG, 100, fos)
                }

                recycle()
            }
            fos.flush()
        }

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        ).toString()
    }
}
