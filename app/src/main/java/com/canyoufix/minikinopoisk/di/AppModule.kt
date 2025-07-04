package com.canyoufix.minikinopoisk.di

import com.canyoufix.minikinopoisk.repository.FilmRepository
import com.canyoufix.minikinopoisk.viemodel.FilmViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val AppModule = module {
    viewModel { FilmViewModel(get()) }
    single<FilmRepository> { FilmRepository(get()) }
}