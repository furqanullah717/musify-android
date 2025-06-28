package com.codewithfk.musify_android.ui.feature.playsong

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.codewithfk.musify_android.data.model.PlaylistModel
import com.codewithfk.musify_android.ui.feature.playlist.PlaylistItem
import com.codewithfk.musify_android.ui.feature.widgets.ErrorScreen
import com.codewithfk.musify_android.ui.feature.widgets.LoadingScreen
import com.codewithfk.musify_android.ui.feature.widgets.MusifySpacer
import com.codewithfk.musify_android.ui.theme.MusifyAndroidTheme
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaySongScreen(
    songID: String, navController: NavController, viewModel: PlaySongViewModel = koinViewModel()
) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )
    val shouldShowSheet = remember { mutableStateOf(false) }
    val playList = remember { mutableStateOf<List<PlaylistModel>>(emptyList()) }
    LaunchedEffect(true) {
        viewModel.fetchData(songID)
        viewModel.event.collectLatest {
            when (it) {
                is PlaySongEvent.showErrorMessage -> {
                    Toast.makeText(navController.context, it.message, Toast.LENGTH_SHORT).show()
                }

                is PlaySongEvent.showPlaylistSelection -> {
                    playList.value = it.list
                    shouldShowSheet.value = true
                }
            }
        }
    }

    val state = viewModel.state.collectAsStateWithLifecycle()
    when (state.value) {
        is PlaySongState.Loading -> {
            LoadingScreen()
        }

        is PlaySongState.Success -> {
            val data = (state.value as PlaySongState.Success)
            data.currentSong?.let {
                PlaySongScreenContent(
                    title = data.currentSong.title,
                    genre = data.currentSong.genre,
                    image = data.currentSong.coverImage,
                    duration = data.duration,
                    currentPosition = data.currentPosition,
                    isPlaying = data.isPlaying,
                    isBuffering = data.isBuffering,
                    onSeekChange = { viewModel.seekTo(it) },
                    onPlayPauseToggle = { viewModel.togglePlayPause() },
                    onPlayListClicked = {
                        viewModel.onAddToPlaylistClicked(songID)
                    }
                )
            }

        }

        is PlaySongState.Error -> {
            val errorMessage = (state.value as PlaySongState.Error).message
            ErrorScreen(errorMessage, "Retry", onPrimaryButtonClicked = {})
        }
    }

    if (shouldShowSheet.value) {
        ModalBottomSheet(onDismissRequest = {
            shouldShowSheet.value = false
        }, sheetState = sheetState) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Select Playlist",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                LazyColumn {
                    items(playList.value) {
                        PlaylistItem(
                            playlistModel = it
                        ) {
                            viewModel.addSongToPlaylist(playListID = it.id, songID = songID)
                            shouldShowSheet.value = false
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun PlaySongScreenContent(
    title: String,
    genre: String,
    image: String,
    duration: Long,
    currentPosition: Long,
    isPlaying: Boolean = false,
    isBuffering: Boolean = false,
    onSeekChange: (Long) -> Unit,
    onPlayPauseToggle: () -> Unit,
    onPlayListClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NowPlayingContent(
            title = title,
            isPlayList = false
        )
        SongContent(
            title = title,
            genre = genre,
            image = image,
        )
        SongActions(
            isBuffering = isBuffering,
            currentPosition = currentPosition,
            duration = duration,
            isPlaying = isPlaying,
            onSeekChange = onSeekChange,
            onPlayPauseToggle = {
                onPlayPauseToggle.invoke()
            },
            onNextClicked = {
                // Handle next song
            },
            onPreviousClicked = {
                // Handle previous song
            },
            onPlayListClicked = onPlayListClicked
        )
    }

}

@Composable
fun SongContent(
    title: String,
    genre: String,
    image: String,
) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        MusifySpacer(16.dp)
        AsyncImage(
            model = image, contentDescription = "Song Cover Image",
            modifier = Modifier.size(300.dp)
        )
        MusifySpacer(16.dp)
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = genre,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SongActions(
    isBuffering: Boolean,
    currentPosition: Long,
    duration: Long,
    isPlaying: Boolean = false,
    onSeekChange: (Long) -> Unit,
    onPlayPauseToggle: () -> Unit,
    onNextClicked: () -> Unit = {},
    onPreviousClicked: () -> Unit = {},
    onPlayListClicked: () -> Unit = {}
) {
    MusifySpacer(16.dp)
    if (isBuffering) {
        LinearProgressIndicator()
    }

    Slider(
        value = currentPosition.toFloat(),
        onValueChange = {
            onSeekChange(it.toLong())
        },
        modifier = Modifier.fillMaxWidth(),
        valueRange = 0f..duration.toFloat(),
    )
    Row {
        Text(
            text = formattedTime(currentPosition),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Box(Modifier.weight(1f))
        Text(
            text = formattedTime(duration),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousClicked) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_media_previous),
                contentDescription = "Previous",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        val ic = if (isPlaying) {
            android.R.drawable.ic_media_pause
        } else {
            android.R.drawable.ic_media_play
        }

        IconButton(
            onClick = onPlayPauseToggle,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                painter = painterResource(id = ic),
                contentDescription = "Play/Pause",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        IconButton(onClick = onNextClicked) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_media_next),
                contentDescription = "Next",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        IconButton(onClick = onPlayListClicked) {
            Icon(
                painter = painterResource(id = androidx.media3.session.R.drawable.media3_icon_playlist_add),
                contentDescription = "add to playlist",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }

}

fun formattedTime(millis: Long): String {
    val duration = millis / 1000
    val minutes = (duration / 60).toInt()
    val seconds = (duration % 60).toInt()
    return String.format("%02d:%02d", minutes, seconds)
}

@Composable
fun NowPlayingContent(title: String, isPlayList: Boolean = false) {
    val msg = if (isPlayList) "Playing from Playlist" else "Now Playing"
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = msg.uppercase(), style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.surfaceVariant
        )
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}


@Preview
@Composable
fun PlaySongScreenPreview() {
    MusifyAndroidTheme(darkTheme = true) {
        PlaySongScreenContent(
            title = "Song Title",
            genre = "Pop",
            image = "https://example.com/image.jpg",
            duration = 300000L,
            currentPosition = 150000L,
            isPlaying = true,
            isBuffering = false, {

            }, {}, {}
        )
    }

}

















