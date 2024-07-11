package com.example.rmas.data.rules

import android.net.Uri


object Validator {
    fun validateIme(ime:String):ValidationResult{
        return ValidationResult(
            (!ime.isNullOrEmpty() && ime.length>=6 && ime.length<=30)
        )
    }
    fun validatePrezime(prezime:String):ValidationResult{
        return ValidationResult(
            (!prezime.isNullOrEmpty() && prezime.length>=4 && prezime.length<=30)
        )
    }
    fun validateEmail(email:String) :ValidationResult{ /*TODO doraditi*/
        return ValidationResult(
            (!email.isNullOrEmpty())
        )
    }
    fun validatePassword(password:String):ValidationResult { /*TODO doraditi*/
        return ValidationResult(
            (!password.isNullOrEmpty() && password.length>=6)
        )
    }
    fun validateUsername(username:String) :ValidationResult{
        return ValidationResult(
            (!username.isNullOrEmpty())
        )
    }
    fun validateTelefon(telefon:String):ValidationResult{ /*TODO doraditi*/
        return ValidationResult(
            (!telefon.isNullOrEmpty())
        )
    }
    fun validateImage(image: Uri):ValidationResult{
        return ValidationResult(
            (image!=Uri.EMPTY)
        )
    }
}
data class ValidationResult(
    val status:Boolean=false
)