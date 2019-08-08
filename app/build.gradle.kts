import com.android.tools.build.bundletool.utils.Versions

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("com.squareup.sqldelight")
    id("kotlinx-serialization")
}

android {
    compileSdkVersion(29)
    buildToolsVersion("29.0.1")
    defaultConfig {
        applicationId = "net.mbonnin.photostream"
        minSdkVersion(23)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    packagingOptions {
        exclude("META-INF/INDEX.LIST")
        exclude("META-INF/*.kotlin_module")
    }
    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("androidx.appcompat:appcompat:1.0.2")
    implementation("androidx.core:core-ktx:1.0.2")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("androidx.recyclerview:recyclerview:1.1.0-beta02")
    implementation("com.squareup.picasso:picasso:2.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.0-M2")
    implementation("com.squareup.sqldelight:android-driver:1.1.4")
    implementation("com.squareup.sqldelight:coroutines-extensions-jvm:1.1.4")
    implementation("io.ktor:ktor-client-core-jvm:1.2.2")
    implementation("io.ktor:ktor-client-json-jvm:1.2.2")
    implementation("io.ktor:ktor-client-okhttp:1.2.2")
    implementation("io.ktor:ktor-client-serialization-jvm:1.2.2")

    testImplementation("junit:junit:4.12")

    androidTestImplementation("androidx.test:runner:1.2.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.2.0")
}
