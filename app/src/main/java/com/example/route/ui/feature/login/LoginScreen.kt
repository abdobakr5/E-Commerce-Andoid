package com.example.route.ui.feature.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.route.R
import com.example.route.ui.core.components.CustomElevatedButton
import com.example.route.ui.core.components.CustomTextField

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel(factory = LoginViewModel.factory(LocalContext.current)),
    onLoginClick: () -> Unit = {},
    onCreateAccountClick: () -> Unit = {},
) {
    val colorScheme = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) {
            onLoginClick()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = colorScheme.primary)
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TopLogoSection()
        WelcomeText()
        WelcomeTextSecond()
        UserText()
        CustomTextField(
            value = state.email,
            onValueChange = { viewModel.onIntent(LoginIntent.OnEmailChange(it)) },
            ColorScheme = colorScheme,
            hintText = stringResource(R.string.enter_your_email),
            hintStyle = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Light,
                color = colorScheme.tertiary,
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            errorText = state.emailError
        )
        PassText()
        CustomTextField(
            value = state.password,
            onValueChange = { viewModel.onIntent(LoginIntent.OnPasswordChange(it)) },
            ColorScheme = colorScheme,
            hintText = stringResource(R.string.enter_your_password),
            hintStyle = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Light,
                color = colorScheme.tertiary,
            ),
            visualTransformation = if (state.passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            errorText = state.passwordError,
            trailingIcon = {
                IconButton(onClick = { viewModel.onIntent(LoginIntent.TogglePasswordVisibility) }) {
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
        ForgetPassText()

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
                onClick = { viewModel.onIntent(LoginIntent.SubmitLogin) },
                text = stringResource(R.string.login),
                colorScheme = colorScheme,
            )
        }

        EndSection(
            text1 = stringResource(R.string.dont_have_account),
            text2 = stringResource(R.string.create_account),
            onCreateAccountClick = onCreateAccountClick,
            colorScheme = colorScheme
        )
    }
}

@Composable
fun TopLogoSection(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.logo),
        contentDescription = "Route Logo",
        modifier = Modifier
            .size(width = 200.dp, height = 100.dp)
            .padding(top = 50.dp)
    )
}

@Composable
fun WelcomeText(modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    Text(
        text = "Welcome Back To Route",
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 26.dp)
        ,
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.secondary
        ),
        )
}

@Composable
fun WelcomeTextSecond(modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    Text(
        text = "Please sign in with your mail",
        modifier = modifier
            .fillMaxWidth()
        ,
        style = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Light,
            color = colorScheme.secondary
        ),
    )
}

@Composable
fun UserText(modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    Text(
        text = "Email",
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
        ,
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = colorScheme.secondary
        ),
    )
}

@Composable
fun PassText(modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    Text(
        text = "Password",
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
        ,
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = colorScheme.secondary
        ),
    )
}

@Composable
fun ForgetPassText(modifier: Modifier = Modifier) {
    val colorScheme = MaterialTheme.colorScheme
    Text(
        text = "Forgot password",
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
        ,
        style = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.W400,
            color = colorScheme.secondary
        ),
        textAlign = TextAlign.End
    )
}

@Composable
fun EndSection(modifier: Modifier = Modifier,
                  colorScheme: ColorScheme,
                  text1: String,
                  text2: String,
                  onCreateAccountClick:() -> Unit ) {
    val colorScheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 16.dp ),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text1,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = colorScheme.secondary
            ),
            )
        TextButton(
            onClick = onCreateAccountClick,
        ){
            Text(
                text = text2,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.secondary
                ),
            )
        }
    }
}