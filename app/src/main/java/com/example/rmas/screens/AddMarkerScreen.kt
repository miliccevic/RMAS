package com.example.rmas.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.rmas.presentation.marker.MarkerUIEvent
import com.example.rmas.utils.ImageUtils
import com.example.rmas.viewmodels.MarkerViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMarkerScreen(navController: NavController, markerViewModel: MarkerViewModel = viewModel()) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val state = markerViewModel.markerUIState.collectAsState()

    val imageUtils = ImageUtils(context)
    var currentPhoto by rememberSaveable { mutableStateOf<String?>(null) }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data?.data
                currentPhoto = if (data == null) {
                    imageUtils.currentPhotoPath
                } else {
                    imageUtils.getPathFromGalleryUri(data)
                }
            }
        }
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _: Boolean ->
        launcher.launch(imageUtils.getIntent())
    }

    val options =
        listOf("Rupa na putu", "Rad na putu", "Saobraćajna nezgoda", "Zatvorena ulica", "Ostalo")
    val expanded = rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Dodaj na mapu") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth(),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
            )
        }
    ) { values ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(28.dp)
                .verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(values),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (currentPhoto == null) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .border(
                                width = 1.dp,
                                color = OutlinedTextFieldDefaults.colors().unfocusedPlaceholderColor,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            colorFilter = ColorFilter.tint(OutlinedTextFieldDefaults.colors().unfocusedPlaceholderColor),
                            imageVector = Icons.Filled.CameraAlt,
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(70.dp)
                                .clickable {
                                    if (ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.CAMERA
                                        ) != PackageManager.PERMISSION_GRANTED
                                    ) {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    } else {
                                        launcher.launch(imageUtils.getIntent())
                                    }
                                }
                        )
                    }
                } else {
                    val uri = Uri.fromFile(File(currentPhoto))
                    markerViewModel.onEvent(
                        MarkerUIEvent.ImageChanged(uri),
                        context,
                        onClick = {})
                    Image(
                        painter = rememberAsyncImagePainter(model = state.value.image),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(90.dp)
                            .border(
                                width = 1.dp,
                                color = OutlinedTextFieldDefaults.colors().unfocusedPlaceholderColor,
                                shape = CircleShape
                            )
                            .clickable {
                                launcher.launch(imageUtils.getIntent())
                            }
                    )
                }
                Spacer(modifier = Modifier.height(15.dp))
                if (state.value.imageError != null) {
                    Text(
                        text = state.value.imageError!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 12.dp),
                        fontSize = 12.sp
                    )
                }
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp)),
                    value = state.value.title,
                    onValueChange = {
                        markerViewModel.onEvent(
                            MarkerUIEvent.TitleChanged(it),
                            context,
                            onClick = {})
                    },
                    label = { Text(text = "Naslov") },
                    keyboardOptions = KeyboardOptions.Default,
                    isError = state.value.titleError != null,
                    supportingText = {
                        if (state.value.titleError != null) {
                            Text(
                                text = state.value.titleError!!,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp)),
                    value = state.value.description,
                    onValueChange = {
                        markerViewModel.onEvent(
                            MarkerUIEvent.DescriptionChanged(it),
                            context,
                            onClick = {})
                    },
                    label = { Text(text = "Opis") },
                    keyboardOptions = KeyboardOptions.Default,
                    isError = state.value.descriptionError != null,
                    supportingText = {
                        if (state.value.descriptionError != null) {
                            Text(
                                text = state.value.descriptionError!!,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                ExposedDropdownMenuBox(
                    expanded = expanded.value,
                    onExpandedChange = {
                        expanded.value = !expanded.value
                    }
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(4.dp))
                            .menuAnchor(),
                        value = state.value.type,
                        onValueChange = {
                        },
                        label = { Text(text = "Kategorija") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
                        },
                        readOnly = true
                    )
                    ExposedDropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false }
                    ) {
                        options.forEach {
                            DropdownMenuItem(
                                text = { Text(text = it) },
                                onClick = {
                                    expanded.value = false
                                    markerViewModel.onEvent(
                                        MarkerUIEvent.TypeChanged(it),
                                        context,
                                        onClick = {})
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        markerViewModel.onEvent(
                            MarkerUIEvent.AddMarkerClicked,
                            context,
                            onClick = { navController.navigateUp() })
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(48.dp),
                    contentPadding = PaddingValues(),
                ) {
                    if (markerViewModel.addInProgress.value) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.inversePrimary,
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Dodaj na mapu",
                            fontSize = 18.sp,
                        )
                    }
                }
            }
        }
    }
}