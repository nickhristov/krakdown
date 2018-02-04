package com.github.krakdown.extensions

import com.github.krakdown.extensions.GraphVertexType.*

/**
 * For grammar definition see the corresponding parser.
 *
 * @see com.github.krakdown.extensions.GraphDiagramToken
 * @see com.github.krakdown.extensions.GraphDiagramParser
 */
object GraphDiagramLexer {

    fun process(input: String) : List<GraphDiagramToken> {
        var lineNumber = 1
        var i = 0
        val result = mutableListOf<GraphDiagramToken>()
        val lambdas : List<(Int,String, Int) -> LexicalMatch> = listOf(
                this::participant,
                this::database,
                this::queue,
                this::interfacetoken,
                this::process,
                this::decision,
                this::swimlane,
                this::terminal,
                this::connector,
                this::predefprocess,
                this::io,
                this::astoken,
                this::label,
                this::newLine,
                this::colon,
                this::dashes,
                this::dots,
                this::backward,
                this::backwardx,
                this::forward,
                this::cardinality0,
                this::cardinality1,
                this::cardinalityN,
                this::forwardx,
                this::space,
                this::name)
        while (i < input.length) {

            // FIXME: the map here should be lazy in order for it to be performant
            val lambdaResult = lambdas.map { it(i, input, lineNumber) }.first { it.chars > 0 }
            i += lambdaResult.chars
            if (lambdaResult.token !is Space) {  // throw spaces away right here instead of letting the parser having to deal with them
                result.add(lambdaResult.token)
            }
            if (lambdaResult.token is Newline) {
                lineNumber++
            }
        }
        return result
    }

    private fun name(startIdx: Int, input: String, lineNumber: Int): LexicalMatch {
        return if(input[startIdx] != ' ') {
            var chars = 0
            var contents = ""
            for (idx in startIdx until input.length) {
                if (input[idx] == ' ' || input[idx] == '\n' || input[idx] == '\t') {
                    break
                }
                contents += input[idx]
                chars++
            }
            return LexicalMatch(chars, NameToken(contents, lineNumber))
        } else {
            EMPTY_LEXICAL_RESULT
        }
    }

    private fun label(index: Int, input: String, lineNumber: Int): LexicalMatch {
        return if(input[index] == '"') {
            var chars = 0
            var count = 0
            var contents = ""
            for (idx in index until input.length) {
                chars++
                if (input[idx] == '"') {
                    ++count
                } else if (count == 1) {
                    contents += input[idx]
                }
                if (count > 1) {
                    break
                }
            }
            return LexicalMatch(chars, LabelToken(contents, lineNumber))
        } else {
            EMPTY_LEXICAL_RESULT
        }
    }

    private fun decision(index: Int, input: String, lineNumber: Int): LexicalMatch {
        return match(index, input, "decision", VertexTypeToken(DECISION, lineNumber))
    }

    private fun terminal(index: Int, input: String, lineNumber: Int): LexicalMatch {
        return match(index, input, "terminal", VertexTypeToken(TERMINAL, lineNumber))
    }

    private fun swimlane(index: Int, input: String, lineNumber: Int): LexicalMatch {
        return match(index, input, "swimlane", VertexTypeToken(SWIMLANE, lineNumber))
    }

    private fun connector(index: Int, input: String, lineNumber: Int): LexicalMatch {
        return match(index, input, "connector", VertexTypeToken(CONNECTOR, lineNumber))
    }

    private fun space(index: Int, input: String, lineNumber: Int): LexicalMatch {
        return if(input[index] == ' ') {
        LexicalMatch(1, Space(lineNumber))
    } else {
        EMPTY_LEXICAL_RESULT
    }
    }

    private fun io(index: Int, input: String, lineNumber: Int): LexicalMatch {
        return match(index, input, "io", VertexTypeToken(IO, lineNumber))
    }

    private fun participant(index: Int, input: String, lineNumber: Int): LexicalMatch {
        return match(index, input, "participant", VertexTypeToken(PARTICIPANT, lineNumber))
    }

    private fun predefprocess(index: Int, input: String, lineNumber: Int): LexicalMatch {
        return match(index, input, "predefprocess", VertexTypeToken(PREDEFPROCESS,lineNumber))
    }

    private fun process(index: Int, input: String, lineNumber: Int): LexicalMatch {
        return match(index, input, "process", VertexTypeToken(PROCESS,lineNumber))
    }

    private fun queue(index: Int, input: String, lineNumber: Int): LexicalMatch {
        return match(index, input, "queue", VertexTypeToken(QUEUE,lineNumber))
    }

    private fun interfacetoken(index: Int, input: String, lineNumber: Int): LexicalMatch {
        return match(index, input, "interface", VertexTypeToken(INTERFACE,lineNumber))
    }

    private fun database(index: Int, input: String, lineNumber: Int): LexicalMatch {
        return match(index, input, "database", VertexTypeToken(DATABASE,lineNumber))
    }

    private fun match(index: Int, input: String, match:String, token: GraphDiagramToken): LexicalMatch {
        return if(input.length > (index+match.length+1)) {
            val toMatch = input.substring(index, minOf(input.length, index+match.length))
            if (toMatch == match) {
                LexicalMatch(match.length, token)
            } else {
                EMPTY_LEXICAL_RESULT
            }
        } else {
            EMPTY_LEXICAL_RESULT
        }
    }


    private fun newLine(index: Int, input: String, lineNumber: Int): LexicalMatch {
        return if(input[index] == '\n') {
            LexicalMatch(1, Newline(lineNumber))
        } else {
            EMPTY_LEXICAL_RESULT
        }
    }

    private fun cardinalityN(index: Int, input: String, lineNumber: Int): LexicalMatch {
        return match(index, input, "for N", CardinalityN(lineNumber))
    }


    private fun cardinality1(index: Int, input: String, lineNumber: Int): LexicalMatch {
        return match(index, input, "for 1", Cardinality1(lineNumber))
    }


    private fun cardinality0(index: Int, input: String, lineNumber: Int): LexicalMatch {
        return match(index, input, "for 0", Cardinality0(lineNumber))
    }

    private fun astoken(index: Int, input: String, lineNumber: Int): LexicalMatch {
        return if((input.length > index+2)) {
            val substring = input.substring(index)
            if (substring.startsWith("as") || substring.startsWith("AS")) {
                LexicalMatch(2, As(lineNumber))
            } else {
                EMPTY_LEXICAL_RESULT
            }
        } else {
            EMPTY_LEXICAL_RESULT
        }
    }

    private fun dots(index: Int, input: String, lineNumber: Int): LexicalMatch {
        return if((input.length > index+2) && input[index] == '.' && input[index+1] == '.') {
            LexicalMatch(2, Dots(lineNumber))
        } else {
            EMPTY_LEXICAL_RESULT
        }
    }

    private fun dashes(index: Int, input: String, lineNumber: Int): LexicalMatch {
        return if((input.length > index+2) && input[index] == '-' && input[index+1] == '-') {
            LexicalMatch(2, Dashes(lineNumber))
        } else {
            EMPTY_LEXICAL_RESULT
        }
    }

    private fun colon(index: Int, input: String, lineNumber: Int): LexicalMatch {
        return if(input[index] == ':') {
            LexicalMatch(1, Colon(lineNumber))
        } else {
            EMPTY_LEXICAL_RESULT
        }
    }

    private fun forward(index: Int, input: String, lineNumber: Int): LexicalMatch {
        return if((input.length > index+2) && input[index] == '-' && input[index+1] == '>') {
            LexicalMatch(2, Forward(lineNumber))
        } else {
            EMPTY_LEXICAL_RESULT
        }
    }

    private fun forwardx(index: Int, input: String, lineNumber: Int): LexicalMatch {
        return if((input.length > index+2) && input[index] == '-' && input[index+1] == 'x') {
            LexicalMatch(2, ForwardX(lineNumber))
        } else {
            EMPTY_LEXICAL_RESULT
        }
    }

    private fun backward(index: Int, input: String, lineNumber: Int): LexicalMatch {
        return if((input.length > index+2) && input[index] == '<' && input[index+1] == '-') {
            LexicalMatch(2, Backward(lineNumber))
        } else {
            EMPTY_LEXICAL_RESULT
        }
    }

    private fun backwardx(index: Int, input: String, lineNumber: Int): LexicalMatch {
        return if((input.length > index+2) && input[index] == 'x' && input[index+1] == '-') {
            LexicalMatch(2, BackwardX(lineNumber))
        } else {
            EMPTY_LEXICAL_RESULT
        }
    }

    private data class LexicalMatch(val chars:Int, val token: GraphDiagramToken)

    private object NullToken : GraphDiagramToken("Null", -1)

    private val EMPTY_LEXICAL_RESULT = LexicalMatch(0, NullToken)
}

