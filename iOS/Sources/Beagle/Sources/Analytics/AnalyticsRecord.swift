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

public struct AnalyticsRecord {

    public let platform = "ios"
    public var values: [String: DynamicObject]

    public let type: RecordType

    public enum RecordType {
        case screen(Screen)
        case action(Action)
    }
    
    public init(type: RecordType, values: [String: DynamicObject] = [:]) {
        self.type = type
        self.values = values
    }

    public struct Screen {
        public var url, screenId: String?
    }

    public struct Action {
        public var beagleAction: String
        public var event: String?
        public var screen: String?
        public var component: Component

        public struct Component {
            public var id: String?
            public var type: String?
            public var position: Position

            public struct Position { let x, y: Double }
        }
    }
}

extension AnalyticsRecord {

    public func toDictionary() -> [String: DynamicObject] {
        var dict = values

        switch type {
        case .action(let action):
            dict["type"] = "action"
            let object = transformToDynamicObject(action).asDictionary()
            dict.merge(object, uniquingKeysWith: { $1 })

        case .screen(let screen):
            dict["type"] = "screen"
            let object = transformToDynamicObject(screen).asDictionary()
            dict.merge(object, uniquingKeysWith: { $1 })
        }

        dict["platform"] = .string(platform)
        return dict
    }
}

extension AnalyticsRecord: Encodable {

    public func encode(to encoder: Encoder) throws {
        let dict = self.toDictionary()
        try dict.encode(to: encoder)
    }
}
