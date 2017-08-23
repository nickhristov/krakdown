package com.github.krakdown.block

import com.github.krakdown.BlockRule
import com.github.krakdown.EMPTY_PARSE_NODE_RESULT
import com.github.krakdown.Node
import com.github.krakdown.ParseNodeResult
import com.github.krakdown.block.node.ThematicBreakNode

class ThematicBreakRule : BlockRule {

    override fun generate(input: List<String>): ParseNodeResult {
        val firstLine = input[0]
        if (matches(firstLine, '-') || matches(firstLine,'_') || matches(firstLine, '*')) {
            return ParseNodeResult(listOf(ThematicBreakNode), 1)
        } else {
            return EMPTY_PARSE_NODE_RESULT
        }
    }

    private fun matches(firstLine: String, mc: Char): Boolean {
        var state = 0   // 0 = matching init spaces, 1 matching required char
        var spacecount = 0
        var charcount = 0
        // there are possible optimizations here to remove branching, but this is a low priority fix
        for (idx in 0 until firstLine.length) {
            val c = firstLine[idx]
            if (c != mc && c != ' ') {
                return false
            }
            if (c == mc) {
                state = 1
                ++charcount
            }
            if (c == ' ' && state == 0) {
                ++spacecount
            }
            if (spacecount > 3) {
                return false
            }
        }
        return charcount >= 3
    }

    override fun postProcessOutput(nodes: MutableList<Node>) {

    }

}