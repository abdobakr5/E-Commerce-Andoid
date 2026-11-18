package com.example.route.data.repository

import com.example.route.data.local.AuthPreferences
import com.example.route.data.remote.RetrofitClient
import com.example.route.data.remote.RouteApiService
import com.example.route.data.remote.dto.AuthResponse
import com.example.route.data.remote.dto.SignInRequest
import com.example.route.data.remote.dto.SignUpRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import retrofit2.HttpException

interface AuthRepository {
    suspend fun signIn(email: String, password: String): AuthResponse
    suspend fun signUp(
        name: String,
        email: String,
        password: String,
        rePassword: String,
        phone: String,
    ): AuthResponse

    fun getToken(): Flow<String?>
    fun getUserName(): Flow<String?>
    fun getUserEmail(): Flow<String?>
    suspend fun clearSession()
}

@Serializable
private data class ApiErrorResponse(
    val message: String? = null,
    val errors: ApiFieldError? = null,
)

@Serializable
private data class ApiFieldError(
    val msg: String? = null,
)

class AuthRepositoryImpl(
    private val prefs: AuthPreferences,
    private val api: RouteApiService = RetrofitClient.api,
) : AuthRepository {

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    override suspend fun signIn(email: String, password: String): AuthResponse {
        return try {
            val response = api.signIn(SignInRequest(email = email.trim(), password = password))
            if (response.token.isNotBlank()) {
                prefs.saveSession(
                    token = response.token,
                    name = response.user?.name.orEmpty(),
                    email = response.user?.email ?: email.trim()
                )
            }
            response
        } catch (e: HttpException) {
            throw IllegalStateException(parseErrorMessage(e), e)
        }
    }

    override suspend fun signUp(
        name: String,
        email: String,
        password: String,
        rePassword: String,
        phone: String,
    ): AuthResponse {
        return try {
            val response = api.signUp(
                SignUpRequest(
                    name = name.trim(),
                    email = email.trim(),
                    password = password,
                    rePassword = rePassword,
                    phone = phone.trim()
                )
            )
            if (response.token.isNotBlank()) {
                prefs.saveSession(
                    token = response.token,
                    name = response.user?.name ?: name.trim(),
                    email = response.user?.email ?: email.trim()
                )
            }
            response
        } catch (e: HttpException) {
            throw IllegalStateException(parseErrorMessage(e), e)
        }
    }

    override fun getToken(): Flow<String?> = prefs.tokenFlow

    override fun getUserName(): Flow<String?> = prefs.userNameFlow

    override fun getUserEmail(): Flow<String?> = prefs.userEmailFlow

    override suspend fun clearSession() {
        prefs.clearSession()
    }

    private fun parseErrorMessage(e: HttpException): String {
        val rawBody = e.response()?.errorBody()?.string().orEmpty()
        if (rawBody.isNotBlank()) {
            runCatching {
                val parsed = json.decodeFromString<ApiErrorResponse>(rawBody)
                return parsed.errors?.msg ?: parsed.message ?: "Request failed (${e.code()})"
            }
        }
        return "Request failed (${e.code()})"
    }
}
