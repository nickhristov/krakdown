package com.github.krakdown.inline

class InlineParser (val lexer: InlineLexer, val inlineTokenHandlers: List<InlineTokenHandler>) {

    fun parse(input : String) : List<InlineNode> {
        val tokens = lexer.tokenize(input)
        return parse(tokens)
    }

    fun parse(input: List<InlineToken>): List<InlineNode> {
        var idx = 0
        val result = ArrayList<InlineNode>()
        while (idx < input.size) {
            var increment = 0
            for (handler in inlineTokenHandlers) {
                increment = handler.handleToken(this, idx, input, result)
                if (increment > 0) {
                    break
                }
            }
            if (increment == 0) {
                throw IllegalArgumentException("No handlers handled token " + input[idx])
            }
            idx += increment
        }
        return result
    }
    constructor() : this(InlineLexer(), listOf(
        EmphasisTokenHandler(),
        CodeInlineTokenHandler(),
        BackslashInlineTokenHandler(),
        InlineTextTokenHandler()
    ))
}

