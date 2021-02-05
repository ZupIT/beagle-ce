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

import Foundation
import UIKit

func generateScreenRecord(
    screen: ScreenType,
    globalConfigIsEnabled: Bool?
) -> AnalyticsRecord? {
    let isScreenDisabled = globalConfigIsEnabled == false
    if isScreenDisabled { return nil }

    return AnalyticsRecord(type: .screen, screen: screenInfo(screen))
}

struct AnalyticsGenerator {

    let info: AnalyticsService.ActionInfo
    let globalConfig: AnalyticsConfig.AttributesByActionName?

    // MARK: - Action

    func createRecord() -> AnalyticsRecord? {
        guard let name = getActionName() else { return assertNeverGetsHere(or: nil) }
        guard let config = configForAction(named: name) else { return nil }

        let action = AnalyticsRecord.Action(
            beagleAction: name,
            event: info.event,
            component: componentInfo()
        )

        return .init(
            type: .action(action),
            screen: screenInfo(info.controller.screenType),
            values: attributesAndAdditionalEntries(config: config)
        )
    }
}

// MARK: - Private

private extension AnalyticsGenerator {

    func getActionName() -> String? {
        Mirror(reflecting: info.action).descendant("_beagleAction_") as? String
            ?? info.controller.dependencies.decoder.nameForAction(ofType: type(of: info.action))
    }

    func configForAction(named: String) -> ActionConfig? {
        guard let global = globalConfig else {
            // we need to store all attributes until globalConfig gets set
            return .init(attributes: .all)
        }

        let attributes = global[named] ?? []

        switch info.action.analytics {
        case .disabled:
            return nil
        case .enabled(nil), nil:
            return .init(attributes: .some(attributes))
        case .enabled(let analytics?):
            return .init(
                attributes: .some(analytics.attributes ?? [] + attributes),
                additional: analytics.additionalEntries ?? [:]
            )
        }
    }

    struct ActionConfig {
        let attributes: ActionAttributes
        var additional = [String: DynamicObject]()
    }

    func attributesAndAdditionalEntries(config: ActionConfig) -> [String: DynamicObject] {
        info.action.getSomeAttributes(config.attributes, contextProvider: info.origin)
            .merging(config.additional) { _, new in new }
    }

    func componentInfo() -> AnalyticsRecord.Action.Component {
        let name = info.origin.componentType
            .flatMap(info.controller.dependencies.decoder.nameForComponent(ofType:))

        let id = info.origin.accessibilityIdentifier

        let point = info.origin.convert(CGPoint.zero, to: nil)
        let position = (
            x: Double(point.x),
            y: Double(point.y)
        )
        return .init(id: id, type: name, position: .init(x: position.x, y: position.y))
    }
}

private func screenInfo(_ screenType: ScreenType) -> String? {
    switch screenType {
    case .remote(let remote):
        return remote.url
    case .declarative(let declarative):
        return declarative.identifier
    case .declarativeText:
        return ""
    }
}
