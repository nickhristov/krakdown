package com.github.krakdown.block.node

import com.github.krakdown.Node
import com.github.krakdown.NodeVisitor

class ParagraphNode(val lines: List<String>) : Node() {
    override fun visit(visitor: NodeVisitor) {
        visitor.acceptParagraphNode(this)
    }
}