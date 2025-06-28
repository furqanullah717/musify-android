package com.codewithfk.musify_android.data.repository

import com.codewithfk.musify_android.data.model.CreatePlaylistRequest
import com.codewithfk.musify_android.data.model.PlaylistModel
import com.codewithfk.musify_android.data.model.UpdatePlayListSongRequest
import com.codewithfk.musify_android.data.model.UpdatePlayListSongResponse
import com.codewithfk.musify_android.data.network.ApiService
import com.codewithfk.musify_android.data.network.Resource
import org.koin.core.annotation.Single

@Single
class PlaylistRepository(private val apiService: ApiService) {


    suspend fun getPlaylists(): Resource<List<PlaylistModel>> {
        return try {
            val response = apiService.getPlaylists()
            if (response.isSuccessful) {
                Resource.Success(response.body() ?: emptyList())
            } else {
                Resource.Error("Failed to fetch playlists")
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.message}")
        }
    }


    suspend fun createPlaylist(playlistRequest: CreatePlaylistRequest): Resource<PlaylistModel> {
        return try {
            val response = apiService.createPlaylist(playlistRequest)
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Failed to create playlist")
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.message}")
        }
    }

    suspend fun addSongToPlaylist(
        playlistId: String,
        songId: String
    ): Resource<UpdatePlayListSongResponse> {
        return try {
            val response = apiService.addSongToPlaylist(
                playlistId,
                UpdatePlayListSongRequest(
                    songIds = listOf(songId)
                )
            )
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Failed to add song to playlist")
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.message}")
        }
    }

    suspend fun deleteSongFromPlaylist(
        playlistId: String,
        songId: String
    ): Resource<UpdatePlayListSongResponse> {
        return try {
            val response = apiService.removeSongsFromPlaylist(
                playlistId,
                UpdatePlayListSongRequest(
                    songIds = listOf(songId)
                )
            )
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Failed to delete song from playlist")
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.message}")
        }
    }

    suspend fun getPlaylistDetails(playlistID: String): Resource<PlaylistModel> {
        return try {
            val response = apiService.getPlaylistByID(playlistID)
            if (response.isSuccessful) {
                Resource.Success(response.body()!!)
            } else {
                Resource.Error("Failed to fetch playlists")
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.message}")
        }
    }

}