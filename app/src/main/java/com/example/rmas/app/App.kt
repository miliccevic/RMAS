package com.example.rmas.app

import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.rmas.screens.HomeScreen
import com.example.rmas.screens.LoginScreen
import com.example.rmas.screens.RangScreen
import com.example.rmas.screens.SingUpScreen
//<provider
//android:authorities="com.task.master.fileprovider"
//android:name="androidx.core.content.FileProvider"
//android:exported="false"
//android:grantUriPermissions="true">
//<meta-data android:name="android.support.FILE_PROVIDER_PATH"
//android:resource="@xml/file_path"/>
//</provider>

@Composable
fun App(context: Context, navController: NavHostController){
    Surface(
        modifier=Modifier.fillMaxSize(),
        color= Color.White
    ){
        NavHost(navController = navController, startDestination = "LoginScreen") {
            composable("LoginScreen") {
                LoginScreen(context,navController)
            }
            composable("SingUpScreen"){
                SingUpScreen(context, navController)
            }
            composable("HomeScreen"){
                HomeScreen(context,navController)
            }
            composable("RangScreen"){
                RangScreen()
            }
        }
    }
}

