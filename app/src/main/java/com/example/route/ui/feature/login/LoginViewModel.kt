package com.example.route.ui.feature.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.route.data.local.AuthPreferences
import com.example.route.data.repository.AuthRepository
import com.example.route.data.repository.AuthRepositoryImpl
import com.example.route.ui.core.utils.AppValidators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.OnEmailChange -> {
                _state.update {
                    it.copy(email = intent.email, emailError = null, errorMessage = null)
                }
            }

            is LoginIntent.OnPasswordChange -> {
                _state.update {
                    it.copy(password = intent.password, passwordError = null, errorMessage = null)
                }
            }

            LoginIntent.TogglePasswordVisibility -> {
                _state.update { it.copy(passwordVisible = !it.passwordVisible) }
            }

            LoginIntent.ClearError -> {
                _state.update { it.copy(errorMessage = null) }
            }

            LoginIntent.SubmitLogin -> submitLogin()
        }
    }

    private fun submitLogin() {
        val current = _state.value
        val emailError = AppValidators.validateEmail(current.email)
        val passwordError = AppValidators.validatePassword(current.password)

        if (emailError != null || passwordError != null) {
            _state.update {
                it.copy(emailError = emailError, passwordError = passwordError)
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                authRepository.signIn(current.email, current.password)
            }.onSuccess {
                _state.update { it.copy(isLoading = false, isLoggedIn = true) }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Login failed"
                    )
                }
            }
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val repo = AuthRepositoryImpl(AuthPreferences(context.applicationContext))
                    return LoginViewModel(repo) as T
                }
            }
    }
}
