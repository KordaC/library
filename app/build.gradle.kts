import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
}

val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}
fun resolveFromHost(props: Properties, defaultHost: String = "10.0.2.2"): String {
    val host = props.getProperty("backend.host")?.trim().orEmpty().ifEmpty { defaultHost }
    val https = props.getProperty("backend.https", "false")
        .equals("true", ignoreCase = true)
    val scheme = if (https) "https" else "http"
    val port = props.getProperty("backend.port")?.trim()
        ?: if (https) "443" else "8080"
    val portPart = when {
        https && port == "443" -> ""
        !https && port == "80" -> ""
        !https && port == "8080" -> ":8080"
        else -> ":$port"
    }
    return "$scheme://$host$portPart/api/v1/"
}

fun resolveCloudUrl(props: Properties): String {
    val explicit = props.getProperty("backend.url")?.trim()
        ?: throw GradleException(
            "Для release APK укажите в local.properties:\n" +
                    "backend.url=https://ваш-сервер.onrender.com/api/v1/"
        )
    var url = explicit.trimEnd('/')
    if (!url.endsWith("/api/v1")) {
        url = when {
            url.endsWith("/api") -> "$url/v1"
            else -> "$url/api/v1"
        }
    }
    return "$url/"
}

// debug → backend.host (Wi‑Fi / эмулятор), release → backend.url (Render)
val debugBackendBaseUrl = resolveFromHost(localProperties)

val keystoreProperties = Properties().apply {
    val file = rootProject.file("keystore.properties")
    if (file.exists()) {
        file.inputStream().use { load(it) }
    }
}
val releaseKeystoreFile = keystoreProperties.getProperty("storeFile")?.let { rootProject.file(it) }
val hasReleaseKeystore = releaseKeystoreFile != null && releaseKeystoreFile.isFile

android {
    namespace = "com.example.applibrary"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.applibrary"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (hasReleaseKeystore) {
            create("release") {
                storeFile = releaseKeystoreFile
                storePassword = keystoreProperties.getProperty("storePassword")
                keyAlias = keystoreProperties.getProperty("keyAlias")
                keyPassword = keystoreProperties.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"$debugBackendBaseUrl\"")
        }
        release {
            val cloudUrl = resolveCloudUrl(localProperties)
            buildConfigField("String", "BASE_URL", "\"$cloudUrl\"")
            signingConfig = if (hasReleaseKeystore) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            if (cloudUrl.contains("10.0.2.2") || cloudUrl.contains("192.168.")
                || cloudUrl.contains("localhost")
            ) {
                throw GradleException(
                    "backend.url должен быть публичным HTTPS-адресом облака, не локальным IP."
                )
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.livedata)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    implementation(libs.zxing.core)
    implementation(libs.recyclerview)
    implementation(libs.splashscreen)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
