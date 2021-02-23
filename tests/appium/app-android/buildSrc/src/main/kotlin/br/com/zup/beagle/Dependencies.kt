/*
 * Copyright 2020 ZUP IT SERVICOS EM TECNOLOGIA E INOVACAO SA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.zup.beagle

import org.gradle.api.Plugin
import org.gradle.api.Project

class Dependencies : Plugin<Project> {
    override fun apply(project: Project) {}

    object Modules {
        const val androidSample = ":sample"
        const val core = ":beagle"
        const val processor = ":processor"
        const val internalProcessor = ":internal-processor"
        const val androidAnnotation = ":android-annotation"
        const val preview = ":preview"
        const val commonProcessorSharedCode = ":processor-shared-code"
    }

    object Releases {
        const val versionCode = 1
        const val versionName = "1.0"
        const val beagleVersionName = "0.0.1"
    }

    object Versions {
        const val compileSdk = 30
        const val minSdk = 19
        const val targetSdk = 30
        const val buildTools = "30.0.0"
        const val kotlin = "1.4.10"

        const val kotlinCoroutines = "1.3.9"

        const val kotlinPoet = "1.7.1"
        const val okio = "2.9.0"

        const val appcompat = "1.2.0"
        const val viewModel = "2.2.0"
        const val recyclerView = "1.1.0"

        const val moshi = "1.11.0"

        const val soLoader = "0.9.0"

        const val junit5 = "5.7.0"
        const val junit4 = "4.13"

        const val yoga = "1.16.0"

        const val jni = "0.0.4"

        const val webSocket = "1.5.1"
        const val simpleLogger = "1.7.30"

        const val kotlinCoroutinesTest = "1.3.9"
        const val materialDesign = "1.2.1"
        const val googleCompileTesting = "0.18"
        const val googleAutoService = "1.0-rc7"

        const val jsonObject = "20200518"

        const val mockk = "1.10.2"

        const val testRunner = "1.3.1-alpha02"
        const val testExt = "1.1.3-alpha02"
        const val espresso = "3.4.0-alpha02"
        const val archCoreTesting = "2.1.0"
        const val testRules = "1.3.1-alpha02"
        const val testCore = "1.3.1-alpha02"
        const val robolectric = "4.4"

        const val incap = "0.3"

        const val cucumber = "1.2.6"
        const val kotlinCompileTesting = "1.3.1"

        const val gson = "2.8.6"
    }

    object GeneralNames {
        const val testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        const val sampleTestInstrumentationRunner = "br.com.zup.beagle.sample.SampleTestRunner"
        const val consumerProguard = "consumer-rules.pro"
    }

    object ProcessorLibraries {
        const val autoService = "com.google.auto.service:auto-service:${Versions.googleAutoService}"
        const val incap = "net.ltgt.gradle.incap:incap:${Versions.incap}"
        const val incapPrcessor = "net.ltgt.gradle.incap:incap-processor:${Versions.incap}"

        const val kotlinPoet = "com.squareup:kotlinpoet:${Versions.kotlinPoet}"
        const val okio = "com.squareup.okio:okio:${Versions.okio}"
    }

    object GeneralLibraries {
        const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}"
        const val kotlinCoroutines =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.kotlinCoroutines}"

        const val soLoader = "com.facebook.soloader:soloader:${Versions.soLoader}"

        const val yoga = "com.facebook.yoga.android:yoga-layout:${Versions.yoga}"

        const val jni = "com.facebook.fbjni:fbjni:${Versions.jni}"

        const val webSocket = "org.java-websocket:Java-WebSocket:${Versions.webSocket}"
        const val simpleLogger = "org.slf4j:slf4j-simple:${Versions.simpleLogger}"

        const val jsonObject = "org.json:json:${Versions.jsonObject}"
    }

    object GoogleLibraries {
        const val materialDesign = "com.google.android.material:material:${Versions.materialDesign}"
    }

    object AndroidxLibraries {
        const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
        const val coreKtx = "androidx.core:core-ktx:${Versions.appcompat}"
        const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.recyclerView}"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.viewModel}"
        const val viewModelExtensions = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.viewModel}"
    }

    object MoshiLibraries {
        const val moshi = "com.squareup.moshi:moshi:${Versions.moshi}"
        const val kotlin = "com.squareup.moshi:moshi-kotlin:${Versions.moshi}"
        const val adapters = "com.squareup.moshi:moshi-adapters:${Versions.moshi}"
    }

    object GsonLibraries{
        const val gson = "com.google.code.gson:gson:${Versions.gson}"
    }
    object TestLibraries {
        const val junit4 = "junit:junit:${Versions.junit4}"
        const val junitApi = "org.junit.jupiter:junit-jupiter-api:${Versions.junit5}"
        const val junitEngine = "org.junit.jupiter:junit-jupiter-engine:${Versions.junit5}"
        const val junitVintageEngine = "org.junit.vintage:junit-vintage-engine:${Versions.junit5}"
        const val mockk = "io.mockk:mockk:${Versions.mockk}"
        const val kotlinCoroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.kotlinCoroutinesTest}"
        const val googleCompileTesting = "com.google.testing.compile:compile-testing:${Versions.googleCompileTesting}"
        const val archCoreTesting = "androidx.arch.core:core-testing:${Versions.archCoreTesting}"
        const val testRunner = "androidx.test:runner:${Versions.testRunner}"
        const val testExt = "androidx.test.ext:junit:${Versions.testExt}"
        const val testCore = "androidx.test:core:${Versions.testCore}"
        const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espresso}"
        const val espressoContrib = "androidx.test.espresso:espresso-contrib:${Versions.espresso}"
        const val testRules = "androidx.test:rules:${Versions.testRules}"
        const val cucumberAndroid = "info.cukes:cucumber-android:${Versions.cucumber}"
        const val cucumberPicocontainer = "info.cukes:cucumber-picocontainer:${Versions.cucumber}"
        const val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
        const val kotlinCompileTesting = "com.github.tschuchortdev:kotlin-compile-testing:${Versions.kotlinCompileTesting}"
    }

}