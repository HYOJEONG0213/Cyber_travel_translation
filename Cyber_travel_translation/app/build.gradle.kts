import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
}


//val properties = Properties()
//properties.load(project.rootProject.file("local.properties").newDataInputStream())

android {
    namespace = "com.example.cyber_travel_translation"
    compileSdk = 34

    val properties = Properties()
    //properties.load(FileInputStream(rootProject.file("local.properties")))
    properties.load(FileInputStream(rootProject.file("local.properties")))

    defaultConfig {
        applicationId = "com.example.cyber_travel_translation"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val googleApiKey = properties.getProperty("google_api")
        buildConfigField("String", "GOOGLE_API", googleApiKey)
        resValue("string", "GOOGLE_API", properties.getProperty("google_api"))


    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    buildTypes {
        //debug {
        //    val properties = Properties()
        //    properties.load(FileInputStream(rootProject.file("local.properties")))
        //    buildConfigField("String", "GOOGLE_API",  properties.getProperty("google_api"))
        //    resValue("String", "GOOGLE_API", properties.getProperty("google_api"))
        //}
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            // 프로젝트에서 사용
            //buildConfigField("String", "GOOGLE_API", "\"${project.findProperty("google_api") ?: ""}\"")
            // 매니페스트에서 사용
            //resValue("string", "GOOGLE_API", "\"${project.findProperty("google_api") ?: ""}\"")
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }



}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    //구글맵 라이브러리 쓸거임!
    implementation("com.google.android.gms:play-services-maps:17.0.1")
    implementation("com.google.android.gms:play-services-location:18.0.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}