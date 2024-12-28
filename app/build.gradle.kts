plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}


android {
    namespace = "com.example.courseonline"
    compileSdk = 34

    defaultConfig {

        applicationId = "com.example.courseonline"
        minSdk = 31
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        buildConfigField(
                "String",
                "GEMINI_API_KEY",
                "\"${project.findProperty("GEMINI_API_KEY")}\""
        )
        buildConfigField(
                "String",
                "GOOGLE_SIGN_IN_KEY",
                "\"${project.findProperty("GOOGLE_SIGN_IN_KEY")}\""
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
        }
    }
}

dependencies {
    implementation("com.google.auth:google-auth-library-oauth2-http:1.12.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.github.hani-momanii:SuperNova-Emoji:1.1")
    // Zalo
    implementation("com.squareup.okhttp3:okhttp:4.6.0")
    implementation("commons-codec:commons-codec:1.14")
    // add the dependency for the Google AI client SDK for Android
    implementation("com.squareup.picasso:picasso:2.8")
    implementation("androidx.work:work-runtime-ktx:2.7.1")
    implementation("com.google.ai.client.generativeai:generativeai:0.7.0")
    // Required for one-shot operations (to use `ListenableFuture` from Guava Android)
    implementation("com.google.guava:guava:31.0.1-android")
    // Required for streaming operations (to use `Publisher` from Reactive Streams)
    implementation("org.reactivestreams:reactive-streams:1.0.4")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
//    implementation("com.squareup.picasso:picasso:2.8")
    implementation("com.ms-square:expandableTextView:0.1.4")
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")
    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:custom-ui:12.0.0")
    implementation("com.google.android.material:material:1.9.0")
    implementation("com.borax12.materialdaterangepicker:library:2.0")
//    implementation("io.github.glailton.expandabletextview:expandabletextview:1.0.4")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth-ktx:22.1.2")
    implementation("commons-io:commons-io:2.6")
//    implementation("com.github.Yalantis:SearchFilter:v1.0.4")
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("nl.joery.animatedbottombar:library:1.1.0")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-firestore:25.0.0")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.android.gms:play-services-wallet:19.2.1")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.2.0-alpha01")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.github.bmelnychuk:atv:1.2.+")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation(fileTree(mapOf("dir" to "D:\\CourseOnline\\app\\libs\\zaloLibs", "include" to listOf("*.aar", "*.jar"), "exclude" to listOf(""))))
    implementation("com.google.firebase:firebase-messaging:24.0.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}