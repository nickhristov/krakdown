package com.github.krakdown.extensions

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.*

class GraphDiagramParserTest : Spek({
    val parser = GraphDiagramParser()

    given("a document header") {
        it("parses participants") {
            val input = """
                participant First
                participant Second
                participant Third
            """

            val result = parser.parse(input)
            assertEquals("Invalid number of participants", 3, result.vertexes.size)
            assertEquals("First participant is missing", "First", result.vertexes[0].label)
            assertEquals("Second participant is missing", "Second", result.vertexes[1].label)
            assertEquals("Third participant is missing", "Third", result.vertexes[2].label)
        }

        it("parses participant labels correctly") {
            val input = """
                participant F as First
                participant S as Second
                participant T as "Third special"
            """

            val result = parser.parse(input)
            assertEquals("Invalid number of participants", 3, result.vertexes.size)
            assertEquals("First", result.vertexes[0].label)
            assertEquals("F", result.vertexes[0].name)
            assertEquals("Second", result.vertexes[1].label)
            assertEquals("S", result.vertexes[1].name)
            assertEquals("Third special", result.vertexes[2].label)
            assertEquals("T", result.vertexes[2].name)
        }

        it("parses connections") {
            val input = """
                participant First
                participant Second
                participant Third
                First -> Second
                Second -> Third
            """

            val result = parser.parse(input)
            assertEquals("Invalid number of participants", 2, result.connections.size)
            assertEquals("First", result.connections[0].start.label)
            assertEquals("Second", result.connections[0].end.label)
            assertEquals("Second", result.connections[1].start.label)
            assertEquals("Third", result.connections[1].end.label)
        }

        it("adds vertices when they are missing as participants") {
            val input = """
                First -> Second
                Second -> Third
            """
            val result = parser.parse(input)
            assertEquals("Invalid number of participants", 3, result.vertexes.size)
            assertEquals("First participant is missing", "First", result.vertexes[0].name)
            assertEquals("First participant is missing", "Second", result.vertexes[1].name)
            assertEquals("First participant is missing", "Third", result.vertexes[2].name)
        }

        it("parses labels in connections") {
            val input = """
                First -> Second : "This is a simple message"
                Second -> Third : "This is another message"
            """
            val result = parser.parse(input)
            assertEquals("Invalid number of participants", 3, result.vertexes.size)
            assertEquals("First participant is missing", "This is a simple message", result.connections[0].label)
            assertEquals("First participant is missing", "This is another message", result.connections[1].label)
        }

        it("should parse properly encountered issue KR-6") {
            val input = """participant N as "New"
participant Q as "Queued"
participant S as "Submitted"
N -> Q
Q -> S
"""

            val result = parser.parse(input)
            assertEquals("Invalid number of vertices", 3, result.vertexes.size)
        }
    }
})