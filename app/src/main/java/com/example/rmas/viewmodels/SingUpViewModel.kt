package com.example.rmas.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.rmas.data.User
import com.example.rmas.presentation.singup.SingUpUIEvent
import com.example.rmas.presentation.singup.SingUpUIState
import com.example.rmas.presentation.validation.Validator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.Firebase

class SingUpViewModel : ViewModel() {
    //stanje unetih polja
    var singUpUIState = mutableStateOf(SingUpUIState())
    private var allValidationsPassed = mutableStateOf(false)
    fun onEvent(event: SingUpUIEvent, context: Context, navigateToLogin: () -> Unit) {
        when (event) {
            is SingUpUIEvent.ImeChanged -> {
                singUpUIState.value = singUpUIState.value.copy(
                    ime = event.ime
                )
            }

            is SingUpUIEvent.PrezimeChanged -> {
                singUpUIState.value = singUpUIState.value.copy(
                    prezime = event.prezime
                )
            }

            is SingUpUIEvent.EmailChanged -> {
                singUpUIState.value = singUpUIState.value.copy(
                    email = event.email
                )
            }

            is SingUpUIEvent.TelefonChanged -> {
                singUpUIState.value = singUpUIState.value.copy(
                    telefon = event.telefon
                )
            }

            is SingUpUIEvent.UsernameChanged -> {
                singUpUIState.value = singUpUIState.value.copy(
                    username = event.username
                )
            }

            is SingUpUIEvent.PasswordChanged -> {
                singUpUIState.value = singUpUIState.value.copy(
                    password = event.password
                )
            }

            is SingUpUIEvent.RegisterButtonClicked -> {
                singUp(context, navigateToLogin = { navigateToLogin() })
            }

            is SingUpUIEvent.ImageChanged -> {
                singUpUIState.value = singUpUIState.value.copy(
                    image = event.image
                )
            }
        }
    }

    private fun singUp(context: Context, navigateToLogin: () -> Unit) {
        validateData()
        if (!allValidationsPassed.value)
            return
        createUser(
            context,
            navigateToLogin = { navigateToLogin() })
    }

    private fun createUser(context: Context, navigateToLogin: () -> Unit) {
        val db = Firebase.firestore
        db.collection("users").whereEqualTo("username", singUpUIState.value.username).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    if(it.result.isEmpty) {
                        FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(
                                singUpUIState.value.email,
                                singUpUIState.value.password
                            )
                            .addOnCompleteListener { it ->
                                if (it.isSuccessful) {
                                    FirebaseAuth.getInstance().currentUser!!.sendEmailVerification()
                                        .addOnCompleteListener {
                                            addToDatabase(
                                                context,
                                                ime = singUpUIState.value.ime,
                                                prezime = singUpUIState.value.prezime,
                                                email = singUpUIState.value.email,
                                                username = singUpUIState.value.username,
                                                telefon = singUpUIState.value.telefon,
                                                image = singUpUIState.value.image,
                                                onNavigate = { navigateToLogin() }
                                            )
                                        }
                                        .addOnFailureListener { ex ->
                                            Toast.makeText(
                                                context,
                                                "Došlo je do greške prilikom kreiranja naloga.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            FirebaseAuth.getInstance().currentUser!!.delete()
                                            Log.d("TAG", ex.localizedMessage)
                                        }
                                }
                            }
                            .addOnFailureListener { ex ->
                                Toast.makeText(
                                    context,
                                    "Došlo je do greške prilikom kreiranja naloga.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Log.d("TAG", ex.localizedMessage)
                            }
                    }
                    else{
                        Toast.makeText(context,"Uneto korisničko ime već postoji",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(
                    context,
                    "Došlo je do greške prilikom kreiranja naloga.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("TAG", it.localizedMessage)
            }

    }

    private fun addToDatabase(
        context: Context,
        ime: String,
        prezime: String,
        email: String,
        username: String,
        telefon: String,
        image: Uri,
        onNavigate: () -> Unit
    ) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val db = Firebase.firestore
        val storage = FirebaseStorage.getInstance().reference.child("slike_korisnika")
            .child(uid)
        var url: String
        storage.putFile(image)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    storage.downloadUrl
                        .addOnCompleteListener { it1 ->
                            if (it1.isSuccessful) {
                                url = it1.result.toString()
                                val user = User(username, ime, prezime, telefon, url, email, 0L)
                                db.collection("users")
                                    .document(uid)
                                    .set(user)
                                    .addOnFailureListener { ex ->
                                        Log.d("TAG", ex.localizedMessage)
                                        currentUser?.delete()
                                        FirebaseStorage.getInstance().reference.child("slike_korisnika")
                                            .child(uid).delete()
                                        Toast.makeText(
                                            context,
                                            "Došlo je do greške prilikom kreiranja naloga.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .addOnCompleteListener { res ->
                                        if (res.isSuccessful) {
                                            Toast.makeText(
                                                context,
                                                "Email verification link je poslat na vašu email adresu.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            FirebaseAuth.getInstance().signOut()
                                            onNavigate.invoke()
                                        }
                                    }
                            }
                        }
                        .addOnFailureListener { e ->
                            currentUser?.delete()
                            Toast.makeText(
                                context,
                                "Došlo je do greške prilikom kreiranja naloga.",
                                Toast.LENGTH_SHORT
                            ).show()
                            Log.d("TAG", e.localizedMessage)
                        }
                }
            }
            .addOnFailureListener {
                currentUser?.delete()
                Toast.makeText(
                    context,
                    "Došlo je do greške prilikom kreiranja naloga.",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("TAG", it.localizedMessage)
            }
    }

    private fun validateData() {
        val imeResult = Validator.validateIme(ime = singUpUIState.value.ime)
        val prezimeResult = Validator.validatePrezime(prezime = singUpUIState.value.prezime)
        val emailResult = Validator.validateEmail(email = singUpUIState.value.email)
        val usernameResult = Validator.validateUsername(username = singUpUIState.value.username)
        val passwordResult = Validator.validatePassword(password = singUpUIState.value.password)
        val telefonResult = Validator.validateTelefon(telefon = singUpUIState.value.telefon)
        val imageResult = Validator.validateImage(image = singUpUIState.value.image)

        allValidationsPassed.value =
            (imeResult.status && prezimeResult.status && emailResult.status && passwordResult.status
                    && telefonResult.status && usernameResult.status && imageResult.status)

        singUpUIState.value = singUpUIState.value.copy(
            imeError = imeResult.errorMessage,
            prezimeError = prezimeResult.errorMessage,
            emailError = emailResult.errorMessage,
            passwordError = passwordResult.errorMessage,
            usernameError = usernameResult.errorMessage,
            telefonError = telefonResult.errorMessage,
            imageError = imageResult.errorMessage
        )

    }
}