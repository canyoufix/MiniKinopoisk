package com.canyoufix.api.di

import com.canyoufix.api.FilmApiService
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.koin.dsl.module

object ApiModule {
    val module = module {
        single {
            GsonBuilder()
                .setLenient()
                .create()
        }

        single {
            Retrofit.Builder()
                .baseUrl("https://s3-eu-west-1.amazonaws.com/sequeniatesttask/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        single<FilmApiService> {
            get<Retrofit>().create(FilmApiService::class.java)
        }
    }
}