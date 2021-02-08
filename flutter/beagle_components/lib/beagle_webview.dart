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

import 'package:flutter/material.dart';
import 'package:webview_flutter/platform_interface.dart';
import 'package:webview_flutter/webview_flutter.dart';

class BeagleWebView extends StatelessWidget {
  const BeagleWebView({
    Key key,
    this.url,
  }) : super(key: key);

  final String url;

  @override
  Widget build(BuildContext context) {
    return WebView(
      initialUrl: url,
      javascriptMode: JavascriptMode.unrestricted,
      onWebResourceError: _handleError,
      onPageStarted: _handleLoading,
      onPageFinished: _handleSuccess,
    );
  }

  void _handleLoading(String url) {
    // TODO: loading handling is pending until definition of Beagle's screen state handling mechanism.
  }

  void _handleSuccess(String url) {
    // TODO: success handling is pending until definition of Beagle's screen state handling mechanism.
  }

  void _handleError(WebResourceError error) {
    // TODO: error handling is pending until definition of Beagle's screen state handling mechanism.
  }
}
