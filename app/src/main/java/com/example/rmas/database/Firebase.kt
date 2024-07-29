package com.example.rmas.database

import com.example.rmas.data.Location
import com.example.rmas.data.User
import com.example.rmas.presentation.filter.FilterUIState
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject

object Firebase {
    fun getLocations(listener: (List<Location>) -> Unit) {
        val db = Firebase.firestore
        var locations: MutableList<Location>
        db.collection("locations")
            .addSnapshotListener { snap, _ ->
                if(snap!=null){
                    locations=snap.toObjects(Location::class.java)
                    listener(locations)
                }
            }
    }

    fun getUser(uid: String, listener: (User?) -> Unit) {
        val db = Firebase.firestore
        db.collection("users").document(uid).get()
            .addOnSuccessListener {
                if (it.exists())
                    listener(it.toObject(User::class.java))
            }
    }

    fun getAllUsers(listener: (List<User>) -> Unit) {
        val db = Firebase.firestore
        var users: MutableList<User>
        db.collection("users").orderBy("points")
            .addSnapshotListener { snap, _ ->
                if(snap!=null){
                    users=snap.toObjects(User::class.java)
                    listener(users)
                }
            }
    }
}