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

import android.view.View
import br.com.zup.beagle.android.view.ViewFactory
import br.com.zup.beagle.android.view.custom.BeagleFlexView
import br.com.zup.beagle.android.widget.RootView
import br.com.zup.beagle.android.widget.WidgetView
import br.com.zup.beagle.core.ContextComponent
import br.com.zup.beagle.core.ContextData
import br.com.zup.beagle.core.ServerDrivenComponent
import br.com.zup.beagle.widget.Widget
import br.com.zup.beagle.widget.core.Flex

data class Container(
    private val children: List<ServerDrivenComponent>,
    override val context: ContextData? = null
) : WidgetView(), ContextComponent {

    @Transient
    private val viewFactory = ViewFactory()

    override fun buildView(rootView: RootView): View {
        return viewFactory.makeBeagleFlexView(rootView.getContext(), flex ?: Flex())
            .apply {
                addChildren(this, rootView)
            }
    }

    override fun onBind(widget: Widget, view: View) {}


    private fun addChildren(beagleFlexView: BeagleFlexView, rootView: RootView) {
        children.forEach { child ->
            beagleFlexView.addServerDrivenComponent(child, rootView)
        }
    }

}
