package com.example.route.ui.feature.home

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.route.R
import com.example.route.data.remote.dto.CategoryDto
import com.example.route.data.remote.dto.ProductDto
import com.example.route.ui.core.components.BottomNavTab
import com.example.route.ui.core.components.RouteBottomNavBar
import com.example.route.ui.core.components.RouteTopBar

private data class HomeBannerItem(
    @DrawableRes val imageRes: Int,
    val title: String,
    val subtitle: String,
    val searchKeyword: String,
)

private val homeBanners = listOf(
    HomeBannerItem(
        imageRes = R.drawable.home_banner_headphones,
        title = "UP TO\n25% OFF",
        subtitle = "For all Headphones\n& AirPods",
        searchKeyword = "Headphones"
    ),
    HomeBannerItem(
        imageRes = R.drawable.mens_banner,
        title = "Men's\nFashion",
        subtitle = "Latest styles\nfor you",
        searchKeyword = "Men"
    ),
    HomeBannerItem(
        imageRes = R.drawable.womens_banner,
        title = "Women's\nFashion",
        subtitle = "Trending outfits\n& more",
        searchKeyword = "Women"
    ),
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.factory(LocalContext.current)),
    onSearchSubmit: (String) -> Unit = {},
    onBrowseCategories: () -> Unit = {},
    onCategoryClick: (CategoryDto) -> Unit = {},
    onLogoutSuccess: () -> Unit = {},
    onTabSelected: (BottomNavTab) -> Unit = {},
) {
    val colorScheme = MaterialTheme.colorScheme
    val state by viewModel.state.collectAsState()
    var showProfileDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.isLoggedOut) {
        if (state.isLoggedOut) {
            onLogoutSuccess()
        }
    }

    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = {
                Text(
                    text = state.userName.ifBlank { "Profile" },
                    color = colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            },
            text = {
                if (state.userEmail.isNotBlank()) {
                    Text(text = state.userEmail, color = colorScheme.tertiary)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showProfileDialog = false
                        viewModel.onIntent(HomeIntent.Logout)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.error)
                ) {
                    Text(text = stringResource(R.string.logout), color = colorScheme.secondary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showProfileDialog = false }) {
                    Text(text = "Close", color = colorScheme.primary)
                }
            }
        )
    }

    Scaffold(
        modifier = modifier,
        containerColor = colorScheme.secondary,
        topBar = {
            RouteTopBar(
                searchQuery = state.searchQuery,
                onSearchQueryChange = { viewModel.onIntent(HomeIntent.OnSearchQueryChange(it)) },
                onSearchSubmit = {
                    if (state.searchQuery.isNotBlank()) {
                        onSearchSubmit(state.searchQuery)
                    }
                }
            )
        },
        bottomBar = {
            RouteBottomNavBar(
                selectedTab = BottomNavTab.Home,
                onTabSelected = { tab ->
                    if (tab == BottomNavTab.Profile) {
                        showProfileDialog = true
                    } else {
                        onTabSelected(tab)
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.secondary)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            HomeBannerCarousel(
                onShopNowClick = { keyword -> onSearchSubmit(keyword) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Categories",
                    color = colorScheme.onPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "view all",
                    color = colorScheme.onPrimary,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { onBrowseCategories() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (state.isLoading && state.categories.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colorScheme.primary)
                }
            } else {
                LazyHorizontalGrid(
                    rows = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(224.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.categories, key = { it.id }) { category ->
                        HomeCategoryCircleItem(
                            category = category,
                            onClick = { onCategoryClick(category) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Home Appliance",
                color = colorScheme.onPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.products.take(10), key = { it.id }) { product ->
                    HomeApplianceCard(
                        product = product,
                        isFavorite = product.id in state.favoriteIds,
                        onToggleFavorite = {
                            viewModel.onIntent(HomeIntent.ToggleFavorite(product.id))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeBannerCarousel(
    onShopNowClick: (String) -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val pagerState = rememberPagerState(pageCount = { homeBanners.size })

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .height(176.dp)
            .clip(RoundedCornerShape(16.dp))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val banner = homeBanners[page]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onShopNowClick(banner.searchKeyword) }
            ) {
                Image(
                    painter = painterResource(banner.imageRes),
                    contentDescription = banner.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = banner.title,
                            color = colorScheme.primary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 24.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = banner.subtitle,
                            color = colorScheme.onPrimary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(colorScheme.primary)
                            .clickable { onShopNowClick(banner.searchKeyword) }
                            .padding(horizontal = 16.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.shop_now),
                            color = colorScheme.secondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(homeBanners.size) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(
                            if (isSelected) colorScheme.primary else colorScheme.secondary
                        )
                )
            }
        }
    }
}

@Composable
private fun HomeCategoryCircleItem(
    category: CategoryDto,
    onClick: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val localDrawable = resolveHomeCategoryDrawable(category.name)

    Column(
        modifier = Modifier
            .width(84.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color(0xFFEDF1F5))
        ) {
            if (localDrawable != null) {
                Image(
                    painter = painterResource(localDrawable),
                    contentDescription = category.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                AsyncImage(
                    model = category.image,
                    contentDescription = category.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = category.name,
            color = colorScheme.onPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 14.sp
        )
    }
}

@Composable
private fun HomeApplianceCard(
    product: ProductDto,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = Modifier
            .width(150.dp)
            .height(130.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.secondary),
        border = BorderStroke(1.dp, colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (product.imageRes != null) {
                Image(
                    painter = painterResource(product.imageRes),
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                AsyncImage(
                    model = product.imageCover,
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(28.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(colorScheme.secondary)
                    .clickable { onToggleFavorite() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isFavorite) {
                        Icons.Filled.Favorite
                    } else {
                        Icons.Outlined.FavoriteBorder
                    },
                    contentDescription = "Favorite",
                    tint = colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@DrawableRes
private fun resolveHomeCategoryDrawable(categoryName: String): Int? = when {
    categoryName.contains("Women", ignoreCase = true) -> R.drawable.cat_womens_fashion
    categoryName.contains("Men", ignoreCase = true) -> R.drawable.cat_mens_fashion
    categoryName.contains("Electron", ignoreCase = true) ||
        categoryName.contains("Laptop", ignoreCase = true) -> R.drawable.cat_laptops_electronics
    categoryName.contains("Baby", ignoreCase = true) ||
        categoryName.contains("Toy", ignoreCase = true) -> R.drawable.cat_baby_toys
    categoryName.contains("Beauty", ignoreCase = true) -> R.drawable.cat_beauty
    categoryName.contains("Headphone", ignoreCase = true) ||
        categoryName.contains("Music", ignoreCase = true) -> R.drawable.cat_headphones
    categoryName.contains("Skin", ignoreCase = true) -> R.drawable.cat_skincare
    categoryName.contains("Camera", ignoreCase = true) -> R.drawable.cat_cameras
    else -> null
}
