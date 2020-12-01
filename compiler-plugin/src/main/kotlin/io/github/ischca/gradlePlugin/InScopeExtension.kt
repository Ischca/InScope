package io.github.ischca.gradlePlugin

open class InScopeExtension
{
	internal val myAnnotations = mutableListOf<String>()
	var recursive = false
	
	open fun annotation(fqName: String)
	{
		myAnnotations.add(fqName)
	}
	
	open fun annotations(fqNames: List<String>)
	{
		myAnnotations.addAll(fqNames)
	}
	
	open fun annotations(vararg fqNames: String)
	{
		myAnnotations.addAll(fqNames)
	}
}