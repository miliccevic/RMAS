package com.example.rmas.presentation.validation

import android.net.Uri
import android.util.Patterns
import java.util.regex.Matcher
import java.util.regex.Pattern


object Validator {
    fun validateIme(ime: String): ValidationResult {
        if(ime.isNotEmpty() && ime.length <= 30)
            return ValidationResult(true)
        else
            return ValidationResult(false,"Ime ne sme biti prazno.")
    }

    fun validatePrezime(prezime: String): ValidationResult {
        if(prezime.isNotEmpty() && prezime.length <= 30)
            return ValidationResult(true)
        else
            return ValidationResult(false,"Prezime ne sme biti prazno.")
    }

    fun validateEmail(email: String): ValidationResult {
        if(email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches())
            return ValidationResult(true)
        else
            return ValidationResult(false,"Nevalidna email adresa.")
    }

    fun validatePassword(password: String): ValidationResult {
        if(password.isNotEmpty() && isValidPassword(password))
            return ValidationResult(true)
        else
            return ValidationResult(false,"Šifra mora imati šest karaktera, jedan broj, jedan specijalan karakter i jedno veliko slovo.")
    }

    fun validateUsername(username: String): ValidationResult {
        if(username.isNotEmpty())
            return ValidationResult(true)
        else
            return ValidationResult(false,"Korisničko ime ne sme biti prazno.")
    }

    fun validatePasswordLogin(password: String): ValidationResult {
        if(password.isNotEmpty())
          return ValidationResult(true)
        else
            return ValidationResult(false,"Šifra ne sme biti prazna.")
    }

    fun validateTelefon(telefon: String): ValidationResult {
        if(telefon.isNotEmpty() && isValidPhone(telefon))
            return ValidationResult(true)
        else
            return ValidationResult(false,"Nevalidan broj telefona.")
    }

    fun validateImage(image: Uri): ValidationResult {
        if(image != Uri.EMPTY)
          return ValidationResult(true)
        else
            return ValidationResult(false,"Potrebno je izabrati jednu sliku.")
    }
    fun validateTitle(title:String):ValidationResult{
        if(title.isNotEmpty())
            return ValidationResult(true)
        else
            return ValidationResult(false,"Naslov ne sme biti prazan.")
    }
    fun validateDescription(description:String):ValidationResult{
        if(description.isNotEmpty())
            return ValidationResult(true)
        else
            return ValidationResult(false,"Opis ne sme biti prazan.")
    }

    private fun isValidPassword(password: String): Boolean {
        val pattern: Pattern
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        val matcher: Matcher = pattern.matcher(password)
        return matcher.matches()
    }

    private fun isValidPhone(telefon: String): Boolean {
        val pattern: Pattern
        val PATTERN = "^(\\+3816|06)(([0-6]|[8-9])\\d{6,7}|(77|78)\\d{5,6})$"
        pattern = Pattern.compile(PATTERN)
        val matcher: Matcher = pattern.matcher(telefon)
        return matcher.matches()
    }
}

data class ValidationResult(
    val status: Boolean = false,
    val errorMessage:String?=null
)