package com.codewithfk.musify_android.ui.feature.playlist.playlist_details

import com.codewithfk.musify_android.data.model.PlaylistModel

sealed class PlaylistDetailsState {
    object Loading : PlaylistDetailsState()
    data class Success(val data: PlaylistModel) : PlaylistDetailsState()
    data class Error(val message: String) : PlaylistDetailsState()
}