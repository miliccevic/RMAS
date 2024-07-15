package com.example.rmas.data

import android.net.Uri
import com.google.firebase.Timestamp
import java.util.Date

data class Location(
    var id: String = "",
    var user: String = "",
    var title: String = "",
    var latitude: Double? = null,
    var longitude: Double? = null,
    var description: String = "",
    var type:Type=Type.OSTALO,
    var image: Uri = Uri.EMPTY,
    var date:Timestamp = Timestamp(Date())
)
 enum class Type{
     RUPA, SAOBRACAJNA_NEZGODA, RAD, ZATVORENA_ULICA, OSTALO
 }