import com.android.tools.build.bundletool.utils.Versions

// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    extra["kotlin_version"] = "1.3.41"
    repositories {
        google()
        jcenter()

    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.4.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${extra["kotlin_version"]}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${extra["kotlin_version"]}")
        classpath("com.squareup.sqldelight:gradle-plugin:1.1.4")
    }
}

allprojects {
    repositories {
        google()
        jcenter()

    }
}
