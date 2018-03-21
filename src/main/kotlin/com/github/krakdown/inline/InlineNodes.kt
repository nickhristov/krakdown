package com.github.krakdown.inline

import com.github.krakdown.Node

abstract class InlineNode : Node()

class BoldStyleNode(val nodes: List<Node>) : InlineNode()
class EmphasisNode(val nodes: List<Node>) : InlineNode()

class PreformattedStyleNode(val nodes: List<Node>) : InlineNode()
class AnchorNode(val label: String, val href : String, val title: String = "") : InlineNode()