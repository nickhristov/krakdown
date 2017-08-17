package com.github.krakdown.block

import com.github.krakdown.BlockRule
import com.github.krakdown.EMPTY_PARSE_NODE_RESULT
import com.github.krakdown.Node
import com.github.krakdown.ParseNodeResult
import com.github.krakdown.block.node.CodeBlockNode

class IndentedBlockRule : BlockRule {
    override fun postProcessOutput(nodes: MutableList<Node>) {

    }

    override fun generate(input: List<String>): ParseNodeResult {
        var minIndent = Int.MAX_VALUE
        var lineCount = 0
        for (line in input) {
            val indent = countSpaces(line)
            if (indent >= 4 && ! line.isBlank()) {
                minIndent = if (minIndent < indent) minIndent else indent
                ++lineCount
            } else if (lineCount > 0 && line.isBlank()){
                ++lineCount
            } else {
                break
            }
        }
        if (lineCount > 0) {
            return ParseNodeResult(listOf(CodeBlockNode(input.subList(0, lineCount).map { if (it.length > minIndent) it.substring(minIndent) else "" }, "")), lineCount)
        } else {
            return EMPTY_PARSE_NODE_RESULT
        }
    }

    fun countSpaces(line :String) : Int {
        var count = 0
        for (c in line) {
            if (c == ' ') {
                ++count
            } else {
                break
            }
        }
        return count
    }
}