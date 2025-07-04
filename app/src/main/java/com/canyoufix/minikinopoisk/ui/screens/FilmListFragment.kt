package com.canyoufix.minikinopoisk.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.canyoufix.minikinopoisk.R
import com.canyoufix.minikinopoisk.adapter.FilmAdapter
import com.canyoufix.minikinopoisk.adapter.GenreAdapter
import com.canyoufix.minikinopoisk.viemodel.FilmViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch


class FilmListFragment : Fragment(R.layout.fragment_film_list) {

    private val filmViewModel: FilmViewModel by viewModel()
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FilmAdapter
    private lateinit var genreTitle: TextView
    private lateinit var filmTitle: TextView
    private lateinit var genreRecyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = "Фильмы"

        // Инициализация View
        initViews(view)
        setupAdapters()
        setupGenreToggle()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        // Обновляем заголовок при возврате на фрагмент
        (activity as? AppCompatActivity)?.supportActionBar?.apply {
            title = "Фильмы"
        }
    }

    private fun initViews(view: View) {
        progressBar = view.findViewById(R.id.progressBar)
        recyclerView = view.findViewById(R.id.recyclerView)
        genreRecyclerView = view.findViewById(R.id.genreRecyclerView)
        genreTitle = view.findViewById(R.id.genreTitle)
        filmTitle = view.findViewById(R.id.filmTitle)

        // Скрываем элементы до загрузки
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        genreTitle.visibility = View.GONE
        filmTitle.visibility = View.GONE
        genreRecyclerView.visibility = View.GONE
    }

    private fun setupAdapters() {
        // Адаптер для фильмов
        adapter = FilmAdapter { film ->
            val bundle = Bundle().apply {
                putSerializable("film", film)
            }
            findNavController().navigate(R.id.filmDetailFragment, bundle)
        }

        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = adapter

        // Адаптер для жанров
        val genres = listOf(
            "Драма", "Фэнтези", "Криминал", "Детектив", "Мелодрама",
            "Биография", "Комедия", "Фантастика", "Боевик", "Триллер",
            "Мюзикл", "Приключения", "Ужасы"
        )

        val genreAdapter = GenreAdapter(genres) { selectedGenre ->
            filmViewModel.setGenre(selectedGenre)
        }

        genreRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        genreRecyclerView.adapter = genreAdapter
    }

    private fun setupGenreToggle() {
        var isGenreVisible = false

        genreTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0)
        genreTitle.setOnClickListener {
            isGenreVisible = !isGenreVisible
            genreRecyclerView.visibility = if (isGenreVisible) View.VISIBLE else View.GONE
            val arrowIcon = if (isGenreVisible) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
            genreTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, arrowIcon, 0)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Обработка загрузки
                launch {
                    filmViewModel.isLoading.collect { loading ->
                        progressBar.visibility = if (loading) View.VISIBLE else View.GONE
                    }
                }

                // Обработка списка фильмов
                launch {
                    filmViewModel.filteredFilms.collect { films ->
                        if (films.isNotEmpty()) {
                            // VISIBLE заголовков
                            genreTitle.visibility = View.VISIBLE
                            filmTitle.visibility = View.VISIBLE
                            recyclerView.visibility = View.VISIBLE


                            adapter.submitList(films) {

                            }
                        }
                    }
                }

                // Обработка ошибок
                launch {
                    filmViewModel.error.collect { error ->
                        error?.let {
                            showErrorSnackbar()
                            filmViewModel.clearError()
                        }
                    }
                }
            }
        }
    }

    private fun showErrorSnackbar() {
        Snackbar.make(
            requireView(),
            "Ошибка подключения к сети",
            Snackbar.LENGTH_INDEFINITE
        ).apply {
            view.apply {
                (layoutParams as? ViewGroup.MarginLayoutParams)?.setMargins(5, 0, 5, 75)
                minimumWidth = ViewGroup.LayoutParams.MATCH_PARENT
            }
            setActionTextColor(ContextCompat.getColor(requireContext(), R.color.orange))
            setAction("Повторить") {
                filmViewModel.loadFilms()
            }
            addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    filmViewModel.clearError()
                }
            })
            show()
        }
    }
}