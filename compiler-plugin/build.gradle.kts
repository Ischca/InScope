plugins {
	`java-gradle-plugin`
	kotlin("jvm")
	kotlin("kapt")
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