package com.github.krakdown.inline

import com.github.krakdown.NodeVisitor

class SpanText (val text: String) : InlineNode() {
    override fun visit(visitor: NodeVisitor) {
        visitor.acceptSpanText(this)
    }
}