package com.example.translingo.di

import com.example.translingo.data.UserPreference
import com.example.translingo.data.repository.LanguageRepositoryImpl
import com.example.translingo.domain.repository.LanguageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Singleton
    @Provides
    fun provideLanguageRepository(userPreference: UserPreference): LanguageRepository {
        return LanguageRepositoryImpl(userPreference)
    }
}