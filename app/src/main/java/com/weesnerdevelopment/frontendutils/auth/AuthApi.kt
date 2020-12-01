package com.weesnerdevelopment.frontendutils.auth

import retrofit2.http.*
import shared.auth.HashedUser
import shared.auth.User
import shared.base.Response

interface AuthApi {
	@GET("account")
	suspend fun getAccountInfo(@Header("Authorization") authHeader: String): Response

	@POST("login")
	suspend fun login(@Body user: HashedUser): Response

	@POST("signUp")
	suspend fun signUp(@Body user: User): Response

	@PUT(".")
	suspend fun updateUser(@Header("Authorization") authHeader: String, @Body user: User): Response
}
