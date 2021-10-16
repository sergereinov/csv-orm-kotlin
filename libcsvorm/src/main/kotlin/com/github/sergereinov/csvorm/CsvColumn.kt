package com.github.sergereinov.csvorm

@Target(AnnotationTarget.PROPERTY)
annotation class CsvColumn(val columnName: String)

