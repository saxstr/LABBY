pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
                mavenCentral()
            }
            mavenCentral()
        }

        gradlePluginPortal()
        buildscript {
            repositories {
                mavenCentral()
            }
        }
        mavenCentral()
    }

}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()

        mavenCentral()
    }

}




rootProject.name = "gp_test"
include(":app")
 