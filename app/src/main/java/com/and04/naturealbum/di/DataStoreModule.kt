package com.and04.naturealbum.di

import android.content.Context
import com.and04.naturealbum.data.localdata.datastore.DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun providerDataStoreManager(
        @ApplicationContext context: Context
    ) = DataStoreManager(context)
}
