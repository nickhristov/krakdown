package com.github.krakdown.block

import com.github.krakdown.BlockRule
import com.github.krakdown.Node
import com.github.krakdown.ParseNodeResult
import com.github.krakdown.block.node.ParagraphNode
import com.github.krakdown.inline.InlineParser

class ParagraphRule(private val inlineParser: InlineParser) : BlockRule {
    override fun generate(input: List<String>): ParseNodeResult {
       return ParseNodeResult(listOf(ParagraphNode(input[0].trim(), inlineParser::parse)), 1)
    }

    override fun postProcessOutput(nodes: MutableList<Node>) {
        val nodesize = nodes.size
        if (nodesize > 1) {
            val last = nodes[nodesize-1]
            val previous = nodes[nodesize-2]
            if (previous is ParagraphNode && last is ParagraphNode) {
                // collapse the paragraphs together
                previous.text += " " + last.text
                nodes.removeAt(nodesize-1)
            }
        }
    }
}