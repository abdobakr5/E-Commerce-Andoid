package com.example.route.ui.feature.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.route.R
import com.example.route.ui.core.components.CustomElevatedButton
import com.example.route.ui.core.components.CustomTextField
import com.example.route.ui.feature.login.EndSection
import com.example.route.ui.feature.login.TopLogoSection
import com.example.route.ui.theme.RouteTheme

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = viewModel(factory = RegisterViewModel.factory(LocalContext.current)),
    onSignUpClick: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {},
) {
    val colorScheme = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isRegistered) {
        if (state.isRegistered) {
            onSignUpClick()
        }
    }

    val hintStyle = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Light,
        color = colorScheme.tertiary,
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = colorScheme.primary)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TopLogoSection()

        RegisterFieldLabel(text = stringResource(R.string.full_name))
        CustomTextField(
            value = state.fullName,
            onValueChange = { viewModel.onIntent(RegisterIntent.OnFullNameChange(it)) },
            ColorScheme = colorScheme,
            hintText = stringResource(R.string.enter_your_full_name),
            hintStyle = hintStyle,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            errorText = state.fullNameError
        )

        RegisterFieldLabel(text = stringResource(R.string.mobile_number))
        CustomTextField(
            value = state.mobileNumber,
            onValueChange = { viewModel.onIntent(RegisterIntent.OnMobileChange(it)) },
            ColorScheme = colorScheme,
            hintText = stringResource(R.string.enter_your_mobile_no),
            hintStyle = hintStyle,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            errorText = state.mobileError
        )

        RegisterFieldLabel(text = stringResource(R.string.email_address))
        CustomTextField(
            value = state.email,
            onValueChange = { viewModel.onIntent(RegisterIntent.OnEmailChange(it)) },
            ColorScheme = colorScheme,
            hintText = stringResource(R.string.enter_your_email_address),
            hintStyle = hintStyle,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            errorText = state.emailError
        )

        RegisterFieldLabel(text = stringResource(R.string.password))
        CustomTextField(
            value = state.password,
            onValueChange = { viewModel.onIntent(RegisterIntent.OnPasswordChange(it)) },
            ColorScheme = colorScheme,
            hintText = stringResource(R.string.enter_your_password),
            hintStyle = hintStyle,
            visualTransformation = if (state.passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            errorText = state.passwordError,
            trailingIcon = {
                IconButton(onClick = { viewModel.onIntent(RegisterIntent.TogglePasswordVisibility) }) {
                    Icon(
                        imageVector = if (state.passwordVisible) {
                            Icons.Default.Visibility
                        } else {
                            Icons.Default.VisibilityOff
                        },
                        contentDescription = if (state.passwordVisible) "Hide password" else "Show password",
                        tint = colorScheme.tertiary,
                    )
                }
            }
        )

        RegisterFieldLabel(text = stringResource(R.string.confirm_password))
        CustomTextField(
            value = state.confirmPassword,
            onValueChange = { viewModel.onIntent(RegisterIntent.OnConfirmPasswordChange(it)) },
            ColorScheme = colorScheme,
            hintText = stringResource(R.string.confirm_your_password),
            hintStyle = hintStyle,
            visualTransformation = if (state.confirmPasswordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            errorText = state.confirmPasswordError,
            trailingIcon = {
                IconButton(onClick = { viewModel.onIntent(RegisterIntent.ToggleConfirmPasswordVisibility) }) {
                    Icon(
                        imageVector = if (state.confirmPasswordVisible) {
                            Icons.Default.Visibility
                        } else {
                            Icons.Default.VisibilityOff
                        },
                        contentDescription = if (state.confirmPasswordVisible) {
                            "Hide confirm password"
                        } else {
                            "Show confirm password"
                        },
                        tint = colorScheme.tertiary,
                    )
                }
            }
        )

        state.errorMessage?.let { error ->
            Text(
                text = error,
                color = colorScheme.error,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (state.isLoading) {
            CircularProgressIndicator(
                color = colorScheme.secondary,
                modifier = Modifier.padding(top = 24.dp)
            )
        } else {
            CustomElevatedButton(
                onClick = { viewModel.onIntent(RegisterIntent.SubmitRegister) },
                text = stringResource(R.string.sign_up),
                colorScheme = colorScheme,
            )
        }

        EndSection(
            text1 = stringResource(R.string.already_have_account),
            text2 = stringResource(R.string.sign_in),
            onCreateAccountClick = onNavigateToLogin,
            colorScheme = colorScheme
        )
    }
}

@Composable
private fun RegisterFieldLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = colorScheme.secondary
        ),
    )
}

@Preview(showBackground = true)
@Composable
private fun RegisterScreenPreview() {
    RouteTheme {
        RegisterScreen()
    }
}
