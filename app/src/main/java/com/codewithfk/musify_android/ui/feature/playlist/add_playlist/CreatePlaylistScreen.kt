package com.codewithfk.musify_android.ui.feature.playlist.add_playlist

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.codewithfk.musify_android.ui.feature.widgets.ErrorScreen
import com.codewithfk.musify_android.ui.feature.widgets.LoadingScreen
import com.codewithfk.musify_android.ui.feature.widgets.MusifyTextField
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreatePlaylistScreen(
    navController: NavController,
    viewModel: CreatePlaylistViewModel = koinViewModel()
) {

    LaunchedEffect(true) {
        viewModel.event.collectLatest {
            when (it) {
                is CreatePlaylistEvent.showErrorMessage -> {
                    Toast.makeText(navController.context, it.message, Toast.LENGTH_SHORT).show()
                }

                is CreatePlaylistEvent.onPlaylistCreated -> {
                    navController.previousBackStackEntry?.savedStateHandle?.set(
                        "playlistCreated",
                        true
                    )
                    navController.popBackStack()
                }
            }
        }
    }

    val state = viewModel.state.collectAsStateWithLifecycle()
    when (state.value) {
        is CreatePlaylistState.Loading -> {
            LoadingScreen()
        }

        is CreatePlaylistState.Normal -> {
            val name = viewModel.name.collectAsStateWithLifecycle()
            val description = viewModel.description.collectAsStateWithLifecycle()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                MusifyTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = name.value,
                    onValueChange = { viewModel.onNameChanged(it) },
                    label = { Text("Playlist Name") },
                )
                MusifyTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = description.value,
                    onValueChange = { viewModel.onDescriptionChanged(it) },
                    label = { Text("Description") },
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = { viewModel.onAddPlaylistClicked() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create Playlist")
                }

            }
        }

        is CreatePlaylistState.Success -> {
            val playlist = (state.value as CreatePlaylistState.Success).data

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text("Playlist Created Successfully!")
                Text("Name: ${playlist.name}")
                Text("Description: ${playlist.description}")
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Go Back")
                }
            }

        }

        is CreatePlaylistState.Error -> {
            val errorMessage = (state.value as CreatePlaylistState.Error).message
            ErrorScreen(errorMessage, "Retry", onPrimaryButtonClicked = {})
        }
    }
}