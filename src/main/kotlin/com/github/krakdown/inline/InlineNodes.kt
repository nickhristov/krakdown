package com.github.krakdown.inline

import com.github.krakdown.Node
import com.github.krakdown.NodeVisitor
import com.github.krakdown.block.node.HtmlNode

abstract class InlineNode : Node()

class BoldStyleNode(val nodes: List<Node>) : InlineNode() {
    override fun visit(visitor: NodeVisitor) {
        visitor.acceptHtml(HtmlNode("<b>"))
        nodes.forEach( { it.visit(visitor) })
        visitor.acceptHtml(HtmlNode("</b>"))
    }
}

class EmStyleNode(val nodes: List<Node>) : InlineNode() {
    override fun visit(visitor: NodeVisitor) {
        visitor.acceptHtml(HtmlNode("<em>"))
        nodes.forEach({ it.visit(visitor )})
        visitor.acceptHtml(HtmlNode("</em>"))
    }
}

class PreformattedStyleNode(val nodes: List<Node>) : InlineNode() {
    override fun visit(visitor: NodeVisitor) {
        visitor.acceptHtml(HtmlNode("<code>"))
        nodes.forEach({ it.visit(visitor )})
        visitor.acceptHtml(HtmlNode("</code>"))
    }
}