package com.github.krakdown

interface BlockRule {

    /**
     * Generate one or more nodes from the input lines.
     */
    fun generate(input: List<String>) : ParseNodeResult

    fun <V> dropTailElements(input: List<V>, predicate: (V) -> Boolean) : List<V> {
        if (input.isEmpty()) {
            return input
        }
        var endIndex = input.size
        for (i in (0 .. (endIndex-1)).reversed()) {
            if (predicate(input[i])) {
                endIndex = i
            } else {
                break
            }
        }
        return input.subList(0, endIndex)
    }

    fun postProcessOutput(nodes: MutableList<Node>)
}