package com.example.friendupp.di

import android.util.Log
import com.example.friendupp.model.Response
import com.example.friendupp.model.SocialException
import com.example.friendupp.model.User
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
const val DEFAULT_PROFILE_PICTURE_URL=" https://firebasestorage.googleapis.com/v0/b/socialv2-340711.appspot.com/o/default.png?alt=media&token=2a56c977-4809-4a27-9e4b-fbd1f625283e"

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private var oneTapClient: SignInClient,
    @Named(SIGN_IN_REQUEST)
    private var signInRequest: BeginSignInRequest,
    @Named(SIGN_UP_REQUEST)
    private var signUpRequest: BeginSignInRequest,
    private val db: FirebaseFirestore
) : AuthRepository {
    override val isUserAuthenticatedInFirebase = auth.currentUser != null
    override val currentUser: FirebaseUser?
        get() = auth.currentUser
    override suspend fun signin(email: String, password: String): Response<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Log.d("TAG","SIGNIN")

            Response.Success(result.user!!)
        } catch (e: Exception) {
            e.printStackTrace()
            Response.Failure(SocialException("signIn error",e))
        }
    }

    override suspend fun signup(
        name: String,
        email: String,
        password: String
    ): Response<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result?.user?.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(name).build()
            )
            val isNewUser = result.additionalUserInfo?.isNewUser ?: false
            if (isNewUser) {
                addUserToFirestore(name)
                Response.Success(auth.currentUser)
            }
            Response.Success(auth.currentUser!!)
        } catch (e: Exception) {
            e.printStackTrace()
            Response.Failure(SocialException("signup error",e))
        }

    }

    override fun logout() {
        auth.signOut()
    }

    override  suspend fun deleteAccount(id:String) {

            db.collection("Users").document(id).delete().await()
    }
    override fun deleteAuth() {
        auth.currentUser?.delete()
    }
    override fun resetPassword(new_password:String) {
        auth.currentUser?.updatePassword(new_password)
    }
    override suspend fun oneTapSignInWithGoogle(): OneTapSignInResponse {
        return try {
            val signInResult = oneTapClient.beginSignIn(signInRequest).await()
            Response.Success(signInResult)

        } catch (e: Exception) {
            try {
                Log.d("looooo,", auth.toString())
                val signUpResult = oneTapClient.beginSignIn(signUpRequest).await()
                Log.d("looooo,", "s")
                Response.Success(signUpResult)
            } catch (e: Exception) {
                Log.d("looooo,", "exep" + e.toString())
                Response.Failure(SocialException("oneTapSignInWithGoogle error",e))
            }
        }
    }

    override suspend fun firebaseSignInWithGoogle(
        googleCredential: AuthCredential
    ): SignInWithGoogleResponse {
        return try {
            val authResult = auth.signInWithCredential(googleCredential).await()
            val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
            if (isNewUser) {
                addUserToFirestore(null)
            }
            Response.Success(true)
        } catch (e: Exception) {
            Response.Failure(SocialException("firebaseSignInWithGoogle error",e))
        }
    }

    private suspend fun addUserToFirestore(name:String?) {
        auth.currentUser?.apply {
            val user = User(
                name = name,
                email = this.email,
                id = uid,
                pictureUrl = DEFAULT_PROFILE_PICTURE_URL,
                username = null,
                biography = "",
                location = "",
                blocked_ids = ArrayList(),
                friends_ids = HashMap(),
                invited_ids = ArrayList(),
                user_requests = ArrayList(),
                activities = ArrayList(),
                activitiesCreated = 0,
                usersReached = 0,
                tags = ArrayList()
            )
            db.collection("Users").document(uid).set(user).await()
        }
    }

}

