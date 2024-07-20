package com.example.rmas.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rmas.R
import com.example.rmas.viewmodels.SingUpViewModel
import com.example.rmas.presentation.singup.SingUpUIEvent
import com.example.rmas.utils.ImageUtils
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingUpScreen(navController: NavController, singUpViewModel: SingUpViewModel = viewModel()) {
    val context = LocalContext.current
    val state = singUpViewModel.singUpUIState
    val scrollState = rememberScrollState()
    val ime = remember { mutableStateOf("") }
    val prezime = remember { mutableStateOf("") }
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val telefon = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val imgUrl = remember { mutableStateOf(Uri.EMPTY) }

    val visible = remember { mutableStateOf(false) }

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Registracija", color = Color.Black) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.LightGray,
                ),
                /*TODO ruzna boja*/
                modifier = Modifier
                    .fillMaxWidth(),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack("LoginScreen", false) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                },
            )
        }
    ) { values ->
        Surface(
            color = Color.White,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(28.dp) /*TODO padding top manje*/
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(values)
                    .verticalScroll(scrollState)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp)),
                    label = { Text(text = "Ime") },
                    colors = OutlinedTextFieldDefaults.colors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    value = ime.value,
                    onValueChange = {
                        ime.value = it
                        singUpViewModel.onEvent(
                            SingUpUIEvent.ImeChanged(it),
                            context,
                            navigateToLogin = { navController.popBackStack("LoginScreen", false) })
                    },
                    isError = state.value.imeError!=null
                )
                if(state.value.imeError!=null){
                    Text(text = state.value.imeError!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier=Modifier.align(Alignment.End))
                }
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp)),
                    label = { Text(text = "Prezime") },
                    colors = OutlinedTextFieldDefaults.colors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    value = prezime.value,
                    onValueChange = {
                        prezime.value = it
                        singUpViewModel.onEvent(
                            SingUpUIEvent.PrezimeChanged(it),
                            context,
                            navigateToLogin = { navController.popBackStack("LoginScreen", false) })
                    },
                    isError = state.value.prezimeError!=null
                )
                if(state.value.prezimeError!=null){
                    Text(text = state.value.prezimeError!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier=Modifier.align(Alignment.End))
                }
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp)),
                    label = { Text(text = "Telefon") },
                    colors = OutlinedTextFieldDefaults.colors(
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    value = telefon.value,
                    onValueChange = {
                        telefon.value = it
                        singUpViewModel.onEvent(
                            SingUpUIEvent.TelefonChanged(it),
                            context,
                            navigateToLogin = { navController.popBackStack("LoginScreen", false) })
                    },
                    isError = state.value.telefonError!=null
                )
                if(state.value.telefonError!=null){
                    Text(text = state.value.telefonError!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier=Modifier.align(Alignment.End))
                }
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp)),
                    label = { Text(text = "Email") },
                    colors = OutlinedTextFieldDefaults.colors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    value = email.value,
                    onValueChange = {
                        email.value = it
                        singUpViewModel.onEvent(
                            SingUpUIEvent.EmailChanged(it),
                            context,
                            navigateToLogin = { navController.popBackStack("LoginScreen", false) })
                    },
                    isError = state.value.emailError!=null
                )
                if(state.value.emailError!=null){
                    Text(text = state.value.emailError!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier=Modifier.align(Alignment.End))
                }
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp)),
                    label = { Text(text = "Korisničko ime") },
                    colors = OutlinedTextFieldDefaults.colors(
                    ),
                    keyboardOptions = KeyboardOptions.Default,
                    value = username.value,
                    onValueChange = {
                        username.value = it
                        singUpViewModel.onEvent(
                            SingUpUIEvent.UsernameChanged(it),
                            context,
                            navigateToLogin = {
                                navController.popBackStack("LoginScreen", false)
                            })
                    },
                    isError = state.value.usernameError!=null
                )
                if(state.value.usernameError!=null){
                    Text(text = state.value.usernameError!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier=Modifier.align(Alignment.End))
                }
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp)),
                    label = { Text(text = "Šifra") },
                    colors = OutlinedTextFieldDefaults.colors(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    value = password.value,
                    isError = state.value.passwordError!=null,
                    onValueChange = {
                        password.value = it
                        singUpViewModel.onEvent(
                            SingUpUIEvent.PasswordChanged(it),
                            context,
                            navigateToLogin = { navController.popBackStack("LoginScreen", false) })
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
                        PasswordVisualTransformation()
                )
                if(state.value.passwordError!=null){
                    Text(text = state.value.passwordError!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier=Modifier.align(Alignment.End)) /*TODO*/
                }
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
                                //bitmap= bitmap.value.asImageBitmap(),
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
                                        //openBottomSheet.value = true
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
                            singUpViewModel.onEvent(
                                SingUpUIEvent.ImageChanged(uri),
                                context,
                                navigateToLogin = { navController.popBackStack("LoginScreen", false) })
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
                if(state.value.imageError!=null){ /*TODO*/
                    Text(text = state.value.imageError!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier=Modifier.align(Alignment.End))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        singUpViewModel.onEvent(
                            SingUpUIEvent.RegisterButtonClicked,
                            context,
                            navigateToLogin = { navController.popBackStack("LoginScreen", false) })
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
                            text = "Registruj se",
                            fontSize = 18.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
