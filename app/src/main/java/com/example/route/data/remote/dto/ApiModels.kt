package com.example.route.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ApiListResponse<T>(
    val results: Int = 0,
    val data: List<T> = emptyList(),
)

@Serializable
data class CategoryDto(
    @SerialName("_id") val id: String,
    val name: String,
    val slug: String = "",
    val image: String = "",
    @Transient val imageRes: Int? = null,
)

@Serializable
data class SubCategoryDto(
    @SerialName("_id") val id: String,
    val name: String,
    val slug: String = "",
    val category: String = "",
)

@Serializable
data class CategorySummaryDto(
    @SerialName("_id") val id: String = "",
    val name: String = "",
)

@Serializable
data class ProductDto(
    @SerialName("_id") val id: String,
    val title: String,
    val slug: String = "",
    val description: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0,
    val priceAfterDiscount: Double? = null,
    val imageCover: String = "",
    val images: List<String> = emptyList(),
    val ratingsAverage: Double = 0.0,
    val ratingsQuantity: Int = 0,
    val category: CategorySummaryDto? = null,
    @Transient val imageRes: Int? = null,
)

@Serializable
data class SignInRequest(
    val email: String,
    val password: String,
)

@Serializable
data class SignUpRequest(
    val name: String,
    val email: String,
    val password: String,
    val rePassword: String,
    val phone: String,
)

@Serializable
data class UserDto(
    val name: String = "",
    val email: String = "",
    val role: String = "",
)

@Serializable
data class AuthResponse(
    val message: String = "",
    val token: String = "",
    val user: UserDto? = null,
)
