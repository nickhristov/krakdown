package com.github.krakdown.block.node

import com.github.krakdown.Node
import com.github.krakdown.NodeVisitor

class TextNode(var text: String) : Node() {
    override fun visit(visitor: NodeVisitor) {
        visitor.acceptTextNode(this)
    }

}