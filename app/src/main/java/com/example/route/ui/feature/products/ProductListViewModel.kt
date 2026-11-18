package com.example.route.ui.feature.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.route.data.repository.LocalStoreRepository
import com.example.route.data.repository.StoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProductListViewModel(
    private val repository: StoreRepository = LocalStoreRepository(),
) : ViewModel() {

    private val _state = MutableStateFlow(ProductListState())
    val state: StateFlow<ProductListState> = _state.asStateFlow()

    private var lastFilter: ProductListIntent.LoadProducts? = null

    fun onIntent(intent: ProductListIntent) {
        when (intent) {
            is ProductListIntent.LoadProducts -> {
                lastFilter = intent
                loadProducts(intent)
            }

            ProductListIntent.Retry -> {
                lastFilter?.let(::loadProducts)
            }

            is ProductListIntent.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = intent.query) }
            }

            is ProductListIntent.ToggleFavorite -> {
                _state.update { current ->
                    val updated = if (intent.productId in current.favoriteIds) {
                        current.favoriteIds - intent.productId
                    } else {
                        current.favoriteIds + intent.productId
                    }
                    current.copy(favoriteIds = updated)
                }
            }

            is ProductListIntent.AddToCart -> {
                _state.update { current ->
                    val currentCount = current.cartCounts[intent.productId] ?: 0
                    current.copy(
                        cartCounts = current.cartCounts + (intent.productId to currentCount + 1)
                    )
                }
            }
        }
    }

    private fun loadProducts(filter: ProductListIntent.LoadProducts) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    title = filter.title,
                    searchQuery = filter.keyword.orEmpty()
                )
            }

            runCatching {
                val filtered = repository.getProducts(
                    categoryId = filter.categoryId,
                    subCategoryId = filter.subCategoryId,
                    keyword = filter.keyword
                )
                if (filtered.isEmpty() && filter.subCategoryId != null && filter.categoryId != null) {
                    repository.getProducts(categoryId = filter.categoryId)
                } else {
                    filtered
                }
            }.onSuccess { products ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        products = products
                    )
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Failed to load products"
                    )
                }
            }
        }
    }
}
