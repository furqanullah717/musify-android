package com.codewithfk.musify_android.ui.feature.playlist.add_playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithfk.musify_android.data.model.CreatePlaylistRequest
import com.codewithfk.musify_android.data.network.Resource
import com.codewithfk.musify_android.data.repository.PlaylistRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class CreatePlaylistViewModel(private val playlistRepository: PlaylistRepository) : ViewModel() {

    private val _state = MutableStateFlow<CreatePlaylistState>(CreatePlaylistState.Normal)
    val state: StateFlow<CreatePlaylistState> = _state

    private val _event = MutableSharedFlow<CreatePlaylistEvent>()
    val event = _event.asSharedFlow()

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()
    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description.asStateFlow()

    fun onNameChanged(value: String) {
        _name.value = value
    }

    fun onDescriptionChanged(value: String) {
        _description.value = value
    }

    fun onAddPlaylistClicked() {
        if (_name.value.isEmpty()) {
            _event.tryEmit(CreatePlaylistEvent.showErrorMessage("Playlist name cannot be empty"))
            return
        }
        if (_description.value.isEmpty()) {
            _event.tryEmit(CreatePlaylistEvent.showErrorMessage("Playlist description cannot be empty"))
            return
        }

        viewModelScope.launch {
            _state.value = CreatePlaylistState.Loading
            try {
                val response = playlistRepository.createPlaylist(
                    CreatePlaylistRequest(
                        name = _name.value,
                        description = _description.value
                    )
                )
                when (response) {
                    is Resource.Success -> {
                        _state.value = CreatePlaylistState.Success(response.data)
                        _event.emit(CreatePlaylistEvent.onPlaylistCreated(response.data))
                    }

                    is Resource.Error -> {
                        _state.value = CreatePlaylistState.Error(response.message)
                        _event.emit(CreatePlaylistEvent.showErrorMessage(response.message))
                    }
                }
            } catch (e: Exception) {
                _state.value = CreatePlaylistState.Error(e.message ?: "An error occurred")
                _event.emit(CreatePlaylistEvent.showErrorMessage(e.message ?: "An error occurred"))
            }
        }

    }
}