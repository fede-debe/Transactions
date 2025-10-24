package com.application.transactions.di

import android.content.Context
import androidx.room.Room
import com.application.transactions.data.local.dao.TransactionDao
import com.application.transactions.data.local.datasource.TransactionsLocalDataSource
import com.application.transactions.data.local.datasource.TransactionsLocalDataSourceImpl
import com.application.transactions.data.local.db.AppDatabase
import com.application.transactions.data.remote.TransactionsApi
import com.application.transactions.data.remote.TransactionsApiImpl
import com.application.transactions.domain.repository.TransactionsRepository
import com.application.transactions.domain.repository.TransactionsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "Candidate.db"
        ).fallbackToDestructiveMigration(false).allowMainThreadQueries().build()
    }

    @Singleton
    @Provides
    fun provideTransactionDao(database: AppDatabase): TransactionDao = database.transactionDao()

    @Singleton
    @Provides
    fun provideTransactionsLocalDataSource(db: AppDatabase, dao: TransactionDao): TransactionsLocalDataSource =
        TransactionsLocalDataSourceImpl(db, dao)

    @Singleton
    @Provides
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    @Singleton
    @Provides
    fun provideTransactionsApi(): TransactionsApi = TransactionsApiImpl()

    @Singleton
    @Provides
    fun provideTransactionsRepository(
        remote: TransactionsApi,
        local: TransactionsLocalDataSource,
        json: Json
    ): TransactionsRepository = TransactionsRepositoryImpl(remote, local, json)
}