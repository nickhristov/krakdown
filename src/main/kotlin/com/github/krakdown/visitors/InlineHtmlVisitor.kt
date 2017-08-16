package com.github.krakdown.visitors

import com.github.krakdown.Node
import com.github.krakdown.NodeVisitor
import com.github.krakdown.block.node.*
import com.github.krakdown.inline.BoldStyleNode
import com.github.krakdown.inline.EmStyleNode
import com.github.krakdown.inline.PreformattedStyleNode

open class InlineHtmlVisitor : NodeVisitor<String> {

    override fun accept(node: Node) : String {
        if (node is HtmlNode) {
            return acceptHtml(node)
        }
        if (node is BlockQuoteNode) {
            return acceptBlockQuote(node)
        }
        if (node is CodeBlockNode) {
            return acceptCodeBlock(node)
        }
        if (node is HeaderNode) {
            return acceptHeaderNode(node)
        }
        if (node is TextNode) {
            return acceptTextNode(node)
        }
        if (node is OrderedListNode) {
            return acceptOrderedListNode(node)
        }
        if (node is UnorderedListNode) {
            return acceptUnorderedListNode(node)
        }
        if (node is ParagraphNode) {
            return acceptParagraphNode(node)
        }
        if (node is EmptyLinesNode) {
            return acceptEmptyLines(node)
        }
        if (node is BoldStyleNode) {
            return acceptBoldStyleNode(node)
        }
        if (node is EmStyleNode) {
            return acceptEmStyleNode(node)
        }
        if (node is PreformattedStyleNode) {
            return acceptPreformattedNode(node)
        }
        if (node is ListItemNode) {
            return acceptListItemNode(node)
        }
        return acceptUnhandledNode(node)
    }

    private fun acceptListItemNode(node: ListItemNode): String {
        val content = node.nodes.map(this::accept).joinToString("")
        return "<li>$content</li>"
    }

    open fun acceptUnhandledNode(node: Node) : String {
        throw Exception("Unsupported node type " + node)
    }

    fun acceptPreformattedNode(node: PreformattedStyleNode): String {
        val content = node.nodes.map(this::accept).joinToString("")
        return "<code>$content</code>"
    }

    fun acceptEmStyleNode(node: EmStyleNode): String {
        val content = node.nodes.map(this::accept).joinToString("")
        return "<em>$content</em>"
    }

    fun acceptBoldStyleNode(node: BoldStyleNode): String {
        val content = node.nodes.map(this::accept).joinToString("")
        return "<strong>$content</strong>"
    }

    fun acceptHtml(htmlNode: HtmlNode) : String {
        val attributes = htmlNode.attributes.map { " ${it.key}=\"${escapeQuotes(it.value)}\"" }
                                            .joinTo(StringBuilder(), " ")
        return "<${htmlNode.elType}>$attributes</${htmlNode.elType}>"
    }

    private fun escapeQuotes(value: String): String {
        val sb =  StringBuilder()
        for (c in value) {
            if (c == '"') {
                sb.append("\\")
            }
            sb.append(c)
        }
        return sb.toString()
    }

    fun acceptBlockQuote(blockQuoteNode: BlockQuoteNode) : String {
        val nestedContent = blockQuoteNode.nodes.map(this::accept).joinToString("")
        return "<blockquote>$nestedContent</blockquote>"
    }

    fun acceptCodeBlock(codeBlockNode: CodeBlockNode) : String {
        val joinedContent = codeBlockNode.lines.map {it + "\n"}.joinToString("")
        if (codeBlockNode.language.isNotBlank()) {
            return "<pre><code class=\"language-${codeBlockNode.language}\">$joinedContent</code></pre>"
        } else {
            return "<pre><code>$joinedContent</code></pre>"
        }
    }

    fun acceptHeaderNode(headerNode: HeaderNode) : String {
        return "<h${headerNode.size}>${headerNode.header}</h${headerNode.size}>"
    }

    fun acceptTextNode(textNode: TextNode) : String {
        return textNode.text
    }

    fun acceptUnorderedListNode(unorderedListNode: UnorderedListNode) : String {
        val listItemContent = unorderedListNode.items.map{ accept(it) }.joinToString("")
        return "<ul>$listItemContent</ul>"

    }

    fun acceptOrderedListNode(orderedListNode: OrderedListNode) : String {
        val listItemContent = orderedListNode.items.map { accept(it) }.joinToString("")
        if (orderedListNode.start > 0) {
            return "<ol start=\"${orderedListNode.start}\">$listItemContent</ol>"
        } else {
            return "<ol>$listItemContent</ol>"
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun acceptEmptyLines(emptyLinesNode: EmptyLinesNode) : String {
        return ""
    }

    fun acceptParagraphNode(paragraphNode: ParagraphNode) :String {
        val inlineContent = paragraphNode.nodes.map(this::accept).joinToString("")
        return  "<p>$inlineContent</p>"
    }

}