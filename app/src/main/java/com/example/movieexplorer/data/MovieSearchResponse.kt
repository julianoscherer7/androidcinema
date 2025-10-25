package com.example.movieexplorer.data

data class MovieSearchResponse(
    val Search: List<Movie>?,
    val totalResults: String?,
    val Response: String,
    val Error: String?
)
