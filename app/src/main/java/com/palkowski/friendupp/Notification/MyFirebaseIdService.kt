package com.palkowski.friendupp.Notification

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseIdService : FirebaseMessagingService() {
    override fun onNewToken(s: String) {
        super.onNewToken(s)
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            updateToken(s)
        }
    }

    private fun updateToken(s: String) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val reference: DatabaseReference =FirebaseDatabase.getInstance("https://friendupp-3ecc2-default-rtdb.europe-west1.firebasedatabase.app").getReference("Tokens")
        val token = Token(s)
        assert(firebaseUser != null)
        reference.child(firebaseUser!!.uid).setValue(token)
    }
}
