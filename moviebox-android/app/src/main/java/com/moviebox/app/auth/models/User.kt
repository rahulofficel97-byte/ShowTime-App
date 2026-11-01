package com.moviebox.app.auth.models

enum class MembershipTier(val displayName: String, val maxResolution: String, val badgeColor: Long) {
    FREE("Free Member", "720p HD", 0xFF8E8E93),
    VIP("VIP Gold", "1080p FHD", 0xFFFFD700),
    PREMIUM_4K("Ultra 4K VIP", "4K Ultra HD & HDR", 0xFFE50914)
}

data class User(
    val id: String,
    val username: String,
    val email: String,
    val plan: MembershipTier = MembershipTier.FREE,
    val token: String,
    val expiresAt: Long = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000) // 30 days
)
