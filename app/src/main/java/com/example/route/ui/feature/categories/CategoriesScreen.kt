package com.example.route.ui.feature.categories

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.route.R
import com.example.route.data.remote.dto.CategoryDto
import com.example.route.data.remote.dto.SubCategoryDto
import com.example.route.ui.core.components.BottomNavTab
import com.example.route.ui.core.components.RouteBottomNavBar
import com.example.route.ui.core.components.RouteTopBar

private val CategoryRailBg = Color(0xFFEDF1F5)

@Composable
fun CategoriesScreen(
    viewModel: CategoriesViewModel = viewModel(),
    onShopNowClick: (CategoryDto) -> Unit = {},
    onSubCategoryClick: (CategoryDto, SubCategoryDto) -> Unit = { _, _ -> },
    onSearchSubmit: (String) -> Unit = {},
    onTabSelected: (BottomNavTab) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    Scaffold(
        containerColor = colorScheme.secondary,
        topBar = {
            RouteTopBar(
                searchQuery = state.searchQuery,
                onSearchQueryChange = { viewModel.onIntent(CategoriesIntent.OnSearchQueryChange(it)) },
                onSearchSubmit = {
                    if (state.searchQuery.isNotBlank()) {
                        onSearchSubmit(state.searchQuery)
                    }
                }
            )
        },
        bottomBar = {
            RouteBottomNavBar(
                selectedTab = BottomNavTab.Categories,
                onTabSelected = onTabSelected
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colorScheme.primary)
                }
            }

            state.errorMessage != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = state.errorMessage ?: "Something went wrong",
                        color = colorScheme.onPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.onIntent(CategoriesIntent.Retry) },
                        colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
                    ) {
                        Text(text = "Retry", color = colorScheme.secondary)
                    }
                }
            }

            else -> {
                CategoriesContent(
                    state = state,
                    onSelectCategory = { viewModel.onIntent(CategoriesIntent.SelectCategory(it)) },
                    onShopNowClick = onShopNowClick,
                    onSubCategoryClick = onSubCategoryClick,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun CategoriesContent(
    state: CategoriesState,
    onSelectCategory: (CategoryDto) -> Unit,
    onShopNowClick: (CategoryDto) -> Unit,
    onSubCategoryClick: (CategoryDto, SubCategoryDto) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    val selectedCategory = state.selectedCategory

    val filteredSubCategories = if (state.searchQuery.isBlank()) {
        state.subCategories
    } else {
        state.subCategories.filter {
            it.name.contains(state.searchQuery, ignoreCase = true)
        }
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.secondary)
    ) {
        LazyColumn(
            modifier = Modifier
                .width(128.dp)
                .fillMaxHeight()
                .background(CategoryRailBg)
        ) {
            items(state.categories, key = { it.id }) { category ->
                val isSelected = category.id == selectedCategory?.id
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isSelected) colorScheme.secondary else CategoryRailBg)
                        .clickable { onSelectCategory(category) }
                        .padding(vertical = 18.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .width(5.dp)
                                .height(36.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(colorScheme.primary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    } else {
                        Spacer(modifier = Modifier.width(13.dp))
                    }

                    Text(
                        text = category.name,
                        color = colorScheme.onPrimary,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            if (selectedCategory != null) {
                Text(
                    text = selectedCategory.name,
                    color = colorScheme.onPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                CategoryBannerCard(
                    category = selectedCategory,
                    onShopNowClick = { onShopNowClick(selectedCategory) }
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (state.isSubCategoriesLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredSubCategories, key = { it.id }) { subCategory ->
                            SubCategoryGridItem(
                                category = selectedCategory,
                                subCategory = subCategory,
                                onClick = { onSubCategoryClick(selectedCategory, subCategory) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryBannerCard(
    category: CategoryDto,
    onShopNowClick: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val localBanner = resolveBannerDrawable(category.name)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(104.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onShopNowClick() }
    ) {
        if (localBanner != null) {
            Image(
                painter = painterResource(localBanner),
                contentDescription = category.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else if (category.imageRes != null) {
            Image(
                painter = painterResource(category.imageRes),
                contentDescription = category.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f))
            )
        } else {
            AsyncImage(
                model = category.image,
                contentDescription = category.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.25f))
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = category.name,
                color = if (localBanner != null) colorScheme.onPrimary else colorScheme.secondary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(colorScheme.primary)
                    .clickable { onShopNowClick() }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Shop Now",
                    color = colorScheme.secondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun SubCategoryGridItem(
    category: CategoryDto,
    subCategory: SubCategoryDto,
    onClick: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val localDrawable = resolveSubCategoryDrawable(category.name, subCategory.name)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(68.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(CategoryRailBg)
        ) {
            if (localDrawable != null) {
                Image(
                    painter = painterResource(localDrawable),
                    contentDescription = subCategory.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (category.imageRes != null) {
                Image(
                    painter = painterResource(category.imageRes),
                    contentDescription = subCategory.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                AsyncImage(
                    model = category.image,
                    contentDescription = subCategory.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = subCategory.name,
            color = colorScheme.onPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@DrawableRes
private fun resolveBannerDrawable(categoryName: String): Int? = when {
    categoryName.contains("Women", ignoreCase = true) -> R.drawable.womens_banner
    categoryName.contains("Men", ignoreCase = true) -> R.drawable.mens_banner
    else -> null
}

@DrawableRes
private fun resolveSubCategoryDrawable(categoryName: String, subName: String): Int? {
    val lower = subName.lowercase()
    return if (categoryName.contains("Women", ignoreCase = true)) {
        when {
            "dress" in lower -> R.drawable.womens_dresses
            "jean" in lower -> R.drawable.womens_jeans
            "skirt" in lower -> R.drawable.womens_skirts
            "pijama" in lower || "pajama" in lower -> R.drawable.womens_pijamas
            "bag" in lower -> R.drawable.womens_bags
            "shirt" in lower -> R.drawable.womens_tshirts
            "shoe" in lower || "footwear" in lower -> R.drawable.womens_footwear
            "eye" in lower || "glass" in lower -> R.drawable.womens_eyewear
            "watch" in lower -> R.drawable.womens_watches
            else -> null
        }
    } else if (categoryName.contains("Men", ignoreCase = true)) {
        when {
            "shirt" in lower -> R.drawable.mens_tshirts
            "short" in lower -> R.drawable.mens_shorts
            "jean" in lower -> R.drawable.mens_jeans
            "pant" in lower || "trouser" in lower -> R.drawable.mens_pants
            "shoe" in lower || "footwear" in lower -> R.drawable.mens_footwear
            "suit" in lower -> R.drawable.mens_suits
            "watch" in lower -> R.drawable.mens_watches
            "bag" in lower -> R.drawable.mens_bags
            "eye" in lower || "glass" in lower -> R.drawable.mens_eyewears
            else -> null
        }
    } else {
        null
    }
}
