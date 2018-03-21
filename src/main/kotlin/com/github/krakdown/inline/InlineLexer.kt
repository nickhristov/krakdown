package com.github.krakdown.inline

import com.github.krakdown.ParsingContext
import com.github.krakdown.util.Stack

interface InlineToken : PegToken

/**
 * A fairly complex hand-built lexer for markdown inline processing.
 *
 * This lexer is context sensitive. It will generate tokens based on the=5
 * current context and prior tokens (it looks behind).
 *
 * FIXME: need to break the complexity down. actually this lexer should just create tokens and let the parser assemble them
 */
class InlineLexer(val tokenizers : List<InlineTokenizer>) {

    fun tokenize(line: String,
                 context: ParsingContext): List<InlineToken> {
        val stack = Stack<InlineToken>()
        val localTokenizers = tokenizers.toTypedArray()
        var charIdx = 0
        while (charIdx < line.length) {
            val char = line[charIdx]
            var delta = 0
            for (tokenizer in localTokenizers) {
                delta = tokenizer.tokenize(char, charIdx, line, stack, context)
                if (delta > 0) {
                    break
                }
            }
            if (delta < 1) {
                throw IllegalStateException("No tokenizer accepted input")
            }
            charIdx += delta
        }
        val result = ArrayList<InlineToken>(stack.count)
        while(stack.isNotEmpty()) {
            result.add(stack.pop())
        }
        return result
    }

    constructor() : this(listOf(
       PredefinedTokenTokenizer(),
       InlineTextTokenizer()
    ))
}

const val defaultCharBufferSize : Int = 512

interface InlineTokenizer {
    fun tokenize(char: Char, index: Int, line: String, stack: Stack<InlineToken>, context: ParsingContext) : Int
}

//class EmphasisTokenizer : InlineTokenizer {
//    override fun tokenize(char: Char, index: Int, line: String, stack: Stack<InlineToken>, context: ParsingContext): Int {
//        if (char == '_' || char == '*' && InlineTokenizerUtils.lastUnmatchedCodeInlineToken(stack) == null) {
//            if (stack.isEmpty()) {
//                stack.push(EmphasisInlineToken(char, 1))
//                return 1
//            }
//            val lastToken = stack.peek()
//            if (lastToken == BackslashToken) {
//                stack.pop() // pop the token, it is "consumed" directly here
//                stack.push(createInlineTextToken(char))
//                return 1
//            }
//            if (stack.peek() !is EmphasisInlineToken) {
//                stack.push(EmphasisInlineToken(char, 1))
//            } else {
//                val existingToken = stack.peek() as EmphasisInlineToken
//                if (existingToken.char == char) {
//                    existingToken.count++
//                } else {
//                    stack.push(EmphasisInlineToken(char, 1))
//                }
//            }
//            return 1
//        } else {
//            return 0
//        }
//    }
//}

//class CodeTokenizer : InlineTokenizer {
//    override fun tokenize(char: Char, index: Int, line: String, stack: Stack<InlineToken>, context: ParsingContext): Int {
//        if (char == '`' || char == '~') {
//            if (stack.isEmpty()) {
//                stack.push(CodeInlineToken(char, 1))
//                return 1
//            }
//            val lastToken = stack.peek()
//            if( lastToken == BackslashToken ) {
//                stack.pop() // consume the backslash token
//                stack.push(createInlineTextToken(char))
//                return 1
//            }
//            if (lastToken is CodeInlineToken && lastToken.char == char) {
//                lastToken.count ++
//                return 1
//            }
//            val lastInlineToken = InlineTokenizerUtils.lastUnmatchedCodeInlineToken(stack)
//            if (lastInlineToken != null) {
//                if (lastInlineToken.count == 1) {
//                    stack.push(CodeInlineToken(char, 1))
//                } else {
//                    val text = lastToken as InlineTextToken
//                    val tailCodeChars = InlineTokenizerUtils.countTailChars(text.characters, char)
//                    if ((tailCodeChars + 1) == lastInlineToken.count) {
//                        // strip last characters and then make completing token
//                        stack.push(CodeInlineToken(char, lastInlineToken.count))
//                        text.characters.setLength(text.characters.length - tailCodeChars)
//                    } else {
//                        text.characters.append(char)
//                    }
//                }
//            } else {
//                stack.push(CodeInlineToken(char, 1))
//            }
//            return 1
//        }
//        return 0
//    }
//}

class InlineTextTokenizer : InlineTokenizer {
    override fun tokenize(char: Char,index: Int, line: String,  stack: Stack<InlineToken>, context: ParsingContext): Int {
        val textToken : InlineTextToken?
        if (stack.isEmpty() || !(stack.peek() is InlineTextToken)) {
            textToken = InlineTextToken(StringBuilder(defaultCharBufferSize))
            stack.push(textToken)
        } else {
            textToken = stack.peek() as InlineTextToken
        }
        textToken.characters.append(char)
        return 1
    }
}

class PredefinedTokenTokenizer : InlineTokenizer {
    private val mapping = mapOf(
            Pair('\\', BackslashToken),
            Pair('`', BacktickToken),
            Pair('*', AsteriskToken),
            Pair('_', UnderscoreToken),
            Pair('[', OpenSquareBracketToken),
            Pair(']', CloseSquareBracketToken),
            Pair('(', OpenParenToken),
            Pair('_', CloseParenToken),
            Pair('\"', DoubleQuoteToken),
            Pair('\'', SingleQuoteToken),
            Pair('<', LessThanToken),
            Pair('>', GreaterThanToken),
            Pair('\n', NewLineToken)

    )
    override fun tokenize(char: Char,index: Int, line: String,  stack: Stack<InlineToken>, context: ParsingContext): Int {
        val token = mapping[char]
        return if (token != null) {
            stack.push(token)
            1
        } else {
            0
        }
    }
}

//class BackslashTokenizer : InlineTokenizer {
//    override fun tokenize(char: Char, index: Int, line: String, stack: Stack<InlineToken>, context: ParsingContext): Int {
//        if (char == '\\') {
//            if (stack.isEmpty()) {
//                stack.push(BackslashToken)
//            } else {
//                if (InlineTokenizerUtils.lastUnmatchedCodeInlineToken(stack) != null) {
//                    // we are inside a code block
//                    val lastToken = stack.peek()
//
//                    if (lastToken is InlineTextToken) {
//                        lastToken.characters.append(char)
//                    } else {
//                        val inlineText = createInlineTextToken(char)
//                        stack.push(inlineText)
//                    }
//                } else { // no open code blocks
//                    val lastToken = stack.peek()
//                    if (lastToken == BackslashToken) {
//                        // there is a double backslash in the input.
//                        // find the last inline text token (if any, and add a backslash)
//                        // otherwise make a inline text token and add the backslash
//                        stack.pop()
//                        if (stack.isNotEmpty()) {
//                            val previousToken = stack.peek()
//                            if (previousToken is InlineTextToken) {
//                                previousToken.characters.append(char)
//                            } else {
//                                stack.push(createInlineTextToken(char))
//                            }
//                        } else {
//                            val replacement = InlineTextToken(StringBuilder(4096))
//                            replacement.characters.append(char)
//                            stack.push(replacement)
//                        }
//                    } else {
//                        stack.push(BackslashToken)
//                    }
//                }
//            }
//            return 1
//        }
//        return 0
//    }
//}

//object InlineTokenizerUtils {
//    fun lastUnmatchedCodeInlineToken(stack: Stack<InlineToken>) : CodeInlineToken? {
//        val codeTokens = stack.filter { it is CodeInlineToken }
//        val size = codeTokens.count
//        if (size > 0 && (size % 2) == 1) {
//             return codeTokens.peek() as CodeInlineToken
//        } else {
//            return null
//        }
//    }
//
//    fun countTailChars(characters: StringBuilder, char: Char): Int {
//        val lastIdx = characters.length
//        for (i in 0..lastIdx) {
//            val j = lastIdx - i - 1
//            if (characters[j] != char) {
//                return i
//            }
//        }
//        return lastIdx
//    }
//}

//class LinkReferenceTokenizer : InlineTokenizer {
//    override fun tokenize(char: Char, index: Int, line: String, stack: Stack<InlineToken>, context: ParsingContext): Int {
//        if (char == '/' && stack.isNotEmpty() && stack.peek() is InlineTextToken) {
//            val lastToken = stack.peek() as InlineTextToken
//            val startBlockIdx = lastToken.characters.lastIndexOf("[")
//            val endBlockIdx = if (startBlockIdx >= 0) lastToken.characters.lastIndexOf("]") else -1
//            val colonAfter = if (endBlockIdx >= 2 && lastToken.characters.length > (endBlockIdx + 2)) lastToken.characters[endBlockIdx+1] == ':' else false
//            val spaceAfterColon = if (colonAfter) lastToken.characters[endBlockIdx+2] == ' ' else false
//            if (startBlockIdx > -1 &&
//                    endBlockIdx > -1  &&
//                    startBlockIdx > -1 &&
//                    startBlockIdx < endBlockIdx &&
//                    colonAfter &&
//                    spaceAfterColon) {
//                // scan forward till we see the next space
//                val nextSpaceIdx = line.indexOf(' ', index)
//
//            }
//        }
//        return 0
//    }
//}

//class LinkTokenizer : InlineTokenizer {
//    override fun tokenize(char: Char, index: Int, line: String, stack: Stack<InlineToken>, context: ParsingContext): Int {
//        if (char == ')' && stack.isNotEmpty() && stack.peek() is InlineTextToken) {
//            val lastToken = stack.peek() as InlineTextToken
//            val startBlockIdx = lastToken.characters.lastIndexOf("[")
//            val endBlockIdx = if (startBlockIdx >= 0) lastToken.characters.lastIndexOf("]") else -1
//            val startParenIdx = if (endBlockIdx >= 0) lastToken.characters.lastIndexOf("(") else -1
//            if (startBlockIdx > -1 &&
//                    endBlockIdx > -1  &&
//                    startBlockIdx > -1 &&
//                    startBlockIdx < endBlockIdx &&
//                    endBlockIdx < startParenIdx) {
//                val label = lastToken.characters.subSequence(startBlockIdx + 1, endBlockIdx)
//                val uri = lastToken.characters.subSequence(startParenIdx+1, lastToken.characters.length)
//                if (labelValidCharacters(label) && uriValidCharacters(uri)) {
//
//                    if (startBlockIdx == 0) {
//                        stack.pop() // the previous inline token is now empty, pop it from the stack as it has no content
//                    } else {
//                        lastToken.characters.setLength(startBlockIdx)
//                    }
//                    stack.push(LabeledLinkToken(uri.toString(), label.toString()))
//                    return 1
//                }
//            }
//        }
//        return 0
//    }
//
//    private fun labelValidCharacters(label: CharSequence): Boolean {
//        return true
//    }
//
//}
//
//class AutoLinkTokenizer : InlineTokenizer {
//    override fun tokenize(char: Char,index: Int, line: String,  stack: Stack<InlineToken>, context: ParsingContext): Int {
//        if (char == '>' && stack.isNotEmpty() && stack.peek() is InlineTextToken) {
//            val lastToken = stack.peek() as InlineTextToken
//            val startBlockIdx = lastToken.characters.lastIndexOf("<")
//            val uri = lastToken.characters.subSequence(startBlockIdx+1, lastToken.characters.length)
//            if (startBlockIdx >= 0 && uriValidCharacters(uri)) {
//                if (startBlockIdx == 0) {
//                    stack.pop() // the previous inline token is now empty, pop it from the stack as it has no content
//                } else {
//                    // FIXME: once StringBuilder is fixed in Kotlin, re-enable setLength
//                    lastToken.characters.setLength(startBlockIdx)
//                }
//                stack.push(LabeledLinkToken(uri.toString(), uri.toString()))
//                return 1
//            }
//        }
//        return 0
//    }
//}

//private fun uriValidCharacters(uri: CharSequence): Boolean {
//    return !uri.contains(Regex.fromLiteral("[^a-zA-Z0-9_&=+?@#.%\\:\\/]"))
//}
//
//data class EmphasisInlineToken(val char: Char, var count: Int) : InlineToken {
//    override fun toString() : String {
//        val result = StringBuilder()
//        repeat(count, { result.append(char) })
//        return result.toString()
//    }
//}
//
//data class CodeInlineToken(val char: Char, var count: Int): InlineToken {
//    override fun toString() : String {
//        val result = StringBuilder()
//        repeat(count, { result.append(char) })
//        return result.toString()
//    }
//}

data class InlineTextToken(var characters: StringBuilder): InlineToken {
    override fun toString() : String {
        return characters.toString()
    }
}

//data class LabeledLinkToken (val url: String, val label: String, val title : String = "") : InlineToken {
//    override fun toString() : String {
//        return "[$label]($url \"$title\")"
//    }
//}

object BackslashToken : InlineToken {
    override fun toString() : String {
        return "\\"
    }
}

object LessThanToken : InlineToken {
    override fun toString() : String {
        return "<"
    }
}

object GreaterThanToken : InlineToken {
    override fun toString() : String {
        return ">"
    }
}

object OpenParenToken : InlineToken {
    override fun toString() : String {
        return "("
    }
}

object CloseParenToken : InlineToken {
    override fun toString() : String {
        return ")"
    }
}

object CloseSquareBracketToken : InlineToken {
    override fun toString() : String {
        return "]"
    }
}

object OpenSquareBracketToken : InlineToken {
    override fun toString() : String {
        return "["
    }
}

object DoubleQuoteToken : InlineToken {
    override fun toString() : String {
        return "\""
    }
}

object SingleQuoteToken : InlineToken {
    override fun toString() : String {
        return "'"
    }
}

object BacktickToken : InlineToken {
    override fun toString() : String {
        return "`"
    }
}

object AsteriskToken : InlineToken {
    override fun toString() : String {
        return "*"
    }
}

object UnderscoreToken : InlineToken {
    override fun toString() : String {
        return "_"
    }
}


object NewLineToken : InlineToken {
    override fun toString() : String {
        return "\n"
    }
}


fun createInlineTextToken(char: Char) : InlineTextToken {
    val token = InlineTextToken(StringBuilder(defaultCharBufferSize))
    token.characters.append(char)
    return token
}



/**
 * make a mutable list have stack like behaviors
 */
fun <E> MutableList<E>.push(element: E) {
    this.add(0, element)
}

fun <E> MutableList<E>.pop() : E {
    return this.removeAt(0)
}

fun <E> MutableList<E>.peek() : E {
    return this[0]
}

// Not thread safe
// Slightly faster than using an array list as a stack
