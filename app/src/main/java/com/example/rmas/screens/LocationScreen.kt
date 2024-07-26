package com.example.rmas.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.example.rmas.data.Location
import com.example.rmas.database.Firebase

@Composable
fun LocationScreen(navController: NavController) {
    var locations by remember { mutableStateOf(emptyList<Location>()) }
    Firebase.getLocations {
        locations = it
    }
}