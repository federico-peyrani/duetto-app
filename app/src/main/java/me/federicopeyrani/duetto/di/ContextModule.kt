package me.federicopeyrani.duetto.di

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ContextModule {

    @Singleton
    @Binds
    abstract fun context(appInstance: Application): Context
}