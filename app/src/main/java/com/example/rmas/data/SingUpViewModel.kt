package com.example.rmas.data

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.rmas.data.rules.Validator
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage

class SingUpViewModel : ViewModel(){
    //stanje unetih polja
    var singUpUIState= mutableStateOf(SingUpUIState())
    var allValidationsPassed= mutableStateOf(false)
    fun onEvent(event:SingUpUIEvent, context: Context, onClick:()->Unit){
        validateData()
        when(event){
            is SingUpUIEvent.ImeChanged->{
                singUpUIState.value=singUpUIState.value.copy(
                    ime=event.ime
                )
            }
            is SingUpUIEvent.PrezimeChanged->{
                singUpUIState.value=singUpUIState.value.copy(
                    prezime=event.prezime
                )
            }
            is SingUpUIEvent.EmailChanged->{
                singUpUIState.value=singUpUIState.value.copy(
                    email=event.email
                )
            }
            is SingUpUIEvent.TelefonChanged->{
                singUpUIState.value=singUpUIState.value.copy(
                    telefon=event.telefon
                )
            }
            is SingUpUIEvent.UsernameChanged->{
                singUpUIState.value=singUpUIState.value.copy(
                    username = event.username
                )
            }
            is SingUpUIEvent.PasswordChanged->{
                singUpUIState.value=singUpUIState.value.copy(
                    password =event.password
                )
            }
            is SingUpUIEvent.RegisterButtonClicked->{
                singUp(context, onClick={onClick()})
            }
            is SingUpUIEvent.ImageChanged->{
                singUpUIState.value=singUpUIState.value.copy(
                    image = event.image
                )
            }
        }
    }
    private fun singUp(context: Context, onClick: () -> Unit){
        createUser(email=singUpUIState.value.email, password=singUpUIState.value.password, context, onClick={onClick()})
    }
    private fun createUser(email:String, password:String, context: Context, onClick: () -> Unit){
        val db= Firebase.firestore /*TODO za email*/
        db.collection("users")
            .document(singUpUIState.value.username)
            .get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val document: DocumentSnapshot = it.result
                    if (document.exists()) {
                        Toast.makeText(context, "Uneto korisničko ime već postoji.",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener {res->
                                if (res.isSuccessful) {
                                    FirebaseAuth.getInstance().currentUser?.sendEmailVerification()
                                        ?.addOnCompleteListener {
                                            Toast.makeText(
                                                context,
                                                "Email verification link je poslat na vašu email adresu.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        ?.addOnFailureListener {
                                            Toast.makeText(context, "Greška prilikom pribavljanja current usera.", Toast.LENGTH_SHORT).show()
                                        }
                                    addToDB(
                                        ime = singUpUIState.value.ime,
                                        prezime = singUpUIState.value.prezime,
                                        email,
                                        username = singUpUIState.value.username,
                                        telefon = singUpUIState.value.telefon,
                                        image=singUpUIState.value.image
                                    )
                                    onClick.invoke()
                                }
                            }
                            .addOnFailureListener { ex->
                                Toast.makeText(context, ex.localizedMessage, Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }
            .addOnFailureListener{
                Toast.makeText(context,it.localizedMessage,Toast.LENGTH_SHORT).show()
            }
    }
    private fun addToDB(ime:String,prezime:String,email: String,username:String,telefon:String, image: Uri){
        val db=Firebase.firestore
        val storage=FirebaseStorage.getInstance().reference.child("Images")
            .child(System.currentTimeMillis().toString())
        var url: String
        storage.putFile(image)
            .addOnCompleteListener{
                if(it.isSuccessful){
                    Log.d("TAG","uspelo")
                    storage.downloadUrl
                        .addOnCompleteListener{ it1->
                            if(it1.isSuccessful) {
                                url = it1.result.toString()
                                val user= hashMapOf(
                                    "ime" to ime,
                                    "prezime" to prezime,
                                    "email" to email,
                                    "username" to username,
                                    "telefon" to telefon,
                                    "slika" to url
                                )
                                db.collection("users")
                                    .document(username)
                                    .set(user)
                                    .addOnCompleteListener {
                                        /*TODO*/
                                    }
                                    .addOnFailureListener {

                                    }
                                Log.d("TAG4", "proslo")
                                Log.d("TAG5",url)
                            }
                        }
                        .addOnFailureListener{ ex->
                            Log.d("TAG3",ex.toString())
                        }
                }
            }
            .addOnFailureListener {
                Log.d("TAG2",it.toString())
            }
    }
    private fun validateData(){
        val imeResult= Validator.validateIme(ime=singUpUIState.value.ime)
        val prezimeResult=Validator.validatePrezime(prezime=singUpUIState.value.prezime)
        val emailResult=Validator.validateEmail(email=singUpUIState.value.email)
        val usernameResult=Validator.validateUsername(username=singUpUIState.value.username)
        val passwordResult=Validator.validatePassword(password=singUpUIState.value.password)
        val telefonResult=Validator.validateTelefon(telefon=singUpUIState.value.telefon)
        val imageResult=Validator.validateImage(image=singUpUIState.value.image)

        singUpUIState.value=singUpUIState.value.copy(
            imeError = imeResult.status,
            prezimeError = prezimeResult.status,
            emailError = emailResult.status,
            passwordError = passwordResult.status,
            usernameError = usernameResult.status,
            telefonError = telefonResult.status,
            imageError = imageResult.status
        )
        allValidationsPassed.value = (imeResult.status && prezimeResult.status && emailResult.status && passwordResult.status
                && telefonResult.status && usernameResult.status && imageResult.status)
    }
}