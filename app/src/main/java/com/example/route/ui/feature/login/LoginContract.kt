package com.example.route.ui.feature.login

data class LoginState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoggedIn: Boolean = false,
)

sealed interface LoginIntent {
    data class OnEmailChange(val email: String) : LoginIntent
    data class OnPasswordChange(val password: String) : LoginIntent
    data object TogglePasswordVisibility : LoginIntent
    data object SubmitLogin : LoginIntent
    data object ClearError : LoginIntent
}
