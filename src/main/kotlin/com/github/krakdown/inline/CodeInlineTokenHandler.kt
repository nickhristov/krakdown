package com.github.krakdown.inline

import com.github.krakdown.ParsingContext
import com.github.krakdown.block.node.TextNode

class CodeInlineTokenHandler : ForwardSeekingHandler() {
    val literalParser = InlineParser(
            InlineLexer(emptyList()), listOf(AnythingAsInlineHandler())
    )

    override fun parseSubNodes(parser: InlineParser, tokens: List<InlineToken>, context: ParsingContext) : List<InlineNode> {
        return literalParser.parse(trimSpaces(tokens), context)
    }

    private fun trimSpaces(tokens: List<InlineToken>): List<InlineToken> {
        if (tokens.isNotEmpty()) {
            val result = ArrayList<InlineToken>(tokens.size)
            val stripStartIdx = 0
            val stripEndIdx = tokens.size-1
            for (idx in 0..(tokens.size-1)) {
                var token = tokens[idx]
                if (idx == stripStartIdx && token is InlineTextToken && token.characters.startsWith(" ")) {
                    token = InlineTextToken(StringBuilder(token.characters.replaceFirst(Regex("^ *"), "")))
                }
                if (idx == stripEndIdx && token is InlineTextToken && token.characters.endsWith(" ")) {
                    token = InlineTextToken(StringBuilder(token.characters.replaceFirst(Regex(" *$"), "")))
                }
                result.add(token)
            }
            return result
        } else {
            return tokens
        }
    }

    override fun toInlineText(token: InlineToken): InlineToken {
        return InlineTextToken(StringBuilder(token.toString()))
    }

    override fun matchToken(token: InlineToken): Boolean {
        return token is CodeInlineToken
    }

    override fun makeNode(token: InlineToken, embeddedNodes: List<InlineNode>): InlineNode {
        return PreformattedStyleNode(embeddedNodes)
    }
}

class AnythingAsInlineHandler : InlineTokenHandler {
    override fun handleToken(parser: InlineParser, index: Int, tokens: List<InlineToken>, result: MutableList<InlineNode>, context: ParsingContext): Int {
        for(idx in index..(tokens.size-1)) {
            val token = tokens[idx]
            result.add(TextNode(token.toString()))
        }
        return tokens.size - index
    }
}