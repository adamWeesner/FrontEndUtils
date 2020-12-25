package com.weesnerdevelopment.frontendutils

import androidx.annotation.StringRes
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch
import shared.auth.HashedUser
import shared.auth.InvalidUserException
import shared.auth.InvalidUserReason

private data class LoginError(@StringRes var message: Int? = null, var enabled: Boolean)

@Composable
fun LoginLayout(
    auth: AuthViewModel?,
    createUser: () -> Unit,
    other: () -> Unit,
    spaceMedium: Dp = 8.dp,
    spaceBetween: Dp = 16.dp
) {
    val (error, setError) = remember {
        mutableStateOf(LoginError(R.string.server_down_message, false))
    }

    val (username, setUsername) = remember { mutableStateOf("") }
    val (password, setPassword) = remember { mutableStateOf("") }

    val loginPressed = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    if (loginPressed.value) {
        loginPressed.value = false
        scope.launch {
            auth?.login(
                hashedUser = HashedUser(username.encode, password.encode),
                success = {
                    if (it != null) other()
                    else setError(LoginError(R.string.no_user_message, true))
                },
                fail = {
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

    Box(Modifier.fillMaxSize().padding(spaceBetween)) {
        Column(Modifier.fillMaxWidth().align(Alignment.Center)) {
            TextInput(
                label = R.string.username,
                oldValue = username,
                modifier = Modifier.fillMaxWidth().padding(bottom = spaceBetween)
            ) {
                setError(LoginError(enabled = false))
                setUsername(it)
            }

            TextInput(
                label = R.string.password,
                keyboardType = KeyboardType.Password,
                oldValue = password,
                modifier = Modifier.fillMaxWidth().padding(bottom = spaceBetween)
            ) {
                setError(LoginError(enabled = false))
                setPassword(it)
            }

            TextButton(
                onClick = { loginPressed.value = true },
                modifier = Modifier.fillMaxWidth().padding(bottom = spaceBetween)
            ) {
                Text(
                    text = stringResource(R.string.login),
                    modifier = Modifier.padding(vertical = spaceMedium)
                )
            }

            TextButton(
                onClick = createUser,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.create_account),
                    modifier = Modifier.padding(vertical = spaceMedium)
                )
            }
        }

        if (error.enabled && error.message != null) {
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

@Preview(showBackground = true)
@Composable
private fun previewLoginLayout() {
    LoginLayout(null, {}, {})
}