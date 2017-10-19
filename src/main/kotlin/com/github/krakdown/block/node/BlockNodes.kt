package com.github.krakdown.block.node

import com.github.krakdown.Node
import com.github.krakdown.inline.InlineNode

data class HtmlNode (val elType: String, val attributes : Map<String, String>) : Node()
data class HeaderNode(val size: Int, val children: List<InlineNode>) : Node()
data class BlockQuoteNode (val nodes: List<Node>) : Node()
data class CodeBlockNode(val lines: List<String>, var language: String) : Node()
data class EmptyLinesNode(val number: Int) : Node()
data class ParagraphNode(var text: String, private val inlineParser: (String) -> List<Node>) : Node() {
    fun getNodes() : List<Node> {
        return inlineParser(text)
    }
}

data class ListItemNode(var nodes: List<Node>, var loose: Boolean = false, var hasTodo:Boolean = false, var todoIsComplete: Boolean = false) : Node()
data class TextNode(var text: String) : InlineNode()
abstract class ListNode(open val items: List<ListItemNode>) : Node()

data class OrderedListNode(override val items: List<ListItemNode>, var start: Int = 0) : ListNode(items)
data class UnorderedListNode(override val items: List<ListItemNode>) : ListNode(items)

data class TableNode(val rowNodes: List<TableRowNode>) : Node()
data class TableRowNode(val cellNodes: List<TableCellNode>) : Node()
data class TableCellNode(val contents: List<InlineNode>) : Node()

object ThematicBreakNode : Node()
