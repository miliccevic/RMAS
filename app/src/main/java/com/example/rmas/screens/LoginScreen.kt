package com.example.rmas.screens

import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rmas.data.LoginUIEvent
import com.example.rmas.data.LoginViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(context: Context,navController: NavController,loginViewModel: LoginViewModel = viewModel()){
    val username= remember { mutableStateOf("") }
    val password= remember { mutableStateOf("") }
    val visible= remember { mutableStateOf(false) }
    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(28.dp)
    ){
        Column(modifier = Modifier
            .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
            ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp)),
                label={ Text(text = "Korisničko ime") },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedLabelColor = Color(0xFF92A3FD),
                    focusedBorderColor = Color(0xFF92A3FD),
                    cursorColor = Color(0xFF92A3FD),
                    containerColor = Color(0xFFF7F8F8)
                ),
                keyboardOptions = KeyboardOptions.Default,
                value = username.value,
                onValueChange ={
                    username.value = it
                    loginViewModel.onEvent(LoginUIEvent.UsernameChanged(it), context, onClick = {navController.navigate("HomeScreen")})
                },
                leadingIcon = {
                    /*TODO dodati*/
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
                    loginViewModel.onEvent(LoginUIEvent.PasswordChanged(it), context, onClick = {navController.navigate("HomeScreen")})
                },
                leadingIcon = {
                    /*TODO dodati*/
                },
                trailingIcon = {
                    val iconImage= if(visible.value){
                        Icons.Filled.Visibility
                    }
                    else{
                        Icons.Filled.VisibilityOff
                    }
                    var description= if(visible.value){
                        "Sakrij šifru"
                    }
                    else{
                        "Prikaži šifru"
                    }
                    IconButton(onClick = { visible.value=!visible.value }) {
                        Icon(imageVector = iconImage, contentDescription = description)
                    }
                },
                visualTransformation =
                if(visible.value)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(onClick = {
                loginViewModel.onEvent(LoginUIEvent.LoginButtonClicked, context, onClick = {navController.navigate("HomeScreen")}) },
                modifier= Modifier
                    .fillMaxWidth()
                    .heightIn(48.dp),
                contentPadding = PaddingValues(),
                enabled= loginViewModel.allValidationsPassed.value,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                )
            ) {
                Box(modifier= Modifier
                    .fillMaxWidth()
                    .heightIn(48.dp)
                    .background(
                        //brush = Brush.horizontalGradient((listOf(Color.Black, Color.Blue))),
                        shape = RoundedCornerShape(50.dp),
                        color = if (loginViewModel.allValidationsPassed.value) Color.Black else Color.Gray
                    ),
                    contentAlignment = Alignment.Center
                )
                {
                    Text(text = "Prijavi se",
                        fontSize =18.sp,
                        color=Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            TextButton(onClick = { navController.navigate("SingUpScreen")},
                modifier=Modifier.fillMaxWidth()
            ) {
                Text(text="Registruj se",
                    color=Color.Black,
                    fontSize=16.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}