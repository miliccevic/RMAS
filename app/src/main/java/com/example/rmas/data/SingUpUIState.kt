package com.example.rmas.data

import android.net.Uri

data class SingUpUIState(
    var ime:String="",
    var prezime:String="",
    var email:String="",
    var username:String="",
    var telefon:String="",
    var password:String="",
    var image: Uri =Uri.EMPTY,

    var imeError:Boolean=false,
    var prezimeError:Boolean=false,
    var emailError:Boolean=false,
    var usernameError:Boolean=false,
    var telefonError:Boolean=false,
    var passwordError:Boolean=false,
    var imageError:Boolean=false
)