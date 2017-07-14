package com.github.krakdown.inline

class CodeInlineTokenHandler : ForwardSeekingHandler() {
    override fun toInlineText(token: InlineToken): InlineToken {
        val codetoken = token as CodeInlineToken
        val str = StringBuilder()
        for (i in 0..codetoken.count) {
            str.append(codetoken.char)
        }
        return InlineTextToken(str)
    }

    override fun matchToken(token: InlineToken): Boolean {
        return token is CodeInlineToken
    }

    override fun makeNode(token: InlineToken, embeddedNodes: List<InlineNode>): InlineNode {
        return PreformattedStyleNode(embeddedNodes)
    }
}