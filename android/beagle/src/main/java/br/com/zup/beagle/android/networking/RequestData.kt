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

package br.com.zup.beagle.android.networking

import java.net.URI

data class RequestData(
    val uri: URI,
    val method: HttpMethod = HttpMethod.GET,
    private val originalHeaders: Map<String, String> = mapOf(),
    val body: String? = null
) {
    companion object {
        const val BEAGLE_PLATFORM_HEADER_KEY = "beagle-platform"
        const val BEAGLE_PLATFORM_HEADER_VALUE = "ANDROID"
    }

    val headers get() = this.originalHeaders + (BEAGLE_PLATFORM_HEADER_KEY to BEAGLE_PLATFORM_HEADER_VALUE)
}

enum class HttpMethod {
    GET,
    POST,
    PUT,
    DELETE,
    HEAD,
    PATCH
}