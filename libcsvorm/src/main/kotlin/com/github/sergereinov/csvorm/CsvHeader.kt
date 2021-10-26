package com.github.sergereinov.csvorm

import java.nio.charset.Charset
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation


/*
    helpful refs:
    https://kotlinlang.org/docs/tutorials/kotlin-for-py/member-references-and-reflection.html
    https://blog.kotlin-academy.com/creating-a-random-instance-of-any-class-in-kotlin-b6168655b64a
 */


/*
    Usage:

    data class Person(
        val id: Int,
        @CsvColumn("first_name") val firstName: String,
        @CsvColumn("last_name") val lastName: String
    )

    val data = "id;first_name;last_name\r1;John;Doe\r".toByteArray(Charsets.ISO_8859_1)

    val lines = data.toString(Charsets.ISO_8859_1).split('\r')
    val csv = CsvHeader.from<Person>(header = lines[0])
    val person = csv.makeInstance(line = lines[1])

    assertTrue(
        person.id == 1 &&
        person.firstName == "John" &&
        person.lastName == "Doe"
    )

 */


class CsvHeader<T>(
    private val kClass: KClass<*>,
    header: String,
    private val delimiter: Char = ';'
) {

    var indexes: Map<String, Int>


    init {

        //parse class declaration
        val columns = mutableMapOf<String, String>()
        kClass.declaredMemberProperties.forEach { prop ->
            val colName = prop.findAnnotation<CsvColumn>()?.columnName ?: prop.name
            if (colName.isNotEmpty()) columns[colName] = prop.name
        }

        //init prop names map
        val names = columns.values.associateWith { -1 }.toMutableMap()

        //parse header
        header.split(delimiter).forEachIndexed { i, colName ->
            columns[colName]?.let { propName -> names[propName] = i }
        }

        indexes = names
    }


    fun makeInstance(line: String): T {

        val data = line.split(delimiter)
        val args = mutableMapOf<KParameter, Any?>()

        val constructor = kClass.constructors.first()
        constructor.parameters.forEach { param ->
            indexes[param.name]?.let { i ->
                if (i >= 0 && i < data.size) {
                    args[param] = makeColumn(param.type.classifier as KClass<*>, data[i])
                }
            }
        }

        @Suppress("UNCHECKED_CAST")
        return constructor.callBy(args) as T
    }

    private fun makeColumn(clazz: KClass<*>, value: String): Any? = when(clazz) {
        Byte::class -> value.toByteOrNull()
        Short::class -> value.toShortOrNull()
        Int::class -> value.toIntOrNull()
        Long::class -> value.toLongOrNull()
        Float::class -> value.toFloatOrNull()
        Double::class -> value.toDoubleOrNull()
        String::class -> value
        else -> null
    }


    companion object {
        inline fun <reified T : Any> from(header: String, delimiter: Char = ';'): CsvHeader<T> {
            return CsvHeader(T::class, header, delimiter)
        }

        inline fun <reified T : Any> parse(
            data: ByteArray?,
            charset: Charset = Charsets.ISO_8859_1,
            delimiter: Char = ';'
        ): List<T> {

            data?.let {
                val lines = data
                    .toString(charset)
                    .split('\r')
                    .filter { it.isNotEmpty() }

                if (lines.size > 1) {
                    val csv = from<T>(lines[0])
                    return lines.subList(1, lines.lastIndex+1).map { csv.makeInstance(it) }
                }
            }

            return listOf()
        }
    }
}
