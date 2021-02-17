plugins {
	`java-gradle-plugin`
	kotlin("jvm")
	kotlin("kapt")
	id("io.github.ischca.compiler-plugin") version "0.0.4"
}

repositories {
	jcenter()
	maven("https://oss.jfrog.org/artifactory/oss-snapshot-local/")
	mavenLocal()
}

dependencies {
	val arrow_version = "1.4.10-SNAPSHOT"
	implementation(project(":compiler-plugin"))
	implementation(project(":annotation"))
	implementation("io.arrow-kt:compiler-plugin:$arrow_version")
	
	// test
	testImplementation("org.junit.jupiter:junit-jupiter:5.4.2")
	testImplementation("io.arrow-kt:meta-test:$arrow_version")
}

tasks {
	test {
		useJUnitPlatform()
	}
}