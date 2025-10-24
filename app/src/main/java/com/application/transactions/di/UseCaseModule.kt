package com.application.transactions.di

import com.application.transactions.domain.repository.usecase.BuildTransactionSectionsUseCase
import com.application.transactions.presentation.mapping.TransactionsUiMapper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    fun providesBuildTransactionSectionsUseCase(): BuildTransactionSectionsUseCase = BuildTransactionSectionsUseCase()

    @Provides
    fun providesTransactionsUiMapper(): TransactionsUiMapper = TransactionsUiMapper()
}