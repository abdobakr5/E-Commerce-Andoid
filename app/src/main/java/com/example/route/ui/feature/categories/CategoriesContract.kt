package com.example.route.ui.feature.categories

import com.example.route.data.remote.dto.CategoryDto
import com.example.route.data.remote.dto.SubCategoryDto

data class CategoriesState(
    val isLoading: Boolean = false,
    val isSubCategoriesLoading: Boolean = false,
    val errorMessage: String? = null,
    val categories: List<CategoryDto> = emptyList(),
    val selectedCategory: CategoryDto? = null,
    val subCategories: List<SubCategoryDto> = emptyList(),
    val searchQuery: String = "",
)

sealed interface CategoriesIntent {
    data object LoadCategories : CategoriesIntent
    data class SelectCategory(val category: CategoryDto) : CategoriesIntent
    data class OnSearchQueryChange(val query: String) : CategoriesIntent
    data object Retry : CategoriesIntent
}
