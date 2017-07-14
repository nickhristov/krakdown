package com.github.krakdown.inline

import com.github.krakdown.block.node.TextNode


class InlineTextTokenHandler : InlineTokenHandler {
    override fun handleToken(parser: InlineParser, index: Int, tokens: List<InlineToken>, result: MutableList<InlineNode>): Int {
        val token = tokens[index]
        if (token is InlineTextToken) {
            result.add(TextNode(token.characters.toString()))
            return 1
        } else {
            return 0
        }
    }
}