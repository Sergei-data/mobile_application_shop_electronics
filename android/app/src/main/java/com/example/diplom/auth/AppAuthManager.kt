package com.example.diplom.auth

import net.openid.appauth.CodeVerifierUtil
import android.content.Context
import android.content.Intent
import net.openid.appauth.AppAuthConfiguration
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues
import net.openid.appauth.connectivity.ConnectionBuilder
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine
import android.util.Base64
import org.json.JSONObject
import org.json.JSONArray



class AppAuthManager(context: Context) {

    private val appContext = context.applicationContext
    private val prefs by lazy {
        appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val connectionBuilder: ConnectionBuilder by lazy {
        if (AuthConfig.ALLOW_INSECURE_HTTP_FOR_DEV) {
            InsecureConnectionBuilder
        } else {
            net.openid.appauth.connectivity.DefaultConnectionBuilder.INSTANCE
        }
    }

    private val appAuthConfig: AppAuthConfiguration by lazy {
        AppAuthConfiguration.Builder()
            .setConnectionBuilder(connectionBuilder)
            .setSkipIssuerHttpsCheck(AuthConfig.ALLOW_INSECURE_HTTP_FOR_DEV)
            .build()
    }

    private val authService by lazy { AuthorizationService(appContext, appAuthConfig) }

    suspend fun createAuthorizationIntent(): Intent {
        val serviceConfig = fetchServiceConfiguration()

        val authRequest = AuthorizationRequest.Builder(
            serviceConfig,
            AuthConfig.CLIENT_ID,
            ResponseTypeValues.CODE,
            AuthConfig.REDIRECT_URI
        )
            .setScope(AuthConfig.SCOPE)
            .build()

        return authService.getAuthorizationRequestIntent(authRequest)
    }

    suspend fun handleAuthorizationResult(data: Intent?): Result<AuthState> {
        val response = data?.let { AuthorizationResponse.fromIntent(it) }
        val ex = data?.let { AuthorizationException.fromIntent(it) }

        if (response == null) {
            return Result.failure(ex ?: IllegalStateException("Authorization failed or was cancelled"))
        }

        val authState = readAuthState() ?: AuthState()
        authState.update(response, ex)

        val (tokenResponse, tokenEx) = exchangeCode(response)
        authState.update(tokenResponse, tokenEx)

        if (tokenResponse == null) {
            return Result.failure(tokenEx ?: IllegalStateException("Token exchange failed"))
        }

        persistAuthState(authState)
        return Result.success(authState)
    }

    fun currentAccessToken(): String? = readAuthState()?.accessToken

    fun isAuthorized(): Boolean = readAuthState()?.isAuthorized == true

    fun clear() {
        prefs.edit().remove(KEY_AUTH_STATE_JSON).apply()
    }

    fun dispose() {
        authService.dispose()
    }

    private suspend fun fetchServiceConfiguration(): AuthorizationServiceConfiguration =
        suspendCancellableCoroutine { cont ->
            AuthorizationServiceConfiguration.fetchFromIssuer(
                AuthConfig.ISSUER_URI,
                { config, ex ->
                    when {
                        config != null -> cont.resume(config)
                        ex != null -> cont.resumeWithException(ex)
                        else -> cont.resumeWithException(
                            IllegalStateException("OIDC discovery failed without details")
                        )
                    }
                },
                connectionBuilder
            )
        }

    private suspend fun exchangeCode(
        response: AuthorizationResponse
    ): Pair<net.openid.appauth.TokenResponse?, AuthorizationException?> =
        suspendCancellableCoroutine { cont ->
            authService.performTokenRequest(response.createTokenExchangeRequest()) { tokenResponse, ex ->
                cont.resume(tokenResponse to ex)
            }
        }

    private fun persistAuthState(state: AuthState) {
        prefs.edit()
            .putString(KEY_AUTH_STATE_JSON, state.jsonSerializeString())
            .apply()
    }

    private fun readAuthState(): AuthState? {
        val json = prefs.getString(KEY_AUTH_STATE_JSON, null) ?: return null
        return runCatching { AuthState.jsonDeserialize(json) }.getOrNull()
    }

    private companion object {
        const val PREFS_NAME = "oidc_auth_prefs"
        const val KEY_AUTH_STATE_JSON = "auth_state_json"
    }

    fun currentAuthUiState(): AuthUiState {
        val state = readAuthState() ?: return AuthUiState()

        val token = state.accessToken ?: state.idToken
        if (token.isNullOrBlank()) {
            return AuthUiState(isAuthorized = state.isAuthorized)
        }

        val claims = decodeJwtPayload(token) ?: return AuthUiState(isAuthorized = state.isAuthorized)

        val preferredUsername = claims.optString("preferred_username").takeIf { it.isNotBlank() }
        val name = claims.optString("name").takeIf { it.isNotBlank() }
        val email = claims.optString("email").takeIf { it.isNotBlank() }

        val displayName = preferredUsername ?: name ?: "Пользователь"

        val roleLabel = when {
            hasRole(claims, "admin") -> "Администратор"
            hasRole(claims, "manager") -> "Менеджер"
            hasRole(claims, "user") -> "Пользователь"
            else -> null
        }

        return AuthUiState(
            isAuthorized = state.isAuthorized,
            displayName = displayName,
            email = email,
            roleLabel = roleLabel
        )
    }




    private fun decodeJwtPayload(token: String): JSONObject? {
        return runCatching {
            val parts = token.split(".")
            if (parts.size < 2) return null

            val payload = Base64.decode(
                parts[1],
                Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
            )

            JSONObject(String(payload, Charsets.UTF_8))
        }.getOrNull()
    }

    private fun hasRole(claims: JSONObject, role: String): Boolean {
        val realmAccess = claims.optJSONObject("realm_access")
        val realmRoles = realmAccess?.optJSONArray("roles")
        if (realmRoles != null && realmRoles.containsString(role)) {
            return true
        }

        val resourceAccess = claims.optJSONObject("resource_access")
        if (resourceAccess != null) {
            val keys = resourceAccess.keys()
            while (keys.hasNext()) {
                val clientKey = keys.next()
                val clientObj = resourceAccess.optJSONObject(clientKey) ?: continue
                val clientRoles = clientObj.optJSONArray("roles") ?: continue
                if (clientRoles.containsString(role)) {
                    return true
                }
            }
        }

        return false
    }

    private fun JSONArray.containsString(value: String): Boolean {
        for (i in 0 until length()) {
            if (optString(i) == value) return true
        }
        return false
    }

    suspend fun createRegistrationIntent(): Intent {
        val serviceConfig = fetchServiceConfiguration()

        val codeVerifier = CodeVerifierUtil.generateRandomCodeVerifier()
        val codeChallenge = CodeVerifierUtil.deriveCodeVerifierChallenge(codeVerifier)

        val authRequest = AuthorizationRequest.Builder(
            serviceConfig,
            AuthConfig.CLIENT_ID,
            ResponseTypeValues.CODE,
            AuthConfig.REDIRECT_URI
        )
            .setScope(AuthConfig.SCOPE)
            .setCodeVerifier(codeVerifier, codeChallenge, "S256")
            .setPrompt("create")
            .build()

        return authService.getAuthorizationRequestIntent(authRequest)
    }
}