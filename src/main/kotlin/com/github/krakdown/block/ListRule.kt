package com.github.krakdown.block

import com.github.krakdown.*
import com.github.krakdown.block.node.*
import com.github.krakdown.inline.InlineParser
import kotlin.Exception

/**
 * List rules have recursive definitions. As such, they may recursively call the block parser to
 * parse nested markdown.
 */
class ListRule(val inlineParser: InlineParser) : BlockRule {

    val unOrderedMatch = Regex("^ *([*\\-+]) .*")
    val unOrderedMatchEmpty = Regex("^ *([*\\-+])$")
    val orderedMatch = Regex("^ *[0-9]+([.)]) .+")
    var parser: BlockParser? = null

    override fun generate(input: List<String>): ParseNodeResult {
        val results = ArrayList<ConsumptionResult>()
        val setting = BulletSetting(Int.MAX_VALUE)
        var linesToConsume = input
        var totalLines = 0
        do {
            val consumptionResult = consume(linesToConsume, setting)
            if (consumptionResult.lines > 0) {
                totalLines += consumptionResult.lines
                results.add(consumptionResult)
                linesToConsume = linesToConsume.subList(consumptionResult.lines, linesToConsume.size)
            }
        } while (consumptionResult.lines > 0)
        if (results.size > 0) {
            return ParseNodeResult(postProcess(groupIntoNodes(results)), totalLines)
        }
        return EMPTY_PARSE_NODE_RESULT
    }

    private fun postProcess(nodes: List<ListNode>) : List<ListNode> {
        return processStartItems(processLooseness(nodes))
    }

    private fun processLooseness(nodes: List<ListNode>): List<ListNode> {
        for (node in nodes) {
            var loose = false
            for (item in node.items) {
                if (item.loose) {
                    loose = true
                    break
                }
            }
            for (item in node.items) {
                val replacement = ArrayList<Node>()
                for (subnode in item.nodes) {
                    if (loose && subnode is TextNode) {
                        replacement.add(ParagraphNode(inlineParser.parse(subnode.text)))
                    } else if (! loose && subnode is ParagraphNode) {
                        replacement.addAll(subnode.nodes)   // unwrap the paragraph node
                    } else {
                        replacement.add(subnode)
                    }
                }
                item.nodes = replacement
            }
        }
        return nodes
    }

    private fun processStartItems(nodes: List<ListNode>): List<ListNode> {
        var previous : Node? = null
        for (node in nodes) {
            if (previous != null) {
                if (node is OrderedListNode && previous is OrderedListNode) {
                    val size = previous.items.size
                    node.start = size + 1   // not zero indexed!!!!
                }
            }
            previous = node
        }
        return nodes
    }

    private fun groupIntoNodes(items: ArrayList<ConsumptionResult>): List<ListNode> {
        val result = ArrayList<ListNode>()
        val accumulator = ArrayList<ConsumptionResult>()
        var previousCharacter = '_'
        for (item in items) {
            if (item.separator != previousCharacter) {
                if (accumulator.size > 0) {
                    result.add(makeNode(previousCharacter, accumulator.map{ it.node }))
                    accumulator.clear()
                }
            }

            accumulator.add(item)
            previousCharacter = item.separator
        }
        if (accumulator.size > 0) {
            result.add(makeNode(previousCharacter, accumulator.map{ it.node }))
        }
        return result
    }

    private fun makeNode(separator: Char, items: List<ListItemNode>): ListNode {
        if (separator == '*' || separator == '+' || separator == '-') {
            return UnorderedListNode(items)
        }
        if (separator == '.' || separator == ')') {
            return OrderedListNode(items)
        }
        throw Exception("Invalid separator character '$separator'")
    }

    private fun consume(input: List<String>, setting: BulletSetting): ConsumptionResult {
        val textAccumulator = ArrayList<String>()
        val bulletAccumulator = ArrayList<String>()
        var itemMatching = true
        var foundItem = false
        var separator = '?'
        for(idx in 0 .. (input.size-1)) {
            val line = input[idx]
            if (isIndented(line, setting)) {
                itemMatching = false
                textAccumulator.add(line.substring(setting.minIndent))
                continue
            }
            if (line.trim().isEmpty()) {
                // empty line
                itemMatching = false
                textAccumulator.add(line)
                continue
            }
            val orderedMatch = orderedMatch.find(line)
            val unorderedMatch = unOrderedMatch.find(line)
            val unorderedEmptyMatch = unOrderedMatchEmpty.find(line)
            var lineIndent = -1
            if (orderedMatch != null) {
                foundItem = true
                lineIndent = recalcOrderedIndentation(line)
            }
            if (unorderedMatch != null) {
                foundItem = true
                lineIndent = recalcUnorderedIndentation(line)
            }
            if (unorderedEmptyMatch != null) {
                lineIndent = recalcUnorderedIndentation(line)
                if (lineIndent > 1) {
                    setting.minIndent = lineIndent
                }
            }
            if (lineIndent > 1) {
                setting.minIndent = lineIndent
            }
            if ((bulletAccumulator.isNotEmpty()) && (unorderedMatch != null || orderedMatch != null || unorderedEmptyMatch != null)) {
                // found next node, break
                break
            }
            if (unorderedMatch != null || orderedMatch != null || unorderedEmptyMatch != null || isIndented(line, setting)) {
                val strippedLine = if ( lineIndent > -1 ) line.substring(lineIndent) else line
                if (itemMatching) {
                    bulletAccumulator.add(strippedLine)
                    if (orderedMatch != null) {
                        separator = orderedMatch.groupValues[1][0]
                    }
                    if (unorderedMatch != null) {
                        separator = unorderedMatch.groupValues[1][0]
                    }
                    if (unorderedEmptyMatch != null) {
                        separator = '*'
                    }
                } else {
                    textAccumulator.add(strippedLine)
                }
                continue
            }

            break   // no match, break
        }
        if (foundItem && bulletAccumulator.isNotEmpty()) {
            val nodes = ArrayList<Node>()
            var loose = false
            if (textAccumulator.isNotEmpty()) {
                val subNodes = parser!!.parse(bulletAccumulator + textAccumulator)
                nodes.addAll(subNodes)
                loose = nodes.filter { it is EmptyLinesNode }.isNotEmpty() || nodes.filter { it is ParagraphNode }.size > 1
            } else {
                nodes.addAll(parser!!.parse(bulletAccumulator))
            }
            return ConsumptionResult(ListItemNode(nodes, loose), textAccumulator.size + bulletAccumulator.size, separator)
        } else {
            return ConsumptionResult(ListItemNode(listOf()), 0, separator)
        }
    }

    private fun concat(join:String, lines:List<String>) : String  {
        var delimiter = ""
        var accumulator = ""
        for (line in lines) {
            accumulator += delimiter
            accumulator += line
            delimiter = join
        }
        return accumulator
    }

    private fun recalcOrderedIndentation(line: String): Int {
        val regex = Regex(" *[1-9][.)] +")
        val replaced = regex.replace(line, "")
        return line.length - replaced.length
    }

    private fun recalcUnorderedIndentation(line: String): Int {
        val regex = Regex(" *[*\\-+] *")
        val replaced = regex.replace(line, "")
        return line.length - replaced.length
    }

    private fun isIndented(line :String, setting: BulletSetting) : Boolean {
        if (line.length < setting.minIndent) {
            return false
        }
        if (setting.minIndent > 0) {
            (0..(setting.minIndent-1))
                    .filter { line[it] != ' ' }
                    .forEach { return false }
        }
        return true
    }

    data class ConsumptionResult(val node : ListItemNode, val lines : Int, val separator: Char)

}

data class BulletSetting(var minIndent: Int)