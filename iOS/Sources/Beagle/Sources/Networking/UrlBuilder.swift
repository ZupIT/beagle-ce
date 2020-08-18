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

public protocol DependencyUrlBuilder {
    var urlBuilder: UrlBuilderProtocol { get }
}

public protocol UrlBuilderProtocol {
    var baseUrl: URL? { get set }

    func build(path: String) -> URL?
}

public class UrlBuilder: UrlBuilderProtocol {

    public var baseUrl: URL?

    public init(baseUrl: URL? = nil) {
        self.baseUrl = baseUrl
    }

    public func build(path: String) -> URL? {
        guard let encodedPath = shouldEncode(path) ? path.addingPercentEncodingForRFC3986 : path else {
            return URL(string: path)
        }
        switch getUrlType(path: path, baseUrl: baseUrl) {
        case .absolutePath:
            return URL(string: encodedPath)
        case .relativePath:
            guard var absolute = baseUrl?.absoluteString else {
                return URL(string: encodedPath)
            }
            if absolute.hasSuffix("/") {
                absolute.removeLast()
            }
            return URL(string: absolute + encodedPath)
        }
    }
    
    private func getUrlType(path: String, baseUrl: URL?) -> UrlType {
        return path.hasPrefix("/") ? .relativePath : .absolutePath
    }
    
    private func shouldEncode(_ path: String) -> Bool {
        guard let decodedPath = path.removingPercentEncoding else { return false }
        return decodedPath == path
    }
}

private enum UrlType {
    case relativePath
    case absolutePath
}

private extension String {
    var addingPercentEncodingForRFC3986: String? {
        let unreserved = "&-._,~/?:;@$"
        let allowedCharacterSet = NSMutableCharacterSet.alphanumeric()
        allowedCharacterSet.addCharacters(in: unreserved)
        return addingPercentEncoding(withAllowedCharacters: allowedCharacterSet as CharacterSet)
    }
}
