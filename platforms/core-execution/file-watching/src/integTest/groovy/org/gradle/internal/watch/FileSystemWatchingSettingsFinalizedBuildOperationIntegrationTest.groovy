/*
 * Copyright 2020 the original author or authors.
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

package org.gradle.internal.watch

import org.gradle.testdistribution.LocalOnly
import org.gradle.integtests.fixtures.BuildOperationsFixture
import org.gradle.internal.watch.options.FileSystemWatchingSettingsFinalizedProgressDetails

@LocalOnly
class FileSystemWatchingSettingsFinalizedBuildOperationIntegrationTest extends AbstractFileSystemWatchingIntegrationTest {

    def operations = new BuildOperationsFixture(executer, temporaryFolder)

    def "emits once when not used"() {
        given:
        settingsFile << "includeBuild 'plugin'"
        file("buildSrc/src/main/java/Thing.java") << "class Thing {}"
        createDir("plugin") {
            file("settings.gradle") << ""
            file("build.gradle") << "plugins { id 'java' }"
            file("buildSrc/src/main/java/Thing.java") << "class Thing {}"
            file("src/main/java/Thing.java") << "class Thing {}"
        }
        file("build.gradle") << "task t"

        when:
        withoutWatchFs().succeeds("t")

        then:
        events().enabled == [false]
    }

    def "emits once when used"() {
        given:
        file("buildSrc/src/main/java/Thing.java") << "class Thing {}"
        file("build.gradle") << "task t"

        when:
        withWatchFs().succeeds("t")

        then:
        events().enabled == [true]
    }

    List<FileSystemWatchingSettingsFinalizedProgressDetails> events() {
        return operations.progress(FileSystemWatchingSettingsFinalizedProgressDetails).details
    }
}
