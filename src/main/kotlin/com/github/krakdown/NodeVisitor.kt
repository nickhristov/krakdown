package com.github.krakdown

interface NodeVisitor<out Context> {
    fun accept(node : Node) : Context
}