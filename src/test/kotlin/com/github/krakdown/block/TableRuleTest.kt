package com.github.krakdown.block

import com.github.krakdown.block.node.TableCellNode
import com.github.krakdown.block.node.TableNode
import com.github.krakdown.block.node.TableRowNode
import com.github.krakdown.createBlockParser
import com.github.krakdown.inline.InlineParser
import com.github.krakdown.visitors.InlineHtmlVisitor
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TableRuleTest : Spek ({

    given("a standard table") {
        val table = """| a | b |
| --- | --- |
| abb | bbba |
"""
        val inlineParser = InlineParser()
        val tableRule = TableRule(inlineParser)

        val input = table.split("\n")
        it("recognizes cell count correctly") {
            val result = tableRule.getCellCount(input[1], true)
            assertEquals(listOf(" --- ", " --- "), result, "Invalid count of cellNodes")
        }

        it ("recognizes correctly header row") {
            val result = tableRule.getCellCount(input[0], false)
            assertEquals(listOf(" a ", " b "), result, "Invalid count of cellNodes")
        }

        it ("header row does not parse like separator row") {
            val result = tableRule.getCellCount(input[0], true)
            assertEquals(emptyList(), result, "Invalid count of cellNodes")
        }

        it ("recognizes correctly body row") {
            val result = tableRule.getCellCount(input[2], false)
            assertEquals(listOf(" abb ", " bbba "), result, "Invalid count of cellNodes")
        }

        it ("parses correctly a table") {
            val result = tableRule.generate(input)
            assertEquals(3, result.lines)
            assertTrue(result.nodes.isNotEmpty())
            assertEquals(1, result.nodes.size)
            val expected = TableNode(listOf(
                    TableRowNode(listOf(TableCellNode(inlineParser.parse(" a ")), TableCellNode(inlineParser.parse(" b ")))),
                    TableRowNode(listOf(TableCellNode(inlineParser.parse(" abb ")), TableCellNode(inlineParser.parse(" bbba "))))
            ))
            assertEquals(expected, result.nodes[0])
        }

        it ("integration test: parser parses input correctly") {
            val nodes = createBlockParser().parse(table)
            val visitor = InlineHtmlVisitor()
            val content = visitor.accept(nodes[0])
            val expectedContent = "<table><tr><td> a </td><td> b </td></tr><tr><td> abb </td><td> bbba </td></tr></table>"
            assertEquals(expectedContent, content)
        }

    }
})