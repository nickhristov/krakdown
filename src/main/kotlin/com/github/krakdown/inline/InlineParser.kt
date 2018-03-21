package com.github.krakdown.inline

import com.github.krakdown.ParsingContext
import com.github.krakdown.block.node.TextNode

/**
 *
 * GRAMMAR:
 *
 *  start : entries
 *  entries: entry *
 *  entry: preformatted | styledentry | plaintext
 *  preformatted: BACKTICK nonbacktick !BACKSLASH BACKTICK
 *  styledentry : boldentry | italicizedentry
 *  boldentry: ASTERISK ASTERISK entry ASTERISK ASTERISK
 *  italicizedentry: ASTERISK entry ASTERISK
 *  nonbacktick: ASTERISK | UNDERSCORE | plaintext
 */
class InlineParser (val lexer: InlineLexer,
                    inputGrammar: PegProduction<InlineToken, InlineNode>?) : PegParserBase<InlineToken, InlineNode>() {

    val grammar: PegProduction<InlineToken, InlineNode>

    constructor() : this(InlineLexer())
    constructor(lexer: InlineLexer) : this(lexer, null)

    init {
        grammar = inputGrammar ?: defaultGrammar()
    }

    private fun defaultGrammar(): PegProduction<InlineToken, InlineNode> {
        val entriesForwardReference = ForwardReference()

        val emphasis = sequence(
                matchToken(AsteriskToken),
                entriesForwardReference,
                matchToken(AsteriskToken),
                transformation = this::makeEmphasisNode)

        val bold = sequence(matchToken(AsteriskToken),
                                 matchToken(AsteriskToken),
                                 entriesForwardReference,
                                 matchToken(AsteriskToken),
                                 matchToken(AsteriskToken),
                                 transformation = this::makeBoldNode)

        val styledentry = choice(bold, emphasis)

        val preformatted = sequence(
                matchToken(BacktickToken),
                matchInlineTextToken(),
                not(matchToken(BackslashToken)),
                matchToken(BacktickToken),
                transformation = this::makePreformattedNode
                )

        val entry = choice(preformatted, styledentry, matchInlineTextToken())
        val entries = this.oneOrMore(entry)
        entriesForwardReference.reference = entries
        return entries

    }

    private fun makePreformattedNode(nodes : List<InlineNode>): List<InlineNode> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun makeBoldNode(input: List<InlineNode>): List<InlineNode> {
        return listOf(BoldStyleNode(input))
    }


    private fun makeEmphasisNode(input: List<InlineNode>): List<InlineNode> {
        return listOf(EmphasisNode(input))
    }

    private fun matchInlineTextToken(): PegProduction<InlineToken, InlineNode> {
        return {
            list, index ->
                if (list[index] !is InlineTextToken) {
                    emptyFailure()
                }
                else {
                    PegMatch(true, 1, listOf(TextNode((list[index] as InlineTextToken).toString())))
                }
        }
    }

    fun parse(input : String, context: ParsingContext) : List<InlineNode> {
        val tokens = lexer.tokenize(input, context).asReversed()
        return parse(tokens, context)
    }

    fun parse(input: List<InlineToken>,
              context: ParsingContext): List<InlineNode> {
        val result = grammar(input, 0)
        if (!result.success) {
            throw IllegalArgumentException("Failed to parse input")
        }
        if (result.matchedTokens != input.size) {
            throw IllegalArgumentException("Dangling tokens left at the end of input")
        }
        return result.nodes
    }

    private fun matchToken(token: InlineToken) : PegProduction<InlineToken, InlineNode> {
        return {
            list, index ->
            if (list[index] == token) {
                PegMatch(true, 1, emptyList())
            } else {
                PegMatch(false, 0, emptyList())
            }
        }
    }

    private class ForwardReference : PegProduction<InlineToken, InlineNode> {
        var reference : PegProduction<InlineToken, InlineNode>? = null
        override fun invoke(p1: List<InlineToken>, p2: Int): PegMatch<InlineNode> {
            if (reference == null) {
                throw IllegalStateException("Invocation before reference is set")
            }
            return reference!!.invoke(p1, p2)
        }
    }
}