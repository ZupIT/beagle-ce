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
import BeagleUI

struct LazyComponentScreen: DeeplinkScreen {
    
    init(path: String, data: [String : String]?) {
    }
    
    func screenController() -> UIViewController {
        return Beagle.screen(.declarative(screen))
    }
    
    var screen: Screen {
        return Screen(
            navigationBar: NavigationBar(title: "Form & LazyComponent"),
            child: Form(
                action: Navigate.addScreen(screen1),
                child: Container(children: [
                    Text("Form & LazyComponent"),
                    FormInput(
                        name: "field",
                        child: LazyComponent(
                            path: .TEXT_LAZY_COMPONENTS_ENDPOINT,
                            initialState: Text("Loading...")
                        )
                    ),
                    FormSubmit(child:
                        Text("FormSubmit")
                    )
                ]).applyFlex(Flex().justifyContent(.spaceBetween)),
                shouldStoreFields: true,
                formId: "firstForm"
            )
        )
    }
    
    var screen1: Screen {
        return Screen(
            navigationBar: NavigationBar(title: "Form & LazyComponent"),
            child: Form(
                action: FormRemoteAction(path: .TEXT_FORM_ENDPOINT, method: .get),
                child: Container(children: [
                    Text("Form & LazyComponent"),
                    FormInput(
                        name: "field1",
                        child: LazyComponent(
                            path: .TEXT_LAZY_COMPONENTS_ENDPOINT,
                            initialState: Text("Loading...")
                        )
                    ),
                    FormSubmit(child:
                        Text("FormSubmit")
                    )
                ]).applyFlex(Flex().justifyContent(.spaceBetween)),
                shouldStoreFields: true,
                formId: "firstForm",
                additionalData: ["cep":"38408","sexo":"masculimno"]
            )
        )
    }
}

extension UITextView: OnStateUpdatable, InputValue {
    public func onUpdateState(component: ServerDrivenComponent) -> Bool {
        guard let w = component as? Text else {
            return false
        }
        text = w.text
        return true
    }
    
    public func getValue() -> Any {
        return text ?? ""
    }
}
