package com.example.route.ui.core.utils

object AppValidators {

    private val phoneRegex = Regex("^[+]?[0-9]{10,15}$")
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun validateFullName(name: String): String? {
        val trimmed = name.trim()
        return when {
            trimmed.isEmpty() -> "Full name is required"
            trimmed.length < 2 -> "Full name must be at least 2 characters"
            else -> null
        }
    }

    fun validateMobileNumber(mobile: String): String? {
        val trimmed = mobile.trim()
        return when {
            trimmed.isEmpty() -> "Mobile number is required"
            !phoneRegex.matches(trimmed) -> "Enter a valid mobile number (10-15 digits)"
            else -> null
        }
    }

    fun validateEmail(email: String): String? {
        val trimmed = email.trim()
        return when {
            trimmed.isEmpty() -> "Email is required"
            !emailRegex.matches(trimmed) -> "Enter a valid email address"
            else -> null
        }
    }

    fun validatePassword(password: String): String? {
        return when {
            password.isEmpty() -> "Password is required"
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
    }

    fun validateConfirmPassword(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isEmpty() -> "Please confirm your password"
            confirmPassword != password -> "Passwords do not match"
            else -> null
        }
    }
}
