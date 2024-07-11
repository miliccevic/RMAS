package com.example.rmas.data

sealed class LoginUIEvent {
    data class UsernameChanged(val username:String):LoginUIEvent()
    data class PasswordChanged(val password:String):LoginUIEvent()
    object LoginButtonClicked: LoginUIEvent()
}