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

import br.com.zup.beagle.exception.BeagleApiException
import br.com.zup.beagle.logger.BeagleMessageLogs
import br.com.zup.beagle.networking.HttpClient
import br.com.zup.beagle.networking.HttpClientFactory
import br.com.zup.beagle.networking.RequestData
import br.com.zup.beagle.networking.ResponseData
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal class BeagleApi(
    private val httpClient: HttpClient = HttpClientFactory().make()
) {

    @Throws(BeagleApiException::class)
    suspend fun fetchData(request: RequestData): ResponseData = suspendCancellableCoroutine { cont ->
        BeagleMessageLogs.logHttpRequestData(request)
        val call = httpClient.execute(
            request = request,
            onSuccess = { response ->
                BeagleMessageLogs.logHttpResponseData(response)
                cont.resume(response)
            }, onError = { response ->
            val exception = BeagleApiException(response)
            BeagleMessageLogs.logUnknownHttpError(exception)
            cont.resumeWithException(
                exception
            )
        })
        cont.invokeOnCancellation {
            call.cancel()
        }
    }
}