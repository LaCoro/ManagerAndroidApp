import com.android.build.gradle.internal.dsl.BuildType
import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

repositories {
    mavenCentral()
    google()
}

android {
    compileSdkVersion(Dependencies.AndroidBuild.compileSDK)
    defaultConfig {
        applicationId = "co.lacoro.admin"
        minSdkVersion(Dependencies.AndroidBuild.minSDK)
        targetSdkVersion(Dependencies.AndroidBuild.targetSDK)
        versionCode = 1
        versionName = "1.0"
    }



    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            resValue("string", "app_version", "v${defaultConfig.versionName}(${defaultConfig.versionCode})")
            setupVariables("production.env", this)
        }

        getByName("debug") {
            resValue("string", "app_version", "v${defaultConfig.versionName}(${defaultConfig.versionCode})_debug")
            firebaseCrashlytics {
                mappingFileUploadEnabled = false
            }
            setupVariables("development.env", this)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    api(project(":z-validations-library"))
    api(project(":core-data-ace")) {
        isTransitive = true
    }
    implementation("com.google.firebase:firebase-messaging:20.3.0")
    implementation("com.google.firebase:firebase-crashlytics:17.2.2")
    implementation("de.keyboardsurfer.android.widget:crouton:1.8.5@aar") {
        exclude(group = "com.google.android", module = "support-v4")
    }
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("com.google.android.material:material:1.2.1")
    implementation("com.google.firebase:firebase-core:17.5.1")
    implementation(group = "com.pubnub", name = "pubnub-gson", version = "4.20.0")
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.4.0")

}

fun setupVariables(environmentPropertiesFile: String, buildType: BuildType){

    val local = Properties()
    val localProperties: File = rootDir.resolve(environmentPropertiesFile)
    if (localProperties.exists()) {
        localProperties.inputStream().use { local.load(it) }
    }else{
        RuntimeException("Missing file $environmentPropertiesFile in the root project")
    }

    buildType.resValue("string", "parse_base_url",local.getProperty("BASE_URL",""))
    buildType.resValue("string","parse_application_id",local.getProperty("PARSE_APPLICATION_ID",""))
    buildType.resValue("string","parse_client_id",local.getProperty("PARSE_CLIENT_ID",""))

    buildType.resValue("string","pubnub_subscribe_key",local.getProperty("PUBLISH_KEY",""))
    buildType.resValue("string","pubnub_publish_key",local.getProperty("SUBSCRIBE_KEY",""))
}

