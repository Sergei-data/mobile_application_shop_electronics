package com.example.diplom

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.diplom.auth.AppAuthManager
import com.example.diplom.core.ui.theme.DiplomTheme
import com.example.diplom.ui.navigation.MainScreen
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.diplom.auth.AuthUiState

class MainActivity : ComponentActivity() {

    private var authUiState by mutableStateOf(AuthUiState())
    private val authManager by lazy { AppAuthManager(applicationContext) }

    override fun onResume() {
        super.onResume()
        authUiState = authManager.currentAuthUiState()
        android.util.Log.d(
            "AuthFlow",
            "onResume: authorized=${authUiState.isAuthorized}, user=${authUiState.displayName}, role=${authUiState.roleLabel}"
        )
    }
    private fun logout() {
        android.util.Log.d("AuthFlow", "Local logout")
        authManager.clear()
        authUiState = AuthUiState()
    }

    private val authLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            android.util.Log.d(
                "AuthFlow",
                "authLauncher resultCode=${result.resultCode}, hasData=${result.data != null}"
            )

            lifecycleScope.launch {
                authManager.handleAuthorizationResult(result.data)
                    .onSuccess {
                        authUiState = authManager.currentAuthUiState()
                        android.util.Log.d(
                            "AuthFlow",
                            "Login success: authorized=${authUiState.isAuthorized}, user=${authUiState.displayName}, role=${authUiState.roleLabel}"
                        )
                    }
                    .onFailure { error ->
                        android.util.Log.e("AuthFlow", "Login failed", error)
                    }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        authUiState = authManager.currentAuthUiState()

        setContent {
            DiplomTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        MainScreen(
                            onLoginClick = ::startLogin,
                            onRegisterClick = ::startRegistration,
                            onLogoutClick = ::logout,
                            onManageProductsClick = {
                                android.util.Log.d("AuthFlow", "Manage products clicked")
                            },
                            onAdminClick = {
                                android.util.Log.d("AuthFlow", "Admin panel clicked")
                            },
                            authUiState = authUiState
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        authManager.dispose()
        super.onDestroy()
    }

    private fun startLogin() {
        android.util.Log.d("AuthFlow", "startLogin called")

        lifecycleScope.launch {
            runCatching { authManager.createAuthorizationIntent() }
                .onSuccess { authIntent ->
                    android.util.Log.d("AuthFlow", "Authorization intent created")
                    authLauncher.launch(authIntent)
                }
                .onFailure { error ->
                    android.util.Log.e("AuthFlow", "Unable to start OIDC login", error)
                }
        }
    }


    private fun startRegistration() {
        android.util.Log.d("AuthFlow", "startRegistration called")

        lifecycleScope.launch {
            runCatching { authManager.createRegistrationIntent() }
                .onSuccess { authIntent ->
                    android.util.Log.d("AuthFlow", "Registration intent created")
                    authLauncher.launch(authIntent)
                }
                .onFailure { error ->
                    android.util.Log.e("AuthFlow", "Unable to start registration", error)
                }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}