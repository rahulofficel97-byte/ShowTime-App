package com.moviebox.app.ui.player

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.PlayerView
import com.moviebox.app.ui.theme.AccentRed
import com.moviebox.app.ui.theme.CinemaBlack
import com.moviebox.app.ui.theme.TextWhite

class PlayerActivity : ComponentActivity() {

    private var player: ExoPlayer? = null

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Keep screen awake during playback
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val streamUrl = intent.getStringExtra(EXTRA_STREAM_URL) ?: ""
        val movieTitle = intent.getStringExtra(EXTRA_MOVIE_TITLE) ?: "Streaming..."
        val quality = intent.getStringExtra(EXTRA_QUALITY) ?: "Auto"

        initializeChunkedPlayer(streamUrl)

        setContent {
            PlayerScreen(
                title = movieTitle,
                quality = quality,
                player = player,
                onBack = { finish() },
                onPip = { enterPipMode() }
            )
        }
    }

    @OptIn(UnstableApi::class)
    private fun initializeChunkedPlayer(url: String) {
        // High-performance chunk-buffer tuning:
        // Plays immediately when 1.5s chunk arrives; buffers 15-50s ahead in RAM without wasting user's data
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                /* minBufferMs = */ 15_000,
                /* maxBufferMs = */ 50_000,
                /* bufferForPlaybackMs = */ 1_500,
                /* bufferForPlaybackAfterRebufferMs = */ 3_000
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("MovieBox-Android/1.0.0 (Linux; Android)")
            .setConnectTimeoutMs(15_000)
            .setReadTimeoutMs(20_000)
            .setAllowCrossProtocolRedirects(true)

        val mediaSourceFactory = DefaultMediaSourceFactory(this)
            .setDataSourceFactory(httpDataSourceFactory)

        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(mediaSourceFactory)
            .setLoadControl(loadControl)
            .build()
            .apply {
                val mediaItem = MediaItem.fromUri(url)
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
            }
    }

    private fun enterPipMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val params = PictureInPictureParams.Builder()
                .setAspectRatio(Rational(16, 9))
                .build()
            enterPictureInPictureMode(params)
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        enterPipMode()
    }

    override fun onPause() {
        super.onPause()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !isInPictureInPictureMode) {
            player?.pause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }

    companion object {
        const val EXTRA_STREAM_URL = "extra_stream_url"
        const val EXTRA_MOVIE_TITLE = "extra_movie_title"
        const val EXTRA_QUALITY = "extra_quality"
    }
}

@Composable
fun PlayerScreen(
    title: String,
    quality: String,
    player: ExoPlayer?,
    onBack: () -> Unit,
    onPip: () -> Unit
) {
    var showControls by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { showControls = !showControls }
    ) {
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    this.player = player
                    useController = true
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Custom Top Bar Overlay
        if (showControls) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = title,
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Playing in $quality • Segmented Chunks",
                            color = AccentRed,
                            fontSize = 12.sp
                        )
                    }
                }

                IconButton(onClick = onPip) {
                    Icon(
                        imageVector = Icons.Default.PictureInPicture,
                        contentDescription = "PiP",
                        tint = TextWhite
                    )
                }
            }
        }
    }
}
