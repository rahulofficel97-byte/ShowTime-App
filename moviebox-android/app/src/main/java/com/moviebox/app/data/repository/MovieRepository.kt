package com.moviebox.app.data.repository

import com.moviebox.app.data.api.MovieBoxApiClient
import com.moviebox.app.data.models.MediaItem
import com.moviebox.app.data.models.MediaType
import com.moviebox.app.data.models.StreamRelease

class MovieRepository(private val apiClient: MovieBoxApiClient = MovieBoxApiClient()) {

    suspend fun getTrending(): List<MediaItem> = apiClient.getTrendingMovies()

    suspend fun getPopularMovies(): List<MediaItem> {
        return apiClient.getTrendingMovies().filter { it.type == MediaType.MOVIE }
    }

    suspend fun getTVSeries(): List<MediaItem> {
        return apiClient.getTrendingMovies().filter { it.type == MediaType.TV_SHOW || it.type == MediaType.ANIME }
    }

    suspend fun search(query: String): List<MediaItem> {
        if (query.isBlank()) return emptyList()
        val all = apiClient.getTrendingMovies()
        return all.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.genres.any { g -> g.contains(query, ignoreCase = true) }
        }
    }

    suspend fun getStreams(mediaId: String): List<StreamRelease> {
        return apiClient.resolveStreamLinks(mediaId)
    }
}
