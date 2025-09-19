package com.udb.ventaexpress.controller

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.OAuthProvider
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout

class AuthController {
    private val auth = FirebaseAuth.getInstance().apply {
        setLanguageCode("es")
    }

    fun currentUser() = auth.currentUser

    // Email/Contraseña
    suspend fun login(email: String, pass: String) {
        Log.v(TAG, "login(): email=$email")
        val result = withTimeout(20_000L) {
            auth.signInWithEmailAndPassword(email, pass).await()
        }
        Log.v(TAG, "login(): OK uid=${result.user?.uid}")
    }

    suspend fun register(email: String, pass: String) {
        Log.v(TAG, "register(): email=$email")
        val result = withTimeout(20_000L) {
            auth.createUserWithEmailAndPassword(email, pass).await()
        }
        Log.v(TAG, "register(): OK uid=${result.user?.uid}")
    }

    fun logout() {
        Log.v(TAG, "logout()")
        auth.signOut()
    }

    // ---------- GitHub (OAuth) ----------
    suspend fun signInWithGitHub(activity: Activity) {
        val provider = OAuthProvider.newBuilder("github.com").apply {
            scopes = listOf("user:email") // pide email público
            addCustomParameter("allow_signup", "true")
        }.build()


        val pending = auth.pendingAuthResult
        if (pending != null) {
            withTimeout(30_000L) { pending.await() }
            return
        }

        withTimeout(60_000L) {
            auth.startActivityForSignInWithProvider(activity, provider).await()
        }
    }

    // ---------- Facebook ----------
    suspend fun signInWithFacebook(accessToken: String) {
        val credential = FacebookAuthProvider.getCredential(accessToken)
        withTimeout(30_000L) {
            auth.signInWithCredential(credential).await()
        }
    }

    companion object { private const val TAG = "AuthController" }
}
