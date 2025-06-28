package com.codewithfk.musify_android.data.network

import com.codewithfk.musify_android.data.model.CreatePlaylistRequest
import com.codewithfk.musify_android.data.model.HomeDataResponse
import com.codewithfk.musify_android.data.model.LoginRequest
import com.codewithfk.musify_android.data.model.LoginResponse
import com.codewithfk.musify_android.data.model.PlaylistModel
import com.codewithfk.musify_android.data.model.RegisterRequest
import com.codewithfk.musify_android.data.model.Song
import com.codewithfk.musify_android.data.model.UpdatePlayListSongRequest
import com.codewithfk.musify_android.data.model.UpdatePlayListSongResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("/status")
    suspend fun getSomething(): Response<Map<String, String>>

    @POST("/auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    @POST("/auth/register")
    suspend fun register(
        @Body registerRequest: RegisterRequest
    ): Response<LoginResponse>

    @GET("/home")
    suspend fun getHomeData(): Response<HomeDataResponse>

    @GET("/songs/{id}")
    suspend fun getSongById(
        @Path("id") id: String
    ): Response<Song>

    @GET("/playlists")
    suspend fun getPlaylists(): Response<List<PlaylistModel>>

    @GET("/playlists/{id}")
    suspend fun getPlaylistByID(@Path("id") id: String): Response<PlaylistModel>

    @POST("/playlists")
    suspend fun createPlaylist(
        @Body playlist: CreatePlaylistRequest
    ): Response<PlaylistModel>

    @POST("/playlists/{id}/songs")
    suspend fun addSongToPlaylist(
        @Path("id") playlistId: String,
        @Body request: UpdatePlayListSongRequest
    ): Response<UpdatePlayListSongResponse>

    @HTTP(method = "DELETE", path = "/playlists/{id}/songs", hasBody = true)
    suspend fun removeSongsFromPlaylist(
        @Path("id") playlistId: String,
        @Body request: UpdatePlayListSongRequest
    ): Response<UpdatePlayListSongResponse>
}