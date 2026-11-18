package com.example.route.ui.feature.register

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

class RegisterViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun onIntent(intent: RegisterIntent) {
        when (intent) {
            is RegisterIntent.OnFullNameChange -> {
                _state.update {
                    it.copy(fullName = intent.fullName, fullNameError = null, errorMessage = null)
                }
            }

            is RegisterIntent.OnMobileChange -> {
                _state.update {
                    it.copy(mobileNumber = intent.mobileNumber, mobileError = null, errorMessage = null)
                }
            }

            is RegisterIntent.OnEmailChange -> {
                _state.update {
                    it.copy(email = intent.email, emailError = null, errorMessage = null)
                }
            }

            is RegisterIntent.OnPasswordChange -> {
                _state.update {
                    it.copy(password = intent.password, passwordError = null, errorMessage = null)
                }
            }

            is RegisterIntent.OnConfirmPasswordChange -> {
                _state.update {
                    it.copy(
                        confirmPassword = intent.confirmPassword,
                        confirmPasswordError = null,
                        errorMessage = null
                    )
                }
            }

            RegisterIntent.TogglePasswordVisibility -> {
                _state.update { it.copy(passwordVisible = !it.passwordVisible) }
            }

            RegisterIntent.ToggleConfirmPasswordVisibility -> {
                _state.update { it.copy(confirmPasswordVisible = !it.confirmPasswordVisible) }
            }

            RegisterIntent.ClearError -> {
                _state.update { it.copy(errorMessage = null) }
            }

            RegisterIntent.SubmitRegister -> submitRegister()
        }
    }

    private fun submitRegister() {
        val current = _state.value
        val fullNameError = AppValidators.validateFullName(current.fullName)
        val mobileError = AppValidators.validateMobileNumber(current.mobileNumber)
        val emailError = AppValidators.validateEmail(current.email)
        val passwordError = AppValidators.validatePassword(current.password)
        val confirmPasswordError = AppValidators.validateConfirmPassword(
            current.password,
            current.confirmPassword
        )

        val hasErrors = listOf(
            fullNameError,
            mobileError,
            emailError,
            passwordError,
            confirmPasswordError
        ).any { it != null }

        if (hasErrors) {
            _state.update {
                it.copy(
                    fullNameError = fullNameError,
                    mobileError = mobileError,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                authRepository.signUp(
                    name = current.fullName,
                    email = current.email,
                    password = current.password,
                    rePassword = current.confirmPassword,
                    phone = current.mobileNumber
                )
            }.onSuccess {
                _state.update { it.copy(isLoading = false, isRegistered = true) }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Registration failed"
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
                    return RegisterViewModel(repo) as T
                }
            }
    }
}
