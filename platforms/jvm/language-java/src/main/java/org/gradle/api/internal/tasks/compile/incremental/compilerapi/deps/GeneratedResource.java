/*
 * Copyright 2021 the original author or authors.
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

package org.gradle.api.internal.tasks.compile.incremental.compilerapi.deps;

import com.google.common.base.Objects;
import org.jspecify.annotations.Nullable;

import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;
import java.io.Serializable;

/**
 * Uniquely identifies a resource that was generated by an annotation processor.
 */
public final class GeneratedResource implements Serializable {
    /**
     * The supported locations into which generated resources may be placed.
     */
    public enum Location {
        CLASS_OUTPUT,
        SOURCE_OUTPUT,
        NATIVE_HEADER_OUTPUT,;

        /**
         * @return null if the given location is not supported.
         */
        @Nullable
        public static Location from(JavaFileManager.Location location) {
            if (location instanceof StandardLocation) {
                switch ((StandardLocation) location) {
                    case CLASS_OUTPUT:
                        return CLASS_OUTPUT;
                    case SOURCE_OUTPUT:
                        return SOURCE_OUTPUT;
                    case NATIVE_HEADER_OUTPUT:
                        return NATIVE_HEADER_OUTPUT;
                    default:
                        break;
                }
            }
            return null;
        }
    }

    private final Location location;
    private final String path;

    public GeneratedResource(Location location, CharSequence pkg, CharSequence relativeName) {
        this(location, pkg.length() == 0 ? relativeName.toString() : pkg.toString().replace('.', '/') + '/' + relativeName);
    }

    public GeneratedResource(Location location, String path) {
        this.location = location;
        this.path = path;
    }

    public Location getLocation() {
        return location;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GeneratedResource that = (GeneratedResource) o;
        return location == that.location &&
            path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(location, path);
    }

    @Override
    public String toString() {
        return path + " in " + location;
    }
}
