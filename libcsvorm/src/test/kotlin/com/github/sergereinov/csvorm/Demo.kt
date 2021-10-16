package com.github.sergereinov.csvorm

import org.junit.Test

class Demo {

    data class Person(
        val id: Int,
        @CsvColumn("first_name") val firstName: String,
        @CsvColumn("last_name") val lastName: String
    )

    private val inputData = "id;first_name;last_name\r1;John;Gola\r2;Peter;Jascriptson\r3;Linda;Javac\r"
    private val inputBytes = inputData.toByteArray(Charsets.ISO_8859_1)

    @Test
    fun `Parse input manually`() {
        val lines = inputData.split("\r").map { it.trim() }.filterNot { it.isEmpty() }
        val csv = CsvHeader.from<Person>(header = lines.first())
        val items: List<Person> = lines.drop(1).map { csv.makeInstance(it) }
        items.forEach { println(it) }
    }

    @Test
    fun `Parse input in one call`() {
        val items: List<Person> = CsvHeader.parse<Person>(inputBytes)
        items.forEach { println(it) }
    }

    @Test
    fun `Make CSV output`() {
        val items = listOf(
            Person(1, "Peter", "Jascriptson"),
            Person(2, "John", "Gola"),
            Person(3, "James", "Javac")
        )

        val outputData = CsvMaker().make(items)
        println(outputData.replace("\r", "\n"))
    }
}
