package com.example.rmas.data

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.rmas.data.rules.Validator
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class LoginViewModel:ViewModel() {

    var loginUIState= mutableStateOf(LoginUIState())
    var allValidationsPassed= mutableStateOf(false)
    fun onEvent(event:LoginUIEvent, context: Context, onClick:()->Unit){
        validateData()
        when(event){
            is LoginUIEvent.UsernameChanged->{
                loginUIState.value=loginUIState.value.copy(
                    username=event.username
                )
            }
            is LoginUIEvent.PasswordChanged->{
                loginUIState.value=loginUIState.value.copy(
                    password =event.password
                )
            }
            is LoginUIEvent.LoginButtonClicked->{
                login(context, onClick={onClick()})
            }
        }
    }
    private fun login(context: Context, onClick: () -> Unit){
        val db= Firebase.firestore
        db.collection("users")
            .document(loginUIState.value.username)
            .get()
            .addOnSuccessListener {
                if(it.exists()) {
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
                                        "Molimo vas potvrdite email.",
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
                }
                else{
                    Toast.makeText(context,"Ne postoji nalog sa unetim korisničkim imenom.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener{
                Toast.makeText(context,it.localizedMessage, Toast.LENGTH_SHORT).show()
            }
    }
    fun logOut(context: Context, onClick: () -> Unit){ //link za home
        val firebaseAuth=FirebaseAuth.getInstance()
        firebaseAuth.signOut()

        if(firebaseAuth.currentUser==null){
            Toast.makeText(context,"Uspešno odjavljivanje.",Toast.LENGTH_SHORT).show()
            onClick.invoke()
        }
    }
    private fun validateData(){
        val usernameResult= Validator.validateUsername(username=loginUIState.value.username)
        val passwordResult= Validator.validatePassword(password=loginUIState.value.password)

        loginUIState.value=loginUIState.value.copy(
            usernameError = usernameResult.status,
            passwordError = passwordResult.status,
        )
        allValidationsPassed.value = (usernameResult.status && passwordResult.status)
    }
}
