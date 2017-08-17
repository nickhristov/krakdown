package com.github.krakdown.block

import com.github.krakdown.BlockParser
import com.github.krakdown.BlockRule
import com.github.krakdown.Node
import com.github.krakdown.ParseNodeResult
import com.github.krakdown.block.node.BlockQuoteNode

class BlockQuoteRule: BlockRule {
    override fun postProcessOutput(nodes: MutableList<Node>) {

    }

    var parser: BlockParser? = null

    override fun generate(input: List<String>): ParseNodeResult {
        val lines = dropTailElements(input.takeWhile { matches(it) }, { it == ""})
        return ParseNodeResult(listOf(BlockQuoteNode(parser!!.parse(lines.map {strip(it) }))), lines.size)
    }

    fun matches(line :String) : Boolean {
        return line.startsWith(">") || line == ""
    }

    fun strip (line: String) : String {
        return if (line != "") line.substring(1)  else ""
    }
}