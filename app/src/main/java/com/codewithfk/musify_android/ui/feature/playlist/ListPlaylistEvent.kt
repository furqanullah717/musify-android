package com.codewithfk.musify_android.ui.feature.playlist

sealed class ListPlaylistEvent {
    data class showErrorMessage(val message: String) : ListPlaylistEvent()
    object createPlaylist : ListPlaylistEvent()
    data class navigateToPlaylistDetails(val playListID: String) : ListPlaylistEvent()
}