plugins {
    id("com.android.library")
    kotlin("android")
}

repositories {
    mavenCentral()
    google()
}


android {
    compileSdkVersion(Dependencies.AndroidBuild.compileSDK)
    defaultConfig {
        minSdkVersion(Dependencies.AndroidBuild.minSDK)
        targetSdkVersion(Dependencies.AndroidBuild.targetSDK)
    }
    buildTypes {

        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    testImplementation("junit:junit:4.13")
    api("com.parse:parse-android:1.13.1")
    implementation("com.parse.bolts:bolts-android:1.4.0")
    implementation("com.google.firebase:firebase-crashlytics:17.2.2")
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.4.10")
}
