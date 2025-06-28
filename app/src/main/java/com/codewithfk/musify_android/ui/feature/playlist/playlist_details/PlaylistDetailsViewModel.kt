package com.codewithfk.musify_android.ui.feature.playlist.playlist_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithfk.musify_android.data.network.Resource
import com.codewithfk.musify_android.data.repository.PlaylistRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class PlaylistDetailsViewModel(private val playlistRepository: PlaylistRepository) : ViewModel() {

    private val _state = MutableStateFlow<PlaylistDetailsState>(PlaylistDetailsState.Loading)
    val state: StateFlow<PlaylistDetailsState> = _state

    private val _event = MutableSharedFlow<PlaylistDetailsEvent>()
    val event = _event.asSharedFlow()

    fun fetchData(playlistID: String) {
        viewModelScope.launch {
            _state.value = PlaylistDetailsState.Loading
            when (val result = playlistRepository.getPlaylistDetails(playlistID)) {
                is Resource.Success -> {
                    _state.value = PlaylistDetailsState.Success(result.data)
                }

                is Resource.Error -> {
                    _state.value = PlaylistDetailsState.Error(result.message ?: "Unknown error")
                }
            }
        }
    }

    fun deleteSongFromPlaylist(playlistId: String, songID: String) {
        viewModelScope.launch {
            when (val result = playlistRepository.deleteSongFromPlaylist(playlistId, songID)) {
                is Resource.Success -> {
                    _event.emit(PlaylistDetailsEvent.showErrorMessage("Song deleted successfully"))
                    fetchData(playlistId) // Refresh the playlist after deletion
                }

                is Resource.Error -> {
                    _event.emit(
                        PlaylistDetailsEvent.showErrorMessage(
                            result.message ?: "Failed to delete song"
                        )
                    )
                }
            }
        }
    }

}