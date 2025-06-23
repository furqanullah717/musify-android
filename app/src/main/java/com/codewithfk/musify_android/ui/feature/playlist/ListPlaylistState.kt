package com.codewithfk.musify_android.ui.feature.playlist

import com.codewithfk.musify_android.data.model.PlaylistModel

sealed class ListPlaylistState {
    object Loading : ListPlaylistState()
    data class Success(val data: List<PlaylistModel>) : ListPlaylistState()
    data class Error(val message: String) : ListPlaylistState()
}