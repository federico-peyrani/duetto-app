package me.federicopeyrani.duetto

import android.app.Application
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.HiltAndroidApp
import me.federicopeyrani.duetto.di.BindingComponentBuilder
import javax.inject.Inject
import javax.inject.Provider

@HiltAndroidApp
class MainApplication : Application() {

    @Inject lateinit var bindingComponentProvider: Provider<BindingComponentBuilder>

    override fun onCreate() {
        super.onCreate()
        DataBindingUtil.setDefaultComponent(bindingComponentProvider.get().build())
    }
}