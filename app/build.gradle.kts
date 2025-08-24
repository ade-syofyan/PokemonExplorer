plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.kapt")
    alias(libs.plugins.hilt)
    //alias(libs.plugins.ksp)
}

android {
    namespace = "com.adeinsoft.pokemonexplorer"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.adeinsoft.pokemonexplorer"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    kapt {
        correctErrorTypes = true
    }

    hilt {
        enableAggregatingTask = false
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

configurations.configureEach {
    resolutionStrategy.eachDependency {
        if (requested.group == "com.squareup" && requested.name == "javapoet") {
            useVersion("1.13.0")
            because("Hilt membutuhkan ClassName.canonicalName() yang ada di javapoet >= 1.13.0")
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.ui.text)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.media3.common.ktx)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.bundles.networking)
    implementation(libs.bundles.room)
//    ksp(libs.room.compiler)
    kapt(libs.room.compiler)
    // new
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    // new
    implementation(libs.bundles.di)
    kapt(libs.hilt.compiler)
    implementation(libs.bundles.images)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.bundles.tests)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.compose.icons)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    implementation(libs.moshi.kotlin)
}