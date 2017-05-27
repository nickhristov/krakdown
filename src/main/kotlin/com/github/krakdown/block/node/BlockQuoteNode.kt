package com.github.krakdown.block.node

import com.github.krakdown.Node
import com.github.krakdown.NodeVisitor

class BlockQuoteNode (val nodes: List<Node>) : Node() {
    override fun visit(visitor: NodeVisitor) {
        visitor.acceptBlockQuote(this)
    }
}