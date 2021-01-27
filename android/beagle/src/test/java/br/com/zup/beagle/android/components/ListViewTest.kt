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

package br.com.zup.beagle.android.components

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.zup.beagle.android.action.Action
import br.com.zup.beagle.android.action.SendRequest
import br.com.zup.beagle.android.components.layout.Container
import br.com.zup.beagle.android.components.list.ListAdapter
import br.com.zup.beagle.android.context.ContextData
import br.com.zup.beagle.android.context.expressionOf
import br.com.zup.beagle.android.testutil.InstantExecutorExtension
import br.com.zup.beagle.android.view.ViewFactory
import br.com.zup.beagle.widget.core.ListDirection
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantExecutorExtension::class)
class ListViewTest : BaseComponentTest() {

    private data class Cell(
        val id: Int,
        val name: String
    )

    private val recyclerView: RecyclerView = mockk(relaxed = true)
    private val beagleRecyclerView: BeagleRecyclerView = mockk(relaxed = true)
    private val layoutManagerSlot = slot<LinearLayoutManager>()
    private val deprecatedAdapterSlot = slot<ListView.ListViewRecyclerAdapter>()
    private val adapterSlot = slot<ListAdapter>()

    private val children = listOf(Container(listOf()))
    private val context = ContextData(
        id = "context",
        value = listOf(Cell(10, "Item 1"), Cell(20, "Item 2"), Cell(30, "Item 3"))
    )
    private val onInit = listOf(SendRequest("http://www.init.com"))
    private val dataSource = expressionOf<List<Any>>("@{context}")
    private val template = Container(children = listOf(Text(expressionOf("@{item.name}"))))
    private val onScrollEnd = listOf(mockk<Action>(relaxed = true))
    private val iteratorName = "list"
    private val key = "id"

    private lateinit var deprecatedListView: ListView
    private lateinit var listView: ListView

    @BeforeEach
    override fun setUp() {
        super.setUp()

        deprecatedListView = ListView(children, ListDirection.VERTICAL)
        listView = ListView(ListDirection.VERTICAL, context, onInit, dataSource, template, onScrollEnd, iteratorName = iteratorName, key = key)

        every { beagleFlexView.addView(any()) } just Runs
        every { anyConstructed<ViewFactory>().makeRecyclerView(rootView.getContext()) } returns recyclerView
        every { anyConstructed<ViewFactory>().makeBeagleRecyclerView(rootView.getContext()) } returns beagleRecyclerView
        every { anyConstructed<ViewFactory>().makeBeagleRecyclerViewScrollIndicatorVertical(rootView.getContext()) } returns beagleRecyclerView
        every { anyConstructed<ViewFactory>().makeBeagleRecyclerViewScrollIndicatorHorizontal(rootView.getContext()) } returns beagleRecyclerView
        every { recyclerView.layoutManager = capture(layoutManagerSlot) } just Runs
        every { recyclerView.adapter = any() } just Runs
    }

    @Test
    fun `GIVEN a deprecatedListView WHEN buildView THEN should return a RecyclerView instance`() {
        // Given When
        val view = deprecatedListView.buildView(rootView)

        // Then
        assertTrue(view is RecyclerView)
    }

    @Test
    fun `GIVEN a deprecatedListView in VERTICAL WHEN buildView THEN should return a RecyclerView in VERTICAL`() {
        // Given
        deprecatedListView = deprecatedListView.copy(direction = ListDirection.VERTICAL)

        // When
        deprecatedListView.buildView(rootView)

        // Then
        assertEquals(RecyclerView.VERTICAL, layoutManagerSlot.captured.orientation)
    }

    @Test
    fun `GIVEN a deprecatedListView in HORIZONTAL WHEN buildView THEN should return a RecyclerView in HORIZONTAL`() {
        // Given
        deprecatedListView = deprecatedListView.copy(direction = ListDirection.HORIZONTAL)

        // When
        deprecatedListView.buildView(rootView)

        // Then
        assertEquals(RecyclerView.HORIZONTAL, layoutManagerSlot.captured.orientation)
    }

    @Test
    fun `GIVEN a deprecatedListView WHEN buildView THEN should create an adapter with children`() {
        // Given
        every { recyclerView.adapter = capture(deprecatedAdapterSlot) } just Runs

        // When
        deprecatedListView.buildView(rootView)

        // Then
        assertEquals(children, deprecatedAdapterSlot.captured.children)
    }

    @Test
    fun `GIVEN a listView WHEN buildView THEN should create an adapter with attributes from list`() {
        // Given
        every { beagleRecyclerView.adapter = capture(adapterSlot) } just Runs

        // When
        listView.buildView(rootView)

        // Then
        assertEquals(template, adapterSlot.captured.template)
        assertEquals(iteratorName, adapterSlot.captured.iteratorName)
        assertEquals(key, adapterSlot.captured.key)
    }

    @Test
    fun `GIVEN a listView WHEN buildView THEN should observeBindChanges`() {
        // Given
        val scrollSlot = slot<RecyclerView.OnScrollListener>()
        val layoutManagerSlot = slot<LinearLayoutManager>()
        every { beagleRecyclerView.addOnScrollListener(capture(scrollSlot)) } just Runs
        every { beagleRecyclerView.adapter = capture(adapterSlot) } just Runs
        every { beagleRecyclerView.layoutManager = capture(layoutManagerSlot) } just Runs

        // When
        listView.buildView(rootView)
        every { beagleRecyclerView.layoutManager } returns layoutManagerSlot.captured
        scrollSlot.captured.onScrolled(beagleRecyclerView, 0, 0)

        // Then
        onScrollEnd.forEach {
            verify(exactly = 1) { it.execute(rootView, beagleRecyclerView) }
        }
    }

    @Test
    fun `Given a listView without scrollIndicator WHEN buildView THEN should call method makeBeagleRecyclerView`() {
        // Given
        listView = ListView(ListDirection.VERTICAL, context, onInit, dataSource, template, onScrollEnd, iteratorName = iteratorName, key = key)

        // When
        listView.buildView(rootView)

        // Then
        verify(exactly = 1) { anyConstructed<ViewFactory>().makeBeagleRecyclerView(rootView.getContext()) }
    }

    @Test
    fun `Given a listView with scrollIndicator vertical WHEN buildView THEN should call method makeBeagleRecyclerViewScrollIndicatorVertical`() {
        // Given
        listView = ListView(ListDirection.VERTICAL, context, onInit, dataSource, template, onScrollEnd, iteratorName = iteratorName, key = key, isScrollIndicatorVisible = true)

        // When
        listView.buildView(rootView)

        // Then
        verify(exactly = 1) { anyConstructed<ViewFactory>().makeBeagleRecyclerViewScrollIndicatorVertical(rootView.getContext()) }
    }

    @Test
    fun `Given a listView with scrollIndicator horizontal WHEN buildView THEN should call method makeBeagleRecyclerViewScrollIndicatorHorizontal`() {
        // Given
        listView = ListView(ListDirection.HORIZONTAL, context, onInit, dataSource, template, onScrollEnd, iteratorName = iteratorName, key = key, isScrollIndicatorVisible = true)

        // When
        listView.buildView(rootView)

        // Then
        verify(exactly = 1) { anyConstructed<ViewFactory>().makeBeagleRecyclerViewScrollIndicatorHorizontal(rootView.getContext()) }
    }
}
