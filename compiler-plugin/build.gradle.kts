plugins {
	id("com.gradle.plugin-publish") version "0.12.0"
	`java-gradle-plugin`
	kotlin("jvm")
	kotlin("kapt")
}

repositories {
	jcenter()
	maven("https://oss.jfrog.org/artifactory/oss-snapshot-local/")
}

gradlePlugin {
	plugins {
		create("inScopePlugin") {
			id = "io.github.ischca.compiler-plugin"
			implementationClass = "io.github.ischca.gradlePlugin.InScopeGradlePlugin"
		}
	}
}

pluginBundle {
	website = "https://github.com/Ischca/InScope"
	vcsUrl = "https://github.com/Ischca/InScope"
	description = "A compiler plugin that provides checks to ensure that function calls are in scope at build time."
	
	(plugins) {
		"inScopePlugin" {
			displayName = "InScope Compiler Plugin"
			tags = listOf("compiler")
		}
	}
}

tasks.register("publish") {
	dependsOn(tasks.publishPlugins)
}

val kotlinVersion: String by project

dependencies {
	val arrow_version = "1.4.10-SNAPSHOT"
	compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin-api:$kotlinVersion")
	compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable:$kotlinVersion")
	implementation("io.arrow-kt:compiler-plugin:$arrow_version")
	compileOnly("com.google.auto.service:auto-service:1.0-rc7")
	kapt("com.google.auto.service:auto-service:1.0-rc7")
	compileOnly(project(":annotation"))
}