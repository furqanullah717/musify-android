package com.codewithfk.musify_android.ui.navigation

import kotlinx.serialization.Serializable

interface MusifyNavRoute {}

@Serializable
object OnboardingRoute : MusifyNavRoute

@Serializable
object LoginRoute : MusifyNavRoute

@Serializable
object RegisterRoute : MusifyNavRoute

@Serializable
object HomeRoute : MusifyNavRoute

@Serializable
data class PlaySongRoute(val id: String) : MusifyNavRoute

@Serializable
object OpenPlayListRoute : MusifyNavRoute

@Serializable
object CreatePlayListRoute : MusifyNavRoute