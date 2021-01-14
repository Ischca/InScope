package io.github.ischca.compilerPlugin

import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.PluginOption
import arrow.meta.plugin.testing.assertThis
import org.junit.jupiter.api.Test

const val annotationSource =
		"""
			|@Retention(AnnotationRetention.SOURCE)
			|@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
			|annotation class Fuga(val blockName: String)
		"""

fun CompilerTest.Companion.InScopeConfig(vararg pluginOptions: PluginOption) =
		listOf(
				addMetaPlugins(InScopePlugin()),
				addCommandLineProcessors(InScopeCommandLineProcessor()),
				addPluginOptions(
						PluginOption(InScopeCommandLineProcessor.PLUGIN_ID,
						             InScopeCommandLineProcessor.ANNOTATION_OPTION.optionName,
						             "io.github.ischca.compilerPlugin.Fuga"),
						*pluginOptions
				)
		)

internal class InScopePluginKtTest
{
	@Test
	fun `should success`()
	{
		assertThis(CompilerTest(
				config = { InScopeConfig() },
				code = {
					//language=kotlin
					"""
						|package io.github.ischca.compilerPlugin
						|
						|
						$annotationSource
						|object Hoge {
						|    @Fuga("transaction")
						|    fun select() = println("select!")
						|}
						|
						|fun transaction() {
						|    Hoge.select()
						|}
					""".trimIndent().source
				},
				assert = { compiles }))
	}
	
	@Test
	fun `should success nested call`()
	{
		assertThis(CompilerTest(
				config = { InScopeConfig() },
				code = {
					//language=kotlin
					"""
						|package io.github.ischca.compilerPlugin
						|
						|
						$annotationSource
						|object Hoge {
						|    @Fuga("transaction")
						|    fun select() = println("select!")
						|}
						|
						|fun transaction(proc: () -> Unit) { proc() }
						|
						|fun main() {
						|    transaction {
						|        Hoge.select()
						|    }
						|}
					""".trimIndent().source
				},
				assert = { compiles }))
	}
	
	@Test
	fun `should success recursive`()
	{
		assertThis(CompilerTest(
				config = {
					InScopeConfig(
							PluginOption(InScopeCommandLineProcessor.PLUGIN_ID,
							             InScopeCommandLineProcessor.RECURSIVE_OPTION.optionName,
							             true.toString()))
				},
				code = {
					//language=kotlin
					"""
						|package io.github.ischca.compilerPlugin
						|
						|
						$annotationSource
						|object Hoge {
						|    @Fuga("transaction")
						|    fun select() = println("select!")
						|}
						|
						|fun transaction(proc: () -> Unit) { proc() }
						|
						|fun caller(proc: () -> Unit) { proc() }
						|
						|fun main() {
						|    transaction {
						|       caller {
						|        Hoge.select()
						|       }
						|    }
						|}
					""".trimIndent().source
				},
				assert = { compiles }))
	}
	
	@Test
	fun `should success child annotation`()
	{
		assertThis(CompilerTest(
				config = { InScopeConfig() },
				code = {
					//language=kotlin
					"""
						|package io.github.ischca.compilerPlugin
						|
						|
						$annotationSource
						|
						|@Fuga("transaction")
						|annotation class Piyo
						|
						|object Hoge {
						|    @Piyo
						|    fun select() = println("select!")
						|}
						|
						|fun transaction(proc: () -> Unit) { proc() }
						|
						|@Piyo
						|fun caller(proc: () -> Unit) { proc() }
						|
						|fun main() {
						|    transaction {
						|       caller {
						|        Hoge.select()
						|       }
						|    }
						|}
					""".trimIndent().source
				},
				assert = { compiles }))
	}
	
	@Test
	fun `should success annotate function`()
	{
		assertThis(CompilerTest(
				config = {
					InScopeConfig(
							PluginOption(InScopeCommandLineProcessor.PLUGIN_ID,
							             InScopeCommandLineProcessor.RECURSIVE_OPTION.optionName,
							             true.toString()))
				},
				code = {
					//language=kotlin
					"""
						|package io.github.ischca.compilerPlugin
						|
						|
						$annotationSource
						|
						|@Fuga("transaction")
						|annotation class Piyo
						|
						|object Hoge {
						|    @Piyo
						|    fun select() = println("select!")
						|}
						|
						|fun transaction(proc: () -> Unit) { proc() }
						|
						|@Piyo
						|fun caller(proc: () -> Unit) { proc() }
						|
						|@Piyo
						|fun main() {
						|    Hoge.select()
						|}
					""".trimIndent().source
				},
				assert = { compiles }))
	}
	
	@Test
	fun `should success multiple block name`()
	{
		assertThis(CompilerTest(
				config = {
					InScopeConfig(
							PluginOption(InScopeCommandLineProcessor.PLUGIN_ID,
							             InScopeCommandLineProcessor.RECURSIVE_OPTION.optionName,
							             true.toString()))
				},
				code = {
					//language=kotlin
					"""
						|package io.github.ischca.compilerPlugin
						|
						$annotationSource
						|
						|@Fuga("transaction")
						|annotation class Piyo
						|
						|object Hoge {
						|    @Piyo
						|    fun select() = println("select!")
						|
						|    @Piyo
						|    fun call() {
						|        Hoge.select()
						|    }
						|}
					""".trimIndent().source
				},
				assert = { compiles }))
	}
	
	@Test
	fun `should success function definition`()
	{
		assertThis(CompilerTest(
				config = { InScopeConfig() },
				code = {
					//language=kotlin
					"""
						|package io.github.ischca.compilerPlugin
						|
						|
						$annotationSource
						|object Hoge {
						|    @Fuga("transaction")
						|    fun select() = println("select!")
						|}
						|
						|fun transaction() {
						|    Hoge.select()
						|}
					""".trimIndent().source
				},
				assert = { compiles }))
	}
	
	@Test
	fun `should success call in init block`()
	{
		assertThis(CompilerTest(
				config = { InScopeConfig() },
				code = {
					//language=kotlin
					"""
						|package io.github.ischca.compilerPlugin
						|
						|
						$annotationSource
						|
						|@Fuga("transaction")
						|fun select() = println("select!")
						|
						|@Fuga("transaction")
						|object Hoge {
						|    init { select() }
						|}
					""".trimIndent().source
				},
				assert = { compiles }))
	}
	
	@Test
	fun `should fail`()
	{
		assertThis(CompilerTest(
				config = { InScopeConfig() },
				code = {
					//language=kotlin
					"""
						|package io.github.ischca.compilerPlugin
						|
						|
						$annotationSource
						|object Hoge {
						|    @Fuga("transaction")
						|    fun select() = println("select!")
						|}
						|
						|fun transaction(proc: () -> Unit) {
						|    proc()
						|}
						|
						|fun main() {
						|    Hoge.select()
						|}
					""".trimIndent().source
				},
				assert = { failsWith { it.contains("must be called in transaction {}") } }))
	}
	
	@Test
	fun `should fail recursive`()
	{
		assertThis(CompilerTest(
				config = {
					InScopeConfig(
							PluginOption(InScopeCommandLineProcessor.PLUGIN_ID,
							             InScopeCommandLineProcessor.RECURSIVE_OPTION.optionName,
							             true.toString()))
				},
				code = {
					//language=kotlin
					"""
						|package io.github.ischca.compilerPlugin
						|
						|
						$annotationSource
						|object Hoge {
						|    @Fuga("transaction")
						|    fun select() = println("select!")
						|}
						|
						|fun caller(proc: () -> Unit) { proc() }
						|
						|fun caller2(proc: () -> Unit) { proc() }
						|
						|fun main() {
						|    caller {
						|       caller2 {
						|        Hoge.select()
						|       }
						|    }
						|}
					""".trimIndent().source
				},
				assert = { failsWith { it.contains("must be called in transaction {}") } }))
	}
	
	@Test
	fun `should fail child annotation`()
	{
		assertThis(CompilerTest(
				config = { InScopeConfig() },
				code = {
					//language=kotlin
					"""
						|package io.github.ischca.compilerPlugin
						|
						|
						$annotationSource
						|
						|@Fuga("transaction")
						|annotation class Piyo
						|
						|object Hoge {
						|    @Piyo
						|    fun select() = println("select!")
						|}
						|
						|fun transaction(proc: () -> Unit) { proc() }
						|
						|fun caller(proc: () -> Unit) { proc() }
						|
						|fun main() {
						|    transaction {
						|       caller {
						|        Hoge.select()
						|       }
						|    }
						|}
					""".trimIndent().source
				},
				assert = { failsWith { it.contains("attach @Fuga or @Piyo annotation") } }))
	}
}