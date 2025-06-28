package com.codewithfk.musify_android.ui.feature.playsong

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithfk.musify_android.data.model.Song
import com.codewithfk.musify_android.data.network.Resource
import com.codewithfk.musify_android.data.repository.MusicRepository
import com.codewithfk.musify_android.data.repository.PlaylistRepository
import com.codewithfk.musify_android.data.service.MusifyPlaybackService
import com.codewithfk.musify_android.data.service.MusifyPlaybackService.Companion.KEY_SONG
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class PlaySongViewModel(
    private val repo: MusicRepository,
    private val context: Context,
    val playlistRepository: PlaylistRepository
) :
    ViewModel() {

    private val _state = MutableStateFlow<PlaySongState>(PlaySongState.Loading)
    val state: StateFlow<PlaySongState> = _state

    private val _event = MutableSharedFlow<PlaySongEvent>()
    val event = _event.asSharedFlow()

    private var playbackService: MusifyPlaybackService? = null
    private var isServiceBound = false
    private var currentSong: Song? = null

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            p0: ComponentName?,
            binder: IBinder?
        ) {
            isServiceBound = true
            playbackService = (binder as MusifyPlaybackService.MusicBinder).getService()
            observePlaybackService()
            currentSong?.let {
                playbackService?.playSong(it)
            } ?: run {
                _state.value = PlaySongState.Error("No song to play")
            }
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isServiceBound = false
            playbackService = null
        }
    }

    private fun observePlaybackService() {
        playbackService?.let { service ->
            service.playerState.onEach {
                _state.value = PlaySongState.Success(
                    isPlaying = it.isPlaying,
                    currentPosition = it.currentPosition.coerceAtLeast(0),
                    duration = it.duration.coerceAtLeast(0),
                    currentSong = it.currentSong,
                    isBuffering = it.isBuffering,
                    error = it.error
                )
            }.launchIn(viewModelScope)
        }
    }

    fun fetchData(songID: String) {
        viewModelScope.launch {
            try {
                val response = repo.getSongById(songID) // Replace with actual song ID
                if (response is Resource.Success) {
                    currentSong = response.data
                    startServiceAndBind(response.data)
                } else {
                    _state.value = PlaySongState.Error("Failed to fetch song data")
                }
            } catch (e: Exception) {
                _state.value = PlaySongState.Error("Network error: ${e.message}")
            }
        }
    }

    fun togglePlayPause() {
        playbackService?.let { service ->
            if (service.isPlaying()) {
                service.pauseSong()
            } else {
                service.resumeSong()
            }
        } ?: run {
            _state.value = PlaySongState.Error("Playback service not bound")
        }
    }

    fun seekTo(position: Long) {
        playbackService?.mediaSessionCallBack?.onSeekTo(position) ?: run {
            _state.value = PlaySongState.Error("Playback service not bound")
        }
    }

    private fun startServiceAndBind(song: Song) {
        val intent = Intent(context, MusifyPlaybackService::class.java).apply {
            action = MusifyPlaybackService.ACTION_PLAY
            putExtra(KEY_SONG, song)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }

        if (!isServiceBound) {
            context.bindService(
                intent,
                serviceConnection,
                Context.BIND_AUTO_CREATE
            )
        }
    }

    fun onAddToPlaylistClicked(songID: String) {
        viewModelScope.launch {
            val list = playlistRepository.getPlaylists()
            when (list) {
                is Resource.Success -> {
                    if (list.data.isEmpty()) {
                        _event.emit(PlaySongEvent.showErrorMessage("No playlists available"))
                    } else {
                        _event.emit(PlaySongEvent.showPlaylistSelection(list.data))
                    }
                }

                is Resource.Error -> {
                    _event.emit(
                        PlaySongEvent.showErrorMessage(
                            list.message ?: "Failed to fetch playlists"
                        )
                    )
                }
            }
        }
    }

    fun addSongToPlaylist(playListID: String, songID: String) {
       viewModelScope.launch {

            val response = playlistRepository.addSongToPlaylist(playListID, songID)
            when (response) {
                is Resource.Success -> {
                    _event.emit(PlaySongEvent.showErrorMessage("Song added to playlist successfully"))
                }
                is Resource.Error -> {
                    _event.emit(
                        PlaySongEvent.showErrorMessage(
                            response.message ?: "Failed to add song to playlist"
                        )
                    )
                }
            }
       }
    }

}