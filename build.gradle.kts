plugins {
	java
	kotlin("jvm") version "1.4.10"
	`maven-publish`
	id("org.hibernate.build.maven-repo-auth") version "3.0.4"dfadfdf
}

repositories {
	jcenter()
}

subprojects {
	apply("plugin" to "java", "plugin" to "kotlin")
	group = "io.github.ischca"
	version = "0.0.4"
	java.sourceCompatibility = JavaVersion.VERSION_1_8
	java.targetCompatibility = JavaVersion.VERSION_1_8
	
	tasks {
		compileKotlin {
			kotlinOptions {
				jvmTarget = "1.8"
			}
		}
	}
	apply("plugin" to "maven-publish")
	publishing {
		repositories {
			mavenLocal()
			maven {
				name = "github"
				url = uri("https://maven.pkg.github.com/ischca/inScope")
			}
		}
		publications {
			register("pluginMaven", MavenPublication::class) {
				groupId = "io.github.ischca"
				afterEvaluate {
					artifactId = tasks.jar.get().archiveBaseName.get()
					if(artifactId == "annotation")
					{
						from(components["java"])
					}
				}
			}
		}
	}
	apply("plugin" to "maven-publish", "plugin" to "org.hibernate.build.maven-repo-auth")
}