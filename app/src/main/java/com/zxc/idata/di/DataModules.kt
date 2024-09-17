package com.zxc.idata.di

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.zxc.idata.data.repository.CellDataRepository
import com.zxc.idata.data.repository.ColumnDescriptionRepository
import com.zxc.idata.data.repository.DefaultCellDataRepository
import com.zxc.idata.data.repository.DefaultColumnDescriptionRepository
import com.zxc.idata.data.repository.DefaultFileDescriptionRepository
import com.zxc.idata.data.repository.DefaultTableOrderRepository
import com.zxc.idata.data.repository.DefaultUserPreferencesRepository
import com.zxc.idata.data.repository.FileDescriptionRepository
import com.zxc.idata.data.repository.TableOrderRepository
import com.zxc.idata.data.repository.UserPreferencesRepository
import com.zxc.idata.data.source.local.AppDatabase
import com.zxc.idata.data.source.local.CellDataDao
import com.zxc.idata.data.source.local.ColumnDescriptionDao
import com.zxc.idata.data.source.local.FileDescriptionDao
import com.zxc.idata.data.source.local.TableOrderDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindFileDescriptionRepository(
        repository: DefaultFileDescriptionRepository
    ): FileDescriptionRepository

    @Singleton
    @Binds
    abstract fun bindColumnDescriptionRepository(
        repository: DefaultColumnDescriptionRepository
    ): ColumnDescriptionRepository

    @Singleton
    @Binds
    abstract fun bindCellDataRepository(
        repository: DefaultCellDataRepository
    ): CellDataRepository

    @Singleton
    @Binds
    abstract fun bindTableOrderRepository(
        repository: DefaultTableOrderRepository
    ): TableOrderRepository

    // 只能是接口和实现类，不能都是实现类，不然会造成循环依赖
    @Singleton
    @Binds
    abstract fun bindUserPreferencesRepository(
        repository: DefaultUserPreferencesRepository
    ): UserPreferencesRepository
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): AppDatabase {
        val builder = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "idata.db"
        )
        builder.setQueryCallback({ sqlQuery, bindArgs ->
            Log.d("sql", "SQL Query: $sqlQuery SQL Args: $bindArgs")
        }, Executors.newSingleThreadExecutor())
        return builder.build()
    }

    @Provides
    fun provideFileMetaDao(database: AppDatabase): FileDescriptionDao =
        database.fileDescriptionDao()

    @Provides
    fun provideColumnDescriptionDao(database: AppDatabase): ColumnDescriptionDao =
        database.columnDescriptionDao()

    @Provides
    fun provideCellDataDao(database: AppDatabase): CellDataDao =
        database.cellDataDao()

    @Provides
    fun provideTableOrderDao(database: AppDatabase): TableOrderDao =
        database.tableOrderDao()
}

private const val USER_PREFERENCES = "user_preferences"

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Singleton
    @Provides
    fun providePreferencesDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            corruptionHandler = ReplaceFileCorruptionHandler(
                produceNewData = { emptyPreferences() }
            ),
            migrations = listOf(SharedPreferencesMigration(appContext, USER_PREFERENCES)),
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { appContext.preferencesDataStoreFile(USER_PREFERENCES) }
        )
    }
}