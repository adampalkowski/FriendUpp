package com.example.friendupp.di

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.friendupp.model.OneTapResponse
import com.example.friendupp.model.Response
import com.example.friendupp.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.friendupp.util.Event
import com.google.android.gms.auth.api.identity.BeginSignInResult

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo : AuthRepository,
    val oneTapClient: SignInClient
): ViewModel() {


    val currentUser :FirebaseUser?
        get()= repo.currentUser


    val isUserAuthenticated get() =repo.isUserAuthenticatedInFirebase

    var oneTapSignInResponse by mutableStateOf<OneTapSignInResponse>(OneTapResponse.Success(null))
        private set
    var signInWithGoogleResponse by mutableStateOf<SignInWithGoogleResponse>(Response.Success(false))
        private set

    fun oneTapSignIn() = viewModelScope.launch {
        oneTapSignInResponse = OneTapResponse.Loading
        oneTapSignInResponse = repo.oneTapSignInWithGoogle()
    }

    fun signInWithGoogle(googleCredential: AuthCredential) = viewModelScope.launch {
        oneTapSignInResponse = OneTapResponse.Loading
        signInWithGoogleResponse = repo.firebaseSignInWithGoogle(googleCredential)
        when(signInWithGoogleResponse ){
            is Response.Success->{
                Log.d("SIGNINWITHGOOGLE","success")

            }
            is Response.Loading->{
                Log.d("SIGNINWITHGOOGLE","LOADING")
            }
            is Response.Failure->{
                    Log.d("SIGNINWITHGOOGLE","failure"+ (signInWithGoogleResponse as Response.Failure).e.message.toString())



            }
        }
    }
    private val _loginFlow= MutableStateFlow<Response<FirebaseUser>?>(null)
    val loginFlow: StateFlow<Response<FirebaseUser>?> = _loginFlow

    private val _signupFlow= MutableStateFlow<Response<FirebaseUser?>?>(null)
    val signupFlow: StateFlow<Response<FirebaseUser?>?> = _signupFlow


    fun signin(email:String,password:String)=viewModelScope.launch {
        _loginFlow.value=Response.Loading
        val result=repo.signin(email, password )
        _loginFlow.value=result
    }
    fun signup(name:String,email:String,password:String)=viewModelScope.launch {
        _signupFlow.value= Response.Loading
        val result=repo.signup(name=name, email = email,password= password )
        _signupFlow.value=result
    }
    fun resetPassword(new_password:String){
        repo.resetPassword(new_password)

    }
    fun resetLoginFlow(){
        _loginFlow.value=null
    }
    fun updateEmail(new_email:String,id:String){
        viewModelScope.launch {
            repo.updateEmail(new_email)
            repo.updateUserEmail(id,new_email)
        }
    }
    fun logout(){
        repo.logout()
        _loginFlow.value=null
        _signupFlow.value=null
    }
    fun deleteAuth(){
        repo.deleteAuth()
        _loginFlow.value=null
        _signupFlow.value=null
    }
     fun deleteAccount(user:User){
         viewModelScope.launch {
            repo.deleteAccount(user.id)
             if(!user.pictureUrl.isNullOrEmpty()){
                 Log.d("DELETEACCOUNT",user.pictureUrl.toString())
                 user.pictureUrl.let {
                     repo.removeUserImage(it!!).collect{

                     }

                 }
             }
         }

    }

}
