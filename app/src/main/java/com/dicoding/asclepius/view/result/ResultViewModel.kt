package com.dicoding.asclepius.view.result

import android.app.Application
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.database.local.entity.History
import com.dicoding.asclepius.database.repository.HistoryRepository

class ResultViewModel (application: Application): ViewModel() {

    private val historyRepository: HistoryRepository = HistoryRepository(application)

    fun insert(history: History) {
        historyRepository.insert(history)
    }
}