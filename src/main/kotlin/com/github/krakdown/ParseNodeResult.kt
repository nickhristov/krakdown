package com.github.krakdown

data class ParseNodeResult(val nodes : List<Node>, val lines: Int)

val EMPTY_PARSE_NODE_RESULT = ParseNodeResult(emptyList(), 0)