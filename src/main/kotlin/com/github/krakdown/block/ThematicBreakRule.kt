package com.github.krakdown.block

import com.github.krakdown.BlockRule
import com.github.krakdown.EMPTY_PARSE_NODE_RESULT
import com.github.krakdown.Node
import com.github.krakdown.ParseNodeResult
import com.github.krakdown.block.node.ThematicBreakNode

class ThematicBreakRule : BlockRule {

    val first = Regex("^ {0,3}[\\- ]{3,}$")
    val second = Regex("^ {0,3}[_ ]{3,}+$")
    val third = Regex("^ {0,3}[* ]{3,}+$")
    override fun generate(input: List<String>): ParseNodeResult {
        val firstLine = input[0]
        if (first.matches(firstLine) || second.matches(firstLine) || third.matches(firstLine)) {
            return ParseNodeResult(listOf(ThematicBreakNode), 1)
        } else {
            return EMPTY_PARSE_NODE_RESULT
        }
    }

    override fun postProcessOutput(nodes: MutableList<Node>) {

    }

}