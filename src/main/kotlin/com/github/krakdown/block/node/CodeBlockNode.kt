package com.github.krakdown.block.node

import com.github.krakdown.Node
import com.github.krakdown.NodeVisitor

class CodeBlockNode(val lines: List<String>, var language: String) : Node() {
    override fun visit(visitor: NodeVisitor) {
        visitor.acceptCodeBlock(this)
    }
}