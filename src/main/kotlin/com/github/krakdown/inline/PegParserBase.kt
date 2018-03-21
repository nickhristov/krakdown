package com.github.krakdown.inline

open class PegParserBase<T : PegToken, N> {

    /**
     * Create a predicate such that the supplied predicates are applied in sequence to the input.
     *
     * This is a more generalized operator than the standard sequence PEG operator and is made so
     * for convenience purposes.
     */
    fun sequence(vararg productions: PegProduction<T, N>,
                 transformation: (List<N>) -> List<N> = {list->list}) : PegProduction<T, N> {
        return  { list, index ->
            var mismatch = false
            var current = index
            val nodes = mutableListOf<N>()
            for(production in productions) {
                val match = applyProduction(production, list, current)
                if (match.success) {
                    current += match.matchedTokens
                    nodes.addAll(match.nodes)
                } else {
                    mismatch = true
                    break
                }
            }
            if (mismatch) {
                emptyFailure()
            } else {
                PegMatch(
                        true,
                        current - index,
                        transformation(nodes.toList())
                )
            }
        }
    }

    /**
     * The choice operator succeeds if either the first operator succeeds, or the second
     * operator succeeds, in this order. If the first operator succeeds, the second operator
     * is not executed.
     *
     * This is a more generalized version of the standard PEG choice operator and is built
     * so for convenience.
     */
    fun choice(vararg productions: PegProduction<T, N>,
               transformation: (List<N>) -> List<N> = {list->list}) : PegProduction<T, N>  {
        return { list, index ->
            var result = emptyFailure()
            for(production in productions) {
                val match = production(list, index)
                if (match.success) {
                    result = match
                    break
                }
            }
            PegMatch(result.success, result.matchedTokens, transformation(result.nodes))
        }
    }

    /**
     * The zero or more operator attempts to apply the specified production zero or more times
     * to the input. It always returns a successful match.
     */
    fun zeroOrMore(production : PegProduction<T, N>,
                   transformation: (List<N>) -> List<N> = {list->list}) : PegProduction<T, N>  {
        return {
            list, index ->
            val mutableList = mutableListOf<N>()
            var current = index
            do {
                val match = applyProduction(production, list, current)
                current += match.matchedTokens
                mutableList.addAll(match.nodes)
            } while (match.success)
            PegMatch(true, current - index, transformation(mutableList.toList()))
        }
    }

    fun oneOrMore(production : PegProduction<T, N>,
                  transformation: (List<N>) -> List<N> = {list->list}) : PegProduction<T, N>  {
        return {
            list, index ->
            val resultList = mutableListOf<N>()
            var current = index
            var match = applyProduction(production, list, current)
            if (match.success) {
                current += match.matchedTokens
                resultList.addAll(match.nodes)
                do {
                    match = applyProduction(production, list, current)
                    current += match.matchedTokens
                    resultList.addAll(match.nodes)
                }
                while(match.success)

                PegMatch(true, current - index, transformation(resultList.toList()))
            } else {
                emptyFailure()
            }
        }
    }

    private fun applyProduction(production: PegProduction<T, N>, list: List<T>, current: Int): PegMatch<N> {
        return if (current < list.size) {
            production(list, current)
        } else {
            emptyFailure()
        }
    }

    /**
     * Apply the specified production once. Return a successful match regardless, optionally consuming the
     * specified tokens, if the input production consumes them.
     */
    fun optional(production : PegProduction<T, N>,
                 transformation: (List<N>) -> List<N> = {list->list}) : PegProduction<T, N>  {
        return {
            list, index ->
            val match = applyProduction(production, list, index)
            PegMatch(true, match.matchedTokens, transformation(match.nodes))
        }
    }

    /**
     * Create a predicate such that if the supplied predicate succeeds, this predicate returns an success without consuming tokens.
     */
    fun and(production: PegProduction<T, N>) : PegProduction<T, N> {
        return {list, index -> if (production(list, index).success) { emptySuccess() } else { emptyFailure() } }
    }

    /**
     * Create a predicate such that if the supplied predicate succeeds, this predicate fails without consuming tokens. If the
     * supplied predicate fails this one succeeds also without consuming tokens.
     */
    fun not(production: PegProduction<T, N>) : PegProduction<T, N> {
        return {list, index -> if (production(list, index).success) { emptyFailure() } else { emptySuccess() } }
    }

    protected fun emptyFailure() : PegMatch<N> {
        return PegMatch(false, 0, emptyList())
    }

    protected fun emptySuccess() : PegMatch<N> {
        return PegMatch(true, 0, emptyList())
    }
}

typealias PegProduction<T, N> = (List<T>, Int) -> PegMatch<N>

interface PegToken

data class PegMatch<out N>(val success: Boolean, val matchedTokens: Int, val nodes: List<N>)