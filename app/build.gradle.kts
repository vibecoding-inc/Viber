plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.vibecoding.viber"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.vibecoding.viber"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // GitHub OAuth configuration
        val githubClientId = project.findProperty("OAUTH_CLIENT_ID") as? String 
            ?: System.getenv("OAUTH_CLIENT_ID") 
            ?: ""
        
        val githubClientSecret = project.findProperty("OAUTH_CLIENT_SECRET") as? String
            ?: System.getenv("OAUTH_CLIENT_SECRET")
            ?: ""
        
        // GitHub App configuration
        val githubAppId = project.findProperty("GITHUB_APP_ID") as? String
            ?: System.getenv("GITHUB_APP_ID")
            ?: ""
        
        val githubAppPrivateKey = project.findProperty("GITHUB_APP_PRIVATE_KEY") as? String
            ?: System.getenv("GITHUB_APP_PRIVATE_KEY")
            ?: ""
        
        val githubAppInstallationId = project.findProperty("GITHUB_APP_INSTALLATION_ID") as? String
            ?: System.getenv("GITHUB_APP_INSTALLATION_ID")
            ?: ""
        
        if (githubClientId.isEmpty()) {
            logger.warn("WARNING: OAUTH_CLIENT_ID is not set. OAuth authentication will not work.")
        }
        
        if (githubClientSecret.isEmpty()) {
            logger.warn("WARNING: OAUTH_CLIENT_SECRET is not set. OAuth authentication will not work.")
        }
        
        if (githubAppId.isEmpty()) {
            logger.warn("INFO: GITHUB_APP_ID is not set. GitHub App authentication will not be available.")
        }
        
        // OAuth App config
        buildConfigField("String", "GITHUB_CLIENT_ID", "\"$githubClientId\"")
        buildConfigField("String", "GITHUB_CLIENT_SECRET", "\"$githubClientSecret\"")
        buildConfigField("String", "GITHUB_REDIRECT_URI", "\"viber://oauth/callback\"")
        
        // GitHub App config
        buildConfigField("String", "GITHUB_APP_ID", "\"$githubAppId\"")
        buildConfigField("String", "GITHUB_APP_PRIVATE_KEY", "\"${githubAppPrivateKey.replace("\n", "\\n")}\"")
        buildConfigField("String", "GITHUB_APP_INSTALLATION_ID", "\"$githubAppInstallationId\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.8.5")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Dependency Injection
    implementation("com.google.dagger:hilt-android:2.50")
    kapt("com.google.dagger:hilt-android-compiler:2.50")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Markdown rendering
    implementation("io.noties.markwon:core:4.6.2")
    implementation("io.noties.markwon:image-coil:4.6.2")

    // Browser
    implementation("androidx.browser:browser:1.8.0")

    // JWT for GitHub App authentication
    implementation("io.jsonwebtoken:jjwt-api:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-gson:0.12.5")
    
    // BouncyCastle for PEM key parsing
    implementation("org.bouncycastle:bcprov-jdk18on:1.77")
    implementation("org.bouncycastle:bcpkix-jdk18on:1.77")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.12.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
