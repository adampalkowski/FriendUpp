package com.palkowski.friendupp.di

import android.util.Log
import com.palkowski.friendupp.Components.getCurrentDateTime
import com.palkowski.friendupp.model.OneTapResponse
import com.palkowski.friendupp.model.Response
import com.palkowski.friendupp.model.SocialException
import com.palkowski.friendupp.model.User
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

const val DEFAULT_PROFILE_PICTURE_URL="https://firebasestorage.googleapis.com/v0/b/socialv2-340711.appspot.com/o/default.png?alt=media&token=2a56c977-4809-4a27-9e4b-fbd1f625283e"

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private var oneTapClient: SignInClient,
    @Named(SIGN_IN_REQUEST)
    private var signInRequest: BeginSignInRequest,
    @Named(SIGN_UP_REQUEST)
    private var signUpRequest: BeginSignInRequest,
    private val db: FirebaseFirestore,
    private val storageRef:StorageReference
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
    ): Response<FirebaseUser?> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result?.user?.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(name).build()
            )
            val isNewUser = result.additionalUserInfo?.isNewUser ?: false

            if (isNewUser) {
                addUserToFirestore(name)
                Response.Success(auth.currentUser)
            } else {
                // User already exists in Firebase Authentication
                val firestoreUser = getUserFromFirestore()
                if (firestoreUser != null) {
                    Response.Success(auth.currentUser)
                } else {
                    // User doesn't exist in Firestore, add them now
                    addUserToFirestore(name)
                    Response.Success(auth.currentUser)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Response.Failure(SocialException("Account already exists", e))
        }
    }

    private suspend fun addUserToFirestore(name: String?) {

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
                tags = ArrayList(),
                accountCreateTime = getCurrentDateTime(),
                geoHash = ""
            , friends_ids_list = ArrayList()
            )
            db.collection("Users").document(uid).set(user).await()
        }
    }

    private suspend fun getUserFromFirestore(): User? {
        return try {
            val snapshot = db.collection("Users").document(auth.currentUser?.uid ?: "").get().await()
            snapshot.toObject(User::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    override fun logout() {
        auth.signOut()
    }
    override fun updateEmail(newEmail:String){
        auth.currentUser?.updateEmail(newEmail)
    }
    override suspend fun updateUserEmail(id:String,email:String) {

        db.collection("Users").document(id).update("email",email).await()
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
            Log.d("ONETAP","123")
            val signInResult = oneTapClient.beginSignIn(signInRequest).await()
            Log.d("ONETAP",signInResult.toString())
            OneTapResponse.Success(signInResult)
        } catch (e: Exception) {
            Log.d("ONETAP","exception"+e.message.toString())

            try {
                Log.d("ONETAP","exception"+e.message.toString())

                val signUpResult = oneTapClient.beginSignIn(signUpRequest).await()
                OneTapResponse.Success(signUpResult)
            } catch (e: Exception) {
                OneTapResponse.Failure(SocialException("oneTapSignInWithGoogle error",e))
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
                addUserToFirestore(authResult.user!!.displayName)
            }

            Response.Success(authResult.user!!)
        } catch (e: Exception) {
            Response.Failure(SocialException("firebaseSignInWithGoogle error",e))
        }
    }

    override suspend fun removeUserImage(
        url: String,
    ): kotlinx.coroutines.flow.Flow<Response<Boolean>> = flow {
        Log.d("ActivityRepositoryImpl",url)
        try {
            Log.d("ActivityRepositoryImpl",url)
            val reference=storageRef.storage.getReferenceFromUrl(url)
            Log.d("ActivityRepositoryImpl",reference.toString())
            reference.delete().await()

            emit(Response.Success(true))

        } catch (e: Exception) {
            Log.d("ActivityRepositoryImpl", "try addProfilePictureToStorage EXCEPTION"+e.toString())
            emit(
                Response.Failure(
                    e = SocialException(
                        "addProfilePictureToStorage exception",
                        Exception()
                    )
                )
            )
        }
    }

}

