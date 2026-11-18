package com.example.route.ui.feature.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.route.data.local.AuthPreferences
import com.example.route.data.repository.AuthRepository
import com.example.route.data.repository.AuthRepositoryImpl
import com.example.route.data.repository.LocalStoreRepository
import com.example.route.data.repository.StoreRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val authRepository: AuthRepository,
    private val storeRepository: StoreRepository = LocalStoreRepository(),
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        observeUserSession()
        onIntent(HomeIntent.LoadHome)
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.LoadHome -> loadHomeData()

            is HomeIntent.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = intent.query) }
            }

            is HomeIntent.ToggleFavorite -> {
                _state.update { current ->
                    val updated = if (intent.productId in current.favoriteIds) {
                        current.favoriteIds - intent.productId
                    } else {
                        current.favoriteIds + intent.productId
                    }
                    current.copy(favoriteIds = updated)
                }
            }

            HomeIntent.Logout -> logout()
        }
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            runCatching {
                coroutineScope {
                    val categoriesDeferred = async { storeRepository.getCategories() }
                    val productsDeferred = async { storeRepository.getProducts() }
                    categoriesDeferred.await() to productsDeferred.await()
                }
            }.onSuccess { (categories, products) ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        categories = categories,
                        products = products
                    )
                }
            }.onFailure {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun observeUserSession() {
        viewModelScope.launch {
            combine(
                authRepository.getUserName(),
                authRepository.getUserEmail()
            ) { name, email ->
                name.orEmpty() to email.orEmpty()
            }.collect { (name, email) ->
                _state.update { it.copy(userName = name, userEmail = email) }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _state.update { it.copy(isLoggingOut = true) }
            authRepository.clearSession()
            _state.update { it.copy(isLoggingOut = false, isLoggedOut = true) }
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val repo = AuthRepositoryImpl(AuthPreferences(context.applicationContext))
                    return HomeViewModel(repo) as T
                }
            }
    }
}
