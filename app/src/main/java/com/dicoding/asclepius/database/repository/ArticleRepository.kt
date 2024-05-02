package com.dicoding.asclepius.database.repository


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.dicoding.asclepius.BuildConfig
import com.dicoding.asclepius.database.remote.response.ArticlesItem
import com.dicoding.asclepius.database.remote.retrofit.ApiService
import com.dicoding.asclepius.database.Result


class ArticleRepository(
    private val apiService: ApiService
) {

    fun getListUser(): LiveData<Result<List<ArticlesItem>>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getArticle("cancer", "health", "en", BuildConfig.API_KEY)
            if (response.status == "ok") {
                emit(Result.Success(response.articles?.filterNotNull() ?: emptyList()))
            } else {
                emit(Result.Error("Error fetching articles"))
            }

        } catch (e: Exception) {
            Log.d("UserRepository", "getListUser: ${e.message.toString()} ")
            emit(Result.Error(e.message.toString()))
        }
    }
}