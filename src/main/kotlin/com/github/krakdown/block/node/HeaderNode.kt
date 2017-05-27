package com.github.krakdown.block.node

import com.github.krakdown.Node
import com.github.krakdown.NodeVisitor

class HeaderNode(val size: Int, val header: String) : Node() {
    override fun visit(visitor: NodeVisitor) {
        visitor.acceptHeaderNode(this)
    }

}