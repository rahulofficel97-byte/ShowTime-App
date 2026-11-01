package com.moviebox.app.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.moviebox.app.auth.models.MembershipTier
import com.moviebox.app.auth.models.User
import com.moviebox.app.data.models.MediaItem
import com.moviebox.app.data.models.StreamRelease
import com.moviebox.app.ui.components.VipBadge
import com.moviebox.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    movie: MediaItem,
    streams: List<StreamRelease>,
    user: User?,
    onPlayStream: (StreamRelease) -> Unit,
    onUpgradeToVip: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showQualitySheet by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = CinemaBlack,
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Image with Back Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                AsyncImage(
                    model = movie.backdropUrl ?: movie.posterUrl,
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, CinemaBlack),
                                startY = 120f
                            )
                        )
                )

                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                        .background(CinemaBlack.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }
            }

            // Info details
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = movie.title,
                    color = TextWhite,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        color = CardDark,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = GoldVIP,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = movie.rating,
                                color = TextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(text = movie.year, color = TextMuted, fontSize = 13.sp)
                    Text(text = "•", color = TextMuted, fontSize = 13.sp)
                    Text(text = movie.duration, color = TextMuted, fontSize = 13.sp)
                    Text(text = "•", color = TextMuted, fontSize = 13.sp)
                    Text(
                        text = movie.genres.joinToString(", "),
                        color = AccentRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Play Now Button (Launches Stream Quality Picker)
                Button(
                    onClick = { showQualitySheet = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, tint = TextWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Play Movie (Select Quality)",
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Offline Download button
                OutlinedButton(
                    onClick = { showQualitySheet = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = TextWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Download Offline", fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Storyline",
                    color = TextWhite,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = movie.overview,
                    color = TextMuted,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // Quality & Chunk Stream Selection Sheet
        if (showQualitySheet) {
            ModalBottomSheet(
                onDismissRequest = { showQualitySheet = false },
                containerColor = CardDark
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Select Stream Quality",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Multi-segment chunked playback enabled for zero buffering",
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    streams.forEach { stream ->
                        val isLocked = stream.requiresVip && user?.plan == MembershipTier.FREE

                        Surface(
                            color = if (isLocked) CinemaBlack else SurfaceDark,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clickable {
                                    showQualitySheet = false
                                    if (isLocked) {
                                        onUpgradeToVip()
                                    } else {
                                        onPlayStream(stream)
                                    }
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = if (isLocked) GoldVIP else AccentRed
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = stream.quality,
                                            color = TextWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = if (stream.isChunkedHls) "Segmented HLS Chunks (Fast)" else "Direct HTTP Range Chunking",
                                            color = TextMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                if (stream.requiresVip) {
                                    VipBadge()
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
