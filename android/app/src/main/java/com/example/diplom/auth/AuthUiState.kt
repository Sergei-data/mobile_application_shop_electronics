package com.example.diplom.auth

data class AuthUiState(
    val isAuthorized: Boolean = false,
    val displayName: String? = null,
    val email: String? = null,
    val roleLabel: String? = null
)