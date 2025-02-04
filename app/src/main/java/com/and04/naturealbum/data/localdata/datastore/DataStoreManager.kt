package com.and04.naturealbum.data.localdata.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.and04.naturealbum.utils.time.toLocalDateTime
import com.and04.naturealbum.utils.time.toSyncDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStoreManager(
    private val context: Context,
) {
    private val Context.dataStore by preferencesDataStore(name = SYNC_TIME)
    val syncTime = context.dataStore.data
        .map { time ->
            time[SYNC_TIME_KEY]?.toLocalDateTime()?.toSyncDate() ?: NEVER_SYNC
        }

    suspend fun setSyncTime(dateTime: String) {
        context.dataStore.edit { store ->
            store[SYNC_TIME_KEY] = dateTime
        }
    }

    suspend fun getDeletedFileNames(): Set<String> {
        return context.dataStore.data.map { preferences ->
            preferences[DELETED_FILE_NAMES_KEY] ?: emptySet()
        }.first()
    }

    suspend fun setDeletedFileName(fileName: String) {
        context.dataStore.edit { preferences ->
            val currentFileNames = preferences[DELETED_FILE_NAMES_KEY] ?: emptySet()
            preferences[DELETED_FILE_NAMES_KEY] = currentFileNames + fileName
        }
    }

    suspend fun removeDeletedFileName(fileName: String) {
        context.dataStore.edit { preferences ->
            val currentFileNames = preferences[DELETED_FILE_NAMES_KEY] ?: emptySet()
            if (currentFileNames.isNotEmpty())
                preferences[DELETED_FILE_NAMES_KEY] = currentFileNames - fileName
        }
    }

    suspend fun clear() {
        context.dataStore.edit { store ->
            store.clear()
        }
    }

    companion object {
        private const val SYNC_TIME = "sync_time"
        const val NEVER_SYNC = "이력 없음"
        private val SYNC_TIME_KEY = stringPreferencesKey(SYNC_TIME)

        private const val DELETED_FILE_NAME = "deleted_file_names"
        private val DELETED_FILE_NAMES_KEY = stringSetPreferencesKey(DELETED_FILE_NAME)
    }
}
