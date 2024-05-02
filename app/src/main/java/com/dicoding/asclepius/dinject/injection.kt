package com.dicoding.asclepius.dinject

import com.dicoding.asclepius.database.remote.retrofit.ApiConfig
import com.dicoding.asclepius.database.repository.ArticleRepository

object Injection {
    fun provideRepository(): ArticleRepository {
        val apiService = ApiConfig.getApiService()

        return ArticleRepository(apiService)
    }
}