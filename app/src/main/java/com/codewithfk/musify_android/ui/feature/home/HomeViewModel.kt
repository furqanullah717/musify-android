package com.codewithfk.musify_android.ui.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codewithfk.musify_android.data.MusifySession
import com.codewithfk.musify_android.data.network.Resource
import com.codewithfk.musify_android.data.repository.HomeRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class HomeViewModel(private val homeRepository: HomeRepository, private  val musifySession: MusifySession) : ViewModel() {

    private val _state = MutableStateFlow<HomeState>(HomeState.Loading)
    val state: StateFlow<HomeState> = _state

    private val _event = MutableSharedFlow<HomeEvent>()
    val event = _event.asSharedFlow()

    init {
        fetchData()
    }
    fun getUserName(): String {
        return musifySession.getUserName()?: "Guest"
    }

    fun fetchData() {
        viewModelScope.launch {
            _state.value = HomeState.Loading
            val data = homeRepository.getHomeData()
            when (data) {
                is Resource.Success -> {
                    _state.value = HomeState.Success(data.data)
                }

                is Resource.Error -> {
                    _state.value = HomeState.Error(data.message)
                    _event.emit(HomeEvent.showErrorMessage(data.message))
                }
            }
        }
    }

    fun onRetryClicked() {
        fetchData()
    }

    fun onSongClicked(value: String) {
        viewModelScope.launch {
            _event.emit(HomeEvent.onSongClick(value))
        }
    }

    fun onPlaylistClicked() {
        viewModelScope.launch {
            _event.emit(HomeEvent.onPlaylistClick)
        }
    }
}