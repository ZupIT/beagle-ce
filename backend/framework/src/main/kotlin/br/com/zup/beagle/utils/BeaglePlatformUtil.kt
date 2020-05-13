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

import br.com.zup.beagle.enums.BeaglePlatform
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode

object BeaglePlatformUtil {

    const val BEAGLE_PLATFORM_HEADER = "beagle-platform"
    private const val BEAGLE_PLATFORM_FIELD = "beaglePlatform"

    fun treatBeaglePlatform(currentPlatform: String?, jsonNode: JsonNode): JsonNode {
        if (jsonNode is ObjectNode) {
            if (currentPlatform != null) {
                removeObjectFromTreeWhenPlatformIsDifferToCurrentPlatform(
                    BeaglePlatform.valueOf(currentPlatform),
                    jsonNode
                )
            }
            removeBeaglePlatformField(jsonNode)
        }
        return jsonNode
    }

    private fun removeObjectFromTreeWhenPlatformIsDifferToCurrentPlatform(
        currentPlatform: BeaglePlatform,
        objectNode: ObjectNode
    ): Boolean {
        var isToRemove = false
        val fieldsToRemove = mutableSetOf<String>()
        objectNode.fields().forEach {
            if (removeObjectFromTreeWhenPlatformIsDifferToCurrentPlatform(
                    currentPlatform = currentPlatform,
                    nodeEntry = it,
                    fieldsToRemove = fieldsToRemove
                )) {
                isToRemove = true
            }
        }
        fieldsToRemove.forEach {
            objectNode.remove(it)
        }
        return isToRemove
    }

    private fun removeObjectFromTreeWhenPlatformIsDifferToCurrentPlatform(
        currentPlatform: BeaglePlatform,
        nodeEntry: MutableMap.MutableEntry<String, JsonNode>,
        fieldsToRemove: MutableSet<String>
    ): Boolean {
        when {
            checkIfCurrentPlatformIsNotAllowedToViewComponent(currentPlatform, nodeEntry) -> {
                return true
            }
            nodeEntry.value is ObjectNode -> {
                if (removeObjectFromTreeWhenPlatformIsDifferToCurrentPlatform(
                        currentPlatform,
                        nodeEntry.value as ObjectNode
                    )) {
                    fieldsToRemove.add(nodeEntry.key)
                }
            }
            nodeEntry.value is ArrayNode -> {
                treatArrayNode(
                    currentPlatform,
                    nodeEntry.value as ArrayNode
                )
            }
        }
        return false
    }

    private fun treatArrayNode(currentPlatform: BeaglePlatform, arrayNode: ArrayNode) {
        val indexToRemoveList = mutableListOf<Int>()
        arrayNode.forEachIndexed { index, node ->
            if (node is ObjectNode) {
                if (removeObjectFromTreeWhenPlatformIsDifferToCurrentPlatform(
                        currentPlatform,
                        node
                    )) {
                    indexToRemoveList.add(index)
                }
            }
        }
        indexToRemoveList.forEach {
            arrayNode.remove(it)
        }
    }

    private fun removeBeaglePlatformField(objectNode: ObjectNode) {
        if (objectNode.has(BEAGLE_PLATFORM_FIELD)) {
            objectNode.remove(BEAGLE_PLATFORM_FIELD)
        }
        objectNode.fields().forEach {
            if (it.value is ObjectNode) {
                removeBeaglePlatformField(it.value as ObjectNode)
            } else if (it.value is ArrayNode) {
                (it.value as ArrayNode).forEach { node ->
                    if (node is ObjectNode) {
                        removeBeaglePlatformField(node)
                    }
                }
            }
        }
    }

    private fun checkIfCurrentPlatformIsNotAllowedToViewComponent(
        currentPlatform: BeaglePlatform,
        node: MutableMap.MutableEntry<String, JsonNode>
    ) =
        node.key == BEAGLE_PLATFORM_FIELD
            && !BeaglePlatform.valueOf((node.value as TextNode).asText())
            .allowToSendComponentToPlatform(currentPlatform)
}