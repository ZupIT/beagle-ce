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

extension UIView {
    static var contextMapKey = "contextMapKey"
    
    private class ObjectWrapper<T> {
        let object: T?
        
        init(_ object: T?) {
            self.object = object
        }
    }

    public var contextMap: [String: Observable<Context>]? {
        get {
            return (objc_getAssociatedObject(self, &UIView.contextMapKey) as? ObjectWrapper)?.object
        }
        set {
            objc_setAssociatedObject(self, &UIView.contextMapKey, ObjectWrapper(newValue), .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        }
    }
    
    // TODO: fix weak reference
    static var observers = "contextObservers"
    private var observers: [ContextObserver]? {
        get {
            return (objc_getAssociatedObject(self, &UIView.observers) as? ObjectWrapper)?.object
        }
        set {
            objc_setAssociatedObject(self, &UIView.observers, ObjectWrapper(newValue), .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        }
    }
    
    // MARK: Context Expression
    
    func configBinding<T>(for expression: ContextExpression, completion: @escaping (T) -> Void) {
        switch expression {
        case let .single(expression):
            configBinding(for: expression, completion: completion)
        case let .multiple(expression):
            configBinding(for: expression, completion: completion)
        }
    }
    
    func evaluate(for expression: ContextExpression) -> Any? {
        switch expression {
        case let .single(expression):
            return evaluate(for: expression)
        case let .multiple(expression):
            return evaluate(for: expression)
        }
    }

    // MARK: Single Expression
    
    private func configBinding<T>(for expression: SingleExpression, completion: @escaping (T) -> Void) {
        guard let context = getContext(with: expression.context) else { return }
        let closure: (Context) -> Void = { context in
            if let value = expression.evaluate(model: context.value) as? T {
                completion(value)
            }
        }
        configBinding(with: context, completion: closure)
        closure(context.value)
    }
    
    private func evaluate(for expression: SingleExpression) -> Any? {
        guard let context = getContext(with: expression.context) else { return nil }
        return expression.evaluate(model: context.value.value)
    }
    
    // MARK: Multiple Expression
    
    private func configBinding<T>(for expression: MultipleExpression, completion: @escaping (T) -> Void) {
        expression.nodes.forEach {
            if case let .expression(single) = $0 {
                guard let context = getContext(with: single.context) else { return }
                configBinding(with: context) { _ in
                    if let value = self.evaluate(for: expression, contextId: single.context) as? T {
                        completion(value)
                    }
                }
            }
        }
        if let value = self.evaluate(for: expression) as? T {
            completion(value)
        }
    }
    
    private func evaluate(for expression: MultipleExpression, contextId: String? = nil) -> Any? {
        var result: String = ""
        
        expression.nodes.forEach {
            switch $0 {
            case let .expression(expression):
                // TODO: create cache mechanism
                result += (evaluate(for: expression) as? String) ?? expression.rawValue
            case let .string(string):
                result += string
            }
        }
        return result
    }
    
    // MARK: Get/Set Context
    
    func getContext(with id: String?) -> Observable<Context>? {
        guard let contextMap = self.contextMap else {
            // TODO: create cache mechanism
            return superview?.getContext(with: id)
        }
        guard let context = contextMap[id] else {
            return superview?.getContext(with: id)
        }
        return context
    }
    
    func setContext(_ context: Context) {
        if let contextMap = contextMap {
            contextMap[context.id]?.value = context
        } else {
            contextMap = [context.id: Observable(value: context)]
        }
    }
    
    // MARK: Private
    
    private func configBinding(with context: Observable<Context>, completion: @escaping (Context) -> Void) {
        let contextObserver = ContextObserver(onContextChange: completion)
        if observers == nil {
            observers = []
        }
        observers?.append(contextObserver)
        context.addObserver(contextObserver)
    }
    
}

private extension Dictionary where Key == String, Value == Observable<Context> {
    subscript(context: String?) -> Observable<Context>? {
        guard let id = context else {
            return self.first?.value
        }
        
        return self[id]
    }
}
