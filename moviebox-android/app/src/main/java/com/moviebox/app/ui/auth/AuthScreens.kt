package com.moviebox.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviebox.app.auth.models.User
import com.moviebox.app.ui.theme.*

@Composable
fun AuthScreen(
    onLoginSuccess: (User) -> Unit,
    onGoogleSignIn: () -> Unit = {},
    onGuestLogin: () -> Unit,
    onBack: () -> Unit,
    onLoginAction: suspend (String, String) -> Result<User>,
    onRegisterAction: suspend (String, String, String) -> Result<User>,
    modifier: Modifier = Modifier
) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaBlack)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "SHOWTIME+",
            color = AccentRed,
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )

        Text(
            text = if (isRegisterMode) "Create Your ShowTime+ Account" else "Welcome to ShowTime+, Streamer",
            color = TextMuted,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(30.dp))

        if (errorMsg != null) {
            Text(
                text = errorMsg!!,
                color = AccentRed,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        if (isRegisterMode) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = AccentRed) },
                colors = outlinedColors(),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = AccentRed) },
            colors = outlinedColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AccentRed) },
            colors = outlinedColors(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    errorMsg = null
                    val res = if (isRegisterMode) {
                        onRegisterAction(username, email, password)
                    } else {
                        onLoginAction(email, password)
                    }
                    res.onSuccess { onLoginSuccess(it) }
                       .onFailure { errorMsg = it.message ?: "Authentication failed." }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = AccentRed),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = if (isRegisterMode) "Sign Up" else "Sign In",
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = { isRegisterMode = !isRegisterMode }) {
            Text(
                text = if (isRegisterMode) "Already have an account? Sign In" else "Don't have an account? Sign Up",
                color = TextMuted,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        HorizontalDivider(color = BorderDark)

        Spacer(modifier = Modifier.height(20.dp))

        // Google Sign-In Button (Firebase Auth)
        Button(
            onClick = onGoogleSignIn,
            colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color.White),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = "G   Continue with Google",
                color = CinemaBlack,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onGuestLogin,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("⚡ One-Tap Guest Access", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun ProfileScreen(
    user: User?,
    onUpgradePlan: () -> Unit,
    onLogout: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaBlack)
            .padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = null, tint = TextWhite)
            }
            Text("My Account", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            color = CardDark,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = user?.username ?: "Guest User",
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = user?.email ?: "guest@moviebox.local",
                    color = TextMuted,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    color = CinemaBlack,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Current Tier", color = TextMuted, fontSize = 11.sp)
                            Text(
                                text = user?.plan?.displayName ?: "Free Member",
                                color = GoldVIP,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                        Button(
                            onClick = onUpgradePlan,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldVIP),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = CinemaBlack)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Upgrade", color = CinemaBlack, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedButton(
            onClick = onLogout,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AccentRed),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Sign Out", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun outlinedColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = CardDark,
    unfocusedContainerColor = CardDark,
    focusedTextColor = TextWhite,
    unfocusedTextColor = TextWhite,
    focusedBorderColor = AccentRed,
    unfocusedBorderColor = BorderDark,
    focusedLabelColor = AccentRed,
    unfocusedLabelColor = TextMuted
)
