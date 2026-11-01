package com.moviebox.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviebox.app.auth.models.User
import com.moviebox.app.data.models.MediaItem
import com.moviebox.app.data.models.MediaType
import com.moviebox.app.ui.components.HeroBanner
import com.moviebox.app.ui.components.MoviePosterCard
import com.moviebox.app.ui.components.VipBadge
import com.moviebox.app.ui.theme.*

@Composable
fun HomeScreen(
    movies: List<MediaItem>,
    user: User?,
    onMovieClick: (MediaItem) -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    onVipClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val heroItem = movies.firstOrNull()
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Movies", "TV Shows", "Anime")

    val filteredMovies = remember(selectedCategory, movies) {
        when (selectedCategory) {
            "Movies" -> movies.filter { it.type == MediaType.MOVIE }
            "TV Shows" -> movies.filter { it.type == MediaType.TV_SHOW }
            "Anime" -> movies.filter { it.type == MediaType.ANIME }
            else -> movies
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaBlack)
    ) {
        // Top App Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "SHOW",
                        color = AccentRed,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "TIME",
                        color = TextWhite,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "+",
                        color = GoldVIP,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    VipBadge(modifier = Modifier)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextWhite
                        )
                    }
                    IconButton(onClick = onProfileClick) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = if (user?.plan?.name != "FREE") GoldVIP else TextWhite
                        )
                    }
                }
            }
        }

        // Hero Cinematic Banner
        if (heroItem != null) {
            item {
                HeroBanner(
                    item = heroItem,
                    onWatchClick = { onMovieClick(heroItem) }
                )
            }
        }

        // Category Pills
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = cat == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = cat },
                        label = {
                            Text(
                                text = cat,
                                color = if (isSelected) TextWhite else TextMuted,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AccentRed,
                            containerColor = CardDark
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) AccentRed else BorderDark,
                            enabled = true,
                            selected = isSelected
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }
        }

        // Row 1: Trending Now
        item {
            SectionHeader(title = "Trending Now 🔥", onSeeAll = {})
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredMovies) { movie ->
                    MoviePosterCard(item = movie, onClick = { onMovieClick(movie) })
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Row 2: Popular Movies
        item {
            SectionHeader(title = "Blockbuster Movies 🍿", onSeeAll = {})
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(movies.filter { it.type == MediaType.MOVIE }.reversed()) { movie ->
                    MoviePosterCard(item = movie, onClick = { onMovieClick(movie) })
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Row 3: Series & Anime
        item {
            SectionHeader(title = "Top Series & Anime ⚡", onSeeAll = {})
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(movies.filter { it.type == MediaType.TV_SHOW || it.type == MediaType.ANIME }) { movie ->
                    MoviePosterCard(item = movie, onClick = { onMovieClick(movie) })
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String, onSeeAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = TextWhite,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "See All",
            color = AccentRed,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
