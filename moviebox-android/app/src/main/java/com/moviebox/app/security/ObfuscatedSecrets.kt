package com.moviebox.app.security

/**
 * 🔒 Obfuscated Secret Store
 *
 * Prevents strings and HMAC signing bytes from being dumped via decompiler string search.
 * Uses dynamic XOR de-masking at runtime.
 */
object ObfuscatedSecrets {

    private const val XOR_KEY: Byte = 0x5A

    // Masked representation of MovieBox default HMAC-MD5 signing bytes:
    // b"\xef\xa8\x91\x97\x4e\xec\xd3\x14\x8d\xf6\x3a\xa6\x11\x60\x2d\xef\xd1\x01\x25\x9b\xa5\x21\x02\x2c\x57\xae\x05\x66\xbd\x8e"
    private val MASKED_HMAC_SECRET = byteArrayOf(
        (0xef xor 0x5a).toByte(), (0xa8 xor 0x5a).toByte(), (0x91 xor 0x5a).toByte(), (0x97 xor 0x5a).toByte(),
        (0x4e xor 0x5a).toByte(), (0xec xor 0x5a).toByte(), (0xd3 xor 0x5a).toByte(), (0x14 xor 0x5a).toByte(),
        (0x8d xor 0x5a).toByte(), (0xf6 xor 0x5a).toByte(), (0x3a xor 0x5a).toByte(), (0xa6 xor 0x5a).toByte(),
        (0x11 xor 0x5a).toByte(), (0x60 xor 0x5a).toByte(), (0x2d xor 0x5a).toByte(), (0xef xor 0x5a).toByte(),
        (0xd1 xor 0x5a).toByte(), (0x01 xor 0x5a).toByte(), (0x25 xor 0x5a).toByte(), (0x9b xor 0x5a).toByte(),
        (0xa5 xor 0x5a).toByte(), (0x21 xor 0x5a).toByte(), (0x02 xor 0x5a).toByte(), (0x2c xor 0x5a).toByte(),
        (0x57 xor 0x5a).toByte(), (0xae xor 0x5a).toByte(), (0x05 xor 0x5a).toByte(), (0x66 xor 0x5a).toByte(),
        (0xbd xor 0x5a).toByte(), (0x8e xor 0x5a).toByte()
    )

    fun getMovieBoxSecretBytes(): ByteArray {
        val result = ByteArray(MASKED_HMAC_SECRET.size)
        for (i in MASKED_HMAC_SECRET.indices) {
            result[i] = (MASKED_HMAC_SECRET[i].toInt() xor XOR_KEY.toInt()).toByte()
        }
        return result
    }

    fun getAppId(): String = "com.moviebox.app"
}
