import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Properties

plugins {
    id("com.android.application")
}

val versionCounterFile = rootProject.file("build-version-counter.properties")
val versionCounterProps = Properties()
if (versionCounterFile.exists()) {
    versionCounterFile.inputStream().use { versionCounterProps.load(it) }
}

val buildDay = SimpleDateFormat("yyyyMMdd", Locale.ROOT).format(Date())
val lastBuildDay = versionCounterProps.getProperty("day", "")
val previousCount = versionCounterProps.getProperty("count", "0").toIntOrNull() ?: 0
val nextCount = if (lastBuildDay == buildDay) (previousCount + 1).coerceAtMost(99) else 1
versionCounterProps.setProperty("day", buildDay)
versionCounterProps.setProperty("count", nextCount.toString())
versionCounterFile.outputStream().use { versionCounterProps.store(it, "Auto-generated build version counter") }

val buildVersionName = "%s%02d".format(Locale.ROOT, buildDay, nextCount)
val buildVersionCode = buildVersionName.toInt()

android {
    namespace = "com.android.ads"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.android.ads"
        minSdk = 28
        targetSdk = 36
        versionName = buildVersionName
        versionCode = buildVersionCode
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }

}

dependencies {
    compileOnly("io.github.libxposed:api:101.0.0")
}
