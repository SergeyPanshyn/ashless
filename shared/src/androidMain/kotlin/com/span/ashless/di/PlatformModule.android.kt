package com.span.ashless.di

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.span.ashless.data.local.AshlessDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module =
    module {
        single {
            val ctx = androidContext()
            Room.databaseBuilder<AshlessDatabase>(
                context = ctx,
                name = ctx.getDatabasePath("ashless.db").absolutePath,
            )
                .fallbackToDestructiveMigration(dropAllTables = true)
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .build()
        }
        single { get<AshlessDatabase>().entryDao() }
        single { get<AshlessDatabase>().programDao() }
        single {
            val ctx = androidContext()
            PreferenceDataStoreFactory.create(
                produceFile = { ctx.filesDir.resolve("ashless_settings.preferences_pb") },
            )
        }
    }
