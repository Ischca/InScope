plugins {
	`java-gradle-plugin`
	kotlin("jvm")
	kotlin("kapt")
	`maven-publish`
}

repositories {
	jcenter()
	maven("https://oss.jfrog.org/artifactory/oss-snapshot-local/")
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

gradlePlugin {
	plugins {
		register("in-scope") {
			id = "io.github.ischca.in-scope"
			implementationClass = "io.github.ischca.gradlePlugin.InScopeGradlePlugin"
		}
	}
}

publishing {
	repositories {
		mavenLocal()
	}
	publications {
		register("pluginMaven", MavenPublication::class) {
			groupId = "io.github.ischca"
			artifactId = "in-scope"
		}
	}
}