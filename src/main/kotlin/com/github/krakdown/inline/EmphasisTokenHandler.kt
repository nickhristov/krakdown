package com.github.krakdown.inline

class EmphasisTokenHandler : ForwardSeekingHandler() {
    override fun toInlineText(token: InlineToken): InlineToken {
        return InlineTextToken(StringBuilder(token.toString()))
    }

    override fun makeNode(token: InlineToken, embeddedNodes: List<InlineNode>): InlineNode {
        val emphasisToken = token as EmphasisInlineToken
        if (emphasisToken.count > 1) {
            return BoldStyleNode(embeddedNodes)
        } else {
            return EmphasisNode(embeddedNodes)
        }
    }

    override fun matchToken(token: InlineToken): Boolean {
        return token is EmphasisInlineToken
    }
}