package com.weesnerdevelopment.frontendutils

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.weesnerdevelopment.frontendutils.auth.Auth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import shared.auth.HashedUser
import shared.auth.User
import shared.base.Response
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val prefs: SharedPreferences,
    private val auth: Auth
) : ViewModel() {
    private val currentUserChannel = BroadcastChannel<User?>(Channel.BUFFERED)
    val currentUserFlow = currentUserChannel.asFlow()
    var currentUser: User? = null

    suspend fun getCurrentUser(result: (error: Throwable) -> Unit) = request({ auth.getUser() }, {
        if (it == null) {
            val oldTokenData = getEncodedUserFromJwt(prefs.getItem("token") ?: "")

            if (oldTokenData != null)
                login(HashedUser(oldTokenData.first, oldTokenData.second), {}, { result(it) })
        }
    }, {
        result(it)
    })

    suspend fun updateUser(user: User, success: (User?) -> Unit, fail: (Throwable) -> Unit) =
        request({ auth.update(user) }, { success(it) }, { fail(it) })

    suspend fun login(hashedUser: HashedUser, success: (User?) -> Unit, fail: (Throwable) -> Unit) =
        request({ auth.login(hashedUser) }, { success(it) }, { fail(it) })

    suspend fun signUp(user: User, success: (User?) -> Unit, fail: (Throwable) -> Unit) =
        request({ auth.signUp(user) }, { success(it) }, { fail(it) })

    suspend fun logout(done: () -> Unit) = auth.logout {
        currentUser = null

        currentUserChannel.offer(null)
        done()
    }

    private inline fun request(
        request: () -> Response,
        success: (User?) -> Any = {},
        fail: (Throwable) -> Unit = {}
    ) = wrappedNetworkRequest<User?>(request) {
        it.onSuccess {
            success(it)
            currentUser = it

            currentUserChannel.offer(it)
        }
        it.onFailure {
            fail(it)
            currentUser = null

            currentUserChannel.offer(null)
        }
    }
}
