package com.example.friendupp.di

import com.example.friendupp.model.Response
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser

typealias OneTapSignInResponse = Response<BeginSignInResult>
typealias SignInWithGoogleResponse = Response<Boolean>

interface AuthRepository {
    val isUserAuthenticatedInFirebase : Boolean
    val currentUser: FirebaseUser?

    suspend fun signin(email:String, password :String):  Response<FirebaseUser>

    suspend fun signup(name:String,email:String,password: String): Response<FirebaseUser>

    fun logout()
    fun deleteAuth()
    fun resetPassword(new_password:String)
     suspend fun deleteAccount(id:String)

    suspend fun oneTapSignInWithGoogle(): OneTapSignInResponse


    suspend fun firebaseSignInWithGoogle(googleCredential: AuthCredential): SignInWithGoogleResponse
}
