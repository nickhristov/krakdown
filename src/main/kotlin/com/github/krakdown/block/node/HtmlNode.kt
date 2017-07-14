package com.github.krakdown.block.node

import com.github.krakdown.Node
import com.github.krakdown.NodeVisitor

class HtmlNode (val html: String) : Node() {
    override fun visit(visitor: NodeVisitor) {
        visitor.acceptHtml(this)
    }
}