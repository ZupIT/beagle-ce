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

package br.com.zup.beagle.android.components.form

import br.com.zup.beagle.android.action.ButtonSubmitAction
import br.com.zup.beagle.android.action.SimpleFormAction
import br.com.zup.beagle.android.components.BaseComponentTest
import br.com.zup.beagle.android.components.Button
import br.com.zup.beagle.android.components.Text
import br.com.zup.beagle.android.context.ContextData
import br.com.zup.beagle.android.extensions.once
import br.com.zup.beagle.android.view.custom.BeagleFlexView
import br.com.zup.beagle.core.ServerDrivenComponent
import io.mockk.*
import org.junit.Test

internal class SimpleFormTest : BaseComponentTest() {

    private val context: ContextData = mockk()
    private val onSubmit: List<SimpleFormAction> = listOf(SimpleFormAction())
    private val children: List<ServerDrivenComponent> = listOf(Text(""))
    private val buttonSubmitAction: ButtonSubmitAction = mockk()

    private lateinit var simpleForm: SimpleForm

    override fun setUp() {
        super.setUp()

        simpleForm = SimpleForm(context, onSubmit, children)
    }

    @Test
    fun build_should_return_a_beagle_flex_view_instance() {
        // When
        val view = simpleForm.buildView(rootView)

        // Then
        kotlin.test.assertTrue(view is BeagleFlexView)
    }
}