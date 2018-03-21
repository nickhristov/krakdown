package com.github.krakdown.inline

import com.github.krakdown.ParsingContext
import com.github.krakdown.block.node.TextNode
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@Suppress("unused")
@RunWith(JUnitPlatform::class)
class InlineParserSpec : Spek({

    given("a inline parser") {
        val parser = InlineParser()

        it("parse simple bold node") {
            val text = InlineTextToken(StringBuilder("lorem ipsum"))
            val nodes = parser.parse(listOf(
                    AsteriskToken,
                    AsteriskToken,
                    text,
                    AsteriskToken,
                    AsteriskToken
            ), ParsingContext(mutableListOf()))

            kotlin.test.assertNotNull(nodes)
            kotlin.test.assertTrue(nodes.size == 1)
            kotlin.test.assertTrue(nodes[0] is BoldStyleNode, "Expected node type to be bold style")
            val boldNode = nodes[0] as BoldStyleNode
            kotlin.test.assertNotNull(boldNode.nodes)
            kotlin.test.assertTrue(boldNode.nodes.size == 1)
            kotlin.test.assertEquals(TextNode("lorem ipsum"), boldNode.nodes[0])
        }
//
//
//        it("parse simple preformatted node") {
//            val preformattedToken = CodeInlineToken('`', 1)
//            val text = InlineTextToken(StringBuilder("lorem ipsum"))
//            val nodes = parser.parse(listOf(
//                    preformattedToken,
//                    text,
//                    preformattedToken
//            ))
//
//            kotlin.test.assertNotNull(nodes)
//            kotlin.test.assertTrue(nodes.size == 1)
//            kotlin.test.assertTrue(nodes[0] is PreformattedStyleNode, "Expected node type to be pre-formatted")
//            val boldNode = nodes[0] as PreformattedStyleNode
//            kotlin.test.assertNotNull(boldNode.nodes)
//            kotlin.test.assertTrue(boldNode.nodes.size == 1)
//            kotlin.test.assertEquals(TextNode("lorem ipsum"), boldNode.nodes[0])
//        }
//
//        it("parse successive text tokens") {
//            val text1 = InlineTextToken(StringBuilder("lorem ipsum"))
//            val text2 = InlineTextToken(StringBuilder("dolor sit amet"))
//            val nodes = parser.parse(listOf(
//                    text1,
//                    text2
//            ))
//
//            kotlin.test.assertNotNull(nodes)
//            kotlin.test.assertTrue(nodes.size == 2)
//            kotlin.test.assertEquals(TextNode("lorem ipsum"), nodes[0])
//            kotlin.test.assertEquals(TextNode("dolor sit amet"), nodes[1])
//        }
//
//        it("parse bold followed by plain text") {
//            val boldToken = EmphasisInlineToken('*', 2)
//            val text1 = InlineTextToken(StringBuilder("lorem ipsum"))
//            val text2 = InlineTextToken(StringBuilder("dolor sit amet"))
//            val nodes = parser.parse(listOf(
//                    boldToken,
//                    text1,
//                    boldToken,
//                    text2
//            ))
//
//            kotlin.test.assertNotNull(nodes)
//            kotlin.test.assertTrue(nodes.size == 2)
//            kotlin.test.assertTrue(nodes[0] is BoldStyleNode)
//            val boldNode = nodes[0] as BoldStyleNode
//            kotlin.test.assertNotNull(boldNode.nodes)
//            kotlin.test.assertTrue(boldNode.nodes.size == 1)
//            kotlin.test.assertEquals(TextNode("lorem ipsum"), boldNode.nodes[0])
//            kotlin.test.assertEquals(TextNode("dolor sit amet"), nodes[1])
//        }

    }

})