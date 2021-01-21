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

package br.com.zup.beagle.newanalytics

import br.com.zup.beagle.android.BaseTest
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mock
import java.sql.Timestamp
import java.util.Calendar
import java.util.Date
import java.util.TimeZone


@DisplayName("Given a Data Report")
internal class DataReportTest : BaseTest(){

    @DisplayName("When create")
    @Nested
    inner class Create {

        @Test
        @DisplayName("Then should create the timestamp with correct time")
        fun testWhenCreateADataReportTestThenShouldGetTheTimestampCorrectly() {
            //Given
            val dataReport = object : DataReport() {
                override fun report(analyticsConfig: AnalyticsConfig): AnalyticsRecord? {
                    return null
                }
            }
            val regex = "[A-Z][a-z][a-z] [A-Z][a-z][a-z] [0-9][0-9] [0-9][0-9]:[0-9][0-9]:[0-9][0-9] UTC [0-9][0-9][0-9][0-9]".toRegex()
            assertTrue(dataReport.timestamp.matches(regex))
        }
    }
}
