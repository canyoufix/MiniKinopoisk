package com.canyoufix.minikinopoisk.ui.screens

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
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
    private lateinit var scrollView: NestedScrollView

    private var lastScrollY = 0
    private var isGenreFilterApplied = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().title = "Фильмы"

        // Инициализация Scroll
        scrollView = view.findViewById(R.id.scrollView)

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

    override fun onPause() {
        super.onPause()
        lastScrollY = scrollView.scrollY
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

        val genreAdapter = GenreAdapter(
            genres = genres,
            getSelectedGenre = { filmViewModel.selectedGenre.value },
            onGenreSelected = { selectedGenre ->
                isGenreFilterApplied = true
                filmViewModel.setGenre(selectedGenre)
            }
        )
        genreRecyclerView.adapter = genreAdapter

        genreRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupGenreToggle() {
        genreTitle.setOnClickListener {
            filmViewModel.toggleGenreVisibility()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            filmViewModel.isGenreVisible.collect { isVisible ->
                genreRecyclerView.visibility = if (isVisible) View.VISIBLE else View.GONE
                val arrowIcon = if (isVisible) R.drawable.ic_arrow_up else R.drawable.ic_arrow_down
                genreTitle.setCompoundDrawablesWithIntrinsicBounds(0, 0, arrowIcon, 0)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
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
                            genreTitle.visibility = View.VISIBLE
                            filmTitle.visibility = View.VISIBLE
                            recyclerView.visibility = View.VISIBLE

                            adapter.submitList(films) {
                                scrollView.post {
                                    if (filmViewModel.userSelectedGenre.value) {
                                        //scrollView.scrollTo(0, filmTitle.top)
                                        filmViewModel.resetUserSelectedGenre()
                                    } else {
                                        scrollView.scrollTo(0, lastScrollY)
                                    }
                                }
                            }
                        }
                    }
                }

                // Сохранение выбранного жанра и подсветки
                launch {
                    filmViewModel.selectedGenre.collect {
                        genreRecyclerView.adapter?.notifyDataSetChanged()
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