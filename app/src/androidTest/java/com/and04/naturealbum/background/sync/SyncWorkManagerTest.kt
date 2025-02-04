package com.and04.naturealbum.background.sync

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.and04.naturealbum.background.workmanager.SynchronizationWorker
import com.and04.naturealbum.data.localdata.datastore.DataStoreManager
import com.and04.naturealbum.data.repository.TestAiRepoImpl
import com.and04.naturealbum.data.repository.firebase.TestAlbumRepoImpl
import com.and04.naturealbum.data.repository.local.testimpl.TestLabelRepoImpl
import com.and04.naturealbum.data.repository.local.testimpl.TestLocalAlbumRepoImpl
import com.and04.naturealbum.data.repository.local.testimpl.TestPhotoDetailRepoImpl
import com.and04.naturealbum.data.repository.local.testimpl.TestSyncRepoImpl
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Test

class SyncWorkManagerTest {

    @Test
    fun syncTest() {
        val request =
            TestListenableWorkerBuilder<SynchronizationWorker>(getApplicationContext())
                .setWorkerFactory(TestWorkerFactory(getApplicationContext()))
                .build()

        runBlocking {
            val result = request.doWork()
            assertThat(result).isEqualTo(ListenableWorker.Result.success())
        }
    }
}

class TestWorkerFactory(private val context: Context) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker {
        return SynchronizationWorker(
            appContext,
            workerParameters,
            photoDetailRepository = TestPhotoDetailRepoImpl(),
            albumRepository = TestAlbumRepoImpl(),
            syncDataStore = DataStoreManager(context),
            retrofitRepository = TestAiRepoImpl(),
            syncRepository = TestSyncRepoImpl(),
            localAlbumRepository = TestLocalAlbumRepoImpl(),
            labelRepository = TestLabelRepoImpl(),
        )
    }
}
