package com.canyoufix.minikinopoisk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.canyoufix.minikinopoisk.R


class GenreAdapter(
    private val genres: List<String>,
    private val onGenreSelected: (String?) -> Unit
) : RecyclerView.Adapter<GenreAdapter.GenreViewHolder>() {

    private var selectedGenre: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_genre, parent, false)
        return GenreViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        holder.bind(genres[position])
    }

    override fun getItemCount(): Int = genres.size

    inner class GenreViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val button: Button = view.findViewById(R.id.genreButton)

        fun bind(genre: String) {
            button.text = genre

            // Меняем цвет выбранной кнопки
            val isSelected = genre == selectedGenre
            val context = button.context
            button.setBackgroundColor(
                if (isSelected) ContextCompat.getColor(context, R.color.orange)
                else ContextCompat.getColor(context, android.R.color.white)
            )

            button.setOnClickListener {
                selectedGenre = if (selectedGenre == genre) {
                    onGenreSelected(null) // сброс фильтра
                    null
                } else {
                    onGenreSelected(genre)
                    genre
                }
                notifyDataSetChanged()
            }
        }
    }
}

