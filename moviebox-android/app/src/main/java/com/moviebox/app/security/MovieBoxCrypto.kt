package com.moviebox.app.security

import android.util.Base64
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * ⚡ MovieBox Cryptographic Signature & Auth Engine
 * Recreated from Rust crypto.rs
 */
object MovieBoxCrypto {

    fun generateXClientToken(timestamp: Long = System.currentTimeMillis() / 1000): String {
        val tsStr = timestamp.toString()
        val reversedTs = tsStr.reversed()
        val md5Hex = md5Hex(reversedTs.toByteArray(Charsets.UTF_8))
        return "$tsStr,$md5Hex"
    }

    fun signRequest(payload: String): String {
        val secretBytes = ObfuscatedSecrets.getMovieBoxSecretBytes()
        val mac = Mac.getInstance("HmacMD5")
        val secretKey = SecretKeySpec(secretBytes, "HmacMD5")
        mac.init(secretKey)
        val hmacBytes = mac.doFinal(payload.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(hmacBytes, Base64.NO_WRAP)
    }

    private fun md5Hex(input: ByteArray): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input)
        return digest.joinToString("") { "%02x".format(it) }
    }
}
