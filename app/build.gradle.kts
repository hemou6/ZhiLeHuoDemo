plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.zhilehuodemo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.zhilehuodemo"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    viewBinding{
        enable=true
    }
}

dependencies {
    // Material Design
    implementation("com.google.android.material:material:1.11.0")
    //MMKV
    implementation("com.tencent:mmkv:1.2.13")
    //Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt("com.github.bumptech.glide:compiler:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.2")
    implementation("com.github.bumptech.glide:okhttp3-integration:4.13.2")
    //时间选择器
    implementation("com.github.gzu-liyujiang.AndroidPicker:WheelPicker:4.0.1")
    implementation("com.github.gzu-liyujiang.AndroidPicker:Common:4.0.1")
    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    //SmartRefreshLayout
    implementation("io.github.scwang90:refresh-layout-kernel:3.0.0-alpha")
    implementation("io.github.scwang90:refresh-header-classics:3.0.0-alpha")
    implementation("io.github.scwang90:refresh-footer-classics:3.0.0-alpha")

    // EventBus
    implementation("org.greenrobot:eventbus:3.3.1")
    //ViewPager2
    implementation("androidx.viewpager2:viewpager2:1.1.0-alpha01")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}