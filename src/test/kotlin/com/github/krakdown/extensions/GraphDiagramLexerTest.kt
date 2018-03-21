package com.github.krakdown.extensions

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.junit.Assert.*

class GraphDiagramLexerTest : Spek ({

    given("a lexer") {
        it("lexes single participant heading") {
            val input = "participant S as Tomcat"
            val result = GraphDiagramLexer.process(input)
            assertEquals("Number of tokens does not match" , 4, result.size)
            val vertexToken = result[0]
            assertTrue("Participant token type does not match", vertexToken is VertexTypeToken)
            vertexToken as VertexTypeToken
            assertEquals("Participant type does not match", GraphVertexType.PARTICIPANT, vertexToken.type)
            val nameToken = result[1]
            assertTrue("Label token does not match", nameToken is NameToken)
            nameToken as NameToken
            assertEquals("Name does not match", "S", nameToken.name)
            val asToken = result[2]
            assertTrue("As token does not match", asToken is As)
            val actualNameToken = result[3]
            assertTrue("Name token type does not match", actualNameToken is NameToken )
            actualNameToken as NameToken
            assertEquals("Label does not match", "Tomcat", actualNameToken.name)
        }

        it("lexes label in heading") {
            val input = """participant S as "Tomcat""""
            val result = GraphDiagramLexer.process(input)
            assertEquals("Number of tokens does not match" , 4, result.size)
            val labelToken = result[3]
            assertTrue("Name token type does not match", labelToken is LabelToken )
            labelToken as LabelToken
            assertEquals("Label does not match", "Tomcat", labelToken.label)
        }

        it ("lexes a participant with out a label in heading") {
            val input = """participant Foo"""
            val result = GraphDiagramLexer.process(input)
            assertEquals("Number of tokens does not match" , 2, result.size)
            val vertexToken = result[0]
            assertTrue("Participant token type does not match", vertexToken is VertexTypeToken)
            vertexToken as VertexTypeToken
            assertEquals("Participant type does not match", GraphVertexType.PARTICIPANT, vertexToken.type)
            val nameToken = result[1]
            assertTrue("Name token does not match", nameToken is NameToken)
            nameToken as NameToken
            assertEquals("Name does not match", "Foo", nameToken.name)
        }

        it ("lexes multiple participants with out a label in heading") {
            val input = """participant Joe
                participant Smith"""
            val result = GraphDiagramLexer.process(input)
            assertEquals("Number of tokens does not match" , 5, result.size)
            val vertexToken = result[0]
            assertTrue("Participant token type does not match", vertexToken is VertexTypeToken)
            vertexToken as VertexTypeToken
            assertEquals("Participant type does not match", GraphVertexType.PARTICIPANT, vertexToken.type)
            val nameToken = result[1]
            assertTrue("Name token does not match", nameToken is NameToken)
            nameToken as NameToken
            assertEquals("Name does not match", "Joe", nameToken.name)
            assertTrue("Newline token mismatch", result[2] is Newline)

            val secondVertexToken = result[3]
            assertTrue("Participant token type does not match", secondVertexToken is VertexTypeToken)
            secondVertexToken as VertexTypeToken
            assertEquals("Participant type does not match", GraphVertexType.PARTICIPANT, secondVertexToken.type)
            val secondNameToken = result[4]
            assertTrue("Name token does not match", secondNameToken is NameToken)
            secondNameToken as NameToken
            assertEquals("Name does not match", "Smith", secondNameToken.name)
        }

        it ("lexes a database in heading") {
            val input = """database MySQL"""
            val result = GraphDiagramLexer.process(input)
            assertEquals("Number of tokens does not match" , 2, result.size)
            val vertexToken = result[0]
            assertTrue("Participant token type does not match", vertexToken is VertexTypeToken)
            vertexToken as VertexTypeToken
            assertEquals("Participant type does not match", GraphVertexType.DATABASE, vertexToken.type)
            val nameToken = result[1]
            assertTrue("Name token does not match", nameToken is NameToken)
            nameToken as NameToken
            assertEquals("Name does not match", "MySQL", nameToken.name)
        }

        it ("lexes a queue in heading") {
            val input = """queue RabbitMQ"""
            val result = GraphDiagramLexer.process(input)
            assertEquals("Number of tokens does not match" , 2, result.size)
            val vertexToken = result[0]
            assertTrue("Participant token type does not match", vertexToken is VertexTypeToken)
            vertexToken as VertexTypeToken
            assertEquals("Participant type does not match", GraphVertexType.QUEUE, vertexToken.type)
            val nameToken = result[1]
            assertTrue("Name token does not match", nameToken is NameToken)
            nameToken as NameToken
            assertEquals("Name does not match", "RabbitMQ", nameToken.name)
        }

        it ("lexes an interface in heading") {
            val input = """interface MyDAO"""
            val result = GraphDiagramLexer.process(input)
            assertEquals("Number of tokens does not match" , 2, result.size)
            val vertexToken = result[0]
            assertTrue("Participant token type does not match", vertexToken is VertexTypeToken)
            vertexToken as VertexTypeToken
            assertEquals("Participant type does not match", GraphVertexType.INTERFACE, vertexToken.type)
            val nameToken = result[1]
            assertTrue("Name token does not match", nameToken is NameToken)
            nameToken as NameToken
            assertEquals("Name does not match", "MyDAO", nameToken.name)
        }

        it ("lexes an swimlane in heading") {
            val input = """swimlane Group1"""
            val result = GraphDiagramLexer.process(input)
            assertEquals("Number of tokens does not match" , 2, result.size)
            val vertexToken = result[0]
            assertTrue("Participant token type does not match", vertexToken is VertexTypeToken)
            vertexToken as VertexTypeToken
            assertEquals("Participant type does not match", GraphVertexType.SWIMLANE, vertexToken.type)
            val nameToken = result[1]
            assertTrue("Name token does not match", nameToken is NameToken)
            nameToken as NameToken
            assertEquals("Name does not match", "Group1", nameToken.name)
        }


        it ("lexes an terminal in heading") {
            val input = """terminal TerminalOne"""
            val result = GraphDiagramLexer.process(input)
            assertEquals("Number of tokens does not match" , 2, result.size)
            val vertexToken = result[0]
            assertTrue("Participant token type does not match", vertexToken is VertexTypeToken)
            vertexToken as VertexTypeToken
            assertEquals("Participant type does not match", GraphVertexType.TERMINAL, vertexToken.type)
            val nameToken = result[1]
            assertTrue("Name token does not match", nameToken is NameToken)
            nameToken as NameToken
            assertEquals("Name does not match", "TerminalOne", nameToken.name)
        }

        it ("lexes an decision in heading") {
            val input = """decision Decision"""
            val result = GraphDiagramLexer.process(input)
            assertEquals("Number of tokens does not match" , 2, result.size)
            val vertexToken = result[0]
            assertTrue("Participant token type does not match", vertexToken is VertexTypeToken)
            vertexToken as VertexTypeToken
            assertEquals("Participant type does not match", GraphVertexType.DECISION, vertexToken.type)
            val nameToken = result[1]
            assertTrue("Name token does not match", nameToken is NameToken)
            nameToken as NameToken
            assertEquals("Name does not match", "Decision", nameToken.name)
        }

        it ("lexes an decision in heading") {
            val input = """process Process"""
            val result = GraphDiagramLexer.process(input)
            assertEquals("Number of tokens does not match" , 2, result.size)
            val vertexToken = result[0]
            assertTrue("Participant token type does not match", vertexToken is VertexTypeToken)
            vertexToken as VertexTypeToken
            assertEquals("Participant type does not match", GraphVertexType.PROCESS, vertexToken.type)
            val nameToken = result[1]
            assertTrue("Name token does not match", nameToken is NameToken)
            nameToken as NameToken
            assertEquals("Name does not match", "Process", nameToken.name)
        }

        it ("lexes an decision in heading") {
            val input = """predefprocess Predef"""
            val result = GraphDiagramLexer.process(input)
            assertEquals("Number of tokens does not match" , 2, result.size)
            val vertexToken = result[0]
            assertTrue("Participant token type does not match", vertexToken is VertexTypeToken)
            vertexToken as VertexTypeToken
            assertEquals("Participant type does not match", GraphVertexType.PREDEFPROCESS, vertexToken.type)
            val nameToken = result[1]
            assertTrue("Name token does not match", nameToken is NameToken)
            nameToken as NameToken
            assertEquals("Name does not match", "Predef", nameToken.name)
        }

        it ("lexes forward between two participants") {
            val input = """Smith -> Joe"""
            val result = GraphDiagramLexer.process(input)
            assertEquals("Number of tokens does not match" , 3, result.size)
            val nameToken = result[0]
            val connectionToken = result[1]
            val secondNameToken = result[2]
            assertTrue("Participant token type does not match", nameToken is NameToken)
            assertTrue("Name token does not match", secondNameToken is NameToken)
            assertTrue("Connection token does not match" , connectionToken is Forward)
            nameToken as NameToken
            secondNameToken as NameToken
            assertEquals("Participant type does not match", "Smith", nameToken.name)
            assertEquals("Participant type does not match", "Joe", secondNameToken.name)
        }

        it ("lexes forward x connection between two participants") {
            val input = """Smith -x Joe"""
            val result = GraphDiagramLexer.process(input)
            assertEquals("Number of tokens does not match" , 3, result.size)
            val nameToken = result[0]
            val connectionToken = result[1]
            val secondNameToken = result[2]
            assertTrue("Participant token type does not match", nameToken is NameToken)
            assertTrue("Name token does not match", secondNameToken is NameToken)
            assertTrue("Connection token does not match" , connectionToken is ForwardX)
            nameToken as NameToken
            secondNameToken as NameToken
            assertEquals("Participant type does not match", "Smith", nameToken.name)
            assertEquals("Participant type does not match", "Joe", secondNameToken.name)
        }


        it ("lexes backward connection between two participants") {
            val input = """Smith <- Joe"""
            val result = GraphDiagramLexer.process(input)
            assertEquals("Number of tokens does not match" , 3, result.size)
            val nameToken = result[0]
            val connectionToken = result[1]
            val secondNameToken = result[2]
            assertTrue("Participant token type does not match", nameToken is NameToken)
            assertTrue("Name token does not match", secondNameToken is NameToken)
            assertTrue("Connection token does not match" , connectionToken is Backward)
            nameToken as NameToken
            secondNameToken as NameToken
            assertEquals("Participant type does not match", "Smith", nameToken.name)
            assertEquals("Participant type does not match", "Joe", secondNameToken.name)
        }

        it ("lexes connection between two participants") {
            val input = """Smith -- Joe"""
            val result = GraphDiagramLexer.process(input)
            assertEquals("Number of tokens does not match" , 3, result.size)
            val nameToken = result[0]
            val connectionToken = result[1]
            val secondNameToken = result[2]
            assertTrue("Participant token type does not match", nameToken is NameToken)
            assertTrue("Name token does not match", secondNameToken is NameToken)
            assertTrue("Connection token does not match" , connectionToken is Dashes)
            nameToken as NameToken
            secondNameToken as NameToken
            assertEquals("Participant type does not match", "Smith", nameToken.name)
            assertEquals("Participant type does not match", "Joe", secondNameToken.name)
        }

        it ("lexes dotted connection between two participants") {
            val input = """Smith .. Joe"""
            val result = GraphDiagramLexer.process(input)
            assertEquals("Number of tokens does not match" , 3, result.size)
            val nameToken = result[0]
            val connectionToken = result[1]
            val secondNameToken = result[2]
            assertTrue("Participant token type does not match", nameToken is NameToken)
            assertTrue("Name token does not match", secondNameToken is NameToken)
            assertTrue("Connection token does not match" , connectionToken is Dots)
            nameToken as NameToken
            secondNameToken as NameToken
            assertEquals("Participant type does not match", "Smith", nameToken.name)
            assertEquals("Participant type does not match", "Joe", secondNameToken.name)
        }
    }
})