package com.canyoufix.minikinopoisk

import android.app.Application
import com.canyoufix.api.di.ApiModule
import com.canyoufix.minikinopoisk.di.AppModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MiniKinopoisk : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MiniKinopoisk)
            modules(
                AppModule, // App
                ApiModule.module // Api module (Retrofit)
            )
        }
    }
}