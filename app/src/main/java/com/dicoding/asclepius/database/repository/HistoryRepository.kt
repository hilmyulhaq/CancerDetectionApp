package com.dicoding.asclepius.database.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.dicoding.asclepius.database.local.entity.History
import com.dicoding.asclepius.database.local.room.HistoryDao
import com.dicoding.asclepius.database.local.room.HistoryDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HistoryRepository(application: Application) {

    private val histoDao: HistoryDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = HistoryDatabase.getDatabase(application)
        histoDao = db.hisDao()
    }

    fun getAllHistory(): LiveData<List<History>> = histoDao.getAllHistory()

    fun insert(history: History) {
        executorService.execute { histoDao.insert(history) }
    }

    fun delete(history: History) {
        executorService.execute { histoDao.delete(history) }
    }

}