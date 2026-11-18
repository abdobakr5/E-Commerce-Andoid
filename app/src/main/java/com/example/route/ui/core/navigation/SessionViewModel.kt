package com.example.route.ui.core.navigation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.route.data.local.AuthPreferences
import com.example.route.data.repository.AuthRepository
import com.example.route.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SessionState(
    val isCheckingSession: Boolean = true,
    val isLoggedIn: Boolean = false,
)

class SessionViewModel(
    private val authRepository: AuthRepository,
    private val splashDelayMs: Long = 1500L,
) : ViewModel() {

    private val _state = MutableStateFlow(SessionState())
    val state: StateFlow<SessionState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val tokenDeferred = async { authRepository.getToken().first() }
            if (splashDelayMs > 0L) delay(splashDelayMs)
            val token = tokenDeferred.await()
            _state.value = SessionState(
                isCheckingSession = false,
                isLoggedIn = !token.isNullOrBlank()
            )
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val repo = AuthRepositoryImpl(AuthPreferences(context.applicationContext))
                    return SessionViewModel(repo) as T
                }
            }
    }
}
