package com.github.krakdown.block

import com.github.krakdown.block.node.UnorderedListNode
import com.github.krakdown.createBlockParser
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TodoListItemSpek : Spek({
    given("a todo line item with dashes in it") {
        it("should parse properly") {
            val parser = createBlockParser()
            val parsed = parser.parse("- [ ] ab-cd")
            assertNotNull(parsed)
            assertEquals(1, parsed.size)
            val node = parsed[0]
            assertTrue(node is UnorderedListNode)
            node as UnorderedListNode
            assertEquals(1, node.items.size)
            val item = node.items[0]
            assertTrue(item.hasTodo)
            assertFalse(item.todoIsComplete)
        }
    }

    given("a completed todo line item with dashes in it") {
        it("should parse properly") {
            val parser = createBlockParser()
            val parsed = parser.parse("- [x] ab-cd")
            assertNotNull(parsed)
            assertEquals(1, parsed.size)
            val node = parsed[0]
            assertTrue(node is UnorderedListNode)
            node as UnorderedListNode
            assertEquals(1, node.items.size)
            val item = node.items[0]
            assertTrue(item.hasTodo)
            assertTrue(item.todoIsComplete)
        }
    }
})