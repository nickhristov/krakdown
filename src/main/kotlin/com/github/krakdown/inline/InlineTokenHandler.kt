package com.github.krakdown.inline

interface InlineTokenHandler {
    fun handleToken(parser: InlineParser, index: Int, tokens:List<InlineToken>, result:MutableList<InlineNode>): Int
}