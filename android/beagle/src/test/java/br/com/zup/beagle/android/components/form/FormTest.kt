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

import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import br.com.zup.beagle.action.FormRemoteAction
import br.com.zup.beagle.android.action.ActionExecutor
import br.com.zup.beagle.android.action.FormValidationActionHandler
import br.com.zup.beagle.action.Navigate
import br.com.zup.beagle.android.components.BaseComponentTest
import br.com.zup.beagle.android.components.form.core.FormDataStoreHandler
import br.com.zup.beagle.android.components.form.core.FormResult
import br.com.zup.beagle.android.components.form.core.FormSubmitter
import br.com.zup.beagle.android.components.form.core.FormValidatorController
import br.com.zup.beagle.android.components.form.core.Validator
import br.com.zup.beagle.android.components.form.core.ValidatorHandler
import br.com.zup.beagle.android.components.utils.hideKeyboard
import br.com.zup.beagle.android.engine.renderer.ViewRendererFactory
import br.com.zup.beagle.android.extensions.once
import br.com.zup.beagle.android.logger.BeagleMessageLogs
import br.com.zup.beagle.android.testutil.RandomData
import br.com.zup.beagle.android.testutil.getPrivateField
import br.com.zup.beagle.android.view.BeagleActivity
import br.com.zup.beagle.android.view.ServerDrivenState
import io.mockk.*
import org.junit.Assert.assertEquals
import org.junit.Test

private const val FORM_INPUT_VIEWS_FIELD_NAME = "formInputs"
private const val FORM_INPUT_HIDDEN_VIEWS_FIELD_NAME = "formInputHiddenList"
private const val FORM_SUBMIT_VIEW_FIELD_NAME = "formSubmitView"
private val INPUT_VALUE = RandomData.string()
private const val INPUT_NAME = "INPUT_NAME"
private const val FORM_GROUP_VALUE = "GROUP"

class FormTest : BaseComponentTest() {

    private val formInput: FormInput = mockk(relaxed = true)
    private val formSubmit: FormSubmit = mockk(relaxed = true)

    private val validator: Validator<Any, Any> = mockk(relaxed = true, relaxUnitFun = true)
    private val beagleActivity: BeagleActivity = mockk()
    private val inputMethodManager: InputMethodManager = mockk()
    private val formInputView: View = mockk(relaxed = true, relaxUnitFun = true)
    private val formSubmitView: View = mockk(relaxed = true, relaxUnitFun = true)
    private val viewGroup: ViewGroup = mockk(relaxed = true)
    private val inputWidget: InputWidget = mockk(relaxed = true)
    private val remoteAction: FormRemoteAction = mockk(relaxed = true, relaxUnitFun = true)
    private val navigateAction: Navigate = mockk()

    private val onClickListenerSlot = slot<View.OnClickListener>()
    private val formSubmitCallbackSlot = slot<(formResult: FormResult) -> Unit>()
    private val runnableSlot = slot<Runnable>()
    private val formParamsSlot = slot<Map<String, String>>()

    private lateinit var form: Form

    override fun setUp() {
        super.setUp()

        mockkStatic("br.com.zup.beagle.android.components.utils.ViewExtensionsKt")
        mockkObject(BeagleMessageLogs)
        mockkConstructor(ActionExecutor::class)
        mockkConstructor(FormSubmitter::class)
        mockkConstructor(FormValidatorController::class)
        mockkConstructor(FormDataStoreHandler::class)
        mockkConstructor(FormValidationActionHandler::class)

        every { BeagleMessageLogs.logFormInputsNotFound(any()) } just Runs
        every { BeagleMessageLogs.logFormSubmitNotFound(any()) } just Runs
        every { anyConstructed<FormDataStoreHandler>().getAllValues(any()) } returns HashMap()
        every { anyConstructed<FormDataStoreHandler>().put(any(), any(), any()) } just Runs
        every { anyConstructed<FormDataStoreHandler>().clear(any()) } just Runs
        every { formInput.required } returns false
        every { viewRender.build(rootView) } returns viewGroup
        every { formInput.name } returns INPUT_NAME
        every { inputWidget.getValue() } returns INPUT_VALUE
        every { formInput.child } returns inputWidget
        every { formInputView.context } returns beagleActivity
        every { formInputView.tag } returns formInput
        every { formSubmitView.hideKeyboard() } just Runs
        every { formSubmitView.tag } returns formSubmit
        every { formSubmitView.context } returns beagleActivity
        every { formSubmitView.setOnClickListener(capture(onClickListenerSlot)) } just Runs
        every { viewGroup.childCount } returns 2
        every { viewGroup.getChildAt(0) } returns formInputView
        every { viewGroup.getChildAt(1) } returns formSubmitView
        every { beagleActivity.getSystemService(any()) } returns inputMethodManager
        every { beagleActivity.runOnUiThread(capture(runnableSlot)) } just Runs
        every {
            anyConstructed<FormSubmitter>().submitForm(any(), capture(formParamsSlot),
                capture(formSubmitCallbackSlot))
        } just Runs
        every { anyConstructed<ViewRendererFactory>().make(any()).build(any()) } returns viewGroup

        val validatorHandler: ValidatorHandler = mockk(relaxed = true)
        every { validatorHandler.getValidator(any()) } returns validator
        every { beagleSdk.validatorHandler } returns validatorHandler


        form = Form(action = mockk(), child = mockk())
    }

    @Test
    fun build_should_not_try_to_iterate_over_children_if_is_not_a_ViewGroup() {

        val viewNotViewGroup = mockk<View>()
        // Given
        every { anyConstructed<ViewRendererFactory>().make(any()).build(any()) } returns viewNotViewGroup

        // When
        val actual = form.buildView(rootView)

        // Then
        assertEquals(viewNotViewGroup, actual)
        verify(exactly = 0) { viewGroup.childCount }
    }

    @Test
    fun build_should_try_to_iterate_over_all_viewGroups() {
        // Given
        val childViewGroup = mockk<ViewGroup>()
        every { childViewGroup.childCount } returns 0
        every { childViewGroup.tag } returns null
        every { viewGroup.childCount } returns 1
        every { viewGroup.getChildAt(any()) } returns childViewGroup

        // When
        form.buildView(rootView)

        // Then
        verify(exactly = 1) { childViewGroup.childCount }
    }

    @Test
    fun build_should_try_to_iterate_over_all_viewGroups_that_is_the_formInput() {
        // Given
        val childViewGroup = mockk<ViewGroup>()
        every { childViewGroup.childCount } returns 0
        every { childViewGroup.tag } returns mockk<FormInput>(relaxed = true)
        every { viewGroup.childCount } returns 1
        every { viewGroup.getChildAt(any()) } returns childViewGroup

        // When
        form.buildView(rootView)

        // Then
        val views = form.getPrivateField<List<View>>(FORM_INPUT_VIEWS_FIELD_NAME)
        assertEquals(1, views.size)
    }

    @Test
    fun build_should_try_to_iterate_over_all_viewGroups_that_is_the_formInputHidden() {
        // Given
        val childViewGroup = mockk<ViewGroup>()
        every { childViewGroup.childCount } returns 0
        every { childViewGroup.tag } returns mockk<FormInputHidden>()
        every { viewGroup.childCount } returns 1
        every { viewGroup.getChildAt(any()) } returns childViewGroup

        // When
        form.buildView(rootView)

        // Then
        val views = form.getPrivateField<List<View>>(FORM_INPUT_HIDDEN_VIEWS_FIELD_NAME)
        assertEquals(1, views.size)
    }

    @Test
    fun build_should_group_formInput_views() {
        form.buildView(rootView)

        val formInputs =
            form.getPrivateField<List<FormInput>>(FORM_INPUT_VIEWS_FIELD_NAME)
        assertEquals(1, formInputs.size)
        assertEquals(formInput, formInputs[0])
        verify { anyConstructed<FormValidatorController>().formSubmitView = formSubmitView }
    }

    @Test
    fun build_should_find_formSubmitView() {
        form.buildView(rootView)

        val actual = form.getPrivateField<View>(FORM_SUBMIT_VIEW_FIELD_NAME)
        assertEquals(formSubmitView, actual)
        verify(exactly = once()) { formSubmitView.setOnClickListener(any()) }
        verify { anyConstructed<FormValidatorController>().configFormSubmit() }
    }

    @Test
    fun build_should_call_configFormSubmit_on_fetchForms() {
        form.buildView(rootView)

        verify { anyConstructed<FormValidatorController>().configFormSubmit() }
    }

    @Test
    fun onClick_of_formSubmit_should_set_formInputViews_on_formValidationActionHandler() {
        // Given When
        executeFormSubmitOnClickListener()

        // Then
        val views = form.getPrivateField<List<FormInput>>(FORM_INPUT_VIEWS_FIELD_NAME)
        verify(exactly = once()) { anyConstructed<FormValidationActionHandler>().formInputs = views }
    }

    @Test
    fun onClick_of_formSubmit_should_submit_remote_form() {
        // Given
        form = form.copy(action = remoteAction)

        // When
        executeFormSubmitOnClickListener()

        // Then
        verify(exactly = once()) { formSubmitView.hideKeyboard() }
        verify(exactly = once()) { anyConstructed<FormSubmitter>().submitForm(any(), any(), any()) }
    }

    @Test
    fun onClick_of_formSubmit_should_trigger_navigate_action() {
        // Given
        form = form.copy(action = navigateAction)

        // When
        executeFormSubmitOnClickListener()

        // Then
        verify(exactly = once()) { formSubmitView.hideKeyboard() }
        verify(exactly = once()) { anyConstructed<ActionExecutor>().doAction(any(), navigateAction) }
    }

    @Test
    fun onClick_of_formSubmit_should_validate_formField_that_is_required_and_is_valid() {
        // Given
        every { formInput.required } returns true
        every { validator.isValid(any(), any()) } returns true

        // When
        executeFormSubmitOnClickListener()

        // Then
        verify(exactly = once()) { validator.isValid(INPUT_VALUE, any()) }
    }

    @Test
    fun onClick_of_formSubmit_should_validate_formField_that_is_required_and_that_not_is_valid() {
        // Given
        every { formInput.required } returns true
        every { validator.isValid(any(), any()) } returns false

        // When
        executeFormSubmitOnClickListener()

        // Then
        verify(exactly = once()) { inputWidget.onErrorMessage(any()) }
    }

    @Test
    fun onClick_of_formSubmit_should_handleFormSubmit_and_call_actionExecutor() {
        // Given
        form = form.copy(action = remoteAction)
        val formResult = FormResult.Success(mockk())

        // When
        executeFormSubmitOnClickListener()
        formSubmitCallbackSlot.captured(formResult)
        runnableSlot.captured.run()

        // Then
        verify(exactly = once()) { anyConstructed<ActionExecutor>().doAction(beagleActivity, formResult.action) }
    }

    @Test
    fun onClick_of_formSubmit_should_trigger_action_and_call_showError() {
        // Given
        form = form.copy(action = remoteAction)

        every { beagleActivity.onServerDrivenContainerStateChanged(any()) } just Runs
        val formResult = FormResult.Error(mockk())

        // When
        executeFormSubmitOnClickListener()
        formSubmitCallbackSlot.captured(formResult)
        runnableSlot.captured.run()

        // Then
        verify(exactly = once()) {
            beagleActivity.onServerDrivenContainerStateChanged(ServerDrivenState.Error(formResult.throwable))
        }
    }

    private fun executeFormSubmitOnClickListener() {
        form.buildView(rootView)
        onClickListenerSlot.captured.onClick(formSubmitView)
    }

    @Test
    fun on_form_submit_should_save_data_locally_if_flag_enabled() {
        // Given
        form = form.copy(shouldStoreFields = true, group = FORM_GROUP_VALUE)

        // When
        executeFormSubmitOnClickListener()

        // Then
        verify {
            anyConstructed<FormDataStoreHandler>().put(
                eq(FORM_GROUP_VALUE),
                eq(INPUT_NAME),
                eq(INPUT_VALUE))
        }
    }

    @Test
    fun on_form_submit_should_not_save_data_locally_if_flag_disabled() {
        // Given
        form = form.copy(shouldStoreFields = false)

        // When
        executeFormSubmitOnClickListener()

        // Then
        verify(exactly = 0) {
            anyConstructed<FormDataStoreHandler>().put(any(), any(), any())
        }
    }

    @Test
    fun on_form_submit_should_send_saved_data() {
        // Given
        val savedKey = "savedKey"
        val savedValue = "savedValue"
        val savedMap = HashMap<String, String>()
        savedMap[savedKey] = savedValue

        form = form.copy(shouldStoreFields = false, group = FORM_GROUP_VALUE, action = remoteAction)

        every { anyConstructed<FormDataStoreHandler>().getAllValues(FORM_GROUP_VALUE) } returns savedMap

        // When
        executeFormSubmitOnClickListener()

        // Then
        assertEquals(formParamsSlot.captured[savedKey], savedValue)
    }

    @Test
    fun when_form_submit_succeeds_should_clear_saved_data() {
        // Given
        form = form.copy(shouldStoreFields = false, group = FORM_GROUP_VALUE, action = remoteAction)

        // When
        executeFormSubmitOnClickListener()
        formSubmitCallbackSlot.captured(FormResult.Success(remoteAction))
        runnableSlot.captured.run()

        // Then
        verify {
            anyConstructed<FormDataStoreHandler>().clear(eq(FORM_GROUP_VALUE))
        }
    }
}