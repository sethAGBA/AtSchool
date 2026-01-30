package com.ecolix.presentation.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.ecolix.presentation.screens.dashboard.DashboardScreen
import com.ecolix.presentation.screens.superadmin.SuperAdminScreen
import com.ecolix.presentation.theme.*

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<LoginScreenModel>()
        val state by screenModel.state.collectAsState()

        var schoolCode by remember { mutableStateOf("") }
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var rememberMe by remember { mutableStateOf(com.ecolix.atschool.api.TokenProvider.rememberMe) }
        var passwordVisible by remember { mutableStateOf(false) }
        val isDark = androidx.compose.foundation.isSystemInDarkTheme()

        LaunchedEffect(Unit) {
            if (com.ecolix.atschool.api.TokenProvider.token != null) {
                if (com.ecolix.atschool.api.TokenProvider.role == "SUPER_ADMIN") {
                    navigator.push(SuperAdminScreen())
                } else {
                    navigator.push(DashboardScreen())
                }
            } else {
                screenModel.reset()
            }
        }

        LaunchedEffect(state) {
            if (state is LoginState.Success) {
                // Verify we typically have a token (avoid stale Success state after logout)
                if (com.ecolix.atschool.api.TokenProvider.token != null) {
                    if (com.ecolix.atschool.api.TokenProvider.role == "SUPER_ADMIN") {
                        navigator.push(SuperAdminScreen())
                    } else {
                        navigator.push(DashboardScreen())
                    }
                } else {
                    screenModel.reset()
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = if (isDark) LoginDarkGradient else LoginLightGradient
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Ambient Background Circles (Simplified)
            Box(
                modifier = Modifier
                    .offset(x = 100.dp, y = (-100).dp)
                    .align(Alignment.TopEnd)
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f))
            )
            Box(
                modifier = Modifier
                    .offset(x = (-100).dp, y = 100.dp)
                    .align(Alignment.BottomStart)
                    .size(300.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
            )

            // Main Card
            Card(
                modifier = Modifier
                    .width(480.dp)
                    .padding(16.dp)
                    .shadow(elevation = 24.dp, shape = RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Logo
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .shadow(8.dp, CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
                                ),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Ecole Manager",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Connexion",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (state is LoginState.Error) {
                        Text(
                            text = (state as LoginState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // School Code
                    OutlinedTextField(
                        value = schoolCode,
                        onValueChange = { schoolCode = it },
                        label = { Text("Code École") },
                        leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6366F1),
                            focusedLabelColor = Color(0xFF6366F1)
                        )
                    )

                    // Username
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Nom d'utilisateur") },
                        leadingIcon = { Icon(Icons.Default.PersonOutline, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6366F1),
                            focusedLabelColor = Color(0xFF6366F1)
                        )
                    )

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mot de passe") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Masquer" else "Afficher"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                screenModel.login(username, password, schoolCode, rememberMe)
                            }
                        ),
                         colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF6366F1),
                            focusedLabelColor = Color(0xFF6366F1)
                        )
                    )

                    // Remember Me
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF6366F1)
                            )
                        )
                        Text("Se souvenir de moi", color = Color.White)
                    }

                    // Login Button
                    Button(
                        onClick = { screenModel.login(username, password, schoolCode, rememberMe) },
                        enabled = state !is LoginState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6366F1)
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                if (state is LoginState.Loading) "Connexion en cours..." else "Se connecter",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }

                    // Forgot Password
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        TextButton(onClick = { /* TODO */ }) {
                            Text("Mot de passe oublie ?", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
