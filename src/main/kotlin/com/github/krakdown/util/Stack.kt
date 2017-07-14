package com.github.krakdown.util

/**
 * Created by nhh on 7/12/17.
 */
class Stack<V> {
    var top : StackNode<V>? = null
    var count = 0
    fun push(value: V) : Unit {
        if (top == null) {
            top = StackNode(value, null)
        } else {
            top = StackNode(value, top)
        }
        count ++
    }

    fun pop(): V {
        val local = top
        if (local == null) {
            throw StackUnderflowException()
        } else {
            count --
            val v = local.value
            top = local.prev
            return v
        }
    }

    fun peek(): V {
        val local = top
        if (local == null) {
            throw StackUnderflowException()
        } else {
            val v = local.value
            return v
        }
    }

    fun isEmpty() : Boolean {
        return top == null
    }

    fun isNotEmpty() : Boolean {
        return top != null
    }

    fun filter(predicate: (V) -> Boolean): Stack<V> {
        var filteredTop : StackNode<V>? = null
        val count = 0
        var current = top
        var writeNode : StackNode<V>? = null
        while(current != null) {
            if (predicate(current.value)) {
                val newNode = StackNode(current.value, null)
                if (writeNode != null) {
                    writeNode.prev = newNode
                }
                writeNode = newNode
                if (filteredTop == null) {
                    filteredTop = writeNode
                }
            }
            current = current.prev
        }

        val result = Stack<V>()
        result.top = filteredTop
        result.count = count
        return result
    }
}

class StackUnderflowException : Exception()

data class StackNode<V>(val value: V, var prev : StackNode<V>?)