package com.codewithfk.musify_android.ui.feature.playlist.playlist_details

sealed class PlaylistDetailsEvent {
    data class showErrorMessage(val message: String) : PlaylistDetailsEvent()
}