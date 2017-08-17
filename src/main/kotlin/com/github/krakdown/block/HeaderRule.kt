package com.github.krakdown.block

import com.github.krakdown.*
import com.github.krakdown.block.node.HeaderNode

class HeaderRule : BlockRule {
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
            if (input[1].startsWith("===")) {
                return ParseNodeResult(listOf(HeaderNode(headerSize(input), input[0])), 2)
            }
            if (input[1].startsWith("---")) {
                return ParseNodeResult(listOf(HeaderNode(headerSize(input), input[0])), 2)
            }
        }
        return EMPTY_PARSE_NODE_RESULT
    }

    private fun generatePrefixForm(input: List<String>): ParseNodeResult {
        if (input.isNotEmpty() && input[0].startsWith("#")) {
            return ParseNodeResult(listOf(HeaderNode(headerSize(input), strip(input[0]))), 1)
        }
        return EMPTY_PARSE_NODE_RESULT
    }

    private fun strip(input: String): String {
        var count = 0
        for (c in input) {
            if (c == '#') {
                ++count
            }
        }
        return input.substring(count).trim()
    }

    private fun headerSize(input: List<String>): Int {
        if (input[0].startsWith("#")) {
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
        } else {
            if (input[1].startsWith("---")) {
                return 2
            } else {
                return 1
            }
        }
    }

}