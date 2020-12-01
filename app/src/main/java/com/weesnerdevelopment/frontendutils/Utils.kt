package com.weesnerdevelopment.frontendutils

import android.content.SharedPreferences
import android.util.Base64
import androidx.core.content.edit
import kimchi.Kimchi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import shared.base.Response
import shared.base.ServerError
import shared.fromJson
import shared.toJson
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * Helper val to create retrofit builder for [OkHttpClient].
 */
val OkHttpClient.retrofit
    get() = Retrofit.Builder().client(this).addConverterFactory(MoshiConverterFactory.create())

/**
 * Base Url for the backend.
 */
const val BASE_URL = "http://api.weesnerdevelopment.com"

/**
 * Auth base endpoint.
 */
const val AUTH_URL = "$BASE_URL/user/"

/**
 * Helper function to create [OkHttpClient] with a retrofit builder and network logging.
 */
inline fun <reified T> OkHttpClient.create(baseUrl: String) = newBuilder()
    .addInterceptor(HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
    .build()
    .retrofit
    .baseUrl(baseUrl)
    .build()
    .create(T::class.java)


inline fun attemptNetworkRequest(attempt: () -> Response) = try {
    attempt()
} catch (e: HttpException) {
    val serverError = e.response()?.errorBody()?.string().parse<ServerError>()
    throw ServerException(serverError)
}

/**
 * Helper to do a network request and catch [Exception]s if there is an error.
 */
inline fun <reified T> wrappedNetworkRequest(
	trying: () -> Response,
	result: (Result<T>) -> Result<T>
) {
    try {
        trying().message.parse<T>().also {
            result(Result.success(it))
        }
    } catch (e: ServerException) {
        result(Result.failure(e))
    } catch (e: ConnectException) {
        result(Result.failure(e))
    } catch (e: HttpException) {
        result(Result.failure(e))
    } catch (e: SocketTimeoutException) {
        result(Result.failure(e))
    } catch (e: NullPointerException) {
        Kimchi.info("Network request for ${T::class} was empty")
    }
}

/**
 * Helper to do a network request and catch [Exception]s if there is an error.
 */
inline fun <reified T> networkRequest(
	trying: () -> T,
	result: (Result<T>) -> Result<T>
) {
    try {
        trying().also {
            result(Result.success(it))
        }
    } catch (e: ServerException) {
        result(Result.failure(e))
    } catch (e: ConnectException) {
        result(Result.failure(e))
    } catch (e: HttpException) {
        result(Result.failure(e))
    } catch (e: SocketTimeoutException) {
        result(Result.failure(e))
    } catch (e: NullPointerException) {
        Kimchi.info("Network request for ${T::class} was empty")
    }
}

/**
 * Helper function to parse a [Response] as [T].
 */
inline fun <reified T> request(request: () -> Response?) = request()?.message.parse<T>()

/**
 * Helper function to create a [networkRequest] with [success] and [fail] results in place.
 */
inline fun <reified T> request(
	request: () -> T,
	success: (T?) -> Unit = {},
	fail: (Throwable) -> Unit = {}
) = networkRequest<T?>(request) {
    it.onSuccess { success(it) }
    it.onFailure { fail(it) }
}

/**
 * Parse the value as [T].
 */
inline fun <reified T> Any?.parse() = when (this) {
	is String -> fromJson<T>()
		?: throw IllegalArgumentException("An error occurred getting message from response")
    else -> toJson()?.fromJson<T>()
        ?: throw IllegalArgumentException("An error occurred getting message from response")
}

/**
 * Server Exception that happens in a 4XX level http status code.
 */
data class ServerException(val error: ServerError) : Throwable()

/**
 * Converts the given string to the default currency.
 */
val String.asMoney: String get() = NumberFormat.getCurrencyInstance().format(this.toDouble())

/**
 * Converts the given string to a [Date].
 */
val String.toDate: Date? get() = SimpleDateFormat.getDateInstance().parse(this)

/**
 * Default date format to be used.
 */
val dateFormat: DateTimeFormatter? = DateTimeFormatter.ofPattern("MMM dd, yyyy")

/**
 * Converts the epoch time to a formatted date string.
 */
val Long.toDateString: String
    get() = LocalDate.ofEpochDay(this / (24 * 60 * 60 * 1000)).format(dateFormat)

/**
 * Converts the given [LocalDate] to UTC epoch millis.
 */
val LocalDate.toUTCMillis
    get() = Clock.fixed(
		this.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneId.of(ZoneOffset.UTC.id)
	).millis()

/**
 * Converts a string to a [Base64] encoded [ByteArray] and gives its contents.
 */
val String.encode get() = String(Base64.encode(this.toByteArray(), Base64.NO_WRAP))

/**
 * Maps the given string to a "Auth Bearer" string.
 */
val String.asBearer get() = "Bearer $this"

/**
 * Save the [key]: [value] pair to shared preferences.
 */
fun SharedPreferences.saveItem(key: String, value: String) = edit { putString(key, value) }

/**
 * get the item from shared preferences matching the given [key].
 */
fun SharedPreferences.getItem(key: String) = getString(key, "")

/**
 * Deletes the item with the given [key] from shared preferences.
 */
fun SharedPreferences.removeItem(key: String) = edit { remove(key) }

/**
 * Decodes the auth token to get the needed user info out of it.
 */
fun getEncodedUserFromJwt(jwt: String): Pair<String, String>? {
    if (jwt.isBlank()) return null

    try {
        val parts = jwt.split(".")
        val tokenBytes = parts[1].toByteArray(charset("UTF-8"))
        val userInfoFromToken = String(Base64.decode(tokenBytes, Base64.DEFAULT)).split(",")
            .filter { it.contains("attr-") }
        val userInfo = userInfoFromToken.map { it.split(":")[1].replace("\"".toRegex(), "") }
        return userInfo[0] to userInfo[1]
    } catch (e: Exception) {
        throw RuntimeException("Couldnt decode jwt $jwt", e)
    }
}

/**
 * Gets the next [timeFrame] after the given epoch millis.
 */
fun Long.next(timeFrame: String): Long {
    val timeFrameSplit = timeFrame.split(" ")
    val date = LocalDateTime.ofInstant(Date(this).toInstant(), ZoneId.of(ZoneOffset.UTC.id))

    val addDays = when (timeFrameSplit[1]) {
		"Week" -> 7
		"Month" -> date.toLocalDate().month.maxLength()
		"Day" -> 1
        else -> throw IllegalArgumentException("${timeFrameSplit[1]} is not a valid type.")
    } * timeFrameSplit[0].toInt()

    return date.plusDays(addDays.toLong()).toInstant(ZoneOffset.UTC).toEpochMilli()
}

