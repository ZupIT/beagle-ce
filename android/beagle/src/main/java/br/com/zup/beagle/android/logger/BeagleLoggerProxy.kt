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

package br.com.zup.beagle.android.logger

import br.com.zup.beagle.android.setup.BeagleEnvironment

internal object BeagleLoggerProxy : BeagleLogger {

    private val logger: BeagleLogger? by lazy { BeagleEnvironment.beagleSdk.logger }

    override fun warning(message: String) = runIfEnable {
        logger?.warning(message)
    }

    override fun error(message: String) = runIfEnable {
        logger?.error(message)
    }

    override fun error(message: String, throwable: Throwable) = runIfEnable {
        logger?.error(message, throwable)
    }

    override fun info(message: String) = runIfEnable {
        logger?.info(message)
    }

    private fun runIfEnable(runBlock: () -> Unit) {
        if (BeagleEnvironment.beagleSdk.config.isLoggingEnabled) {
            runBlock()
        }
    }
}