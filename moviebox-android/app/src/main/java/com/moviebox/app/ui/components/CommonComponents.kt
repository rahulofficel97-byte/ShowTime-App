package com.moviebox.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.moviebox.app.data.models.MediaItem
import com.moviebox.app.ui.theme.*

@Composable
fun MoviePosterCard(
    item: MediaItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(135.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .width(135.dp)
                .height(195.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(CardDark)
        ) {
            AsyncImage(
                model = item.posterUrl,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Rating Badge (Top Right)
            Surface(
                color = CinemaBlack.copy(alpha = 0.82f),
                shape = RoundedCornerShape(bottomStart = 8.dp),
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = GoldVIP,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = item.rating,
                        color = TextWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = item.title,
            color = TextWhite,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = "${item.year} • ${item.genres.firstOrNull() ?: item.type.label}",
            color = TextMuted,
            fontSize = 11.sp,
            maxLines = 1
        )
    }
}

@Composable
fun VipBadge(modifier: Modifier = Modifier) {
    Surface(
        color = GoldVIP,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.WorkspacePremium,
                contentDescription = "VIP",
                tint = CinemaBlack,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = "VIP",
                color = CinemaBlack,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
fun HeroBanner(
    item: MediaItem,
    onWatchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
    ) {
        AsyncImage(
            model = item.backdropUrl ?: item.posterUrl,
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient Fade to AMOLED black
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            CinemaBlack.copy(alpha = 0.4f),
                            CinemaBlack
                        ),
                        startY = 60f
                    )
                )
        )

        // Details & Play Button overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = AccentRed,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "TRENDING #1",
                        color = TextWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${item.year} • ${item.genres.joinToString(", ")}",
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.title,
                color = TextWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = item.overview,
                color = TextMuted,
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onWatchClick,
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(42.dp)
            ) {
                Text(
                    text = "▶  Watch Now (4K)",
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
