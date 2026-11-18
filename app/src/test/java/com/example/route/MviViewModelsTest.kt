package com.example.route

import com.example.route.data.remote.dto.CategoryDto
import com.example.route.data.remote.dto.ProductDto
import com.example.route.data.remote.dto.SubCategoryDto
import com.example.route.data.repository.StoreRepository
import com.example.route.ui.feature.categories.CategoriesIntent
import com.example.route.ui.feature.categories.CategoriesViewModel
import com.example.route.ui.feature.products.ProductListIntent
import com.example.route.ui.feature.products.ProductListViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MviViewModelsTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class FakeStoreRepository(
        var shouldFail: Boolean = false,
    ) : StoreRepository {
        val categories = listOf(
            CategoryDto(id = "cat_women", name = "Women's Fashion"),
            CategoryDto(id = "cat_men", name = "Men's Fashion"),
        )

        val subCategoriesByCat = mapOf(
            "cat_men" to listOf(
                SubCategoryDto(id = "sub_1", name = "T-shirts", category = "cat_men"),
                SubCategoryDto(id = "sub_2", name = "Jeans", category = "cat_men"),
            ),
            "cat_women" to listOf(
                SubCategoryDto(id = "sub_3", name = "Dresses", category = "cat_women"),
            )
        )

        val products = listOf(
            ProductDto(
                id = "prod_1",
                title = "Nike Air Jordan",
                description = "Flexible running shoes",
                price = 2000.0,
                priceAfterDiscount = 1200.0,
                ratingsAverage = 4.6
            ),
            ProductDto(
                id = "prod_2",
                title = "Classic Sneakers",
                description = "Everyday sneakers",
                price = 1500.0,
                ratingsAverage = 4.8
            )
        )

        override suspend fun getCategories(): List<CategoryDto> {
            if (shouldFail) throw RuntimeException("Network error")
            return categories
        }

        override suspend fun getSubCategories(categoryId: String): List<SubCategoryDto> {
            if (shouldFail) throw RuntimeException("Network error")
            return subCategoriesByCat[categoryId].orEmpty()
        }

        override suspend fun getProducts(
            categoryId: String?,
            subCategoryId: String?,
            keyword: String?,
        ): List<ProductDto> {
            if (shouldFail) throw RuntimeException("Network error")
            return products
        }
    }

    @Test
    fun categoriesViewModel_loadsCategoriesAndSelectsMensByDefault() = runTest {
        val repo = FakeStoreRepository()
        val vm = CategoriesViewModel(repo)

        val state = vm.state.value
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertEquals(2, state.categories.size)
        assertEquals("cat_men", state.selectedCategory?.id)
        assertEquals(2, state.subCategories.size)
    }

    @Test
    fun categoriesViewModel_selectCategory_updatesSubCategories() = runTest {
        val repo = FakeStoreRepository()
        val vm = CategoriesViewModel(repo)

        val women = repo.categories.first { it.id == "cat_women" }
        vm.onIntent(CategoriesIntent.SelectCategory(women))

        val state = vm.state.value
        assertEquals("cat_women", state.selectedCategory?.id)
        assertEquals(1, state.subCategories.size)
        assertEquals("Dresses", state.subCategories.first().name)
    }

    @Test
    fun categoriesViewModel_errorAndRetry_recoversCleanly() = runTest {
        val repo = FakeStoreRepository(shouldFail = true)
        val vm = CategoriesViewModel(repo)

        assertNotNull(vm.state.value.errorMessage)

        repo.shouldFail = false
        vm.onIntent(CategoriesIntent.Retry)

        assertNull(vm.state.value.errorMessage)
        assertEquals(2, vm.state.value.categories.size)
    }

    @Test
    fun productListViewModel_loadsProducts_togglesFavorite_andAddsToCart() = runTest {
        val repo = FakeStoreRepository()
        val vm = ProductListViewModel(repo)

        vm.onIntent(ProductListIntent.LoadProducts(categoryId = "cat_men", title = "Men's Fashion"))
        assertEquals("Men's Fashion", vm.state.value.title)
        assertEquals(2, vm.state.value.products.size)

        vm.onIntent(ProductListIntent.ToggleFavorite("prod_1"))
        assertTrue("prod_1" in vm.state.value.favoriteIds)

        vm.onIntent(ProductListIntent.ToggleFavorite("prod_1"))
        assertFalse("prod_1" in vm.state.value.favoriteIds)

        vm.onIntent(ProductListIntent.AddToCart("prod_1"))
        vm.onIntent(ProductListIntent.AddToCart("prod_1"))
        vm.onIntent(ProductListIntent.AddToCart("prod_2"))
        assertEquals(3, vm.state.value.totalCartCount)
    }

    @Test
    fun productListViewModel_searchQuery_updatesState() = runTest {
        val repo = FakeStoreRepository()
        val vm = ProductListViewModel(repo)

        vm.onIntent(ProductListIntent.OnSearchQueryChange("Nike"))
        assertEquals("Nike", vm.state.value.searchQuery)
    }
}
