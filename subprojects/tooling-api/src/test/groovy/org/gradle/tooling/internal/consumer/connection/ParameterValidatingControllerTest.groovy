/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.tooling.internal.consumer.connection

import org.gradle.api.Action
import org.gradle.tooling.BuildController
import org.gradle.tooling.UnsupportedVersionException
import org.gradle.tooling.internal.consumer.versioning.VersionDetails
import spock.lang.Specification

class ParameterValidatingControllerTest extends Specification {
    def version = Mock(VersionDetails)
    def delegate = Mock(BuildController)
    def controller = new ParameterValidatingController(version, delegate)

    def "simply delegates for versions supporting parameterized models for parameter not null"() {
        def model = new Object()
        def modelType = Object.class
        def parameterType = Object.class
        def parameterInitializer = Mock(Action)

        given:
        _ * version.supportsParameterizedToolingModels() >> true
        _ * delegate.getModel(_, modelType, parameterType, parameterInitializer) >> model

        when:
        def result = controller.getModel(null, modelType, parameterType, parameterInitializer)

        then:
        result == model
    }

    def "simply delegates for versions not supporting parameterized models for parameter null"() {
        def model = new Object()
        def modelType = Object.class

        given:
        _ * version.supportsParameterizedToolingModels() >> true
        _ * delegate.getModel(_, modelType, null, null) >> model

        when:
        def result = controller.getModel(null, modelType, null, null)

        then:
        result == model
    }

    def "throws exception for versions not supporting parameterized models for parameter not null"() {
        def model = new Object()
        def modelType = Object.class
        def parameterType = Object.class
        def parameterInitializer = Mock(Action)

        given:
        _ * version.getVersion() >> "4.3"
        _ * version.supportsParameterizedToolingModels() >> false
        _ * delegate.getModel(_, modelType, parameterType, parameterInitializer) >> model

        when:
        controller.getModel(null, modelType, parameterType, parameterInitializer)

        then:
        UnsupportedVersionException e = thrown()
        e.message == "Gradle version 4.3 used does not support parameterized tooling models."
    }
}
