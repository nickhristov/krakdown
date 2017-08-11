package com.github.krakdown.block

import com.github.krakdown.BlockRule
import com.github.krakdown.EMPTY_PARSE_NODE_RESULT
import com.github.krakdown.ParseNodeResult
import com.github.krakdown.block.node.CodeBlockNode

class FencedCodeBlockRule : BlockRule {
    val preambleMatch = Regex("( *)(```+|~~~+)(.*)")
    override fun generate(input: List<String>): ParseNodeResult {
        val match : MatchResult? = hasMatch(input[0])
        if (match != null) {
            val codeBlockLines = input.subList(1, input.size).takeWhile { !hasMatch(it, match) }.map { strip(it, match) }
            val node = CodeBlockNode(codeBlockLines, match.language)
            return ParseNodeResult(listOf(node), minOf(input.size, codeBlockLines.size + 2))
        } else {
            return EMPTY_PARSE_NODE_RESULT
        }
    }

    private fun strip(line: String, match: MatchResult): String {
        val count = (0 .. minOf(match.indent-1, line.length-1)).count { line[it] == ' ' }
        return line.substring(count)
    }

    private fun hasMatch(line: String, match: MatchResult): Boolean {
        var count = 0
        var state = MatchState.SPACE
        var indent = 0
        for (c in line) {
            if (state == MatchState.SPACE) {
                if (c == ' ') {
                    ++indent
                    if (indent > 3) { // too many spaces, this is not matching the indent of the
                                      // start block
                        return false
                    }
                } else if (c == match.char) {
                    state = MatchState.CHAR
                    ++count
                } else {
                    return false            // a different character
                }
            } else {
                if (c == match.char) {
                    ++count
                } else {
                    break
                }
            }
        }
        return count >= match.count
    }

    private fun hasMatch(line: String): MatchResult? {
        val match = preambleMatch.matchEntire(line)
        if (match != null){
            val indent = match.groupValues[1]
            val token = match.groupValues[2]
            val remainder = match.groupValues[3].trim().split(" ")
            val language = if (remainder.isNotEmpty()) remainder[0] else ""
            if (indent.length <= 3) {
                return MatchResult(token.length, indent.length, token[0], language)
            } else {
                return null
            }
        } else {
            return null
        }
    }

    private enum class MatchState {
        SPACE, CHAR
    }
    private data class MatchResult(val count:Int, val indent: Int, val char: Char, var language: String)

}