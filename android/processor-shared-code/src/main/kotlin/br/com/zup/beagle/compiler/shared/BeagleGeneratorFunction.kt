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

package br.com.zup.beagle.compiler.shared

import com.squareup.kotlinpoet.FunSpec
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element

abstract class BeagleGeneratorFunction<T : Annotation>(
//    private val beagleClass: BeagleClass,
    val processingEnv: ProcessingEnvironment,
    private val functionName: String,
    private val annotation: Class<T>) {

    abstract fun buildCodeByElement(element: Element, annotation: Annotation): String

    abstract fun buildCodeByDependency(registeredDependency: Pair<String, String>): String

    abstract fun validationElement(element: Element, annotation: Annotation)

    abstract fun getCodeFormatted(allCodeMappedWithAnnotation: String): String

    abstract fun returnStatementInGenerate(): String

    abstract fun createFuncSpec(name: String): FunSpec.Builder

    open fun generate(roundEnvironment: RoundEnvironment): FunSpec {
        val registeredComponentsInDependencies = getRegisteredComponentsInDependencies()
        val classesWithAnnotation = getAllClassWithAnnotation(roundEnvironment) + registeredComponentsInDependencies
        return createFuncSpec(functionName)
            .addCode(getCodeFormatted(classesWithAnnotation))
            .addStatement(returnStatementInGenerate())
            .build()
    }

    private fun getRegisteredComponentsInDependencies(): java.lang.StringBuilder {
        // TODO: Otimizar
        val test = 3
        val registeredWidgets = StringBuilder()
        processingEnv.elementUtils.getPackageElement(REGISTRAR_COMPONENTS_PACKAGE)?.enclosedElements?.forEach {
            val fullClassName = it.toString()
            val cls = Class.forName(fullClassName)
            val kotlinClass = cls.kotlin
            try {
                (cls.getMethod(functionName).invoke(kotlinClass.objectInstance) as List<Pair<String, String>>).forEach { registeredDependency ->
                    registeredWidgets.append(buildCodeByDependency(registeredDependency))
//                    registeredWidgets.append("\n\t${component.second}::class.java as Class<WidgetView>,")
                }
            } catch (e: NoSuchMethodException) {
                // intentionally left blank
            }
        }
        return registeredWidgets
    }

    fun getFunctionName(): String = functionName

    fun getAllClassWithAnnotation(roundEnvironment: RoundEnvironment): String {
        val test = 1
        val stringBuilder = StringBuilder()
        val elements = roundEnvironment.getElementsAnnotatedWith(annotation)


        elements.forEach { element ->
            val annotation = element.getAnnotation(annotation)

            validationElement(element, annotation)

            stringBuilder.append(buildCodeByElement(element, annotation))
        }

        return stringBuilder.toString()
    }
}