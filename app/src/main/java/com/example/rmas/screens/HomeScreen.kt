package com.example.rmas.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AddLocation
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rmas.viewmodels.LoginViewModel
import com.example.rmas.services.location.UserLocation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    startDestination:String,
    navController: NavController,
    requestPermission: () -> Unit,
    loginViewModel: LoginViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var selectedIndex by rememberSaveable { /*TODO* upitno da li radi*/
        mutableStateOf(1)
    }
    //val fusedLocationProviderClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    //var currentLocation by remember { mutableStateOf<Location?>(null) }
    var userLocation by remember { mutableStateOf(UserLocation.location) }
    var deviceLatLng by remember {
        mutableStateOf(LatLng(43.32, 21.89))
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(deviceLatLng, 15f)
    }
    var uiSettings by remember {
        mutableStateOf(MapUiSettings(zoomControlsEnabled = true))
    }
    var properties by remember {
        mutableStateOf(MapProperties(mapType = MapType.TERRAIN))
    }
    requestPermission() /*TODO*/
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                NavigationDrawerItem(
                    label = { Text(text = "Mapa") },
                    selected = selectedIndex == 1,
                    onClick = {
                        selectedIndex = 1
                        navController.popBackStack("HomeScreen", false)
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Map,
                            contentDescription = "Mapa",
                            tint = Color.Black
                        )
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    label = { Text(text = "Rang lista") },
                    selected = selectedIndex == 2,
                    onClick = {
                        selectedIndex = 2
                        navController.navigate("LeaderboardScreen")
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "Rang lista",
                            tint = Color.Black
                        )
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                HorizontalDivider(modifier = Modifier.padding(5.dp)) //mozda
                NavigationDrawerItem(
                    label = { Text(text = "Odjavi se") },
                    selected = false,
                    onClick = {
                        loginViewModel.logOut(
                            context,
                            onClick = {
                                if(startDestination=="HomeScreen") { /*TODO*/
                                    navController.navigate("LoginScreen")
                                }
                                else {
                                    navController.popBackStack("LoginScreen", false)
                                }
                            })
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color.Black
                        )
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(title = { Text(text = "Mapa", color = Color.Black) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.LightGray,
                    ),/*TODO ruzna boja*/
                    modifier = Modifier
                        .fillMaxWidth(),
                    navigationIcon = {
                        IconButton(onClick = {
                                scope.launch {
                                drawerState.open() }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Menu",
                                tint = Color.Black
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                                scope.launch {
                                drawerState.open() }
                        }) {
                            Icon(
                                imageVector = Icons.Filled.FilterList,
                                contentDescription = "Filter",
                                tint = Color.Black
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    modifier = Modifier
                        .padding(16.dp),
                    onClick = { navController.navigate("InputScreen") },
                ) {
                    Icon(
                        Icons.Filled.AddLocation,
                        contentDescription = ""
                    )
                }
            }
        ) { values ->
            Surface(
                color = Color.White,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)/*TODO padding top manje*/
                    .padding(values)
            ) {
                /*TODO*/
                Box(modifier = Modifier) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = properties,
                        uiSettings = uiSettings,
                        onMapClick = {
                        }
                    ) {
                        if (userLocation.value != null) {
                            cameraPositionState.move(
                                CameraUpdateFactory.newLatLng(
                                    LatLng(
                                        userLocation.value!!.latitude,
                                        userLocation.value!!.longitude
                                    )
                                )
                            )
                            Marker(
                                state = MarkerState(
                                    position = LatLng(
                                        userLocation.value!!.latitude,
                                        userLocation.value!!.longitude
                                    )
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

