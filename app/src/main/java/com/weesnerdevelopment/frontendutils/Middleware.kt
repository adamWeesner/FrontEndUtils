package com.weesnerdevelopment.frontendutils

import shared.base.GenericItem
import shared.base.Response

/**
 * Middleware for connecting to the backend.
 */
interface Middleware<T : GenericItem> {
	/**
	 * Add and item of type [GenericItem] to the database.
	 */
	suspend fun add(item: T): Response

	/**
	 * Remove the given [queryParam] from the database.
	 */
	suspend fun remove(queryParam: String): Response

	/**
	 * Update the [item].
	 */
	suspend fun update(item: T): Response

	/**
	 * Get the item with the given [queryParam].
	 */
	suspend fun getOne(queryParam: String): T?

	/**
	 * Get all of the items.
	 */
	suspend fun getAll(): List<T>
}
