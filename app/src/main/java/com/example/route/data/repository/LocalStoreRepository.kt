package com.example.route.data.repository

import com.example.route.R
import com.example.route.data.remote.dto.CategoryDto
import com.example.route.data.remote.dto.CategorySummaryDto
import com.example.route.data.remote.dto.ProductDto
import com.example.route.data.remote.dto.SubCategoryDto

class LocalStoreRepository : StoreRepository {

    private val categories = listOf(
        CategoryDto(id = "cat_men", name = "Men's Fashion", imageRes = R.drawable.cat_mens_fashion),
        CategoryDto(id = "cat_women", name = "Women's Fashion", imageRes = R.drawable.cat_womens_fashion),
        CategoryDto(id = "cat_skincare", name = "Skincare", imageRes = R.drawable.cat_skincare),
        CategoryDto(id = "cat_beauty", name = "Beauty", imageRes = R.drawable.cat_beauty),
        CategoryDto(id = "cat_headphones", name = "Headphones", imageRes = R.drawable.cat_headphones),
        CategoryDto(id = "cat_cameras", name = "Cameras", imageRes = R.drawable.cat_cameras),
        CategoryDto(id = "cat_laptops", name = "Laptops & Electronics", imageRes = R.drawable.cat_laptops_electronics),
        CategoryDto(id = "cat_baby", name = "Baby & Toys", imageRes = R.drawable.cat_baby_toys),
    )

    private val subCategoriesByCategory = mapOf(
        "cat_men" to listOf(
            SubCategoryDto("sub_m1", "T-shirts", category = "cat_men"),
            SubCategoryDto("sub_m2", "Shorts", category = "cat_men"),
            SubCategoryDto("sub_m3", "Jeans", category = "cat_men"),
            SubCategoryDto("sub_m4", "Pants", category = "cat_men"),
            SubCategoryDto("sub_m5", "Footwear", category = "cat_men"),
            SubCategoryDto("sub_m6", "Suits", category = "cat_men"),
            SubCategoryDto("sub_m7", "Watches", category = "cat_men"),
            SubCategoryDto("sub_m8", "Bags", category = "cat_men"),
            SubCategoryDto("sub_m9", "Eyewears", category = "cat_men"),
        ),
        "cat_women" to listOf(
            SubCategoryDto("sub_w1", "Dresses", category = "cat_women"),
            SubCategoryDto("sub_w2", "Jeans", category = "cat_women"),
            SubCategoryDto("sub_w3", "skirts", category = "cat_women"),
            SubCategoryDto("sub_w4", "pijamas", category = "cat_women"),
            SubCategoryDto("sub_w5", "Bags", category = "cat_women"),
            SubCategoryDto("sub_w6", "T-shirts", category = "cat_women"),
            SubCategoryDto("sub_w7", "Footwear", category = "cat_women"),
            SubCategoryDto("sub_w8", "Eyewear", category = "cat_women"),
            SubCategoryDto("sub_w9", "Watches", category = "cat_women"),
        ),
        "cat_skincare" to listOf(
            SubCategoryDto("sub_s1", "Face Wash", category = "cat_skincare"),
            SubCategoryDto("sub_s2", "Moisturizer", category = "cat_skincare"),
            SubCategoryDto("sub_s3", "Sunscreen", category = "cat_skincare"),
            SubCategoryDto("sub_s4", "Serum", category = "cat_skincare"),
            SubCategoryDto("sub_s5", "Toner", category = "cat_skincare"),
            SubCategoryDto("sub_s6", "Masks", category = "cat_skincare"),
        ),
        "cat_beauty" to listOf(
            SubCategoryDto("sub_b1", "Lipstick", category = "cat_beauty"),
            SubCategoryDto("sub_b2", "Foundation", category = "cat_beauty"),
            SubCategoryDto("sub_b3", "Perfume", category = "cat_beauty"),
            SubCategoryDto("sub_b4", "Mascara", category = "cat_beauty"),
            SubCategoryDto("sub_b5", "Eyeliner", category = "cat_beauty"),
            SubCategoryDto("sub_b6", "Blush", category = "cat_beauty"),
        ),
        "cat_headphones" to listOf(
            SubCategoryDto("sub_h1", "Wireless", category = "cat_headphones"),
            SubCategoryDto("sub_h2", "Over-Ear", category = "cat_headphones"),
            SubCategoryDto("sub_h3", "In-Ear", category = "cat_headphones"),
            SubCategoryDto("sub_h4", "AirPods", category = "cat_headphones"),
            SubCategoryDto("sub_h5", "Gaming", category = "cat_headphones"),
            SubCategoryDto("sub_h6", "Speakers", category = "cat_headphones"),
        ),
        "cat_cameras" to listOf(
            SubCategoryDto("sub_c1", "DSLR", category = "cat_cameras"),
            SubCategoryDto("sub_c2", "Mirrorless", category = "cat_cameras"),
            SubCategoryDto("sub_c3", "Lenses", category = "cat_cameras"),
            SubCategoryDto("sub_c4", "Tripods", category = "cat_cameras"),
            SubCategoryDto("sub_c5", "Action Cam", category = "cat_cameras"),
            SubCategoryDto("sub_c6", "Bags", category = "cat_cameras"),
        ),
        "cat_laptops" to listOf(
            SubCategoryDto("sub_l1", "Laptops", category = "cat_laptops"),
            SubCategoryDto("sub_l2", "Monitors", category = "cat_laptops"),
            SubCategoryDto("sub_l3", "Keyboards", category = "cat_laptops"),
            SubCategoryDto("sub_l4", "Mice", category = "cat_laptops"),
            SubCategoryDto("sub_l5", "Tablets", category = "cat_laptops"),
            SubCategoryDto("sub_l6", "Accessories", category = "cat_laptops"),
        ),
        "cat_baby" to listOf(
            SubCategoryDto("sub_t1", "Strollers", category = "cat_baby"),
            SubCategoryDto("sub_t2", "Plush Toys", category = "cat_baby"),
            SubCategoryDto("sub_t3", "Blocks", category = "cat_baby"),
            SubCategoryDto("sub_t4", "Baby Care", category = "cat_baby"),
            SubCategoryDto("sub_t5", "Clothing", category = "cat_baby"),
            SubCategoryDto("sub_t6", "Games", category = "cat_baby"),
        ),
    )

    private val allProducts = listOf(
        ProductDto(
            id = "p_1",
            title = "Nike Air Jordan",
            description = "Nike shoes flexible for running and daily wear",
            price = 2000.0,
            priceAfterDiscount = 1200.0,
            ratingsAverage = 4.6,
            category = CategorySummaryDto("cat_men", "Men's Fashion"),
            imageRes = R.drawable.mens_footwear
        ),
        ProductDto(
            id = "p_2",
            title = "Nike Air Jordan Sport",
            description = "Lightweight breathable sneakers for women & men",
            price = 1500.0,
            priceAfterDiscount = 1100.0,
            ratingsAverage = 4.5,
            category = CategorySummaryDto("cat_women", "Women's Fashion"),
            imageRes = R.drawable.womens_footwear
        ),
        ProductDto(
            id = "p_3",
            title = "Classic Cotton T-Shirt",
            description = "Soft slim fit cotton t-shirt for everyday comfort",
            price = 1300.0,
            priceAfterDiscount = 1000.0,
            ratingsAverage = 4.8,
            category = CategorySummaryDto("cat_men", "Men's Fashion"),
            imageRes = R.drawable.mens_tshirts
        ),
        ProductDto(
            id = "p_4",
            title = "Tailored Classic Suit",
            description = "Modern tailored formal suit with premium fabric",
            price = 2000.0,
            priceAfterDiscount = 1500.0,
            ratingsAverage = 4.8,
            category = CategorySummaryDto("cat_men", "Men's Fashion"),
            imageRes = R.drawable.mens_suits
        ),
        ProductDto(
            id = "p_5",
            title = "Chronograph Leather Watch",
            description = "Water resistant stainless steel classic watch",
            price = 2100.0,
            priceAfterDiscount = 1800.0,
            ratingsAverage = 4.7,
            category = CategorySummaryDto("cat_men", "Men's Fashion"),
            imageRes = R.drawable.mens_watches
        ),
        ProductDto(
            id = "p_6",
            title = "Urban Travel Backpack",
            description = "Durable everyday backpack with laptop sleeve",
            price = 2500.0,
            priceAfterDiscount = 1900.0,
            ratingsAverage = 4.6,
            category = CategorySummaryDto("cat_men", "Men's Fashion"),
            imageRes = R.drawable.mens_bags
        ),
        ProductDto(
            id = "p_7",
            title = "Summer Midi Dress",
            description = "Elegant breathable dress for casual & formal wear",
            price = 1600.0,
            priceAfterDiscount = 1250.0,
            ratingsAverage = 4.7,
            category = CategorySummaryDto("cat_women", "Women's Fashion"),
            imageRes = R.drawable.womens_dresses
        ),
        ProductDto(
            id = "p_8",
            title = "Leather Tote Bag",
            description = "Spacious everyday handbag with minimalist design",
            price = 1800.0,
            priceAfterDiscount = 1400.0,
            ratingsAverage = 4.9,
            category = CategorySummaryDto("cat_women", "Women's Fashion"),
            imageRes = R.drawable.womens_bags
        ),
        ProductDto(
            id = "p_9",
            title = "Studio Wireless Headphones",
            description = "Noise cancelling over-ear headphones with deep bass",
            price = 2400.0,
            priceAfterDiscount = 1800.0,
            ratingsAverage = 4.8,
            category = CategorySummaryDto("cat_headphones", "Headphones"),
            imageRes = R.drawable.cat_headphones
        ),
        ProductDto(
            id = "p_10",
            title = "Pro DSLR Camera",
            description = "High resolution digital camera for photography",
            price = 9500.0,
            priceAfterDiscount = 8200.0,
            ratingsAverage = 4.9,
            category = CategorySummaryDto("cat_cameras", "Cameras"),
            imageRes = R.drawable.cat_cameras
        ),
        ProductDto(
            id = "p_11",
            title = "Hydrating Skincare Set",
            description = "Complete daily facial cleanser and moisturizer kit",
            price = 950.0,
            priceAfterDiscount = 750.0,
            ratingsAverage = 4.7,
            category = CategorySummaryDto("cat_skincare", "Skincare"),
            imageRes = R.drawable.cat_skincare
        ),
        ProductDto(
            id = "p_12",
            title = "UltraSlim Work Laptop",
            description = "Fast performance laptop for study and productivity",
            price = 18000.0,
            priceAfterDiscount = 15900.0,
            ratingsAverage = 4.8,
            category = CategorySummaryDto("cat_laptops", "Laptops & Electronics"),
            imageRes = R.drawable.cat_laptops_electronics
        ),
    )

    override suspend fun getCategories(): List<CategoryDto> = categories

    override suspend fun getSubCategories(categoryId: String): List<SubCategoryDto> =
        subCategoriesByCategory[categoryId].orEmpty()

    override suspend fun getProducts(
        categoryId: String?,
        subCategoryId: String?,
        keyword: String?,
    ): List<ProductDto> {
        var result = allProducts
        if (!categoryId.isNullOrBlank()) {
            val byCategory = result.filter { it.category?.id == categoryId }
            if (byCategory.isNotEmpty()) result = byCategory
        }
        if (!keyword.isNullOrBlank()) {
            val byKeyword = result.filter {
                it.title.contains(keyword, ignoreCase = true) ||
                    it.description.contains(keyword, ignoreCase = true) ||
                    it.category?.name?.contains(keyword, ignoreCase = true) == true
            }
            if (byKeyword.isNotEmpty()) result = byKeyword
        }
        return result
    }
}
