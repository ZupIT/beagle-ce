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

package br.com.zup.beagle.android.compiler

import br.com.zup.beagle.compiler.shared.ANDROID_OPERATION
import br.com.zup.beagle.compiler.shared.GenerateFunctionOperation
import br.com.zup.beagle.compiler.shared.GenerateFunctionWidget
import br.com.zup.beagle.compiler.shared.GenericFactoryProcessor
import br.com.zup.beagle.compiler.shared.WIDGET_VIEW
import br.com.zup.beagle.compiler.shared.error
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.IOException
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment

internal data class BeagleSetupProcessor(
    private val processingEnv: ProcessingEnvironment,
    private val registerActionProcessorProcessor: RegisterActionProcessorProcessor =
        RegisterActionProcessorProcessor(processingEnv),
    private val beagleSetupPropertyGenerator: BeagleSetupPropertyGenerator =
        BeagleSetupPropertyGenerator(processingEnv),
    private val registerAnnotationProcessor: RegisterControllerProcessor =
        RegisterControllerProcessor(processingEnv),
    private val registerBeagleAdapterProcessor: RegisterBeagleAdapterProcessor =
        RegisterBeagleAdapterProcessor(processingEnv),
) {

    private val widgetFactoryProcessor = GenericFactoryProcessor(
        processingEnv,
        REGISTERED_WIDGETS_GENERATED,
        GenerateFunctionWidget(processingEnv)
    )

    private val operationFactoryProcessor = GenericFactoryProcessor(
        processingEnv,
        REGISTERED_OPERATIONS_GENERATED,
        GenerateFunctionOperation(processingEnv)
    )

    fun process(
        basePackageName: String,
        beagleConfigClassName: String,
        roundEnvironment: RoundEnvironment
    ) {
        val beagleSetupClassName = "BeagleSetup"

        val properties = beagleSetupPropertyGenerator.generate(
            basePackageName,
            roundEnvironment
        )
        val typeSpec = TypeSpec.classBuilder(beagleSetupClassName)
            .addModifiers(KModifier.PUBLIC, KModifier.FINAL)
            .addSuperinterface(ClassName(BEAGLE_SDK.packageName, BEAGLE_SDK.className))
            .addFunction(widgetFactoryProcessor.createFunction())
            .addFunction(operationFactoryProcessor.createFunction())
            .addFunction(registerActionProcessorProcessor.createRegisteredActionsFunction())


        val beagleSetupFile = addDefaultImports(basePackageName, beagleSetupClassName, beagleConfigClassName)

        val propertyIndex = properties.indexOfFirst { it.name == "serverDrivenActivity" }

        var property = properties[propertyIndex]

        widgetFactoryProcessor.process(basePackageName, roundEnvironment, WIDGET_VIEW, false)
        operationFactoryProcessor.process(basePackageName, roundEnvironment, ANDROID_OPERATION, false)
        registerActionProcessorProcessor.process(basePackageName, roundEnvironment)
        registerAnnotationProcessor.process(basePackageName, roundEnvironment, property.initializer.toString())
        registerBeagleAdapterProcessor.process(
            BEAGLE_CUSTOM_ADAPTER.packageName,
            roundEnvironment)

        val defaultActivity = registerAnnotationProcessor.defaultActivityRegistered
        property = beagleSetupPropertyGenerator.implementServerDrivenActivityProperty(
            defaultActivity,
            isFormatted = true
        )

        val newProperties = properties.toMutableList().apply {
            this[propertyIndex] = property
        }

        val newTypeSpecBuilder = typeSpec.addProperties(newProperties)
            .addProperty(createBeagleConfigAttribute(beagleConfigClassName))
            .addProperty(PropertySpec.builder(
                "typeAdapterResolver",
                ClassName(BEAGLE_CUSTOM_ADAPTER.packageName, BEAGLE_CUSTOM_ADAPTER.className),
                KModifier.OVERRIDE
            ).initializer("${BEAGLE_CUSTOM_ADAPTER_IMPL.className}()")
                .build())
        try {
            beagleSetupFile
                .addType(newTypeSpecBuilder.build())
                .build()
                .writeTo(processingEnv.filer)
        } catch (e: IOException) {
            val errorMessage = "Error when trying to generate code.\n${e.message!!}"
            processingEnv.messager.error(errorMessage)
        }
    }

    private fun addDefaultImports(
        basePackageName: String,
        beagleSetupClassName: String,
        beagleConfigClassName: String
    ): FileSpec.Builder {
        return FileSpec.builder(
            basePackageName,
            beagleSetupClassName
        ).addImport(BEAGLE_CONFIG.packageName, BEAGLE_CONFIG.className)
            .addImport(BEAGLE_SDK.packageName, BEAGLE_SDK.className)
            .addImport(FORM_LOCAL_ACTION_HANDLER.packageName, FORM_LOCAL_ACTION_HANDLER.className)
            .addImport(DEEP_LINK_HANDLER.packageName, DEEP_LINK_HANDLER.className)
            .addImport(HTTP_CLIENT_HANDLER.packageName, HTTP_CLIENT_HANDLER.className)
            .addImport(BEAGLE_LOGGER.packageName, BEAGLE_LOGGER.className)
            .addImport(BEAGLE_IMAGE_DOWNLOADER.packageName, BEAGLE_IMAGE_DOWNLOADER.className)
            .addImport(CONTROLLER_REFERENCE.packageName, CONTROLLER_REFERENCE.className)
            .addImport(BEAGLE_CUSTOM_ADAPTER_IMPL.packageName, BEAGLE_CUSTOM_ADAPTER_IMPL.className)
            .addImport(basePackageName, beagleConfigClassName)
            .addImport(ClassName(ANDROID_ACTION.packageName, ANDROID_ACTION.className), "")
            .addAnnotation(
                AnnotationSpec.builder(Suppress::class.java)
                    .addMember("%S, %S, %S", "OverridingDeprecatedMember", "DEPRECATION", "UNCHECKED_CAST")
                    .build()
            )
    }

    private fun createBeagleConfigAttribute(beagleConfigClassName: String): PropertySpec {
        return PropertySpec.builder(
            "config",
            ClassName(BEAGLE_CONFIG.packageName, BEAGLE_CONFIG.className),
            KModifier.OVERRIDE
        ).initializer("$beagleConfigClassName()").build()
    }

    companion object {
        internal const val REGISTERED_WIDGETS_GENERATED = "RegisteredWidgets"
        internal const val REGISTERED_OPERATIONS_GENERATED = "RegisteredOperations"
    }
}