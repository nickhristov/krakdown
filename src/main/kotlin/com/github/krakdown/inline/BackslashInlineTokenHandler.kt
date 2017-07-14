package com.github.krakdown.inline

class BackslashInlineTokenHandler : InlineTokenHandler {
    override fun handleToken(parser: InlineParser, index: Int, tokens: List<InlineToken>, result: MutableList<InlineNode>): Int {
        if (tokens[index] == BackslashToken) {
            return 1
        } else {
            return 0
        }
    }
}