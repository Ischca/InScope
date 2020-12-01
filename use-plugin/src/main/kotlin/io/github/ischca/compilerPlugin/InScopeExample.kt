package io.github.ischca.compilerPlugin

import io.github.ischca.annotation.InScope

@InScope("caller")
annotation class A

@A
annotation class B

object Hoge
{
	@B
	fun select() = println("select!")
}

fun caller(proc: () -> Unit)
{
	proc()
}

@B
fun caller2(proc: () -> Unit)
{
	proc()
}

fun main()
{
	caller {
		caller2 {
			Hoge.select()
		}
	}
}