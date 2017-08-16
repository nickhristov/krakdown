package com.github.krakdown.block

import com.github.krakdown.BlockRule
import com.github.krakdown.ParseNodeResult
import com.github.krakdown.block.node.ParagraphNode
import com.github.krakdown.inline.InlineParser

class ParagraphRule(val inlineParser: InlineParser) : BlockRule {
    override fun generate(input: List<String>): ParseNodeResult {
       return ParseNodeResult(listOf(ParagraphNode(inlineParser.parse(input[0].trim()))), 1)
    }
}