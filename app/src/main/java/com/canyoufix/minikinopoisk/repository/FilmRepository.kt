package com.canyoufix.minikinopoisk.repository

import com.canyoufix.api.FilmApiService
import com.canyoufix.data.model.Film

class FilmRepository(
    private val apiService: FilmApiService
) {
    suspend fun getFilms(): List<Film> {
        return apiService.getFilms().films
    }
}