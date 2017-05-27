package com.github.krakdown.block

import com.github.krakdown.BlockRule
import com.github.krakdown.EMPTY_PARSE_NODE_RESULT
import com.github.krakdown.ParseNodeResult
import com.github.krakdown.block.node.EmptyLinesNode

class EmptyLinesRule: BlockRule {
    override fun generate(input: List<String>): ParseNodeResult {
        val count = input
                .takeWhile { it == "" }
                .count()
        if (count > 0) {
            return ParseNodeResult(listOf(EmptyLinesNode(count)), count)
        } else {
            return EMPTY_PARSE_NODE_RESULT
        }
    }

}