package com.github.krakdown

import com.github.krakdown.block.*

class BlockParser (val rules: Array<BlockRule>) {

    /**
     * A very simple parser that does not work in the traditional sense of using a
     * lexer and then working the set of tokens produced by the lexer.
     *
     * As such, this parser does not produce an AST.
     *
     * Operation: the input is chopped into lines.
     *
     * The lines are then continuously fed through an ordered set of rules.
     * Each rule either matches the (line)+ or not. if matching it indicates
     * the number of lines consumed, and produces block nodes.
     *
     */
    fun parse(content : String) : List<Node> {
        return parse(content.split("\n"))
    }

    fun parse(input: List<String>) : List<Node> {
        var lines = input
        val nodes = ArrayList<Node>()
        while (lines.isNotEmpty()) {
            var matches = false

            for(rule in rules) {
                val ruleParseResult = rule.generate(lines)
                if (ruleParseResult.lines > 0) {
                    matches = true
                    nodes.addAll(ruleParseResult.nodes)
                    lines = lines.subList(ruleParseResult.lines, lines.size)
                    break
                }
            }
            if (! matches) { // prevent infinite looping if no rule consumed the result
                throw IllegalStateException("No rule matched current line: " + lines[0])
            }
        }
        return nodes

    }
}
fun createBlockParser() : BlockParser {
    val listRule = ListRule()
    val blockQuoteRule = BlockQuoteRule()

    val parser = BlockParser(arrayOf(
            HeaderRule(),
            listRule,
            IndentedBlockRule(),
            FencedCodeBlockRule(),
            blockQuoteRule,
            EmptyLinesRule(),
            ParagraphRule()))
    listRule.parser = parser
    blockQuoteRule.parser = parser
    return parser
}
