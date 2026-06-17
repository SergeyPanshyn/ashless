package com.span.ashless.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.span.ashless.data.local.AshlessDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSHomeDirectory

actual fun platformModule(): Module =
    module {
        single {
            Room.databaseBuilder<AshlessDatabase>(name = "${NSHomeDirectory()}/Documents/ashless.db")
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .build()
        }
        single { get<AshlessDatabase>().entryDao() }
    }
