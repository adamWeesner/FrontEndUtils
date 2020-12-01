package com.weesnerdevelopment.frontendutils.auth

import shared.auth.HashedUser
import shared.auth.User
import shared.base.Response

interface Auth {
	suspend fun getUser(): Response

	suspend fun signUp(user: User): Response

	suspend fun login(user: HashedUser): Response

	suspend fun update(user: User): Response

	suspend fun logout(complete: () -> Unit): Response
}
