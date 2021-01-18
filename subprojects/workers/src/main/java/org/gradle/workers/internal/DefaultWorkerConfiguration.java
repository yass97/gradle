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

package org.gradle.workers.internal;

import com.google.common.collect.Lists;
import org.gradle.api.Action;
import org.gradle.api.ActionConfiguration;
import org.gradle.api.internal.DefaultActionConfiguration;
import org.gradle.process.JavaForkOptions;
import org.gradle.process.internal.JavaForkOptionsFactory;
import org.gradle.util.GUtil;
import org.gradle.workers.ClassLoaderWorkerSpec;
import org.gradle.workers.IsolationMode;
import org.gradle.workers.ProcessWorkerSpec;
import org.gradle.workers.WorkerConfiguration;
import org.gradle.workers.WorkerSpec;

import java.io.File;
import java.util.List;

public class DefaultWorkerConfiguration extends DefaultActionConfiguration implements WorkerConfiguration {
    private final ActionConfiguration actionConfiguration = new DefaultActionConfiguration();
    private final JavaForkOptionsFactory forkOptionsFactory;
    private IsolationMode isolationMode = IsolationMode.AUTO;
    private JavaForkOptions forkOptions;
    private String displayName;
    private List<File> classpath = Lists.newArrayList();

    public DefaultWorkerConfiguration(JavaForkOptionsFactory forkOptionsFactory) {
        this.forkOptionsFactory = forkOptionsFactory;
    }

    @Override
    public IsolationMode getIsolationMode() {
        return isolationMode;
    }

    @Override
    public void setIsolationMode(IsolationMode isolationMode) {
        this.isolationMode = isolationMode == null ? IsolationMode.AUTO : isolationMode;
    }

    @Override
    public void forkOptions(Action<? super JavaForkOptions> forkOptionsAction) {
        forkOptionsAction.execute(getForkOptions());
    }

    @Override
    public JavaForkOptions getForkOptions() {
        if (forkOptions == null) {
            forkOptions = forkOptionsFactory.newDecoratedJavaForkOptions();
        }
        return forkOptions;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public Iterable<File> getClasspath() {
        return classpath;
    }

    @Override
    public void setClasspath(Iterable<File> classpath) {
        this.classpath = Lists.newArrayList(classpath);
    }

    @Override
    public void classpath(Iterable<File> files) {
        GUtil.addToCollection(classpath, files);
    }

    @Override
    public void params(Object... params) {
        actionConfiguration.params(params);
    }

    @Override
    public void setParams(Object... params) {
        actionConfiguration.setParams(params);
    }

    @Override
    public Object[] getParams() {
        return actionConfiguration.getParams();
    }

    void adaptTo(WorkerSpec workerSpec) {
        if (workerSpec instanceof ClassLoaderWorkerSpec) {
            ClassLoaderWorkerSpec classLoaderWorkerSpec = (ClassLoaderWorkerSpec) workerSpec;
            classLoaderWorkerSpec.getClasspath().from(getClasspath());
        }

        if (workerSpec instanceof ProcessWorkerSpec) {
            ProcessWorkerSpec processWorkerSpec = (ProcessWorkerSpec) workerSpec;
            processWorkerSpec.getClasspath().from(getClasspath());
            getForkOptions().copyTo(processWorkerSpec.getForkOptions());
        }
    }

}
