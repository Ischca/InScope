plugins {
	kotlin("jvm")
	maven
	signing
	`maven-publish`
	id("org.hibernate.build.maven-repo-auth") version "3.0.4"
}

repositories {
	jcenter()
}

publishing {
	repositories {
		mavenLocal()
		maven {
			name = "sonatype"
			url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
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
			pom {
				name.set("InScope")
				description.set("A compiler plugin that provides checks to ensure that function calls are in scope at build time.")
				url.set("https://github.com/Ischca/InScope")
				licenses {
					license {
						name.set("The Apache License, Version 2.0")
						url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
					}
				}
				developers {
					developer {
						id.set("ischca")
						name.set("Ischca")
						email.set("03.suiseiseki@gmail.com")
					}
				}
				scm {
					connection.set("https://github.com/Ischca/InScope.git")
					developerConnection.set("https://github.com/Ischca/InScope.git")
					url.set("https://github.com/Ischca/InScope")
				}
			}
		}
	}
}
signing {
	useGpgCmd()
	sign(publishing.publications["pluginMaven"])
}