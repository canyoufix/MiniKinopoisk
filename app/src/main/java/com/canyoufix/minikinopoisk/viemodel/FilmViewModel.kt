package com.canyoufix.minikinopoisk.viemodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canyoufix.data.model.Film
import com.canyoufix.minikinopoisk.repository.FilmRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FilmViewModel(private val repository: FilmRepository) : ViewModel() {

    private val _films = MutableStateFlow<List<Film>>(emptyList())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _selectedGenre = MutableStateFlow<String?>(null)
    val selectedGenre: StateFlow<String?> = _selectedGenre

    private val _userSelectedGenre = MutableStateFlow(false)
    val userSelectedGenre: StateFlow<Boolean> = _userSelectedGenre

    private val _isGenreVisible = MutableStateFlow(false)
    val isGenreVisible: StateFlow<Boolean> = _isGenreVisible

    // Отфильтрованные фильмы
    val filteredFilms = combine(_films, _selectedGenre) { films, genre ->
        val sortedFilms = films.sortedBy { it.localized_name?.lowercase() }

        if (genre.isNullOrBlank()) {
            sortedFilms
        } else {
            sortedFilms.filter { film ->
                film.genres?.any { it.equals(genre, ignoreCase = true) } == true
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    init {
        loadFilms()
    }

    fun loadFilms() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = repository.getFilms()
                _films.value = response
            } catch (e: Exception) {
                _error.value = "Ошибка загрузки: ${e.message}"
                _films.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setGenre(genre: String?) {
        _selectedGenre.value = genre
        _userSelectedGenre.value = true
    }

    fun toggleGenreVisibility() {
        _isGenreVisible.value = !_isGenreVisible.value
    }

    fun resetUserSelectedGenre() {
        _userSelectedGenre.value = false
    }

    fun clearError() {
        _error.value = null
    }
}