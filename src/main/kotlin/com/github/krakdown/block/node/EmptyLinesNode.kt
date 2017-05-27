package com.github.krakdown.block.node

import com.github.krakdown.Node
import com.github.krakdown.NodeVisitor

class EmptyLinesNode(val number: Int) : Node() {
    override fun visit(visitor: NodeVisitor) {
        visitor.acceptEmptyLines(this)
    }

}