package com.codewithfk.musify_android.data.model

data class PlaylistModel(
    val id: String,
    val name: String,
    val description: String?,
    val coverImage: String?,
    val userId: String,
    val songs: List<Song>? = emptyList(),
    val songCount: Int? = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
