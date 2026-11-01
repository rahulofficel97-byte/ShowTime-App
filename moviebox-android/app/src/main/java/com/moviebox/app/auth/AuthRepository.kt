package com.moviebox.app.auth

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.moviebox.app.auth.models.MembershipTier
import com.moviebox.app.auth.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * 🔐 Unified Auth & Firebase Cloud Firestore Repository
 * Handles Google Auth, Email/Password, Guest Access, and Cloud Sync for Plans & History
 */
class AuthRepository(context: Context) {

    private val tokenManager = TokenManager(context)
    private val _currentUser = MutableStateFlow<User?>(tokenManager.getUser())
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Firebase Singletons (with safe lazy fallback)
    private val firebaseAuth: FirebaseAuth? by lazy {
        try { FirebaseAuth.getInstance() } catch (_: Exception) { null }
    }

    private val firestore: FirebaseFirestore? by lazy {
        try { FirebaseFirestore.getInstance() } catch (_: Exception) { null }
    }

    /**
     * 🌐 Google Sign-In Integration
     */
    suspend fun signInWithGoogle(idToken: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val auth = firebaseAuth ?: return@withContext localFallbackAuth("google_user@gmail.com", "Google Streamer")
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val fbUser = authResult.user ?: throw Exception("Google user is null.")

            val user = syncUserWithFirestore(
                uid = fbUser.uid,
                email = fbUser.email ?: "google_user@gmail.com",
                name = fbUser.displayName ?: "Google User"
            )
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * ✉️ Email / Password Sign In with Firebase & Local Cache
     */
    suspend fun login(email: String, pass: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val auth = firebaseAuth
            if (auth != null) {
                val res = auth.signInWithEmailAndPassword(email, pass).await()
                val fbUser = res.user ?: throw Exception("Login failed.")
                val user = syncUserWithFirestore(
                    uid = fbUser.uid,
                    email = fbUser.email ?: email,
                    name = fbUser.displayName ?: email.substringBefore("@")
                )
                Result.success(user)
            } else {
                localFallbackAuth(email, email.substringBefore("@"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 📝 Email / Password Registration with Firestore DB Profile Setup
     */
    suspend fun register(username: String, email: String, pass: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val auth = firebaseAuth
            if (auth != null) {
                val res = auth.createUserWithEmailAndPassword(email, pass).await()
                val fbUser = res.user ?: throw Exception("Registration failed.")
                val user = syncUserWithFirestore(
                    uid = fbUser.uid,
                    email = email,
                    name = username
                )
                Result.success(user)
            } else {
                localFallbackAuth(email, username)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * ⚡ Anonymous / One-Tap Guest Access
     */
    fun continueAsGuest(): User {
        val guestUser = User(
            id = "guest_" + UUID.randomUUID().toString().take(8),
            username = "Guest Movie Fan",
            email = "guest@moviebox.local",
            plan = MembershipTier.FREE,
            token = "mb_guest_token"
        )
        tokenManager.saveUser(guestUser)
        _currentUser.value = guestUser

        // Also create anonymous account in Firebase if available
        try {
            firebaseAuth?.signInAnonymously()
        } catch (_: Exception) {}

        return guestUser
    }

    /**
     * 👑 Upgrade Membership Plan (Syncs immediately with Firestore Database)
     */
    suspend fun upgradePlan(tier: MembershipTier) = withContext(Dispatchers.IO) {
        val current = _currentUser.value ?: return@withContext
        val updated = current.copy(plan = tier)
        tokenManager.saveUser(updated)
        _currentUser.value = updated

        // Push to Firestore Database
        try {
            firestore?.collection("users")?.document(current.id)?.set(
                mapOf(
                    "plan" to tier.name,
                    "updatedAt" to System.currentTimeMillis()
                ),
                SetOptions.merge()
            )?.await()
        } catch (_: Exception) {}
    }

    /**
     * 🔄 Syncs or pulls User from Firebase Cloud Firestore Database
     */
    private suspend fun syncUserWithFirestore(uid: String, email: String, name: String): User {
        var plan = MembershipTier.FREE
        try {
            val doc = firestore?.collection("users")?.document(uid)?.get()?.await()
            if (doc != null && doc.exists()) {
                val savedPlan = doc.getString("plan")
                if (savedPlan != null) {
                    plan = try { MembershipTier.valueOf(savedPlan) } catch (_: Exception) { MembershipTier.FREE }
                }
            } else {
                // Initialize default user document in Firestore
                firestore?.collection("users")?.document(uid)?.set(
                    mapOf(
                        "uid" to uid,
                        "email" to email,
                        "displayName" to name,
                        "plan" to MembershipTier.FREE.name,
                        "createdAt" to System.currentTimeMillis()
                    )
                )?.await()
            }
        } catch (_: Exception) {}

        val user = User(
            id = uid,
            username = name,
            email = email,
            plan = plan,
            token = "fb_tok_" + UUID.randomUUID().toString().replace("-", "")
        )
        tokenManager.saveUser(user)
        _currentUser.value = user
        return user
    }

    private fun localFallbackAuth(email: String, name: String): Result<User> {
        val user = User(
            id = UUID.randomUUID().toString(),
            username = name.replaceFirstChar { it.uppercase() },
            email = email,
            plan = MembershipTier.FREE,
            token = "mb_sec_" + UUID.randomUUID().toString().take(12)
        )
        tokenManager.saveUser(user)
        _currentUser.value = user
        return Result.success(user)
    }

    fun logout() {
        try { firebaseAuth?.signOut() } catch (_: Exception) {}
        tokenManager.clear()
        _currentUser.value = null
    }

    fun isLoggedIn(): Boolean = _currentUser.value != null
}
