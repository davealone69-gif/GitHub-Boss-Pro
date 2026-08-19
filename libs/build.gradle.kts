plugins {
    id("githubboss.android.library")
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.davealone69.githubboss.libs"

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3)
}
