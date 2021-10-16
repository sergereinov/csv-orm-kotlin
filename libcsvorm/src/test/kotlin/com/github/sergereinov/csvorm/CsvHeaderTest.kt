package com.github.sergereinov.csvorm

import org.junit.Assert.*;
import org.junit.Test

class CsvHeaderTest {

    @Test
    fun `parse CsvColumn annotation with CsvHeader`() {

        data class TestCsv(
            @CsvColumn("col_f1")
            val f1: Int,
            @CsvColumn("col_f2")
            val f2: String = "---",
            @CsvColumn("col_f3")
            val f3: String,
            val f4: String
        )

        val header = "a;b;col_f1;c;d;col_f3;e;f4"
        val line = "123;abc;456;zxc;qwe;pop;789;bnm"

        assertEquals(
            mapOf<String, Int>("f1" to 2, "f2" to -1, "f3" to 5, "f4" to 7),
            CsvHeader.from<TestCsv>(header).indexes
        )

        assertEquals(
            TestCsv(f1 = 456, f3 = "pop", f4 = "bnm"),
            CsvHeader.from<TestCsv>(header).makeInstance(line)
        )
    }

}