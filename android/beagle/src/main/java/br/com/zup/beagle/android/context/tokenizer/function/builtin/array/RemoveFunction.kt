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

package br.com.zup.beagle.android.context.tokenizer.function.builtin.array

import br.com.zup.beagle.android.context.tokenizer.function.Function
import br.com.zup.beagle.android.context.tokenizer.function.builtin.getFirstElementAsMutableList

internal class RemoveFunction : Function {
    override fun functionName(): String = "remove"

    override fun execute(vararg params: Any?): List<Any> {
        val array = params.getFirstElementAsMutableList()
        val element = params[1] as Any
        var shouldRemove = true
        while (shouldRemove) {
            shouldRemove = array.remove(element)
        }
        return array
    }

}