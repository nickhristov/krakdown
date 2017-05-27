package com.github.krakdown.block

import com.github.krakdown.BlockRule
import com.github.krakdown.ParseNodeResult
import com.github.krakdown.block.node.ParagraphNode

class ParagraphRule : BlockRule {
    override fun generate(input: List<String>): ParseNodeResult {
       return ParseNodeResult(listOf(ParagraphNode(listOf(input[0].trim()))), 1)
    }
}