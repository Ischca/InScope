package io.github.ischca.gradlePlugin

import com.google.auto.service.AutoService
import io.github.ischca.compilerPlugin.InScopeCommandLineProcessor
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

@AutoService(KotlinCompilerPluginSupportPlugin::class)
class InScopeGradlePlugin: KotlinCompilerPluginSupportPlugin
{
	override fun getCompilerPluginId(): String = InScopeCommandLineProcessor.PLUGIN_ID
	override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact(
			groupId = "io.github.ischca", artifactId = "in-scope", version = "0.0.2"
	)
	
	override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean
	{
		return kotlinCompilation.target.project.plugins.hasPlugin(InScopeGradlePlugin::class.java)
	}
	
	override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>>
	{
		val project = kotlinCompilation.target.project
		val extension = project.extensions.findByType(InScopeExtension::class.java)
				?: InScopeExtension()
		return kotlinCompilation.target.project.provider {
			val options = mutableListOf<SubpluginOption>()
			for(annotation in extension.myAnnotations)
			{
				options += SubpluginOption(InScopeCommandLineProcessor.ANNOTATION_OPTION.optionName, annotation)
			}
			options += SubpluginOption(InScopeCommandLineProcessor.RECURSIVE_OPTION.optionName,
			                           extension.recursive.toString())
			options
		}
	}
	
	override fun apply(target: Project)
	{
		target.extensions.create("inScope", InScopeExtension::class.java)
	}
}