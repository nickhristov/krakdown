package com.github.krakdown.inline

class EmphasisTokenHandler : ForwardSeekingHandler() {
    override fun toInlineText(token: InlineToken): InlineToken {
        val codetoken = token as EmphasisInlineToken
        val str = StringBuilder()
        for (i in 0..codetoken.count) {
            str.append(codetoken.char)
        }
        return InlineTextToken(str)
    }

    override fun makeNode(token: InlineToken, embeddedNodes: List<InlineNode>): InlineNode {
        val emphasisToken = token as EmphasisInlineToken
        if (emphasisToken.count > 1) {
            return BoldStyleNode(embeddedNodes)
        } else {
            return EmStyleNode(embeddedNodes)
        }
    }

    override fun matchToken(token: InlineToken): Boolean {
        return token is EmphasisInlineToken
    }
}