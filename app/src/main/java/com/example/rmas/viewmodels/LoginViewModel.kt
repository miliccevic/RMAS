package com.example.rmas.viewmodels

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.rmas.presentation.login.LoginUIEvent
import com.example.rmas.presentation.login.LoginUIState
import com.example.rmas.presentation.validation.Validator
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class LoginViewModel : ViewModel() {
    var loginUIState = mutableStateOf(LoginUIState())
    var allValidationsPassed = mutableStateOf(false)

    //samo jedan observer
    fun onEvent(event: LoginUIEvent, context: Context, onClick: () -> Unit) {
        when (event) {
            is LoginUIEvent.UsernameChanged -> {
                loginUIState.value = loginUIState.value.copy(
                    username = event.username
                )
            }

            is LoginUIEvent.PasswordChanged -> {
                loginUIState.value = loginUIState.value.copy(
                    password = event.password
                )
            }

            is LoginUIEvent.LoginButtonClicked -> {
                login(context, onClick = { onClick() })
            }
        }
    }

    private fun login(context: Context, onClick: () -> Unit) {
        validateData()
        if (!allValidationsPassed.value)
            return
        val db = Firebase.firestore
        db.collection("users")
            .document(loginUIState.value.username)
            .get()
            .addOnSuccessListener {
                if (it.exists()) {
                    FirebaseAuth
                        .getInstance()
                        .signInWithEmailAndPassword(
                            it.get("email").toString(),
                            loginUIState.value.password
                        )
                        .addOnFailureListener {
                            Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT)
                                .show() /*TODO druga ikonica*/
                        }
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                if (FirebaseAuth.getInstance().currentUser?.isEmailVerified == false) {
                                    Toast.makeText(
                                        context,
                                        "Molimo vas potvrdite email adresu.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Uspešno logovanje.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onClick.invoke()
                                }
                            }
                        }
                } else {
                    Toast.makeText(
                        context,
                        "Ne postoji nalog sa unetim korisničkim imenom.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener {
                Log.d("TAG", it.localizedMessage)
                Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }

    fun logOut(context: Context, onClick: () -> Unit) {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signOut()

        if (firebaseAuth.currentUser == null) {
            Toast.makeText(context, "Uspešno odjavljivanje.", Toast.LENGTH_SHORT).show()
        }
        onClick.invoke()
    }

    private fun validateData() {
        val usernameResult = Validator.validateUsername(username = loginUIState.value.username)
        val passwordResult = Validator.validatePasswordLogin(password = loginUIState.value.password)

        allValidationsPassed.value = (usernameResult.status && passwordResult.status)
        loginUIState.value = loginUIState.value.copy(
            usernameError = usernameResult.errorMessage,
            passwordError = passwordResult.errorMessage
        )

    }
}
