package com.example.friendupp.di

import android.app.Application
import android.content.Context
import com.example.friendupp.BuildConfig.MAPS_API_KEY
import com.example.friendupp.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Named
import javax.inject.Qualifier

//Names

const val SIGN_IN_REQUEST = "signInRequest"
const val SIGN_UP_REQUEST = "signUpRequest"

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FirebaseStorageDefault

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FirebaseStorageRes
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FirebaseStorageHighRes
@Module
@InstallIn(ViewModelComponent::class)
class AppModule {
    @Provides
    fun provideFirebaseAuth() = Firebase.auth

    @Provides
    @FirebaseStorageDefault
    fun provideFirebaseStorage() = Firebase.storage("gs://friendupp-3ecc2.appspot.com")
    @Provides
    @FirebaseStorageRes
    fun provideFirebaseStorageRes() = Firebase.storage("gs://friendupp-3ecc2/")
    @Provides
    @FirebaseStorageHighRes
    fun provideFirebaseStorageHighRes() = Firebase.storage("gs://friendupp-3ecc2-knwji")

    @Provides
    fun provideFirebaseFirestore() = Firebase.firestore

    @Provides
    fun provideOneTapClient(
        @ApplicationContext
        context: Context
    ) = Identity.getSignInClient(context)

    @Provides
    @Named(SIGN_IN_REQUEST)
    fun provideSignInRequest(
        app: Application
    ) = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(app.getString(R.string.default_web_client_id))
                .setFilterByAuthorizedAccounts(true)
                .build())
        .setAutoSelectEnabled(true)
        .build()

    @Provides
    @Named(SIGN_UP_REQUEST)
    fun provideSignUpRequest(
        app: Application
    ) = BeginSignInRequest.builder()
        .setGoogleIdTokenRequestOptions(
            BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                .setSupported(true)
                .setServerClientId(app.getString(R.string.default_web_client_id))
                .setFilterByAuthorizedAccounts(false)
                .build())
        .build()

    @Provides
    fun provideGoogleSignInOptions(
        app: Application
    ) = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(app.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    @Provides
    fun provideGoogleSignInClient(
        app: Application,
        options: GoogleSignInOptions
    ) = GoogleSignIn.getClient(app, options)

    @Provides
    fun provideAuthRepository(
        auth: FirebaseAuth,
        oneTapClient: SignInClient,
        @Named(SIGN_IN_REQUEST)
        signInRequest: BeginSignInRequest,
        @Named(SIGN_UP_REQUEST)
        signUpRequest: BeginSignInRequest,
        db: FirebaseFirestore
    ): AuthRepository = AuthRepositoryImpl(
        auth = auth,
        oneTapClient = oneTapClient,
        signInRequest = signInRequest,
        signUpRequest = signUpRequest,
        db = db
    )
    @Provides
    fun provideChatRepository(
        db: FirebaseFirestore,
        @FirebaseStorageRes resStorage: FirebaseStorage,
        @FirebaseStorageDefault lowResStorage: FirebaseStorage,
    ): ChatRepository = ChatRepositoryImpl(
        chatCollectionsRef =db.collection("groups"),
        messagesRef = db.collection("Chats"),
        resStorage=resStorage.reference,
        lowResStorage= lowResStorage.reference
    )
    @Provides
    fun provideActivityRepository(
        db: FirebaseFirestore,
        @FirebaseStorageRes resStorage: FirebaseStorage,
        @FirebaseStorageDefault lowResStorage: FirebaseStorage
    ):ActivityRepository= ActivityRepositoryImpl(
        activitiesRef =db.collection("Activities"),
        activeUsersRef =db.collection("ActiveUsers"),
        usersRef =db.collection("Users"),
        chatCollectionsRef =db.collection("groups") ,
        messagessRef =db.collection("Chats"),
        resStorage=resStorage.reference,
        lowResStorage = lowResStorage.reference
    )
    @Provides
    fun provideUsersRepository(
        db: FirebaseFirestore,
        @FirebaseStorageDefault storage: FirebaseStorage,
        @FirebaseStorageRes resStorage: FirebaseStorage
    ):UserRepository= UserRepositoryImpl(
        usersRef =db.collection("Users"),
        chatCollectionsRef =db.collection("groups"),
        storageRef =storage.reference,
        resStorage=resStorage.reference
    )
    @Provides
    fun provideProfileRepository(
        auth: FirebaseAuth,
        oneTapClient: SignInClient,
        signInClient: GoogleSignInClient,
        db: FirebaseFirestore
    ): ProfileRepository = ProfileRepositoryImpl(
        auth = auth,
        oneTapClient = oneTapClient,
        signInClient = signInClient,
        db = db
    )
}