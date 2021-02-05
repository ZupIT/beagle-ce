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

extension TextInput: ServerDrivenComponent {
    public func toView(renderer: BeagleRenderer) -> UIView {
        let textInputView = TextInputView(onChange: onChange,
                                          onBlur: onBlur,
                                          onFocus: onFocus,
                                          controller: renderer.controller)
        
        setupExpressions(toView: textInputView, renderer: renderer)
        
        if let styleId = styleId {
            textInputView.beagle.applyStyle(for: textInputView as UITextField, styleId: styleId, with: renderer.controller)
        }
        
        return textInputView
    }
    
    private func setupExpressions(toView view: TextInputView, renderer: BeagleRenderer) {
        renderer.observe(value, andUpdate: \.text, in: view)
        renderer.observe(placeholder, andUpdate: \.placeholder, in: view)
        renderer.observe(type, andUpdate: \.inputType, in: view)
        renderer.observe(disabled, andUpdateManyIn: view) { disabled in
            if let disabled = disabled {
                view.isEnabled = !disabled
            }
        }
        renderer.observe(readOnly, andUpdateManyIn: view) { readOnly in
            if let readOnly = readOnly {
                view.isEnabled = !readOnly
            }
        }
        renderer.observe(hidden, andUpdateManyIn: view) { isHidden in
            if let isHidden = isHidden {
                view.isHidden = isHidden
            }
        }
        renderer.observe(error, andUpdateManyIn: view) { errorMessage in
            if let errorMessage = errorMessage {
                view.errorMessage = errorMessage
            }
        }
        renderer.observe(showError, andUpdateManyIn: view) { showError in
            if let showError = showError {
                view.showError = showError
            }
        }
    }
    
    class TextInputView: UITextField, UITextFieldDelegate, InputValue, WidgetStateObservable, ValidationErrorListener {
        
        var onChange: [Action]?
        var onBlur: [Action]?
        var onFocus: [Action]?
        var showError: Bool = false {
            didSet {
                validationLabel.isHidden = !showError
                if let errorMessage = errorMessage, !errorMessage.isEmpty {
                    layer.borderColor = validationLabel.isHidden ? UIColor.red.cgColor : UIColor.gray.cgColor
                }
            }
        }
        var inputType: TextInputType? {
            didSet {
                setupType()
            }
        }
        var errorMessage: String? {
            didSet {
                validationLabel.text = errorMessage
            }
        }
        
        var observable = Observable<WidgetState>(value: WidgetState(value: text))
        weak var controller: BeagleController?
        
        private lazy var validationLabel: UILabel = {
            let label = UILabel()
            label.textColor = .red
            label.font = .systemFont(ofSize: 15)
            label.numberOfLines = 0
            label.translatesAutoresizingMaskIntoConstraints = false
            return label
        }()
        
        init(
            onChange: [Action]? =  nil,
            onBlur: [Action]? =  nil,
            onFocus: [Action]? =  nil,
            controller: BeagleController
        ) {
            self.onChange = onChange
            self.onBlur = onBlur
            self.onFocus = onFocus
            self.controller = controller
            super.init(frame: .zero)
            setupValidationLabel()
            self.delegate = self
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
            let value: DynamicObject = .dictionary(["value": .string(textField.text ?? "")])
            controller?.execute(actions: onFocus, with: "onFocus", and: value, origin: self)
        }
        
        func textFieldDidEndEditing(_ textField: UITextField) {
            let value: DynamicObject = .dictionary(["value": .string(textField.text ?? "")])
            controller?.execute(actions: onBlur, with: "onBlur", and: value, origin: self)
        }
        
        func textFieldShouldReturn(_ textField: UITextField) -> Bool {
            resignFirstResponder()
        }
        
        func textField(_ textField: UITextField, shouldChangeCharactersIn range: NSRange, replacementString string: String) -> Bool {
            var updatedText: String?
            if let text = textField.text,
               let textRange = Range(range, in: text) {
               updatedText = text.replacingCharacters(in: textRange, with: string)
            }
            
            textField.text = updatedText
            textChanged(updatedText)
            
            let value: DynamicObject = .dictionary(["value": .string(updatedText ?? "")])
            controller?.execute(actions: onChange, with: "onChange", and: value, origin: self)
            
            return false
        }
    }
}

private extension TextInput.TextInputView {
    
    func textChanged(_ text: String?) {
        observable.value.value = text
    }

    func setupType() {
        let inputType = self.inputType ?? .text

        switch inputType {
        case .email:
            keyboardType = .emailAddress
        case .number, .date:
            keyboardType = .numberPad
            setupToolBar()
        case .password:
            keyboardType = .default
            isSecureTextEntry = true
        case .text:
            keyboardType = .default
        }
    }
    
    func setupValidationLabel() {
        if !showError {
            addSubview(validationLabel)
            validationLabel.anchor(top: bottomAnchor, left: leftAnchor, right: rightAnchor, topConstant: 1)
            layer.borderWidth = 0.5
            layer.cornerRadius = 5
        }
    }
    
    func setupToolBar() {
        let toolBar = UIToolbar()
        let spacer = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil)
        let doneButton = UIBarButtonItem(barButtonSystemItem: .done, target: self, action: #selector(resignFirstResponder))
        toolBar.items = [spacer, doneButton]
        toolBar.sizeToFit()
        inputAccessoryView = toolBar
    }
}
