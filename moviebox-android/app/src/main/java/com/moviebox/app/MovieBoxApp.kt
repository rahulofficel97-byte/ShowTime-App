package com.moviebox.app

import android.app.Application
import com.moviebox.app.auth.AuthRepository
import com.moviebox.app.data.repository.MovieRepository
import com.moviebox.app.security.AntiTamperManager

class MovieBoxApp : Application() {

    lateinit var authRepository: AuthRepository
        private set

    lateinit var movieRepository: MovieRepository
        private set

    override fun onCreate() {
        super.onCreate()

        // 🛡️ Execute 100% Hack-Free & Anti-Tamper Security Audit
        AntiTamperManager.verifyIntegrity(this)

        // Initialize Core Singletons
        authRepository = AuthRepository(this)
        movieRepository = MovieRepository()
    }
}
