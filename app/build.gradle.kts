plugins { id("com.android.application"); id("org.jetbrains.kotlin.android") }
android {
    namespace = "app.luma"
    compileSdk = 35
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions { jvmTarget = "21" }
    defaultConfig { applicationId = "app.luma"; minSdk = 26; targetSdk = 35; versionCode = 1; versionName = "1.0" }
    buildFeatures { }
}