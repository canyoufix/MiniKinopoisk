package com.canyoufix.minikinopoisk.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.canyoufix.data.model.Film
import com.canyoufix.minikinopoisk.R


class FilmAdapter(
    private val onItemClick: (Film) -> Unit
) : ListAdapter<Film, FilmAdapter.FilmViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_film, parent, false)
        return FilmViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val film = getItem(position)
        holder.bind(film)
        holder.itemView.setOnClickListener {
            onItemClick(film)
        }
    }


    class FilmViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title = view.findViewById<TextView>(R.id.titleTextView)
        private val image = view.findViewById<ImageView>(R.id.posterImageView)

        fun bind(film: Film) {
            title.text = film.localized_name

            // Загрузка картинки через Glide
            try {
                Glide.with(itemView)
                    .load(film.image_url)
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .fallback(R.drawable.placeholder_image)
                    .into(image)
            } catch (e: Exception) {
                image.setImageResource(R.drawable.placeholder_image)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Film>() {
            override fun areItemsTheSame(oldItem: Film, newItem: Film): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Film, newItem: Film): Boolean =
                oldItem == newItem
        }
    }
}
