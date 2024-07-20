package com.example.rmas.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rmas.R
import com.example.rmas.presentation.marker.MarkerUIEvent
import com.example.rmas.utils.ImageUtils
import com.example.rmas.viewmodels.MarkerViewModel
import com.google.firebase.auth.FirebaseAuth
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMarkerScreen(navController: NavController, markerViewModel: MarkerViewModel = viewModel()) {
    val context = LocalContext.current
    val state = markerViewModel.markerUIState
    val description = remember { mutableStateOf("") }
    val title = remember { mutableStateOf("") }
    val imgUrl = remember { mutableStateOf(Uri.EMPTY) } /*TODO crop*/

    val imageUtils = ImageUtils(context)

    var currentPhoto by remember { mutableStateOf<String?>(null) }
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data?.data
                currentPhoto = if (data == null) {
                    // Camera intent
                    imageUtils.currentPhotoPath
                } else {
                    // Gallery Pick Intent
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
    var expanded = remember { mutableStateOf(false) }
    var selectedOption = remember { mutableStateOf(options[0]) }
    markerViewModel.onEvent(MarkerUIEvent.TypeChanged(selectedOption.value), context, onClick = {})

    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp)),
                value = title.value,
                onValueChange = {
                    title.value = it
                    markerViewModel.onEvent(MarkerUIEvent.TitleChanged(it), context, onClick = {})
                },
                label = { Text(text = "Naslov") },
                keyboardOptions = KeyboardOptions.Default,
                colors = OutlinedTextFieldDefaults.colors(
                    /*TODO*/
                ),
                isError = state.value.titleError != null
            )
            if (state.value.titleError != null) {
                Text(
                    text = state.value.titleError!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.End)
                )
            }
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp)),
                value = description.value,
                onValueChange = {
                    description.value = it
                    markerViewModel.onEvent(
                        MarkerUIEvent.DescriptionChanged(it),
                        context,
                        onClick = {})
                },
                label = { Text(text = "Opis") },
                keyboardOptions = KeyboardOptions.Default,
                colors = OutlinedTextFieldDefaults.colors(
                    /*TODO*/
                ),
                isError = state.value.descriptionError != null
            )
            if (state.value.descriptionError != null) {
                Text(
                    text = state.value.descriptionError!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.End)
                )
            }
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
                    value = selectedOption.value,
                    onValueChange = {

                    },
                    label = { Text(text = "Kategorija") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
                    },
                    colors = OutlinedTextFieldDefaults.colors(

                    ),
                    readOnly = true
                )
                ExposedDropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false } /*TODO cela duzina*/
                ) {
                    options.forEach {
                        DropdownMenuItem(
                            text = { Text(text = it) },
                            onClick = {
                                selectedOption.value = it
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
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .height(100.dp)
            ) {/*TODO*/
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 10.dp)
                ) {
                    if (currentPhoto == null) {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_photo_camera_24),
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(100.dp)
                                .background(Color.Gray)
                                .border(
                                    width = 1.dp,
                                    color = Color.Black,
                                    shape = CircleShape
                                )
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
                    } else {
                        val imageBitmap = BitmapFactory.decodeFile(currentPhoto).asImageBitmap()
                        bitmap = imageBitmap
                        val uri = Uri.fromFile(File(currentPhoto))
                        imgUrl.value = uri
                        markerViewModel.onEvent(
                            MarkerUIEvent.ImageChanged(uri),
                            context,
                            onClick = {})
                        Image(
                            bitmap = imageBitmap,
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(100.dp)
                                .background(Color.Gray)
                                .border(
                                    width = 1.dp,
                                    color = Color.Black,
                                    shape = CircleShape
                                )
                                .clickable {
                                    launcher.launch(imageUtils.getIntent())
                                }
                        )
                    }
                }
            }
            if (state.value.imageError != null) { /*TODO*/
                Text(
                    text = state.value.imageError!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.End)
                )
            }
            Button(
                onClick = {
                    markerViewModel.onEvent(MarkerUIEvent.AddMarkerClicked, context, onClick = {navController.popBackStack("HomeScreen",false)})
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(48.dp),
                contentPadding = PaddingValues(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(48.dp)
                        .background(
                            shape = RoundedCornerShape(50.dp),
                            color = Color.Black
                        ),
                    contentAlignment = Alignment.Center
                )
                {
                    Text(
                        text = "Dodaj na mapu",
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}