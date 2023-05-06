package com.example.translingo.di

import android.content.Context
import androidx.room.Room
import com.example.translingo.data.UserPreference
import com.example.translingo.data.database.TranslingoDatabase
import com.example.translingo.data.repository.LanguageRepositoryImpl
import com.example.translingo.domain.repository.LanguageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Singleton
    @Provides
    fun provideLanguageRepository(
        userPreference: UserPreference,
        translingoDatabase: TranslingoDatabase
    ): LanguageRepository {
        return LanguageRepositoryImpl(userPreference, translingoDatabase)
    }
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): TranslingoDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            TranslingoDatabase::class.java,
            "Translingo.db"
        ).build()
    }
}