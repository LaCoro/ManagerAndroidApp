plugins {
    id("com.android.library")
    kotlin("android")
}


buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.0")
    }
}

android {
    compileSdkVersion(Dependencies.AndroidBuild.compileSDK)
    defaultConfig {
        minSdkVersion(Dependencies.AndroidBuild.minSDK)
        targetSdkVersion(Dependencies.AndroidBuild.targetSDK)
    }
}


dependencies {
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.4.10")
}
repositories {
    mavenCentral()
}
