package com.codewithfk.musify_android.ui.feature.playsong

import com.codewithfk.musify_android.data.model.PlaylistModel

sealed class PlaySongEvent {
    data class showErrorMessage(val message: String) : PlaySongEvent()
    data class showPlaylistSelection(val list: List<PlaylistModel>) : PlaySongEvent()
}