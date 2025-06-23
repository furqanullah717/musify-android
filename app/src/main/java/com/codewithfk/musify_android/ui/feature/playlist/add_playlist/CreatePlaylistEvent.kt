package com.codewithfk.musify_android.ui.feature.playlist.add_playlist

import com.codewithfk.musify_android.data.model.PlaylistModel

sealed class CreatePlaylistEvent {
    data class showErrorMessage(val message: String) : CreatePlaylistEvent()
    data class onPlaylistCreated(val data: PlaylistModel) : CreatePlaylistEvent()
}