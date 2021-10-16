package com.github.sergereinov.csvorm

import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties


/*
    Usage:

    data class Person(
        val id: Int,
        @CsvColumn("first_name") val firstName: String,
        @CsvColumn("last_name") val lastName: String
    )

    val list = listOf(
        Person(1, "John", "Doe")
    )

    val text = CsvMaker().make(list)

    assertTrue(
        text == "id;first_name;last_name\r1;John;Doe\r"
    )

*/


class CsvMaker(
    val delimiter: Char = ';'
) {

    inline fun <reified T : Any> make(list: List<T>): String =
        list.joinToString(
            separator = "",
            prefix = makeHeader<T>()
        ) { makeLine(it) }

    inline fun <reified T : Any> makeHeader(): String =
        T::class.declaredMemberProperties.joinToString(
            separator = delimiter.toString(),
            postfix = "\r"
        ) { prop -> prop.findAnnotation<CsvColumn>()?.columnName ?: prop.name }

    inline fun <reified T : Any> makeLine(instance: T): String =
        T::class.memberProperties.joinToString(
            separator = delimiter.toString(),
            postfix = "\r"
        ) { prop -> prop.get(instance).toString() }

}