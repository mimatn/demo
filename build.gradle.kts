plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
    alias(libs.plugins.spotless)
    application
}

dependencies {
    implementation(libs.bundles.jackson)
    implementation(libs.bundles.dataframe)

    detektPlugins(libs.detektfmt)
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("com.example.demo.MainKt")
}

detekt {
    config.setFrom(file("config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
    parallel = true
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    format("misc") {
        target("**/*.gradle", "**/*.md", "**/.gitignore", "**/*.xml", "**/*.properties")

        trimTrailingWhitespace()
        leadingTabsToSpaces(2)
        endWithNewline()
    }

    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }

    java {
        importOrder("java", "javax", "com.aton", "")

        removeUnusedImports()
        googleJavaFormat()
    }
}
