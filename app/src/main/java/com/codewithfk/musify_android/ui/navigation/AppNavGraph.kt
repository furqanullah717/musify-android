package com.codewithfk.musify_android.ui.navigation

import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.codewithfk.musify_android.data.model.CreatePlaylistRequest
import com.codewithfk.musify_android.ui.feature.home.HomeScreen
import com.codewithfk.musify_android.ui.feature.login.LoginScreen
import com.codewithfk.musify_android.ui.feature.onboarding.OnboardingScreen
import com.codewithfk.musify_android.ui.feature.playlist.ListPlaylistScreen
import com.codewithfk.musify_android.ui.feature.playlist.add_playlist.CreatePlaylistScreen
import com.codewithfk.musify_android.ui.feature.playlist.playlist_details.PlaylistDetailsScreen
import com.codewithfk.musify_android.ui.feature.playsong.PlaySongScreen
import com.codewithfk.musify_android.ui.feature.register.RegisterScreen


@Composable
fun AppNavGraph(navController: NavHostController, startDestination: MusifyNavRoute) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable<OnboardingRoute> {
            OnboardingScreen(
                navController
            )
        }
        composable<LoginRoute> {
            LoginScreen(navController)
        }

        composable<RegisterRoute> {
            RegisterScreen(navController)
        }
        composable<HomeRoute> {
            HomeScreen(navController)
        }
        composable<PlaySongRoute> {
            val route = it.toRoute<PlaySongRoute>()
            PlaySongScreen(route.id, navController)
        }
        composable<PlaylistDetailsRoute> {
            val route = it.toRoute<PlaylistDetailsRoute>()
            PlaylistDetailsScreen(route.id, navController)
        }
        composable<OpenPlayListRoute> {
            ListPlaylistScreen(navController)
        }
        composable<CreatePlayListRoute> {
            CreatePlaylistScreen(navController)
        }
    }
}