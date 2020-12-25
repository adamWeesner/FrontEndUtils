package com.weesnerdevelopment.frontendutils

import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kimchi.Kimchi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import shared.auth.HashedUser
import shared.auth.InvalidUserException
import shared.auth.InvalidUserReason
import shared.auth.User

@Composable
fun CreateAccountLayout(
    auth: AuthViewModel?,
    other: () -> Unit,
    inputType: TextInputType = TextInputType.Normal,
    spaceMedium: Dp = 8.dp,
    spaceBetween: Dp = 16.dp
) {
    val (error, setError) = remember {
        mutableStateOf(LoginError(R.string.server_down_message, false))
    }

    val (name, setName) = remember { mutableStateOf("") }
    val (email, setEmail) = remember { mutableStateOf("") }
    val (username, setUsername) = remember { mutableStateOf("") }
    val (password, setPassword) = remember { mutableStateOf("") }

    val (createAccount, setCreateAccount) = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    if (createAccount) createAccountLogic(
        scope,
        auth,
        User(
            name = name,
            email = email,
            username = username.encode,
            password = password.encode
        ),
        other,
        setError
    )

    Box(Modifier.fillMaxSize().padding(spaceBetween)) {
        Column(Modifier.fillMaxWidth().align(Alignment.Center)) {
            TextInput(
                label = R.string.name,
                oldValue = name,
                modifier = Modifier.fillMaxWidth().padding(bottom = spaceBetween),
                textInputType = inputType
            ) {
                setError(LoginError(enabled = false))
                setName(it)
            }

            TextInput(
                label = R.string.email,
                oldValue = email,
                modifier = Modifier.fillMaxWidth().padding(bottom = spaceBetween),
                textInputType = inputType
            ) {
                setError(LoginError(enabled = false))
                setEmail(it)
            }

            TextInput(
                label = R.string.username,
                oldValue = username,
                modifier = Modifier.fillMaxWidth().padding(bottom = spaceBetween),
                textInputType = inputType
            ) {
                setError(LoginError(enabled = false))
                setUsername(it)
            }

            TextInput(
                label = R.string.password,
                keyboardType = KeyboardType.Password,
                oldValue = password,
                modifier = Modifier.fillMaxWidth().padding(bottom = spaceBetween),
                textInputType = inputType
            ) {
                setError(LoginError(enabled = false))
                setPassword(it)
            }

            TextButton(
                onClick = { setCreateAccount(true) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.create),
                    modifier = Modifier.padding(vertical = spaceMedium)
                )
            }
        }

        if (error.enabled && error.message != null) {
            setName("")
            setEmail("")
            setUsername("")
            setPassword("")
            Text(
                text = stringResource(error.message!!),
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colors.error
            )
        }
    }
}

private fun createAccountLogic(
    scope: CoroutineScope,
    auth: AuthViewModel?,
    user: User,
    other: () -> Unit,
    setError: (LoginError) -> Unit
) {
    scope.launch {
        auth?.signUp(
            user = user,
            success = {
                if (it != null) other()
                else setError(LoginError(R.string.no_user_message, true))
            },
            fail = {
                Kimchi.error("Threw signing user up", it)
                when (it) {
                    is ServerException -> {
                        when (it.error.message.parse<InvalidUserException>().reasonCode) {
                            InvalidUserReason.NoUserFound.code ->
                                setError(LoginError(R.string.no_user_message, true))
                            InvalidUserReason.InvalidUserInfo.code ->
                                setError(LoginError(R.string.invalid_data_message, true))
                            else ->
                                setError(LoginError(R.string.failed_login_message, true))
                        }
                    }
                    else -> setError(LoginError(R.string.server_down_message, true))
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun previewCreateAccountLayout() {
    CreateAccountLayout(null, {})
}