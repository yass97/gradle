plugins {
    id("application")
}

repositories {
    mavenCentral()
}

// Declare the dependency on the producer project
dependencies {
    implementation(project(":producer"))
}

tasks.register("checkResolvedVariant") {
    val configurationsToCheck = project.configurations.filter { configuration ->
        // Skip `test*` configurations and `annotationProcessor` during configuration
        !(configuration.name.startsWith("test") || configuration.name == "annotationProcessor")
    }

    doLast {
        configurationsToCheck.forEach { configuration ->
            if (configuration.isCanBeResolved) {
                println("Configuration: ${configuration.name}")
                val resolvedArtifacts = configuration.incoming.artifacts.resolvedArtifacts
                resolvedArtifacts.get().forEach { artifact ->
                    println("-Artifact: ${artifact.file}")
                }
                val resolvedComponents = configuration.incoming.resolutionResult.allComponents
                resolvedComponents.forEach { component ->
                    if (component.id.displayName == "project :producer") {
                        println("- Component: ${component.id}")
                        component.variants.forEach {
                            println("    - Variant: ${it}")
                            it.attributes.keySet().forEach { key ->
                                println("       - ${key.name} -> ${it.attributes.getAttribute(key)}")
                            }
                        }
                    }
                }
            }
        }

    }
}

tasks.register("artifactWithAttributeAndView") {
    doLast {
        val configuration = configurations.runtimeClasspath.get()
        println("Attributes used to resolve '${configuration.name}':")
        configuration.attributes.keySet().forEach { attribute ->
            val value = configuration.attributes.getAttribute(attribute)
            println("  - ${attribute.name} = $value")
        }

        println("\nAttributes in ArtifactView for 'LibraryElements = classes:'")
        val artifactView = configuration.incoming.artifactView {
            attributes {
                attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named("classes"))
            }
        }

        artifactView.artifacts.artifactFiles.files.forEach {
            println("- Artifact: ${it.name}")
        }

        artifactView.attributes.keySet().forEach { attribute ->
            val value = artifactView.attributes.getAttribute(attribute)
            println("  - ${attribute.name} = $value")
        }
    }
}

tasks.register("artifactWithAttributeAndVariantReselectionView") {
    doLast {
        val configuration = configurations.runtimeClasspath.get()
        println("Attributes used to resolve '${configuration.name}':")
        configuration.attributes.keySet().forEach { attribute ->
            val value = configuration.attributes.getAttribute(attribute)
            println("  - ${attribute.name} = $value")
        }

        println("\nAttributes in ArtifactView for 'Category = production:'")
        val artifactView = configuration.incoming.artifactView {
            withVariantReselection()
            attributes {
                attribute(Category.CATEGORY_ATTRIBUTE, objects.named("production"))
            }
        }

        artifactView.artifacts.artifactFiles.files.forEach {
            println("- Artifact: ${it.name}")
        }

        artifactView.attributes.keySet().forEach { attribute ->
            val value = artifactView.attributes.getAttribute(attribute)
            println("  - ${attribute.name} = $value")
        }
    }
}
