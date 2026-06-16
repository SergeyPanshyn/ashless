package com.span.ashless

import android.app.Application
import com.span.ashless.di.dataModule
import com.span.ashless.di.domainModule
import com.span.ashless.di.platformModule
import com.span.ashless.di.presentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class AshlessApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@AshlessApplication)
            modules(dataModule, domainModule, presentationModule, platformModule())
        }
    }
}
