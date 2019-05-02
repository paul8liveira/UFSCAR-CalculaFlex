package br.blog.paul8liveira.calculaflex.extensions

fun Double.format(digits: Int) =  java.lang.String.format("%.${digits}f", this)