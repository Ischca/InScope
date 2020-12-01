plugins {
	kotlin("jvm")
	`maven-publish`
}

repositories {
	jcenter()
}

publishing {
	repositories {
		mavenLocal()
	}
	publications {
		register("pluginMaven", MavenPublication::class) {
			groupId = "io.github.ischca"
			artifactId = "annotation"
			from(components["java"])
		}
	}
}