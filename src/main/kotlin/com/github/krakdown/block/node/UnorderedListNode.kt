package com.github.krakdown.block.node

import com.github.krakdown.NodeVisitor

class UnorderedListNode(items: List<ListNodeItem>) : ListNode(items) {
    override fun visit(visitor: NodeVisitor) {
        visitor.acceptUnorderedListNode(this)
    }
}