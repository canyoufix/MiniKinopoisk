package com.canyoufix.api

import com.canyoufix.data.model.FilmResponse
import retrofit2.http.GET

interface FilmApiService {
    @GET("films.json")
    suspend fun getFilms(): FilmResponse
}