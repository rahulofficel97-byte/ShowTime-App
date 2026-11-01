package com.moviebox.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.moviebox.app.auth.models.User
import com.moviebox.app.data.models.MediaItem
import com.moviebox.app.data.models.StreamRelease
import com.moviebox.app.ui.auth.AuthScreen
import com.moviebox.app.ui.auth.ProfileScreen
import com.moviebox.app.ui.details.DetailScreen
import com.moviebox.app.ui.home.HomeScreen
import com.moviebox.app.ui.membership.MembershipPlanScreen
import com.moviebox.app.ui.player.PlayerActivity
import com.moviebox.app.ui.search.SearchScreen
import com.moviebox.app.ui.theme.CinemaBlack
import com.moviebox.app.ui.theme.MovieBoxTheme
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    object Search : Screen()
    data class Detail(val movie: MediaItem) : Screen()
    object Auth : Screen()
    object Profile : Screen()
    object Membership : Screen()
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as MovieBoxApp
        val authRepo = app.authRepository
        val movieRepo = app.movieRepository

        setContent {
            MovieBoxTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = CinemaBlack
                ) {
                    val user by authRepo.currentUser.collectAsState()
                    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
                    var movies by remember { mutableStateOf<List<MediaItem>>(emptyList()) }
                    var activeStreams by remember { mutableStateOf<List<StreamRelease>>(emptyList()) }
                    val scope = rememberCoroutineScope()

                    LaunchedEffect(Unit) {
                        movies = movieRepo.getTrending()
                    }

                    when (val screen = currentScreen) {
                        is Screen.Home -> {
                            HomeScreen(
                                movies = movies,
                                user = user,
                                onMovieClick = { movie ->
                                    scope.launch {
                                        activeStreams = movieRepo.getStreams(movie.id)
                                        currentScreen = Screen.Detail(movie)
                                    }
                                },
                                onSearchClick = { currentScreen = Screen.Search },
                                onProfileClick = {
                                    currentScreen = if (user != null) Screen.Profile else Screen.Auth
                                },
                                onVipClick = { currentScreen = Screen.Membership }
                            )
                        }

                        is Screen.Search -> {
                            SearchScreen(
                                allMovies = movies,
                                onMovieClick = { movie ->
                                    scope.launch {
                                        activeStreams = movieRepo.getStreams(movie.id)
                                        currentScreen = Screen.Detail(movie)
                                    }
                                },
                                onBack = { currentScreen = Screen.Home }
                            )
                        }

                        is Screen.Detail -> {
                            DetailScreen(
                                movie = screen.movie,
                                streams = activeStreams,
                                user = user,
                                onPlayStream = { stream ->
                                    val intent = Intent(this@MainActivity, PlayerActivity::class.java).apply {
                                        putExtra(PlayerActivity.EXTRA_STREAM_URL, stream.streamUrl)
                                        putExtra(PlayerActivity.EXTRA_MOVIE_TITLE, screen.movie.title)
                                        putExtra(PlayerActivity.EXTRA_QUALITY, stream.quality)
                                    }
                                    startActivity(intent)
                                },
                                onUpgradeToVip = { currentScreen = Screen.Membership },
                                onBack = { currentScreen = Screen.Home }
                            )
                        }

                        is Screen.Auth -> {
                            AuthScreen(
                                onLoginSuccess = { currentScreen = Screen.Home },
                                onGuestLogin = {
                                    authRepo.continueAsGuest()
                                    currentScreen = Screen.Home
                                },
                                onGoogleSignIn = {
                                    scope.launch {
                                        authRepo.signInWithGoogle("sample_google_token")
                                        currentScreen = Screen.Home
                                    }
                                },
                                onBack = { currentScreen = Screen.Home },
                                onLoginAction = { email, pass -> authRepo.login(email, pass) },
                                onRegisterAction = { user, email, pass -> authRepo.register(user, email, pass) }
                            )
                        }

                        is Screen.Profile -> {
                            ProfileScreen(
                                user = user,
                                onUpgradePlan = { currentScreen = Screen.Membership },
                                onLogout = {
                                    authRepo.logout()
                                    currentScreen = Screen.Home
                                },
                                onBack = { currentScreen = Screen.Home }
                            )
                        }

                        is Screen.Membership -> {
                            MembershipPlanScreen(
                                user = user,
                                onSelectPlan = { tier ->
                                    scope.launch {
                                        authRepo.upgradePlan(tier)
                                    }
                                    currentScreen = Screen.Home
                                },
                                onBack = { currentScreen = Screen.Home }
                            )
                        }
                    }
                }
            }
        }
    }
}
