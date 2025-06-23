package com.codewithfk.musify_android.ui.feature.playlist

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.codewithfk.musify_android.R
import com.codewithfk.musify_android.data.model.PlaylistModel
import com.codewithfk.musify_android.ui.feature.widgets.ErrorScreen
import com.codewithfk.musify_android.ui.feature.widgets.LoadingScreen
import com.codewithfk.musify_android.ui.navigation.CreatePlayListRoute
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@Composable
fun ListPlaylistScreen(
    navController: NavController,
    viewModel: ListPlaylistViewModel = koinViewModel()
) {

    LaunchedEffect(true) {
        viewModel.event.collectLatest {
            when (it) {
                is ListPlaylistEvent.showErrorMessage -> {
                    Toast.makeText(navController.context, it.message, Toast.LENGTH_SHORT).show()
                }

                ListPlaylistEvent.createPlaylist -> {
                    navController.navigate(CreatePlayListRoute) {
                        restoreState = true
                    }
                }
            }
        }
    }

    val state = viewModel.state.collectAsStateWithLifecycle()
    val navBackStackEntry = navController.currentBackStackEntry
    LaunchedEffect(navBackStackEntry) {
        navBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("playlistCreated")
            ?.observeForever { created ->
                if (created == true) {
                    viewModel.fetchData()
                    navBackStackEntry.savedStateHandle.remove<Boolean>("playlistCreated")
                }
            }
    }
    when (state.value) {
        is ListPlaylistState.Loading -> {
            LoadingScreen()
        }

        is ListPlaylistState.Success -> {
            val data = (state.value as ListPlaylistState.Success).data
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Button(modifier = Modifier.fillMaxWidth(), onClick = {
                    viewModel.onCreatePlaylistClicked()
                }) {
                    Text("Create Playlist")
                }
                if (data.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        Text("No playlists found", modifier = Modifier.padding(16.dp))
                    }
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(data) { playlistModel: PlaylistModel ->
                            PlaylistItem(playlistModel) {

                            }
                        }
                    }
                }
            }

        }

        is ListPlaylistState.Error -> {
            val errorMessage = (state.value as ListPlaylistState.Error).message
            ErrorScreen(errorMessage, "Retry", onPrimaryButtonClicked = {})
        }
    }
}

@Composable
fun PlaylistItem(playlistModel: PlaylistModel, onItemClick: () -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick.invoke() }
            .padding(8.dp)
    ) {
        val iamge = getImageFromPlaylist(playlistModel)
        if(iamge!=null){
            AsyncImage(
                model = getImageFromPlaylist(playlistModel),
                contentDescription = "Playlist Cover",
                modifier = Modifier.size(60.dp)
            )
        }else{
            Image(
                painter = painterResource(R.drawable.ic_profile),
                contentDescription = "Playlist Cover",
                modifier = Modifier.size(60.dp)
            )
        }

        Column {
            Text(
                text = playlistModel.name,
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${playlistModel.songs?.size?:0} songs",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}


fun getImageFromPlaylist(playlistModel: PlaylistModel): String? {
    val imageUrl = playlistModel.coverImage
    val songImage = if(playlistModel.songs?.isNotEmpty()==true) {
        playlistModel.songs[0].coverImage
    } else {
        imageUrl
    }
    return songImage
}














