package com.weesnerdevelopment.frontendutils

import android.os.Bundle
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import kimchi.Kimchi
import kimchi.analytics.AnalyticsWriter
import kimchi.analytics.Property

class GoogleAnalytics : AnalyticsWriter {
	override fun writeEvent(name: String, properties: List<Property<Any>>) {
		Firebase.analytics.logEvent(name) {
			properties.forEach {
				when (it.value) {
					is String -> param(it.key, it.value as String)
					is Long -> param(it.key, it.value as Long)
					is Bundle -> param(it.key, it.value as Bundle)
					is Double -> param(it.key, it.value as Double)
					else -> Kimchi.warn("Logged an even for a type not supported ${it.value::class.java}")
				}
			}
		}
	}

	override fun writeProperties(properties: List<Property<Any>>) {
		properties.forEach { Firebase.analytics.setUserProperty(it.key, it.value.toString()) }
	}

	override fun writeScreen(name: String, properties: List<Property<Any>>) {
		Kimchi.info("Wrote screen.. firebase needs an activity.. $name")
	}
}
