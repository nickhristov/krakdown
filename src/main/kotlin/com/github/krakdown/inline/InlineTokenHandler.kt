package com.github.krakdown.inline

import com.github.krakdown.ParsingContext

interface InlineTokenHandler {
    /**
     * @param parser the inline parser that all handlers are using
     * @param index the index from which to handling
     * @param result an accumulator where the resulting nodes are to be stored
     * @return the number of tokens handled
     */
    fun handleToken(parser: InlineParser,
                    index: Int,
                    tokens: List<InlineToken>,
                    result: MutableList<InlineNode>,
                    context: ParsingContext): Int
}