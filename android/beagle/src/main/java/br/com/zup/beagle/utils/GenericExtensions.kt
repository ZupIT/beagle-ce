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

package br.com.zup.beagle.utils

import br.com.zup.beagle.core.Bind
import java.lang.reflect.ParameterizedType
import java.lang.reflect.WildcardType

fun <I, G> Any.implementsGenericTypeOf(
    interfaceClazz: Class<I>,
    genericType: Class<G>
): Boolean {
    return this::class.java.genericInterfaces.filterIsInstance<ParameterizedType>()
        .filter {
            val rawType = it.rawType as Class<*>
            rawType == interfaceClazz
        }.any {
            val types = it.actualTypeArguments
            val paramType = types[0]
            val type = if (paramType is WildcardType) {
                paramType.upperBounds[0]
            } else {
                paramType
            }
            val typeClass = type as Class<*>
            genericType == typeClass
        }
}

fun <T : Any> getValueNull(binding: Bind<T>, property: T?): T? {
    return when (binding) {
        is Bind.Expression<T> -> {
            property
        }
        is Bind.Value<T> -> {
            binding.value
        }
        else -> {
            throw IllegalStateException("Invalid bind instance")
        }
    }
}

fun <T : Any> getValueNotNull(binding: Bind<T>, property: T): T {
    return when (binding) {
        is Bind.Expression<T> -> {
            property
        }
        is Bind.Value<T> -> {
            binding.value
        }
        else -> {
            throw IllegalStateException("Invalid bind instance")
        }
    }
}