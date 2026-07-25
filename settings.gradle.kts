pluginManagement {
    repositories {
        google() // 必须包含 Google 仓库
        mavenCentral() // 必须包含 Maven Central 仓库
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "ZhrBlog"
include(":app")
