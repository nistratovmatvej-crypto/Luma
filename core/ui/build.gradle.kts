plugins { id("com.android.library"); id("org.jetbrains.kotlin.android"); id("org.jetbrains.kotlin.plugin.compose") }
android { namespace = "app.luma.core.ui"; compileSdk = 35; defaultConfig { minSdk = 26 }; buildFeatures { compose = true }; compileOptions { sourceCompatibility = JavaVersion.VERSION_17; targetCompatibility = JavaVersion.VERSION_17 }; kotlinOptions { jvmTarget = "17" } 
dependencies { implementation("androidx.compose.ui:ui:1.8.1"); implementation("androidx.compose.material3:material3:1.3.2") }
