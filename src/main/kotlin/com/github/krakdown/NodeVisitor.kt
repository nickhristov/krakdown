package com.github.krakdown

import com.github.krakdown.block.node.*

interface NodeVisitor {
    fun acceptBlockQuote(blockQuoteNode: BlockQuoteNode)
    fun acceptCodeBlock(codeBlockNode: CodeBlockNode)
    fun acceptHeaderNode(headerNode: HeaderNode)
    fun acceptTextNode(textNode: TextNode)
    fun acceptUnorderedListNode(unorderedListNode: UnorderedListNode)
    fun acceptOrderedListNode(orderedListNode: OrderedListNode)
    fun acceptParagraphNode(paragraphNode: ParagraphNode)
    fun acceptEmptyLines(emptyLinesNode: EmptyLinesNode)
    fun acceptHtml(htmlNode: HtmlNode)
}