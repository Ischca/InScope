plugins {
	java
	kotlin("jvm") version "1.4.10"
	maven
}

repositories {
	jcenter()
}

subprojects {
	apply("plugin" to "java", "plugin" to "kotlin")
	group = "io.github.ischca"
	version = "0.0.4"
	java {
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
		withJavadocJar()
		withSourcesJar()
	}
	
	tasks {
		compileKotlin {
			kotlinOptions {
				jvmTarget = "1.8"
			}
		}
		javadoc {
			if(JavaVersion.current().isJava9Compatible)
			{
				(options as StandardJavadocDocletOptions).addBooleanOption("html5", true)
			}
		}
	}
}