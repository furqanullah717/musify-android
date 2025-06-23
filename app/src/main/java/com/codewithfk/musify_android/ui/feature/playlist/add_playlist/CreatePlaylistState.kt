package com.codewithfk.musify_android.ui.feature.playlist.add_playlist

import com.codewithfk.musify_android.data.model.PlaylistModel

sealed class CreatePlaylistState {
    object Loading : CreatePlaylistState()
    object Normal : CreatePlaylistState()
    data class Success(val data: PlaylistModel) : CreatePlaylistState()
    data class Error(val message: String) : CreatePlaylistState()
}