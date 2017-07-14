package com.github.krakdown.inline

import com.github.krakdown.util.Stack

interface InlineToken

/**
 * A fairly complex hand-built lexer for markdown inline processing.
 *
 * This lexer is context sensitive. It will generate tokens based on the
 * current context and prior tokens (it looks behind).
 */
class InlineLexer(val tokenizers : List<InlineTokenizer>) {

    fun tokenize(line: String): List<InlineToken> {
        val stack = Stack<InlineToken>()
        val localTokenizers = tokenizers.toTypedArray()
        for (char in line) {
            for (tokenizer in localTokenizers) {
                tokenizer.tokenize(char, stack) && break
            }
        }
        val result = ArrayList<InlineToken>(stack.count)
        while(stack.isNotEmpty()) {
            result.add(stack.pop())
        }
        return result
    }

    constructor() : this(listOf(
            BackslashTokenizer(),
            CodeTokenizer(),
            EmphasisTokenizer(),
            LinkTokenizer(),
            AutoLinkTokenizer(),
            InlineTextTokenizer()
    ))
}


interface InlineTokenizer {
    fun tokenize(char: Char, stack: Stack<InlineToken>) : Boolean
}

class EmphasisTokenizer : InlineTokenizer {
    override fun tokenize(char: Char, stack: Stack<InlineToken>): Boolean {
        if (char == '_' || char == '*' && InlineTokenizerUtils.lastUnmatchedCodeInlineToken(stack) == null) {
            if (stack.isEmpty()) {
                stack.push(EmphasisInlineToken(char, 1))
                return true
            }
            val lastToken = stack.peek()
            if (lastToken == BackslashToken) {
                stack.pop() // pop the token, it is "consumed" directly here
                stack.push(createInlineTextToken(char))
                return true
            }
            if (! (stack.peek() is EmphasisInlineToken)) {
                stack.push(EmphasisInlineToken(char, 1))
            } else {
                val existingToken = stack.peek() as EmphasisInlineToken
                if (existingToken.char == char) {
                    existingToken.count++
                } else {
                    stack.push(EmphasisInlineToken(char, 1))
                }
            }
            return true
        } else {
            return false
        }
    }
}

class CodeTokenizer : InlineTokenizer {
    override fun tokenize(char: Char, stack: Stack<InlineToken>): Boolean {
        if (char == '`' || char == '~') {
            if (stack.isEmpty()) {
                stack.push(CodeInlineToken(char, 1))
                return true
            }
            val lastToken = stack.peek()
            if( lastToken == BackslashToken ) {
                stack.pop() // consume the backslash token
                stack.push(createInlineTextToken(char))
                return true
            }
            if (lastToken is CodeInlineToken && lastToken.char == char) {
                lastToken.count ++
                return true
            }
            val lastInlineToken = InlineTokenizerUtils.lastUnmatchedCodeInlineToken(stack)
            if (lastInlineToken != null) {
                if (lastInlineToken.count == 1) {
                    stack.push(CodeInlineToken(char, 1))
                } else {
                    val text = lastToken as InlineTextToken
                    val tailCodeChars = InlineTokenizerUtils.countTailChars(text.characters, char)
                    if ((tailCodeChars + 1) == lastInlineToken.count) {
                        // strip last characters and then make completing token
                        stack.push(CodeInlineToken(char, lastInlineToken.count))

                        text.characters.setLength(text.characters.length - tailCodeChars)
                    } else {
                        text.characters.append(char)
                    }
                }
            } else {
                stack.push(CodeInlineToken(char, 1))
            }
            return true
        }
        return false
    }
}

class InlineTextTokenizer : InlineTokenizer {
    override fun tokenize(char: Char, stack: Stack<InlineToken>): Boolean {
        val textToken : InlineTextToken?
        if (stack.isEmpty() || !(stack.peek() is InlineTextToken)) {
            textToken = InlineTextToken(StringBuilder(4096))
            stack.push(textToken)
        } else {
            textToken = stack.peek() as InlineTextToken
        }
        textToken.characters.append(char)
        return true
    }
}

class BackslashTokenizer : InlineTokenizer {
    override fun tokenize(char: Char, stack: Stack<InlineToken>): Boolean {
        if (char == '\\') {
            if (stack.isEmpty()) {
                stack.push(BackslashToken)
            } else {
                if (InlineTokenizerUtils.lastUnmatchedCodeInlineToken(stack) != null) {
                    // we are inside a code block
                    val lastToken = stack.peek()

                    if (lastToken is InlineTextToken) {
                        lastToken.characters.append(char)
                    } else {
                        val inlineText = createInlineTextToken(char)
                        stack.push(inlineText)
                    }
                } else { // no open code blocks
                    val lastToken = stack.peek()
                    if (lastToken == BackslashToken) {
                        // there is a double backslash in the input.
                        // find the last inline text token (if any, and add a backslash)
                        // otherwise make a inline text token and add the backslash
                        stack.pop()
                        if (stack.isNotEmpty()) {
                            val previousToken = stack.peek()
                            if (previousToken is InlineTextToken) {
                                previousToken.characters.append(char)
                            } else {
                                stack.push(createInlineTextToken(char))
                            }
                        } else {
                            val replacement = InlineTextToken(StringBuilder(4096))
                            replacement.characters.append(char)
                            stack.push(replacement)
                        }
                    } else {
                        stack.push(BackslashToken)
                    }
                }
            }
            return true
        }
        return false
    }

}
object InlineTokenizerUtils {
    fun lastUnmatchedCodeInlineToken(stack: Stack<InlineToken>) : CodeInlineToken? {
        val codeTokens = stack.filter { it is CodeInlineToken }
        val size = codeTokens.count
        if (size > 0 && (size % 2) == 1) {
             return codeTokens.peek() as CodeInlineToken
        } else {
            return null
        }
    }

    fun countTailChars(characters: StringBuilder, char: Char): Int {
        val lastIdx = characters.length
        for (i in 0..lastIdx) {
            val j = lastIdx - i - 1
            if (characters[j] != char) {
                return i
            }
        }
        return lastIdx
    }

    fun emphasisToInlineText(token: InlineToken): InlineTextToken {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class LinkTokenizer : InlineTokenizer {
    override fun tokenize(char: Char, stack: Stack<InlineToken>): Boolean {
        if (char == ')' && stack.isNotEmpty() && stack.peek() is InlineTextToken) {
            val lastToken = stack.peek() as InlineTextToken
            val startBlockIdx = lastToken.characters.lastIndexOf("[")
            val endBlockIdx = if (startBlockIdx >= 0) lastToken.characters.lastIndexOf("]") else -1
            val startParenIdx = if (endBlockIdx >= 0) lastToken.characters.lastIndexOf("(") else -1
            if (startBlockIdx > -1 &&
                    endBlockIdx > -1  &&
                    startBlockIdx > -1 &&
                    startBlockIdx < endBlockIdx &&
                    endBlockIdx < startParenIdx) {
                val label = lastToken.characters.subSequence(startBlockIdx, endBlockIdx)
                val uri = lastToken.characters.subSequence(startParenIdx, lastToken.characters.length)
                if (labelValidCharacters(label) && uriValidCharacters(uri)) {
                    lastToken.characters.setLength(startBlockIdx)
                    stack.push(LabeledLinkToken(uri.toString(), label.toString()))
                    return true
                }
            }
        }
        return false
    }

    private fun labelValidCharacters(label: CharSequence): Boolean {
        return true
    }

    private fun uriValidCharacters(uri: CharSequence): Boolean {
        return !uri.contains(Regex.fromLiteral("[^a-z\\:\\/]"))    // FIXME: replace the regex
    }
}

class AutoLinkTokenizer : InlineTokenizer {
    override fun tokenize(char: Char, stack: Stack<InlineToken>): Boolean {
        return false
    }
}

data class EmphasisInlineToken(val char: Char, var count: Int) : InlineToken
data class CodeInlineToken(val char: Char, var count: Int): InlineToken
data class InlineTextToken(val characters: StringBuilder): InlineToken
data class LabeledLinkToken (val url: String, val label: String) : InlineToken

object BackslashToken : InlineToken

fun createInlineTextToken(char: Char) : InlineTextToken {
    val token = InlineTextToken(StringBuilder(4096))
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
