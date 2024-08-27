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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.rmas.viewmodels.SingUpViewModel
import com.example.rmas.presentation.singup.SingUpUIEvent
import com.example.rmas.utils.ImageUtils
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingUpScreen(navController: NavController, singUpViewModel: SingUpViewModel = viewModel()) {
    val context = LocalContext.current
    val state = singUpViewModel.singUpUIState.collectAsState()
    val scrollState = rememberScrollState()

    val visible = rememberSaveable { mutableStateOf(false) }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Registracija") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth(),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
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
                .padding(
                    top = 10.dp,
                    start = 28.dp,
                    end = 28.dp,
                    bottom = 28.dp
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(values)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp)),
                    label = { Text(text = "Ime") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    value = state.value.ime,
                    onValueChange = {
                        singUpViewModel.onEvent(
                            SingUpUIEvent.ImeChanged(it),
                            context,
                            navigateToLogin = { })
                    },
                    isError = state.value.imeError != null,
                    supportingText = {
                        if (state.value.imeError != null) {
                            Text(
                                text = state.value.imeError!!,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp)),
                    label = { Text(text = "Prezime") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    value = state.value.prezime,
                    onValueChange = {
                        singUpViewModel.onEvent(
                            SingUpUIEvent.PrezimeChanged(it),
                            context,
                            navigateToLogin = { })
                    },
                    isError = state.value.prezimeError != null,
                    supportingText = {
                        if (state.value.prezimeError != null) {
                            Text(
                                text = state.value.prezimeError!!,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp)),
                    label = { Text(text = "Telefon") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    value = state.value.telefon,
                    onValueChange = {
                        singUpViewModel.onEvent(
                            SingUpUIEvent.TelefonChanged(it),
                            context,
                            navigateToLogin = { })
                    },
                    isError = state.value.telefonError != null,
                    supportingText = {
                        if (state.value.telefonError != null) {
                            Text(
                                text = state.value.telefonError!!,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp)),
                    label = { Text(text = "Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    value = state.value.email,
                    onValueChange = {
                        singUpViewModel.onEvent(
                            SingUpUIEvent.EmailChanged(it),
                            context,
                            navigateToLogin = { })
                    },
                    isError = state.value.emailError != null,
                    supportingText = {
                        if (state.value.emailError != null) {
                            Text(
                                text = state.value.emailError!!,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp)),
                    label = { Text(text = "Korisničko ime") },
                    keyboardOptions = KeyboardOptions.Default,
                    value = state.value.username,
                    onValueChange = {
                        singUpViewModel.onEvent(
                            SingUpUIEvent.UsernameChanged(it),
                            context,
                            navigateToLogin = {})
                    },
                    isError = state.value.usernameError != null,
                    supportingText = {
                        if (state.value.usernameError != null) {
                            Text(
                                text = state.value.usernameError!!,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp)),
                    label = { Text(text = "Šifra") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    value = state.value.password,
                    isError = state.value.passwordError != null,
                    onValueChange = {
                        singUpViewModel.onEvent(
                            SingUpUIEvent.PasswordChanged(it),
                            context,
                            navigateToLogin = { })
                    },
                    trailingIcon = {
                        val iconImage = if (visible.value) {
                            Icons.Filled.Visibility
                        } else {
                            Icons.Filled.VisibilityOff
                        }
                        IconButton(onClick = { visible.value = !visible.value }) {
                            Icon(imageVector = iconImage, contentDescription = "")
                        }
                    },
                    visualTransformation =
                    if (visible.value)
                        VisualTransformation.None
                    else
                        PasswordVisualTransformation(),
                    supportingText = {
                        if (state.value.passwordError != null) {
                            Text(
                                text = state.value.passwordError!!,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
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
                                .clip(CircleShape)
                                .size(70.dp)
                        )
                    }
                } else {
                    val uri = Uri.fromFile(File(currentPhoto))
                    singUpViewModel.onEvent(
                        SingUpUIEvent.ImageChanged(uri),
                        context,
                        navigateToLogin = {})
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
                Spacer(modifier = Modifier.height(15.dp))
                Button(
                    onClick = {
                        singUpViewModel.onEvent(
                            SingUpUIEvent.RegisterButtonClicked,
                            context,
                            navigateToLogin = { navController.navigateUp() })
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(48.dp),
                    contentPadding = PaddingValues()
                ) {
                    if (singUpViewModel.singUpInProgress.value) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.inversePrimary,
                            modifier = Modifier.size(ButtonDefaults.IconSize),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Registruj se",
                            fontSize = 18.sp,
                        )
                    }
                }
            }
        }
    }
}
