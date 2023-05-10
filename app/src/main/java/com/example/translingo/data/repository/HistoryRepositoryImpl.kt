package com.example.translingo.data.repository

import com.example.translingo.data.database.TranslingoDatabase
import com.example.translingo.data.database.entities.HistoryEntity
import com.example.translingo.data.toDomain
import com.example.translingo.data.toEntity
import com.example.translingo.domain.model.History
import com.example.translingo.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    database: TranslingoDatabase
) : HistoryRepository {
    private val historyDao = database.historyDao()

    override suspend fun saveTranslation(history: History) {
        historyDao.insertHistory(history.toEntity())
    }

    override suspend fun deleteTranslation(history: History) {
        historyDao.deleteHistory(history.toEntity())
    }

    override fun getTranslationHistoryByDateDescAsFlow(): Flow<List<History>> {
        return historyDao.getHistoryByDateDesc().map { list -> list.map(HistoryEntity::toDomain) }
    }

    override suspend fun addOrRemoveFavorite(id: Int) {
        var historyToUpdate = historyDao.getHistoryById(id) ?: return
        val isFavorite = historyToUpdate.isFavorite
        historyToUpdate = historyToUpdate.copy(isFavorite = !isFavorite)
        historyDao.insertHistory(historyToUpdate)
    }
}