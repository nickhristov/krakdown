package com.github.krakdown.inline

import com.github.krakdown.ParsingContext

class BackslashInlineTokenHandler : InlineTokenHandler {
    override fun handleToken(parser: InlineParser, index: Int, tokens: List<InlineToken>, result: MutableList<InlineNode>, context: ParsingContext): Int {
        if (tokens[index] == BackslashToken) {
            return 1
        } else {
            return 0
        }
    }
}