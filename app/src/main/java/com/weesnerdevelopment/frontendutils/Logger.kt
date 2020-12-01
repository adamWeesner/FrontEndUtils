package com.weesnerdevelopment.frontendutils

import com.google.firebase.crashlytics.FirebaseCrashlytics
import kimchi.logger.LogLevel
import kimchi.logger.LogWriter

class CrashlyticsLogger : LogWriter {
	override fun log(level: LogLevel, message: String, cause: Throwable?) {
		FirebaseCrashlytics.getInstance().log("$message caused by ${cause?.message}")
	}

	override fun shouldLog(level: LogLevel, cause: Throwable?): Boolean {
		return when (level) {
			LogLevel.WARNING, LogLevel.ERROR -> true
			else -> false
		}
	}
}
