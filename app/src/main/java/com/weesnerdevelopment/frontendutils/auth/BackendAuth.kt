package com.weesnerdevelopment.frontendutils.auth

import android.content.SharedPreferences
import com.weesnerdevelopment.frontendutils.*
import okhttp3.OkHttpClient
import shared.auth.HashedUser
import shared.auth.TokenResponse
import shared.auth.User
import shared.base.HttpStatus
import shared.base.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackendAuth @Inject constructor(
	private val prefs: SharedPreferences,
	okHttpClient: OkHttpClient
) : Auth {
	private val httpClient = okHttpClient.create<AuthApi>(AUTH_URL)

	private val token
		get() = prefs.getItem("token")?.asBearer
			?: throw IllegalArgumentException("somehow token was empty..")

	override suspend fun getUser(): Response = httpClient.getAccountInfo(token)

	override suspend fun signUp(user: User): Response {
		val response = attemptNetworkRequest { httpClient.signUp(user) }

		val token = response.message.parse<TokenResponse>().token
			?: throw IllegalArgumentException("Something happened getting token")

		prefs.saveItem("token", token)
		return getUser()
	}

	override suspend fun login(user: HashedUser): Response {
		val response = attemptNetworkRequest { httpClient.login(user) }

		val token = response.message.parse<TokenResponse>().token
			?: throw IllegalArgumentException("Something happened getting token")

		prefs.saveItem("token", token)
		return getUser()
	}

	override suspend fun update(user: User) = httpClient.updateUser(token, user)

	override suspend fun logout(complete: () -> Unit): Response {
		prefs.removeItem("token").also { complete() }
		return Response(HttpStatus.OK, "Successfully logged out")
	}
}
