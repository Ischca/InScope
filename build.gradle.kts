plugins {
	java
	kotlin("jvm") version "1.4.10"
	`maven-publish`
}

repositories {
	jcenter()
}

subprojects {
	apply("plugin" to "java", "plugin" to "kotlin")
	group = "io.github.ischca"
	version = "0.0.3"
	java.sourceCompatibility = JavaVersion.VERSION_1_8
	java.targetCompatibility = JavaVersion.VERSION_1_8
	
	tasks {
		compileKotlin {
			kotlinOptions {
				jvmTarget = "1.8"
			}
		}
	}
}