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

package br.com.zup.beagle.android.components.layout

import br.com.zup.beagle.android.components.BaseComponentTest
import br.com.zup.beagle.android.view.ViewFactory
import br.com.zup.beagle.widget.core.Flex
import br.com.zup.beagle.widget.core.FlexDirection
import io.mockk.every
import io.mockk.slot
import org.junit.Assert.assertEquals
import org.junit.Test

class VerticalTest : BaseComponentTest() {

    private val flexSlot = slot<Flex>()

    override fun setUp() {
        super.setUp()
        every { anyConstructed<ViewFactory>().makeBeagleFlexView(any(), capture(flexSlot)) } returns beagleFlexView
    }

    @Test
    fun getYogaFlexDirection_should_return_COLUMN_REVERSE_when_reversed_is_true() {
        // Given
        val vertical = Vertical(listOf(), reversed = true)

        // When
        vertical.buildView(rootView)

        // Then
        assertEquals(FlexDirection.COLUMN_REVERSE, flexSlot.captured.flexDirection)
    }

    @Test
    fun getYogaFlexDirection_should_return_COLUMN_when_reversed_is_false() {
        // Given
        val vertical = Vertical(listOf(), reversed = false)

        // When
        vertical.buildView(rootView)

        // Then
        assertEquals(FlexDirection.COLUMN, flexSlot.captured.flexDirection)
    }
}
