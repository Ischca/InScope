package io.github.ischca.compilerPlugin

import arrow.meta.CliPlugin
import arrow.meta.Meta
import arrow.meta.invoke
import arrow.meta.phases.CompilerContext
import arrow.meta.plugins.proofs.phases.annotations
import com.google.auto.service.AutoService
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.com.intellij.psi.PsiElement
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.descriptors.annotations.AnnotationDescriptor
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallElement
import org.jetbrains.kotlin.psi.KtConstructorCalleeExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.psiUtil.getTextWithLocation
import org.jetbrains.kotlin.psi.psiUtil.parents
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.calls.callUtil.getResolvedCall
import org.jetbrains.kotlin.resolve.constants.ArrayValue
import org.jetbrains.kotlin.resolve.descriptorUtil.annotationClass
import org.jetbrains.kotlin.resolve.descriptorUtil.firstArgument
import org.jetbrains.kotlin.resolve.source.getPsi
import org.jetbrains.kotlin.util.suffixIfNot
import java.util.*
import kotlin.contracts.ExperimentalContracts

@AutoService(ComponentRegistrar::class)
class InScopePlugin: Meta
{
	@ExperimentalContracts
	override fun intercept(ctx: CompilerContext): List<CliPlugin> =
			listOf(
					inScope
			)
}

private const val ANNOTATION_NAME = "io.github.ischca.annotation.InScope"

private val Meta.inScope: CliPlugin
	get() = "InScope" {
		// Get annotation name from plugin option.
		val targetAnnotationName = this.configuration?.get(InScopeConfigurationKeys.ANNOTATION)
				?: ANNOTATION_NAME
		val targetAnnotationFqName = FqName(targetAnnotationName)
		val recursive = this.configuration?.get(InScopeConfigurationKeys.RECURSIVE)
				?: false
		
		meta(
				callChecker { resolvedCall, el, context ->
					if(el is KtConstructorCalleeExpression) return@callChecker
					val bindingContext = context.trace.bindingContext
					// Get annotations of the calling method.
					val manager = PsiManager.getInstance(project)
					val annotations = resolvedCall.resultingDescriptor.annotations.findRecursive(targetAnnotationFqName,
					                                                                             manager)
					if(annotations.isNotEmpty())
					{
						val targetAnnotation = annotations.firstOrNull { it.fqName == targetAnnotationFqName }
						// Get the block name from the annotation argument.
						val blockNames = when(val argument = targetAnnotation?.firstArgument())
						{
							is ArrayValue -> argument.value.map { it.stringTemplateValue() }
							else -> null
						}
						if(blockNames == null)
						{
							error(el,
							      "No argument is defined for the annotation \"$targetAnnotationName\". " +
									      "Define an argument of type String that takes a \"block name\" as its first argument.")
							return@callChecker
						}
						println("function=[${el.text}], blockName=[${blockNames}]")
						// Explore the caller and get one that matches the block name.
						val target = el.parent.parents.reduce { acc, _ ->
							when(acc)
							{
								is KtCallElement ->
								{
									if(!recursive || blockNames.contains(acc.calleeExpression?.text))
									{
										acc
									} else acc.parent
								}
								is KtNamedFunction ->
								{
									if(acc.isTopLevel || !recursive || blockNames.contains(acc.name))
									{
										acc
									} else acc.parent
								}
								else -> acc.parent
							}
						}
						// If the caller does not match the block name, it will result in an error.
						if((target !is KtCallElement ||
									!(blockNames.contains(target.calleeExpression?.text)
											|| (target.annotations(bindingContext)
											.any { t -> annotations.any { a -> t.fqName == a.fqName } }))
									)
							&& (target !is KtNamedFunction ||
									!(blockNames.contains(target.name)
											|| (target.annotations(bindingContext)
											.any { t -> annotations.any { a -> t.fqName == a.fqName } })
											)
									)
						)
						{
							val annotationNamesText = annotations.joinToString(separator = " or ") {
								"@${it.fqName?.shortName()}"
							}
							error(el,
							      "fun ${el.text.suffixIfNot("()")} is must be called in $blockNames {} " +
									      if(!recursive) "or attach $annotationNamesText annotation." else ".")
						}
					}
				}
		)
	}

private fun CompilerContext.error(el: PsiElement, message: String)
{
	val list = el.getTextWithLocation()
			.split(" ")
	val path = list.getOrNull(4)
	val lineAndColumn = list.getOrNull(2)
			?.removeSurrounding("(", ")")
			?.split(",")
	val line = lineAndColumn?.getOrNull(0)
			?.toInt()
	val column = lineAndColumn?.getOrNull(1)
			?.toInt()
	
	val compilerMessageLocation = if(line != null && column != null)
		CompilerMessageLocation.create(path,
		                               line,
		                               column,
		                               list.getOrNull(0))
	else null
	messageCollector?.report(CompilerMessageSeverity.ERROR, message,
	                         compilerMessageLocation)
}

private fun KtCallElement.annotations(context: BindingContext): Annotations =
		calleeExpression?.getResolvedCall(context)?.resultingDescriptor?.annotations ?: Annotations.EMPTY

/**
 * Recursively check the annotations attached to the annotations and return them in a list.
 */
fun Annotations.findRecursive(fqName: FqName, manager: PsiManager): List<AnnotationDescriptor>
{
	// Check the "fully qualified name" for annotations.
	val annotation = findAnnotation(fqName)
	return when
	{
		annotation != null -> listOf(annotation)
		// If the annotation name did not match, look for that annotation that was granted.
		isEmpty() -> emptyList()
		else -> filter {
			it.source.getPsi()
					?.let { psi -> manager.isInProject(psi) } == true
		}
				.flatMap {
					val parents = it.annotationClass
							?.annotations
							?.findRecursive(fqName, manager) ?: emptyList()
					if(parents.isNotEmpty()) parents.plus(listOf(it)) else parents
				}
	}
}