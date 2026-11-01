package com.moviebox.app.data.models

enum class MediaType(val label: String) {
    MOVIE("Movie"),
    TV_SHOW("TV Series"),
    ANIME("Anime"),
    LIVE_TV("Live TV")
}

data class MediaItem(
    val id: String,
    val title: String,
    val posterUrl: String,
    val backdropUrl: String? = null,
    val rating: String = "8.5",
    val year: String = "2025",
    val type: MediaType = MediaType.MOVIE,
    val genres: List<String> = listOf("Action", "Sci-Fi"),
    val overview: String = "",
    val duration: String = "2h 15m",
    val seasonsCount: Int = 1
)

data class StreamRelease(
    val quality: String, // "4K UHD", "1080p FHD", "720p HD", "Auto"
    val streamUrl: String,
    val isChunkedHls: Boolean = true,
    val isDash: Boolean = false,
    val requiresVip: Boolean = false, // 4K can be reserved for VIP/Premium!
    val headers: Map<String, String> = emptyMap()
)

data class EpisodeItem(
    val episodeId: String,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val title: String,
    val thumbnail: String? = null
)
