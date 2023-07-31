package com.palkowski.friendupp.di

import com.palkowski.friendupp.model.OneTapResponse
import com.palkowski.friendupp.model.Response
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser

typealias OneTapSignInResponse = OneTapResponse<BeginSignInResult>
typealias SignInWithGoogleResponse = Response<Boolean>

interface AuthRepository {
    val isUserAuthenticatedInFirebase : Boolean
    val currentUser: FirebaseUser?

    suspend fun signin(email:String, password :String):  Response<FirebaseUser>

    suspend fun signup(name:String,email:String,password: String): Response<FirebaseUser?>

    fun logout()
    fun updateEmail(new_email:String)
    suspend fun  updateUserEmail(id:String,email:String)
    fun deleteAuth()
    fun resetPassword(new_password:String)
     suspend fun deleteAccount(id:String)

    suspend fun oneTapSignInWithGoogle(): OneTapSignInResponse
     suspend fun removeUserImage( url: String ): kotlinx.coroutines.flow.Flow<Response<Boolean>>
    suspend fun firebaseSignInWithGoogle(googleCredential: AuthCredential): SignInWithGoogleResponse
}
