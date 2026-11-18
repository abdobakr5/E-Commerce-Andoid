package com.example.route.ui.feature.categories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.route.data.remote.dto.CategoryDto
import com.example.route.data.repository.LocalStoreRepository
import com.example.route.data.repository.StoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val repository: StoreRepository = LocalStoreRepository(),
) : ViewModel() {

    private val _state = MutableStateFlow(CategoriesState())
    val state: StateFlow<CategoriesState> = _state.asStateFlow()

    init {
        onIntent(CategoriesIntent.LoadCategories)
    }

    fun onIntent(intent: CategoriesIntent) {
        when (intent) {
            CategoriesIntent.LoadCategories,
            CategoriesIntent.Retry -> loadCategories()

            is CategoriesIntent.SelectCategory -> selectCategory(intent.category)

            is CategoriesIntent.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = intent.query) }
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            runCatching {
                repository.getCategories()
            }.onSuccess { categories ->
                val initialCategory = categories.firstOrNull {
                    it.name.startsWith("Men", ignoreCase = true)
                } ?: categories.firstOrNull()

                _state.update {
                    it.copy(
                        isLoading = false,
                        categories = categories,
                        selectedCategory = initialCategory
                    )
                }

                initialCategory?.let { loadSubCategories(it.id) }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.localizedMessage ?: "Failed to load categories"
                    )
                }
            }
        }
    }

    private fun selectCategory(category: CategoryDto) {
        if (_state.value.selectedCategory?.id == category.id) return
        _state.update {
            it.copy(
                selectedCategory = category,
                subCategories = emptyList()
            )
        }
        loadSubCategories(category.id)
    }

    private fun loadSubCategories(categoryId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isSubCategoriesLoading = true) }
            runCatching {
                repository.getSubCategories(categoryId)
            }.onSuccess { subCategories ->
                _state.update {
                    it.copy(
                        isSubCategoriesLoading = false,
                        subCategories = subCategories
                    )
                }
            }.onFailure {
                _state.update { it.copy(isSubCategoriesLoading = false) }
            }
        }
    }
}
