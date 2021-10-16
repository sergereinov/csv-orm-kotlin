package com.github.sergereinov.csvorm

import org.junit.Assert.*
import org.junit.Test

class CsvMakerTest {

    @Test
    fun `make csv text`() {

        data class TestCsv(
            @CsvColumn("col_f1")
            val f1: Int,
            @CsvColumn("col_f2")
            val f2: String = "---",
            @CsvColumn("col_f3")
            val f3: String,
            val f4: String
        )

        val list = listOf(
            TestCsv(1, "f21", "f31", "f41"),
            TestCsv(f1 = 2, f3 = "f32", f4 = "f42")
        )

        assertEquals(
            "col_f1;col_f2;col_f3;f4\r1;f21;f31;f41\r2;---;f32;f42\r",
            CsvMaker().make(list)
        )

    }
}