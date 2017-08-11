package com.github.krakdown.inline

import com.github.krakdown.Node
import com.github.krakdown.NodeVisitor
import com.github.krakdown.block.node.HtmlNode

abstract class InlineNode : Node()

class BoldStyleNode(val nodes: List<Node>) : InlineNode()
class EmStyleNode(val nodes: List<Node>) : InlineNode()

class PreformattedStyleNode(val nodes: List<Node>) : InlineNode()