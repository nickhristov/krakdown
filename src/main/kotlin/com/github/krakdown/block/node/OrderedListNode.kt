package com.github.krakdown.block.node

import com.github.krakdown.Node
import com.github.krakdown.NodeVisitor

class OrderedListNode(items: List<ListNodeItem>, var start: Int = 0) : ListNode(items) {
    override fun visit(visitor: NodeVisitor) {
        visitor.acceptOrderedListNode(this)
    }
}

abstract class ListNode(val items: List<ListNodeItem>) : Node()