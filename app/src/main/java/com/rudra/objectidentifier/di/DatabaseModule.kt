package com.rudra.objectidentifier.di

import android.content.Context
import androidx.room.Room
import com.rudra.objectidentifier.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "object_identifier.db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideScanHistoryDao(database: AppDatabase) = database.scanHistoryDao()
}
