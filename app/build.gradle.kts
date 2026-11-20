plugins { id("com.android.application"); id("org.jetbrains.kotlin.android"); id("org.jetbrains.kotlin.plugin.compose") }
android { namespace = "app.luma"; compileSdk = 35; defaultConfig { applicationId = "app.luma"; minSdk = 26; targetSdk = 35; versionCode = 1; versionName = "0.1.0" }; compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }; kotlinOptions { jvmTarget = "17" }; buildFeatures { compose = true } }
dependencies {
 implementation(project(":core:ui"))
 implementation("androidx.activity:activity-compose:1.10.1")
 implementation("androidx.compose.ui:ui:1.8.1")
 implementation("androidx.compose.ui:ui-tooling-preview:1.8.1")
 implementation("androidx.compose.material3:material3:1.3.2")
 implementation("androidx.compose.material:material-icons-extended:1.7.8")
 implementation("androidx.navigation:navigation-compose:2.8.9")
 implementation("androidx.datastore:datastore-preferences:1.1.7")
 implementation("androidx.media3:media3-exoplayer:1.6.1")
 implementation("androidx.media3:media3-ui:1.6.1")
}
