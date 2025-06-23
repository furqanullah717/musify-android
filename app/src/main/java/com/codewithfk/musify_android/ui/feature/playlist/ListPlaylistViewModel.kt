package com.codewithfk.musify_android.ui.feature.playlist

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
class ListPlaylistViewModel(private val playlistRepository: PlaylistRepository) : ViewModel() {

    private val _state = MutableStateFlow<ListPlaylistState>(ListPlaylistState.Loading)
    val state: StateFlow<ListPlaylistState> = _state

    private val _event = MutableSharedFlow<ListPlaylistEvent>()
    val event = _event.asSharedFlow()

    init {
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            try {
                _state.value = ListPlaylistState.Loading
                val response = playlistRepository.getPlaylists()
                when (response) {
                    is Resource.Success -> {
                        val playlists = response.data
                        _state.value = ListPlaylistState.Success(playlists)
                    }

                    is Resource.Error -> {
                        _state.value = ListPlaylistState.Error(response.message)
                        _event.emit(ListPlaylistEvent.showErrorMessage(response.message))
                        return@launch
                    }
                }
            } catch (e: Exception) {
                _state.value = ListPlaylistState.Error(e.message ?: "An error occurred")
                _event.emit(ListPlaylistEvent.showErrorMessage(e.message ?: "An error occurred"))
            }
        }
    }

    fun onCreatePlaylistClicked(){
        viewModelScope.launch {
            _event.emit(ListPlaylistEvent.createPlaylist)
        }
    }

    fun retry() {
        fetchData()
    }

}