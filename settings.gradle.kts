pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenLocal()
		maven("https://oss.jfrog.org/artifactory/oss-snapshot-local/")
	}
	
	resolutionStrategy {
	}
}
rootProject.name = "in-scope"
include("annotation", "compiler-plugin", "use-plugin")