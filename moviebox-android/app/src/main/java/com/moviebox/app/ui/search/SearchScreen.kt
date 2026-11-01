package com.moviebox.app.ui.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviebox.app.data.models.MediaItem
import com.moviebox.app.ui.components.MoviePosterCard
import com.moviebox.app.ui.theme.*

@Composable
fun SearchScreen(
    allMovies: List<MediaItem>,
    onMovieClick: (MediaItem) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }

    val searchResults = remember(query, allMovies) {
        if (query.isBlank()) {
            allMovies
        } else {
            allMovies.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.genres.any { g -> g.contains(query, ignoreCase = true) }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaBlack)
            .padding(16.dp)
    ) {
        // Search Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = TextWhite
                )
            }

            TextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search 10,000+ movies, series...", color = TextMuted) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = AccentRed
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = TextMuted
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = CardDark,
                    unfocusedContainerColor = CardDark,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedIndicatorColor = AccentRed,
                    unfocusedIndicatorColor = BorderDark
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (query.isBlank()) "Recommended For You" else "Results (${searchResults.size})",
            color = TextMuted,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 110.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(searchResults) { movie ->
                MoviePosterCard(item = movie, onClick = { onMovieClick(movie) })
            }
        }
    }
}
