package io.github.ischca.compilerPlugin

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CliOptionProcessingException
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

object InScopeConfigurationKeys
{
	val ANNOTATION: CompilerConfigurationKey<String> =
			CompilerConfigurationKey.create("annotation qualified name")
	val RECURSIVE: CompilerConfigurationKey<Boolean> =
			CompilerConfigurationKey.create("true is recursive check")
}

@AutoService(CommandLineProcessor::class)
class InScopeCommandLineProcessor: CommandLineProcessor
{
	companion object
	{
		val ANNOTATION_OPTION = CliOption(optionName = "annotation",
		                                  valueDescription = "<fqname>",
		                                  description = "Annotation qualified name",
		                                  required = false,
		                                  allowMultipleOccurrences = false)
		val RECURSIVE_OPTION = CliOption(optionName = "recursive",
		                                 valueDescription = "true or false",
		                                 description = "True is recursive check, default is false",
		                                 required = false,
		                                 allowMultipleOccurrences = false)
		val PLUGIN_ID = "io.github.ischca.in-scope"
	}
	
	override val pluginId: String
		get() = PLUGIN_ID
	
	override val pluginOptions: Collection<AbstractCliOption>
		get() = listOf(
				ANNOTATION_OPTION,
				RECURSIVE_OPTION
		)
	
	override fun processOption(option: AbstractCliOption,
	                           value: String,
	                           configuration: CompilerConfiguration) = when(option)
	{
		ANNOTATION_OPTION -> configuration.put(InScopeConfigurationKeys.ANNOTATION, value)
		RECURSIVE_OPTION -> configuration.put(InScopeConfigurationKeys.RECURSIVE, value.toBoolean())
		else -> throw CliOptionProcessingException("Unknown option: ${option.optionName}")
	}
}