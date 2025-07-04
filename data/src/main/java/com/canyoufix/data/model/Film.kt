package com.canyoufix.data.model

import java.io.Serializable

data class Film(
    val id: Int,
    val localized_name: String?,
    val name: String?,
    val year: Int?,
    val rating: Double?,
    val image_url: String?,
    val description: String?,
    val genres: List<String>?
): Serializable