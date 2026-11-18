package com.example.route.ui.feature.products

import com.example.route.data.remote.dto.ProductDto

data class ProductListState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val title: String = "Products",
    val products: List<ProductDto> = emptyList(),
    val searchQuery: String = "",
    val favoriteIds: Set<String> = emptySet(),
    val cartCounts: Map<String, Int> = emptyMap(),
) {
    val totalCartCount: Int
        get() = cartCounts.values.sum()
}

sealed interface ProductListIntent {
    data class LoadProducts(
        val categoryId: String? = null,
        val subCategoryId: String? = null,
        val keyword: String? = null,
        val title: String = "Products",
    ) : ProductListIntent

    data class OnSearchQueryChange(val query: String) : ProductListIntent
    data class ToggleFavorite(val productId: String) : ProductListIntent
    data class AddToCart(val productId: String) : ProductListIntent
    data object Retry : ProductListIntent
}
