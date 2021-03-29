package me.federicopeyrani.duetto.di.modules

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import me.federicopeyrani.duetto.data.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, "app-database").build()

    @Provides
    @Singleton
    fun providesTrackDao(appDatabase: AppDatabase) = appDatabase.trackDao()

    @Provides
    @Singleton
    fun playHistoryDao(appDatabase: AppDatabase) = appDatabase.playHistoryDao()
}