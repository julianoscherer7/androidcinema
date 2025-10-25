package com.example.movieexplorer.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieexplorer.data.Movie
import com.example.movieexplorer.data.MovieDetail
import com.example.movieexplorer.data.MovieSearchResponse
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Interface da API
interface MovieApiService {
    @GET("/")
    suspend fun searchMovies(
        @Query("apikey") apiKey: String,
        @Query("s") query: String,
        @Query("type") type: String = "movie"
    ): MovieSearchResponse
}

class MovieViewModel : ViewModel() {
    // 🔥 SUBSTITUA PELA SUA CHAVE DA API OMDb
    private val apiKey = "8f785fa1"

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://www.omdbapi.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val movieApiService = retrofit.create(MovieApiService::class.java)

    // Estados usando mutableStateOf com delegação correta
    var movies by mutableStateOf<List<Movie>>(emptyList())
        private set

    var searchQuery by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    fun searchMovies() {
        if (searchQuery.isEmpty()) return

        isLoading = true
        error = null

        viewModelScope.launch {
            try {
                val response = movieApiService.searchMovies(
                    apiKey = apiKey,
                    query = searchQuery
                )

                if (response.Response == "True") {
                    movies = response.Search ?: emptyList()
                } else {
                    error = response.Error ?: "No movies found"
                    movies = emptyList()
                }
            } catch (e: Exception) {
                error = "Network error: ${e.message}"
                movies = emptyList()
            } finally {
                isLoading = false
            }
        }
    }

    fun clearError() {
        error = null
    }
}