package com.codewithfk.musify_android.ui.feature.playlist.playlist_details

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.codewithfk.musify_android.ui.feature.widgets.ErrorScreen
import com.codewithfk.musify_android.ui.feature.widgets.LoadingScreen
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun PlaylistDetailsScreen(
    playlistId: String,
    navController: NavController,
    viewModel: PlaylistDetailsViewModel = koinViewModel()
) {

    LaunchedEffect(true) {
        viewModel.fetchData(playlistId)
        viewModel.event.collectLatest {
            when (it) {
                is PlaylistDetailsEvent.showErrorMessage -> {
                    Toast.makeText(navController.context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    val state = viewModel.state.collectAsStateWithLifecycle()
    when (state.value) {
        is PlaylistDetailsState.Loading -> {
            LoadingScreen()
        }

        is PlaylistDetailsState.Success -> {
            val data = (state.value as PlaylistDetailsState.Success).data

            data.songs?.let {
                LazyColumn {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            it.firstOrNull()?.coverImage?.let { image ->
                                AsyncImage(
                                    model = image,
                                    contentDescription = "Playlist Cover",
                                    modifier = Modifier.size(200.dp)
                                )
                            }
                            it.firstOrNull()?.title?.let { name ->
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    modifier = Modifier.align(Alignment.TopCenter)
                                )
                            }
                        }
                    }
                    items(it) { song ->
                        Row {
                            AsyncImage(
                                model = song.coverImage,
                                contentDescription = "Song Cover",
                                modifier = Modifier.size(50.dp)
                            )
                            Column {
                                Text(
                                    text = song.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                )
                                Text(
                                    text = song.artist.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                        .padding(start = 8.dp)
                                )
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton({
                                viewModel.deleteSongFromPlaylist(songID = song.id, playlistId = data.id)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Song",
                                    tint = Color.Red
                                )
                            }

                        }
                    }
                }
            }
        }

        is PlaylistDetailsState.Error -> {
            val errorMessage = (state.value as PlaylistDetailsState.Error).message
            ErrorScreen(errorMessage, "Retry", onPrimaryButtonClicked = {})
        }
    }
}