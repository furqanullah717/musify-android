package com.codewithfk.musify_android.data.model

data class CreatePlaylistRequest(
    val coverImage: String? = null,
    val description: String? = null,
    val name: String,
    val songIds: List<String> = emptyList<String>(),
)