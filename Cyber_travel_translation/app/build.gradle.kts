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


        buildConfigField("String", "PAPAGO_ID", properties.getProperty("papago_id"))
        resValue("string", "PAPAGO_ID", properties.getProperty("papago_id"))
        buildConfigField("String", "PAPAGO_API", properties.getProperty("papago_api"))
        resValue("string", "PAPAGO_API", properties.getProperty("papago_api"))


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

    packagingOptions {
        exclude ("META-INF/DEPENDENCIES")
        exclude ("META-INF/LICENSE")
        exclude ("META-INF/LICENSE.txt")
        exclude ("META-INF/license.txt")
        exclude ("META-INF/NOTICE")
        exclude ("META-INF/NOTICE.txt")
        exclude ("META-INF/notice.txt")
        exclude ("META-INF/ASL2.0")
    }
    useLibrary ("org.apache.http.legacy")



}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    //구글맵 라이브러리 쓸거임!
    implementation("com.google.android.gms:play-services-maps:17.0.1")
    implementation("com.google.android.gms:play-services-location:18.0.0")
    implementation ("com.google.android.gms:play-services-vision:20.1.3")
    implementation ("com.google.android.gms:play-services-panorama:17.1.0")

    //파파고 이미지 번역 라이브러리 쓸거임!
    //파파고 서버에 전송할거임!
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    //implementation("org.apache.httpcomponents:httpclient:4.5.13") {
    //    exclude(group = "org.apache.httpcomponents", module = "httpclient")
    //}
    //implementation("org.apache.httpcomponents:httpclient-android:4.3.5.1")
    //implementation("org.apache.httpcomponents:httpmime:4.5.13")


    //Volly 쓸거임!
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.google.android.gms:play-services-panorama:17.1.0")


    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}