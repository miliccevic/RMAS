package com.example.rmas.data

import android.net.Uri
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint
import java.util.Date

data class Location(
    var userId: String = "",
    var title: String = "",
    var location:GeoPoint=GeoPoint(0.0,0.0),
    var description: String = "",
    var type:String="",
    var image: String="",
    var date:Timestamp = Timestamp(Date())
)