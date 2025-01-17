@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    id("kotlin-parcelize")
    id("embrace-swazzler")
}

android {
    namespace = "meshki.studio.negarname"
    compileSdk = 34

    defaultConfig {
        applicationId = "meshki.studio.negarname"
        minSdk = 21
        targetSdk = 34
        versionCode = 3
        versionName = "1.2"

        resourceConfigurations.plus(listOf("en", "fa"))
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            applicationIdSuffix = ".debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    androidResources {
        generateLocaleConfig = true
    }
}

swazzler {
    disableComposeDependencyInjection.set(false)
}

dependencies {
    implementation(project(":Kalendar:kalendar"))
    // implementation(project(":Kalendar:kalendar-endlos"))
    implementation(libs.kotlin.serialization)
    implementation(libs.kotlin.datetime)
    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.koin.compose.navigation)
    implementation(libs.compose.navigation)
    implementation(libs.compose.navigation.runtime)
    implementation(libs.room.runtime)
    implementation(libs.androidx.datastore.preferences.core)
    ksp(libs.room.compiler)
    implementation(libs.room.ktx)
    implementation(libs.gson)
    implementation(libs.richtext)
    implementation(libs.timber)
    implementation(libs.datastore)
    implementation("io.embrace:embrace-android-bug-shake:0.9.0")

    implementation("com.github.lincollincol:amplituda:2.2.2")
    implementation("com.github.lincollincol:compose-audiowaveform:1.1.1")
    implementation("com.github.samanzamani:PersianDate:1.7.1")
    implementation("com.github.zj565061763:compose-wheel-picker:1.0.0-beta04")
    implementation(libs.splashscreen)
    implementation(libs.zip4j)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}
