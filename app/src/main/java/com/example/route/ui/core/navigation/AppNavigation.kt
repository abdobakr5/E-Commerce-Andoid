package com.example.route.ui.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.route.ui.core.components.BottomNavTab
import com.example.route.ui.feature.categories.CategoriesScreen
import com.example.route.ui.feature.home.HomeScreen
import com.example.route.ui.feature.login.LoginScreen
import com.example.route.ui.feature.products.ProductListScreen
import com.example.route.ui.feature.register.RegisterScreen
import com.example.route.ui.feature.splash.SplashScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    sessionViewModel: SessionViewModel = viewModel(
        factory = SessionViewModel.factory(LocalContext.current)
    ),
) {
    val sessionState by sessionViewModel.state.collectAsState()
    val navController = rememberNavController()

    val onBottomTabSelected: (BottomNavTab) -> Unit = { tab ->
        when (tab) {
            BottomNavTab.Home -> navigateSingleTop(navController, HomeRoute)
            BottomNavTab.Categories -> navigateSingleTop(navController, CategoriesRoute)
            BottomNavTab.Favorites -> navigateSingleTop(navController, ProductListRoute(title = "Products"))
            BottomNavTab.Profile -> navigateSingleTop(navController, HomeRoute)
        }
    }

    NavHost(
        navController = navController,
        startDestination = SplashRoute,
        modifier = modifier
    ) {
        composable<SplashRoute> {
            SplashScreen()
            LaunchedEffect(sessionState.isCheckingSession) {
                if (!sessionState.isCheckingSession) {
                    val destination: Any = if (sessionState.isLoggedIn) HomeRoute else LoginRoute
                    navController.navigate(destination) {
                        popUpTo(SplashRoute) { inclusive = true }
                    }
                }
            }
        }
        composable<LoginRoute> {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(HomeRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                },
                onCreateAccountClick = {
                    navController.navigate(RegisterRoute)
                }
            )
        }
        composable<RegisterRoute> {
            RegisterScreen(
                onSignUpClick = {
                    navController.navigate(HomeRoute) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable<HomeRoute> {
            HomeScreen(
                onSearchSubmit = { query ->
                    navController.navigate(ProductListRoute(keyword = query, title = query))
                },
                onBrowseCategories = {
                    navigateSingleTop(navController, CategoriesRoute)
                },
                onCategoryClick = { category ->
                    navController.navigate(
                        ProductListRoute(
                            categoryId = category.id,
                            title = category.name
                        )
                    )
                },
                onLogoutSuccess = {
                    navController.navigate(LoginRoute) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onTabSelected = onBottomTabSelected
            )
        }
        composable<CategoriesRoute> {
            CategoriesScreen(
                onShopNowClick = { category ->
                    navController.navigate(
                        ProductListRoute(
                            categoryId = category.id,
                            title = category.name
                        )
                    )
                },
                onSubCategoryClick = { category, subCategory ->
                    navController.navigate(
                        ProductListRoute(
                            categoryId = category.id,
                            subCategoryId = subCategory.id,
                            title = subCategory.name
                        )
                    )
                },
                onSearchSubmit = { query ->
                    navController.navigate(
                        ProductListRoute(
                            keyword = query,
                            title = query
                        )
                    )
                },
                onTabSelected = onBottomTabSelected
            )
        }
        composable<ProductListRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<ProductListRoute>()
            ProductListScreen(
                categoryId = route.categoryId,
                subCategoryId = route.subCategoryId,
                keyword = route.keyword,
                title = route.title,
                onTabSelected = onBottomTabSelected
            )
        }
    }
}

private fun navigateSingleTop(navController: NavHostController, route: Any) {
    navController.navigate(route) {
        launchSingleTop = true
    }
}
