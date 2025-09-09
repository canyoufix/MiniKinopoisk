package com.canyoufix.minikinopoisk.ui.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.canyoufix.data.model.Film
import com.canyoufix.minikinopoisk.R
import com.google.android.material.appbar.MaterialToolbar
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class FilmDetailFragment : Fragment(R.layout.fragment_film_detail) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val film = arguments?.getSerializable("film") as? Film ?: run {
            findNavController().navigateUp()
            return
        }

        // Настройка Toolbar через Activity
        (activity as? AppCompatActivity)?.apply {
            supportActionBar?.title = film.name
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        setupViews(view, film)
    }

    private fun setupViews(view: View, film: Film) {
        // Постер
        val imageView = view.findViewById<ImageView>(R.id.posterImageView)

        Glide.with(view.context)
            .load(film.image_url)
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .fallback(R.drawable.placeholder)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(14)))
            .into(imageView)

        // Название
        view.findViewById<TextView>(R.id.titleTextView).text = film.localized_name

        // Жанры и год
        val genres = film.genres?.joinToString(", ") ?: ""
        val year = film.year?.toString() ?: ""
        val genresAndYearView = view.findViewById<TextView>(R.id.genresAndYearTextView)

        val genresAndYearText = "$genres${if (genres.isNotEmpty() && year.isNotEmpty()) ", " else ""}${if (year.isNotEmpty()) "$year год" else ""}"

        if (genresAndYearText.isNotBlank()) {
            genresAndYearView.text = genresAndYearText
            genresAndYearView.visibility = View.VISIBLE
        } else {
            genresAndYearView.visibility = View.GONE
        }

        // Рейтинг
        val ratingView = view.findViewById<TextView>(R.id.ratingTextView)
        val ratingText = film.rating?.let {
            DecimalFormat("#.#", DecimalFormatSymbols(Locale.US)).format(it)
        }

        val kinopoiskView = view.findViewById<TextView>(R.id.kinopoiskTextView)
        val kinopoiskText = getString(R.string.kinopoisk)

        if (!ratingText.isNullOrBlank()) {
            ratingView.text = ratingText
            kinopoiskView.text = kinopoiskText
            ratingView.visibility = View.VISIBLE
        } else {
            ratingView.visibility = View.GONE
        }

        // Описание
        val descriptionView = view.findViewById<TextView>(R.id.descriptionTextView)
        if (!film.description.isNullOrBlank()) {
            descriptionView.text = film.description
            descriptionView.visibility = View.VISIBLE
        } else {
            descriptionView.visibility = View.GONE
        }
    }


    override fun onDestroyView() {
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        super.onDestroyView()
    }
}