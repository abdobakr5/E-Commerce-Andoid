package com.example.route.ui.core.navigation

import kotlinx.serialization.Serializable

@Serializable
object SplashRoute

@Serializable
object HomeRoute

@Serializable
object RegisterRoute

@Serializable
object LoginRoute

@Serializable
object CategoriesRoute

@Serializable
data class ProductListRoute(
    val categoryId: String? = null,
    val subCategoryId: String? = null,
    val keyword: String? = null,
    val title: String = "Products",
)
