package com.github.krakdown.visitors

import com.github.krakdown.NodeVisitor
import com.github.krakdown.block.node.*
import com.github.krakdown.inline.SpanText

class HtmlVisitor : NodeVisitor {

    var content = ""

    override fun acceptBlockQuote(blockQuoteNode: BlockQuoteNode) {
        val nested = HtmlVisitor()
        blockQuoteNode.nodes.forEach { it.visit(nested) }
        content += "<blockquote>${nested.content}</blockquote>"
    }

    override fun acceptSpanText(spanText: SpanText) {
        content += "<span>${spanText.text}</span>"
    }

    override fun acceptCodeBlock(codeBlockNode: CodeBlockNode) {
        val joinedContent = join("", codeBlockNode.lines.map {it + "\n"})
        if (codeBlockNode.language.isNotBlank()) {
            content += "<pre><code class=\"language-${codeBlockNode.language}\">$joinedContent</code></pre>"
        } else {
            content += "<pre><code>$joinedContent</code></pre>"
        }
    }

    override fun acceptHeaderNode(headerNode: HeaderNode) {
        content += "<h${headerNode.size}>${headerNode.header}</h${headerNode.size}>"
    }

    override fun acceptTextNode(textNode: TextNode) {
        content += textNode.text
    }

    override fun acceptUnorderedListNode(unorderedListNode: UnorderedListNode) {
        val listItemContent = join("", unorderedListNode.items.map{ formatListItem(it) })
        content += "<ul>$listItemContent</ul>"
    }

    override fun acceptOrderedListNode(orderedListNode: OrderedListNode) {
        val listItemContent = join("", orderedListNode.items.map { formatListItem(it) })
        if (orderedListNode.start > 0) {
            content += "<ol start=\"${orderedListNode.start}\">$listItemContent</ol>"
        } else {
            content += "<ol>$listItemContent</ol>"
        }
    }

    override fun acceptEmptyLines(emptyLinesNode: EmptyLinesNode) {

    }

    override fun acceptParagraphNode(paragraphNode: ParagraphNode) {
        val inlineContent = join("\n", paragraphNode.lines.map { inlineFormat(it) })
        content += "<p>$inlineContent</p>"
    }

    private fun formatListItem(item : ListNodeItem): String {
        val nested = HtmlVisitor()
        item.nodes.forEach { it.visit(nested) }
        return "<li>${nested.content}</li>"
    }

    private fun inlineFormat(it: String): String {
        // TODO: not done yet
        return it
    }

    fun join(delimiter: String, lines: List<String>) : String {
        var result = ""
        var usedDelimiter = ""
        for (line in lines) {
            result += usedDelimiter
            result += line
            usedDelimiter = delimiter
        }
        return result
    }

}