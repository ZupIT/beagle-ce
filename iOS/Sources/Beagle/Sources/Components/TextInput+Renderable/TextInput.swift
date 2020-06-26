//
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

import UIKit
import BeagleSchema

extension TextInput: ServerDrivenComponent {
    public func toView(renderer: BeagleRenderer) -> UIView {
        let textInputView = TextInputView(onChange: onChange,
                                          onBlur: onBlur,
                                          onFocus: onFocus,
                                          controller: renderer.controller)
        
        textInputView.styleId = styleId
        renderer.observe(value, andUpdate: \.text, in: textInputView)
        renderer.observe(placeholder, andUpdate: \.placeholder, in: textInputView)
        renderer.observe(type, andUpdate: \.inputType, in: textInputView)
        renderer.observe(disabled, andUpdateManyIn: textInputView) { disabled in
            if let disabled = disabled {
                textInputView.isEnabled = !disabled
            }
        }
        renderer.observe(readOnly, andUpdateManyIn: textInputView) { readOnly in
            if let readOnly = readOnly {
                textInputView.isEnabled = !readOnly
            }
        }
        renderer.observe(hidden, andUpdateManyIn: textInputView) { isHidden in
            if let isHidden = isHidden {
                textInputView.isHidden = isHidden
            }
        }
        
        return textInputView
    }
    
    class TextInputView: UITextField, UITextFieldDelegate, InputValue, WidgetStateObservable, ValidationErrorListener {
        
        var value: String?
        var disabled: Bool?
        var readOnly: Bool?
        var type: TextInputType?
        var onChange: [RawAction]?
        var onBlur: [RawAction]?
        var onFocus: [RawAction]?
        var inputType: TextInputType?
        var observable = Observable<WidgetState>(value: WidgetState(value: text))
        weak var controller: BeagleController?
        
        var styleId: String? {
            didSet { applyStyle() }
        }
        
        init(
            value: String? =  nil,
            placeholder: String? =  nil,
            disabled: Bool? =  nil,
            readOnly: Bool? =  nil,
            type: TextInputType? =  nil,
            hidden: Bool? =  nil,
            onChange: [RawAction]? =  nil,
            onBlur: [RawAction]? =  nil,
            onFocus: [RawAction]? =  nil,
            controller: BeagleController
        ) {
            self.value = value
            self.disabled = disabled
            self.readOnly = readOnly
            self.type = type
            self.onChange = onChange
            self.onBlur = onBlur
            self.onFocus = onFocus
            self.controller = controller
            super.init(frame: .zero)
            self.delegate = self
            self.isHidden = hidden ?? false
            self.placeholder = placeholder
            setup()
        }
        
        required init?(coder: NSCoder) {
            fatalError("init(coder:) has not been implemented")
        }
   
        func getValue() -> Any {
            return text ?? ""
        }
        
        func onValidationError(message: String?) {
            controller?.dependencies.logger.log(Log.form(.validationInputNotValid(inputName: "TextInput - " + (message ?? "Validation Error"))))
        }
        
        func textFieldDidBeginEditing(_ textField: UITextField) {
            let context = Context(id: "onFocus", value: .dictionary(["value": .string(textField.text ?? "")]))
            controller?.execute(actions: onFocus, with: context, sender: self)
        }
        
        func textFieldDidEndEditing(_ textField: UITextField) {
            let context = Context(id: "onBlur", value: .dictionary(["value": .string(textField.text ?? "")]))
            controller?.execute(actions: onBlur, with: context, sender: self)
        }
        
        func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
            var updatedText: String?
            if let text = textField.text,
               let textRange = Range(range, in: text) {
               updatedText = text.replacingCharacters(in: textRange, with: string)
            }
            textField.text = updatedText
            
            let context = Context(id: "onChange", value: .dictionary(["value": .string(updatedText ?? "")]))
            controller?.execute(actions: onChange, with: context, sender: self)
            
            return false
        }
    }
}

private extension TextInput.TextInputView {
    
    func applyStyle() {
        guard let styleId = styleId else { return }
        controller?.dependencies.theme.applyStyle(for: self as UITextField, withId: styleId)
    }
    
    func setupObservable() {
        addTarget(self, action: #selector(textChanged), for: .editingChanged)
    }
    
    @objc func textChanged() {
        observable.value.value = text
    }

    func setup() {
        setupValues()
        setupType()
        setupObservable()
    }
    
    func setupValues() {
        text = value
        isEnabled = disabled ?? readOnly ?? true
    }
    
    func setupType() {
        inputType = type ?? .text

        switch inputType {
        case .email:
            keyboardType = .emailAddress
        case .number, .date:
            keyboardType = .numberPad
        case .password:
            keyboardType = .default
            isSecureTextEntry = true
        case .text, .none:
            keyboardType = .default
        }
    }
}
