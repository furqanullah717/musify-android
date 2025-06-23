package com.codewithfk.musify_android.data.repository

import com.codewithfk.musify_android.data.model.CreatePlaylistRequest
import com.codewithfk.musify_android.data.model.PlaylistModel
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

}