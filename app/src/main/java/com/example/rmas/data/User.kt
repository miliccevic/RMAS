package com.example.rmas.data

import android.net.Uri
import com.google.firebase.firestore.DocumentId

data class User(
    var id:String="",
    var username:String="",
    var ime:String="",
    var prezime:String="",
    var telefon:String="",
    var image:String="",
    var email:String="",
    var points:Long=0L
)