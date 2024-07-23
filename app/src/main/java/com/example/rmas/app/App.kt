package com.example.rmas.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rmas.screens.HomeScreen
import com.example.rmas.screens.AddMarkerScreen
import com.example.rmas.screens.LoginScreen
import com.example.rmas.screens.LeaderboardScreen
import com.example.rmas.screens.SingUpScreen
import com.google.firebase.auth.FirebaseUser


@Composable
fun App(user: FirebaseUser?, navController: NavHostController, requestPermission: () -> Unit) {
    val startDestination: String = if (user == null) 
        "LoginScreen"
    else "HomeScreen"
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        NavHost(navController = navController, startDestination = startDestination) {
            composable("LoginScreen") {
                LoginScreen(navController)
            }
            composable("SingUpScreen") {
                SingUpScreen(navController)
            }
            composable("HomeScreen") {
                HomeScreen(startDestination, navController, requestPermission)
            }
            composable("LeaderboardScreen") {
                LeaderboardScreen()
            }
            composable("AddMarkerScreen") {
                AddMarkerScreen(navController)
            }
        }
    }
}

