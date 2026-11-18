package com.example.route

import com.example.route.data.remote.dto.AuthResponse
import com.example.route.data.remote.dto.CategoryDto
import com.example.route.data.remote.dto.ProductDto
import com.example.route.data.remote.dto.SubCategoryDto
import com.example.route.data.remote.dto.UserDto
import com.example.route.data.repository.AuthRepository
import com.example.route.data.repository.StoreRepository
import com.example.route.ui.core.navigation.SessionViewModel
import com.example.route.ui.feature.home.HomeIntent
import com.example.route.ui.feature.home.HomeViewModel
import com.example.route.ui.feature.login.LoginIntent
import com.example.route.ui.feature.login.LoginViewModel
import com.example.route.ui.feature.register.RegisterIntent
import com.example.route.ui.feature.register.RegisterViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
class AuthAndHomeViewModelsTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class FakeAuthRepository(
        initialToken: String? = null,
        initialName: String? = null,
        initialEmail: String? = null,
        var errorToThrow: Throwable? = null,
    ) : AuthRepository {

        private val tokenFlow = MutableStateFlow(initialToken)
        private val nameFlow = MutableStateFlow(initialName)
        private val emailFlow = MutableStateFlow(initialEmail)

        override suspend fun signIn(email: String, password: String): AuthResponse {
            errorToThrow?.let { throw it }
            tokenFlow.value = "jwt_token_123"
            nameFlow.value = "Route User"
            emailFlow.value = email
            return AuthResponse(
                message = "success",
                token = "jwt_token_123",
                user = UserDto(name = "Route User", email = email, role = "user")
            )
        }

        override suspend fun signUp(
            name: String,
            email: String,
            password: String,
            rePassword: String,
            phone: String,
        ): AuthResponse {
            errorToThrow?.let { throw it }
            tokenFlow.value = "jwt_token_456"
            nameFlow.value = name
            emailFlow.value = email
            return AuthResponse(
                message = "success",
                token = "jwt_token_456",
                user = UserDto(name = name, email = email, role = "user")
            )
        }

        override fun getToken(): Flow<String?> = tokenFlow
        override fun getUserName(): Flow<String?> = nameFlow
        override fun getUserEmail(): Flow<String?> = emailFlow

        override suspend fun clearSession() {
            tokenFlow.value = null
            nameFlow.value = null
            emailFlow.value = null
        }
    }

    @Test
    fun loginViewModel_invalidInput_setsFieldErrors() = runTest {
        val vm = LoginViewModel(FakeAuthRepository())
        vm.onIntent(LoginIntent.OnEmailChange("invalid"))
        vm.onIntent(LoginIntent.OnPasswordChange("123"))
        vm.onIntent(LoginIntent.SubmitLogin)

        assertNotNull(vm.state.value.emailError)
        assertNotNull(vm.state.value.passwordError)
        assertFalse(vm.state.value.isLoggedIn)
    }

    @Test
    fun loginViewModel_validInput_logsInSuccessfully() = runTest {
        val vm = LoginViewModel(FakeAuthRepository())
        vm.onIntent(LoginIntent.OnEmailChange("user@route.com"))
        vm.onIntent(LoginIntent.OnPasswordChange("123456"))
        vm.onIntent(LoginIntent.SubmitLogin)

        assertTrue(vm.state.value.isLoggedIn)
        assertNull(vm.state.value.errorMessage)
        assertFalse(vm.state.value.isLoading)
    }

    @Test
    fun loginViewModel_apiError_setsErrorMessage() = runTest {
        val repo = FakeAuthRepository(errorToThrow = IllegalStateException("Incorrect email or password"))
        val vm = LoginViewModel(repo)
        vm.onIntent(LoginIntent.OnEmailChange("user@route.com"))
        vm.onIntent(LoginIntent.OnPasswordChange("123456"))
        vm.onIntent(LoginIntent.SubmitLogin)

        assertFalse(vm.state.value.isLoggedIn)
        assertEquals("Incorrect email or password", vm.state.value.errorMessage)
    }

    @Test
    fun registerViewModel_validInput_registersSuccessfully() = runTest {
        val vm = RegisterViewModel(FakeAuthRepository())
        vm.onIntent(RegisterIntent.OnFullNameChange("Ahmed Ali"))
        vm.onIntent(RegisterIntent.OnMobileChange("01012345678"))
        vm.onIntent(RegisterIntent.OnEmailChange("ahmed@route.com"))
        vm.onIntent(RegisterIntent.OnPasswordChange("123456"))
        vm.onIntent(RegisterIntent.OnConfirmPasswordChange("123456"))
        vm.onIntent(RegisterIntent.SubmitRegister)

        assertTrue(vm.state.value.isRegistered)
        assertNull(vm.state.value.errorMessage)
    }

    @Test
    fun registerViewModel_mismatchedPasswords_setsConfirmPasswordError() = runTest {
        val vm = RegisterViewModel(FakeAuthRepository())
        vm.onIntent(RegisterIntent.OnFullNameChange("Ahmed Ali"))
        vm.onIntent(RegisterIntent.OnMobileChange("01012345678"))
        vm.onIntent(RegisterIntent.OnEmailChange("ahmed@route.com"))
        vm.onIntent(RegisterIntent.OnPasswordChange("123456"))
        vm.onIntent(RegisterIntent.OnConfirmPasswordChange("654321"))
        vm.onIntent(RegisterIntent.SubmitRegister)

        assertFalse(vm.state.value.isRegistered)
        assertNotNull(vm.state.value.confirmPasswordError)
    }

    @Test
    fun sessionViewModel_detectsExistingTokenForAutoLogin() = runTest {
        val loggedInSession = SessionViewModel(
            authRepository = FakeAuthRepository(initialToken = "saved_jwt"),
            splashDelayMs = 0L
        )
        assertFalse(loggedInSession.state.value.isCheckingSession)
        assertTrue(loggedInSession.state.value.isLoggedIn)

        val loggedOutSession = SessionViewModel(
            authRepository = FakeAuthRepository(initialToken = null),
            splashDelayMs = 0L
        )
        assertFalse(loggedOutSession.state.value.isCheckingSession)
        assertFalse(loggedOutSession.state.value.isLoggedIn)
    }

    @Test
    fun homeViewModel_displaysUserAndLogsOut() = runTest {
        val repo = FakeAuthRepository(
            initialToken = "saved_jwt",
            initialName = "Ahmed",
            initialEmail = "ahmed@route.com"
        )
        val fakeStoreRepo = object : StoreRepository {
            override suspend fun getCategories() = listOf(CategoryDto("c1", "Men's Fashion"))
            override suspend fun getSubCategories(categoryId: String) = emptyList<SubCategoryDto>()
            override suspend fun getProducts(
                categoryId: String?,
                subCategoryId: String?,
                keyword: String?,
            ) = listOf(ProductDto(id = "p1", title = "Washer", price = 5000.0))
        }
        val vm = HomeViewModel(repo, fakeStoreRepo)

        assertEquals("Ahmed", vm.state.value.userName)
        assertEquals("ahmed@route.com", vm.state.value.userEmail)
        assertEquals(1, vm.state.value.categories.size)
        assertEquals(1, vm.state.value.products.size)

        vm.onIntent(HomeIntent.ToggleFavorite("p1"))
        assertTrue("p1" in vm.state.value.favoriteIds)

        vm.onIntent(HomeIntent.Logout)
        assertTrue(vm.state.value.isLoggedOut)
    }
}
