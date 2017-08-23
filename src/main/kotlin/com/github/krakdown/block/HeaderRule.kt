package com.github.krakdown.block

import com.github.krakdown.*
import com.github.krakdown.block.node.HeaderNode
import com.github.krakdown.inline.InlineParser

class HeaderRule(val inline: InlineParser) : BlockRule {

    val atxRegex = Regex("^ {0,3}#{1,6} +.+")
    val setext1 = Regex("^ {0,3}=+")
    val setext2 = Regex("^ {0,3}-+")

    override fun postProcessOutput(nodes: MutableList<Node>) {

    }

    override fun generate(input: List<String>): ParseNodeResult {
        val prefixForm : ParseNodeResult = generatePrefixForm(input)
        if (prefixForm.lines > 0) {
            return prefixForm
        } else {
            return generatePostfixForm(input)
        }
    }

    private fun generatePostfixForm(input: List<String>): ParseNodeResult {
        if (input.size > 1) {
            if (input[0].isNotBlank() && (setext1.matches(input[1]) || setext2.matches(input[1]))) {
                return ParseNodeResult(listOf(HeaderNode(headerSize(input), inline.parse(input[0].trim()))), 2)
            }
        }
        return EMPTY_PARSE_NODE_RESULT
    }

    private fun generatePrefixForm(input: List<String>): ParseNodeResult {
        if (atxRegex.matches(input[0])) {
            return ParseNodeResult(listOf(HeaderNode(headerSize(input), inline.parse(strip(input[0])))), 1)
        }
        return EMPTY_PARSE_NODE_RESULT
    }

    private fun strip(input: String): String {
        var count = 0
        var found = false
        // bleh, can be probably simplified
        for (c in input) {
            if (c == ' ' || c == '#') {
                if (c == '#') {
                    found = true
                }
                if (c == ' ' && found) {
                    break
                }
            } else {
                break
            }
            ++count
        }
        return input.substring(count).trim()
    }

    private fun headerSize(input: List<String>): Int {
        if (atxRegex.matches(input[0])) {
            var count = 0
            for (c in input[0]) {
                if (c == '#') {
                    ++count
                }
                if (count > 5) {
                    break
                }
            }
            return count
        } else if(input.size > 1) {
            if (input[1].startsWith("---")) {
                return 2
            } else {
                return 1
            }
        } else {
            throw IllegalStateException("Invalid input")
        }
    }

}