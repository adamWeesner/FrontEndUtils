package com.weesnerdevelopment.frontendutils

import android.content.Context
import android.content.SharedPreferences
import com.weesnerdevelopment.frontendutils.auth.Auth
import com.weesnerdevelopment.frontendutils.auth.BackendAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FrontEndUtilsModule {
    @Provides
    fun provideOkHttp() = OkHttpClient.Builder().build()

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)

    @Provides
    fun provideAuthMiddleware(
        prefs: SharedPreferences,
        okHttpClient: OkHttpClient
    ): Auth = BackendAuth(prefs, okHttpClient)
}
