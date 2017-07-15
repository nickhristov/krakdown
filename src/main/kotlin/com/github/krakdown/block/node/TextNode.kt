package com.github.krakdown.block.node

import com.github.krakdown.NodeVisitor
import com.github.krakdown.inline.InlineNode

class TextNode(var text: String) : InlineNode() {
    override fun visit(visitor: NodeVisitor) {
        visitor.acceptTextNode(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (!( other is TextNode) ) {
            return false
        }

        if (text != other.text) return false

        return true
    }

    override fun hashCode(): Int {
        return text.hashCode()
    }


}