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

package br.com.zup.beagle.data

import br.com.zup.beagle.cache.CacheManager
import br.com.zup.beagle.core.ServerDrivenComponent
import br.com.zup.beagle.data.serializer.BeagleSerializer
import br.com.zup.beagle.exception.BeagleException
import br.com.zup.beagle.view.ScreenRequest
import br.com.zup.beagle.view.mapper.toRequestData

internal class ComponentRequester(
    private val beagleApi: BeagleApi = BeagleApi(),
    private val serializer: BeagleSerializer = BeagleSerializer(),
    private val cacheManager: CacheManager = CacheManager()
) {

    @Throws(BeagleException::class)
    suspend fun fetchComponent(screenRequest: ScreenRequest): ServerDrivenComponent {
        val url = screenRequest.url
        val beagleCache = cacheManager.restoreBeagleCacheForUrl(url)
        val responseBody = if (beagleCache?.isHot == true) {
            beagleCache.json
        } else {
            val newScreenRequest = cacheManager.screenRequestWithCache(screenRequest, beagleCache)
            val responseData = beagleApi.fetchData(newScreenRequest.toRequestData())
            cacheManager.handleResponseData(url, beagleCache, responseData)
        }
        return serializer.deserializeComponent(responseBody)
    }
}