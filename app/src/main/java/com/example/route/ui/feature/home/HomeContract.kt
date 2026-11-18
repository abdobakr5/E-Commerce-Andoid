package com.example.route.ui.feature.home

import com.example.route.data.remote.dto.CategoryDto
import com.example.route.data.remote.dto.ProductDto

data class HomeState(
    val userName: String = "",
    val userEmail: String = "",
    val searchQuery: String = "",
    val categories: List<CategoryDto> = emptyList(),
    val products: List<ProductDto> = emptyList(),
    val favoriteIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val isLoggingOut: Boolean = false,
    val isLoggedOut: Boolean = false,
)

sealed interface HomeIntent {
    data object LoadHome : HomeIntent
    data class OnSearchQueryChange(val query: String) : HomeIntent
    data class ToggleFavorite(val productId: String) : HomeIntent
    data object Logout : HomeIntent
}
