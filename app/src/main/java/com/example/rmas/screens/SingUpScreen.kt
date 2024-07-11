package com.example.rmas.screens

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rmas.R
import com.example.rmas.data.SingUpViewModel
import com.example.rmas.data.SingUpUIEvent
import com.example.rmas.utils.ImageUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SingUpScreen( context: Context,navController: NavController,singUpViewModel:SingUpViewModel = viewModel()) {
    val ime = remember { mutableStateOf("") }
    val prezime = remember { mutableStateOf("") }
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val telefon = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val imgUrl = remember{ mutableStateOf(Uri.EMPTY) }

    val visible = remember { mutableStateOf(false) }

    //val openBottomSheet = remember{ mutableStateOf(false) }
    //val sheetState = rememberModalBottomSheetState()
    val imageUtils = ImageUtils(context)

    var currentPhoto by remember { mutableStateOf<String?>(null) }
    var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
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

    //val img:Bitmap=BitmapFactory.decodeResource(Resources.getSystem(),android.R.drawable.ic_menu_report_image)
    //val bitmap = remember { mutableStateOf(img) }

//    val galleryLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) {
//        if(Build.VERSION.SDK_INT<28){
//            bitmap.value=MediaStore.Images.Media.getBitmap(context.contentResolver,it)
//            //ImageDecoder.createSource(context.contentResolver,it)
//        }
//        else{
//            val source=it?.let { it1->
//                ImageDecoder.createSource(context.contentResolver,it1)
//            }
//            bitmap.value=source?.let {it1->
//                ImageDecoder.decodeBitmap(it1)
//            }!!
//        }
//    }
//    val cameraLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.TakePicturePreview()
//    ) {
//        if(it!=null){
//            bitmap.value=it
//        }
//    }
//    val cameraPermission= rememberPermissionState(permission = Manifest.permission.CAMERA)

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
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp)),
                    label = { Text(text = "Ime") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedLabelColor = Color(0xFF92A3FD),
                        focusedBorderColor = Color(0xFF92A3FD),
                        cursorColor = Color(0xFF92A3FD),
                        containerColor = Color(0xFFF7F8F8)
                    ),
                    keyboardOptions = KeyboardOptions.Default,
                    value = ime.value,
                    onValueChange = {
                        ime.value = it
                        singUpViewModel.onEvent(SingUpUIEvent.ImeChanged(it), context, onClick = { navController.popBackStack("LoginScreen", false) })
                    },
                    leadingIcon = {
                        /*TODO dodati*/
                    }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp)),
                    label = { Text(text = "Prezime") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedLabelColor = Color(0xFF92A3FD),
                        focusedBorderColor = Color(0xFF92A3FD),
                        cursorColor = Color(0xFF92A3FD),
                        containerColor = Color(0xFFF7F8F8)
                    ),
                    keyboardOptions = KeyboardOptions.Default,
                    value = prezime.value,
                    onValueChange = {
                        prezime.value = it
                        singUpViewModel.onEvent(SingUpUIEvent.PrezimeChanged(it), context, onClick = { navController.popBackStack("LoginScreen", false) })
                    },
                    leadingIcon = {
                        /*TODO dodati*/
                    }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp)),
                    label = { Text(text = "Telefon") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedLabelColor = Color(0xFF92A3FD),
                        focusedBorderColor = Color(0xFF92A3FD),
                        cursorColor = Color(0xFF92A3FD),
                        containerColor = Color(0xFFF7F8F8)
                    ),
                    keyboardOptions = KeyboardOptions.Default,
                    value = telefon.value,
                    onValueChange = {
                        telefon.value = it
                        singUpViewModel.onEvent(SingUpUIEvent.TelefonChanged(it), context, onClick = { navController.popBackStack("LoginScreen", false) })
                    },
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_phone_24),
                            contentDescription = null
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp)),
                    label = { Text(text = "Email") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedLabelColor = Color(0xFF92A3FD),
                        focusedBorderColor = Color(0xFF92A3FD),
                        cursorColor = Color(0xFF92A3FD),
                        containerColor = Color(0xFFF7F8F8)
                    ),
                    keyboardOptions = KeyboardOptions.Default,
                    value = email.value,
                    onValueChange = {
                        email.value = it
                        singUpViewModel.onEvent(SingUpUIEvent.EmailChanged(it), context, onClick = { navController.popBackStack("LoginScreen", false) })
                    },
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_email_24),
                            contentDescription = null
                        )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp)),
                    label = { Text(text = "Korisničko ime") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedLabelColor = Color(0xFF92A3FD),
                        focusedBorderColor = Color(0xFF92A3FD),
                        cursorColor = Color(0xFF92A3FD),
                        containerColor = Color(0xFFF7F8F8)
                    ),
                    keyboardOptions = KeyboardOptions.Default,
                    value = username.value,
                    onValueChange = {
                        username.value = it
                        singUpViewModel.onEvent(SingUpUIEvent.UsernameChanged(it), context, onClick = { navController.popBackStack("LoginScreen", false) })
                    },
                    leadingIcon = {
                        //Image(painter = painterResource(id = ), contentDescription = )
                    }
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp)),
                    label = { Text(text = "Šifra") },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedLabelColor = Color(0xFF92A3FD),
                        focusedBorderColor = Color(0xFF92A3FD),
                        cursorColor = Color(0xFF92A3FD),
                        containerColor = Color(0xFFF7F8F8)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    value = password.value,
                    onValueChange = {
                        password.value = it
                        singUpViewModel.onEvent(SingUpUIEvent.PasswordChanged(it), context, onClick = { navController.popBackStack("LoginScreen", false) })
                    },
                    leadingIcon = {
                        /*TODO dodati*/
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
                Box(modifier=Modifier
                    .height(100.dp)) {/*TODO*/
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 10.dp)
                    ) {
                        if(currentPhoto==null) {
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
                                        launcher.launch(imageUtils.getIntent())
                                    }
                            )
                        }
                        else{
                            val imageBitmap = BitmapFactory.decodeFile(currentPhoto).asImageBitmap()
                            bitmap=imageBitmap
                            val uri=Uri.fromFile(File(currentPhoto))
                            imgUrl.value = uri
                            Log.d("TAG1",uri.toString())
                            singUpViewModel.onEvent(SingUpUIEvent.ImageChanged(uri), context, onClick = {navController.popBackStack("LoginScreen",false)})
                            Image(
                                bitmap= imageBitmap,
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
                                        launcher.launch(imageUtils.getIntent())
                                    }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        singUpViewModel.onEvent(SingUpUIEvent.RegisterButtonClicked, context, onClick = { navController.popBackStack("LoginScreen", false) })
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(48.dp),
                    contentPadding = PaddingValues(),
                    enabled = singUpViewModel.allValidationsPassed.value,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(48.dp)
                            .background(
                                //brush = Brush.horizontalGradient((listOf(Color.Black, Color.Blue))),
                                shape = RoundedCornerShape(50.dp),
                                color = if (singUpViewModel.allValidationsPassed.value) Color.Black else Color.Gray
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
//                if (openBottomSheet.value) {
//                    CameraBottomSheet(
//                        sheetState = sheetState,
//                        onDismissRequest = {
//                            openBottomSheet.value=false
//                        },
//                        onActionRequest = { value ->
//                            imagePath.value=value
//                        })
//                    ModalBottomSheet(onDismissRequest = { openBottomSheet.value = false },
//                        sheetState = sheetState
//                    ) {
//                        Column(modifier= Modifier
//                            .fillMaxWidth()
//                            .padding(18.dp),
//                            verticalArrangement = Arrangement.spacedBy(20.dp)) {
//                            Row(verticalAlignment = Alignment.CenterVertically,
//                                horizontalArrangement = Arrangement.spacedBy(12.dp),
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .clickable(
//                                        onClick = {
//                                            if (cameraPermission.status.isGranted) {
//                                                cameraLauncher.launch()
//                                            } else {
//                                                cameraPermission.launchPermissionRequest()
//                                            }
//                                            openBottomSheet.value = false
//                                        }
//                                    )) {
//                                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null)
//                                Text(text = "Kamera", fontSize = 22.sp)
//                            }
//                            Row(verticalAlignment = Alignment.CenterVertically,
//                                horizontalArrangement = Arrangement.spacedBy(12.dp),
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .clickable( /*TODO ruzno kad se klikne*/
//                                        onClick = {
//                                            galleryLauncher.launch("image/*")
//                                            openBottomSheet.value = false
//                                        })
//                            ) {
//                                Icon(imageVector = Icons.Default.Image, contentDescription = null)
//                                Text(text = "Galerija", fontSize = 22.sp)
//                            }
//                        }
 //                   }
//                }
            }
        }
    }
}
