package com.moviebox.app.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.moviebox.app.auth.models.MembershipTier
import com.moviebox.app.auth.models.User

/**
 * 🔐 Hardware-Backed Secure Storage for User Tokens and Membership Info
 */
class TokenManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_moviebox_auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveUser(user: User) {
        prefs.edit()
            .putString(KEY_USER_ID, user.id)
            .putString(KEY_USERNAME, user.username)
            .putString(KEY_EMAIL, user.email)
            .putString(KEY_PLAN, user.plan.name)
            .putString(KEY_TOKEN, user.token)
            .putLong(KEY_EXPIRES, user.expiresAt)
            .apply()
    }

    fun getUser(): User? {
        val token = prefs.getString(KEY_TOKEN, null) ?: return null
        val id = prefs.getString(KEY_USER_ID, "guest") ?: "guest"
        val username = prefs.getString(KEY_USERNAME, "MovieFan") ?: "MovieFan"
        val email = prefs.getString(KEY_EMAIL, "guest@moviebox.app") ?: ""
        val planName = prefs.getString(KEY_PLAN, MembershipTier.FREE.name)
        val plan = try {
            MembershipTier.valueOf(planName ?: MembershipTier.FREE.name)
        } catch (_: Exception) {
            MembershipTier.FREE
        }
        val expires = prefs.getLong(KEY_EXPIRES, 0L)

        return User(id = id, username = username, email = email, plan = plan, token = token, expiresAt = expires)
    }

    fun updateMembership(tier: MembershipTier) {
        prefs.edit().putString(KEY_PLAN, tier.name).apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean = prefs.getString(KEY_TOKEN, null) != null

    companion object {
        private const val KEY_USER_ID = "auth_uid"
        private const val KEY_USERNAME = "auth_username"
        private const val KEY_EMAIL = "auth_email"
        private const val KEY_PLAN = "auth_plan"
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_EXPIRES = "auth_expires"
    }
}
