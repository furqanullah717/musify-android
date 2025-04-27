package com.codewithfk.musify_android.data.model

data class Song(
    val artist: Artist,
    val audioUrl: String,
    val coverImage: String,
    val createdAt: Long,
    val duration: Int,
    val genre: String,
    val id: String,
    val releaseDate: Long,
    val title: String,
    val updatedAt: Long
)