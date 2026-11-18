package com.example.route.data.repository

import com.example.route.data.remote.RetrofitClient
import com.example.route.data.remote.RouteApiService
import com.example.route.data.remote.dto.CategoryDto
import com.example.route.data.remote.dto.ProductDto
import com.example.route.data.remote.dto.SubCategoryDto

interface StoreRepository {
    suspend fun getCategories(): List<CategoryDto>
    suspend fun getSubCategories(categoryId: String): List<SubCategoryDto>
    suspend fun getProducts(
        categoryId: String? = null,
        subCategoryId: String? = null,
        keyword: String? = null,
    ): List<ProductDto>
}

class StoreRepositoryImpl(
    private val api: RouteApiService = RetrofitClient.api,
) : StoreRepository {

    override suspend fun getCategories(): List<CategoryDto> =
        api.getCategories().data

    override suspend fun getSubCategories(categoryId: String): List<SubCategoryDto> =
        api.getSubCategories(categoryId).data

    override suspend fun getProducts(
        categoryId: String?,
        subCategoryId: String?,
        keyword: String?,
    ): List<ProductDto> =
        api.getProducts(
            categoryId = categoryId?.takeIf { it.isNotBlank() },
            subCategoryId = subCategoryId?.takeIf { it.isNotBlank() },
            keyword = keyword?.takeIf { it.isNotBlank() },
        ).data
}
