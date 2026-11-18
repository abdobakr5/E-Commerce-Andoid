package com.example.route.data.remote

import com.example.route.data.remote.dto.ApiListResponse
import com.example.route.data.remote.dto.AuthResponse
import com.example.route.data.remote.dto.CategoryDto
import com.example.route.data.remote.dto.ProductDto
import com.example.route.data.remote.dto.SignInRequest
import com.example.route.data.remote.dto.SignUpRequest
import com.example.route.data.remote.dto.SubCategoryDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RouteApiService {

    @POST("auth/signin")
    suspend fun signIn(@Body request: SignInRequest): AuthResponse

    @POST("auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): AuthResponse

    @GET("categories")
    suspend fun getCategories(): ApiListResponse<CategoryDto>

    @GET("categories/{categoryId}/subcategories")
    suspend fun getSubCategories(
        @Path("categoryId") categoryId: String,
    ): ApiListResponse<SubCategoryDto>

    @GET("products")
    suspend fun getProducts(
        @Query("category[in]") categoryId: String? = null,
        @Query("subcategory[in]") subCategoryId: String? = null,
        @Query("keyword") keyword: String? = null,
        @Query("limit") limit: Int = 40,
    ): ApiListResponse<ProductDto>
}
