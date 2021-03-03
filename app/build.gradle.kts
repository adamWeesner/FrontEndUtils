plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    compileSdkVersion(30)
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
        versionCode = 3
        versionName = "1.2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        setProperty("archivesBaseName", "frontEndUtils-$versionName")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }
    buildFeatures {
        compose =true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose_version
    }
    lint {
        isAbortOnError = false
    }
}

dependencies {
    implementation(project(":shared"))

    implementation("androidx.core:core-ktx:1.3.2")
    implementation("com.google.android.material:material:1.3.0")
    implementation("androidx.compose.ui:ui:${Versions.compose_version}")
    implementation("androidx.compose.material:material:${Versions.compose_version}")
    implementation("androidx.compose.ui:ui-tooling:${Versions.compose_version}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.3.0")
    implementation("androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03")
    implementation("com.google.dagger:hilt-android:2.33-beta")
    implementation("com.squareup.retrofit2:converter-moshi:2.7.0")
    implementation("com.squareup.retrofit2:retrofit:2.7.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.7.2")

    api("com.github.Inkapplications.kimchi:kimchi:1.0.2")
    api("com.google.firebase:firebase-analytics-ktx:18.0.2")
    api("com.google.firebase:firebase-crashlytics-ktx:17.3.1")
    api("com.google.firebase:firebase-perf:19.1.1")

    kapt("androidx.hilt:hilt-compiler:1.0.0-alpha03")
    kapt("com.google.dagger:hilt-android-compiler:2.33-beta")

    testImplementation("junit:junit:4.13.1")

    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}