package com.github.krakdown.block

import com.github.krakdown.BlockRule
import com.github.krakdown.Node
import com.github.krakdown.ParseNodeResult
import com.github.krakdown.block.node.TableCellNode
import com.github.krakdown.block.node.TableNode
import com.github.krakdown.block.node.TableRowNode
import com.github.krakdown.inline.InlineParser

class TableRule (val inlineParser: InlineParser) : BlockRule  {

    override fun generate(input: List<String>): ParseNodeResult {
        if (input.size > 2) {
            val separatorCells = getCellCount(input[1], true)
            if (separatorCells.isNotEmpty()) {
                val headerCells = getCellCount(input[0], false)
                if (headerCells.size == separatorCells.size) {
                    // alright we have an actual table. grab greedily data from the input
                    val data = mutableListOf(headerCells)
                    var idx = 2
                    while(idx < input.size) {
                        val lineCells = getCellCount(input[idx], false)
                        if (lineCells.size != headerCells.size) {
                            break
                        } else {
                            data.add(lineCells)
                        }
                        ++idx
                    }
                    val rows = data.map { TableRowNode(it.map { TableCellNode(inlineParser.parse(it) ) }) }
                    return ParseNodeResult(listOf(TableNode(rows)), idx )
                }
            }

        }
        return ParseNodeResult(emptyList(), 0)
    }

    fun getCellCount(value: String, dashesMode: Boolean): List<String> {
        var parts = value.split('|')
        if (parts.isNotEmpty()) {
            if (parts[0].isBlank()) {
                parts = parts.subList(1, parts.size)
            }
            if (parts.isNotEmpty() && parts[parts.size-1].isBlank()) {
                parts = parts.subList(0, parts.size-1)
            }

            // now check for dashes only
            if (dashesMode) {
                for (part in parts) {
                    var dashcount = 0
                    for (c in part) {
                        if (c != ' ' && c != '-' && c != ':') {
                            return emptyList()
                        }
                        if ((c == ' ' || c == ':') && dashcount < 3) {
                            dashcount = 0
                        }
                        if (c == '-') {
                            ++dashcount
                        }
                    }
                    if (dashcount < 3) {
                        return emptyList()
                    }
                }
            }
            return parts
        } else {
            return emptyList()
        }
    }


    override fun postProcessOutput(nodes: MutableList<Node>) {

    }

}