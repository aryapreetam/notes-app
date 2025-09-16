import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.reload.gradle.ComposeHotRun
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree

plugins {
  alias(libs.plugins.multiplatform)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.compose)
  alias(libs.plugins.android.application)
  alias(libs.plugins.hotReload)
  alias(libs.plugins.kotlinx.serialization)
  alias(libs.plugins.sqlDelight)
}

kotlin {
  androidTarget {
    //https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-test.html
    instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
  }

  jvm()

  listOf(
    iosX64(),
    iosArm64(),
    iosSimulatorArm64()
  ).forEach {
    it.binaries.framework {
      baseName = "ComposeApp"
      isStatic = true
    }
  }

  sourceSets {
    commonMain.dependencies {
      implementation(compose.runtime)
      implementation(compose.ui)
      implementation(compose.foundation)
      implementation(compose.material3)
      implementation(compose.materialIconsExtended)
      implementation(compose.components.resources)
      implementation(compose.components.uiToolingPreview)
      implementation(libs.kermit)
      implementation(libs.kotlinx.coroutines.core)
      implementation(libs.androidx.lifecycle.viewmodel)
      implementation(libs.androidx.lifecycle.runtime)
      implementation(libs.androidx.navigation.compose)
      implementation(libs.kotlinx.serialization.json)
      implementation(libs.koin.core)
      implementation(libs.koin.compose)
      implementation(libs.koin.compose.viewmodel)
      implementation(libs.kotlinx.datetime)
      implementation(libs.sqlDelight.coroutines.extensions)
      // use api since the desktop app need to access the Cef to initialize it.
      api(libs.compose.webview.multiplatform)
    }

    commonTest.dependencies {
      implementation(kotlin("test"))
      @OptIn(ExperimentalComposeLibrary::class)
      implementation(compose.uiTest)
      implementation(libs.kotlinx.coroutines.test)
    }

    androidMain.dependencies {
      implementation(compose.uiTooling)
      implementation(libs.androidx.activityCompose)
      implementation(libs.kotlinx.coroutines.android)
      implementation(libs.sqlDelight.driver.android)
      implementation(libs.grizzi91.bouquet)
    }

    jvmMain.dependencies {
      implementation(compose.desktop.currentOs)
      implementation(libs.kotlinx.coroutines.swing)
      implementation(libs.sqlDelight.driver.sqlite)
    }

    iosMain.dependencies {
      implementation(libs.sqlDelight.driver.native)
    }
  }
}

android {
  namespace = "org.notesapp"
  compileSdk = 36

  defaultConfig {
    minSdk = 23
    targetSdk = 36

    applicationId = "org.notesapp.androidApp"
    versionCode = 1
    versionName = "1.0.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    release {
      isMinifyEnabled = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        file("proguard-rules.pro")
      )
      signingConfig = signingConfigs.getByName("release")
    }
  }
}

//https://developer.android.com/develop/ui/compose/testing#setup
dependencies {
  androidTestImplementation(libs.androidx.uitest.junit4)
  debugImplementation(libs.androidx.uitest.testManifest)
}

compose.desktop {
  application {
    mainClass = "MainKt"

    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      packageName = "Personal Notes App"
      packageVersion = "1.0.0"

      linux {
        iconFile.set(project.file("desktopAppIcons/LinuxIcon.png"))
      }
      windows {
        iconFile.set(project.file("desktopAppIcons/WindowsIcon.ico"))
      }
      macOS {
        iconFile.set(project.file("desktopAppIcons/MacosIcon.icns"))
        bundleID = "org.notesapp.desktopApp"
      }
    }
  }
}

tasks.withType<ComposeHotRun>().configureEach {
  mainClass = "MainKt"
}

sqldelight {
  databases {
    create("NotesDB") {
      packageName.set("org.notesapp.db")
    }
  }
}
