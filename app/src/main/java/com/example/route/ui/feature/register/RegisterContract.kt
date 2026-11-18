package com.example.route.ui.feature.register

data class RegisterState(
    val fullName: String = "",
    val mobileNumber: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val fullNameError: String? = null,
    val mobileError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegistered: Boolean = false,
)

sealed interface RegisterIntent {
    data class OnFullNameChange(val fullName: String) : RegisterIntent
    data class OnMobileChange(val mobileNumber: String) : RegisterIntent
    data class OnEmailChange(val email: String) : RegisterIntent
    data class OnPasswordChange(val password: String) : RegisterIntent
    data class OnConfirmPasswordChange(val confirmPassword: String) : RegisterIntent
    data object TogglePasswordVisibility : RegisterIntent
    data object ToggleConfirmPasswordVisibility : RegisterIntent
    data object SubmitRegister : RegisterIntent
    data object ClearError : RegisterIntent
}
