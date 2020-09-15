// THIS IS A GENERATED FILE. DO NOT EDIT THIS
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

public struct TextInput: Widget, AutoDecodable {

    public let value: Expression<String>?
    public let placeholder: Expression<String>?
    public let disabled: Expression<Bool>?
    public let readOnly: Expression<Bool>?
    public let type: Expression<TextInputType>?
    public let hidden: Expression<Bool>?
    public let styleId: String?
    public let onChange: [Action]?
    public let onBlur: [Action]?
    public let onFocus: [Action]?
    public let widgetProperties: WidgetProperties

    public init(
        value: Expression<String>? = nil,
        placeholder: Expression<String>? = nil,
        disabled: Expression<Bool>? = nil,
        readOnly: Expression<Bool>? = nil,
        type: Expression<TextInputType>? = nil,
        hidden: Expression<Bool>? = nil,
        styleId: String? = nil,
        onChange: [Action]? = nil,
        onBlur: [Action]? = nil,
        onFocus: [Action]? = nil,
        widgetProperties: WidgetProperties = WidgetProperties()
    ) {
        self.value = value
        self.placeholder = placeholder
        self.disabled = disabled
        self.readOnly = readOnly
        self.type = type
        self.hidden = hidden
        self.styleId = styleId
        self.onChange = onChange
        self.onBlur = onBlur
        self.onFocus = onFocus
        self.widgetProperties = widgetProperties
    }

}
