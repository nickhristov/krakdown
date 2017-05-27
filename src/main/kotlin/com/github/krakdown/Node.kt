package com.github.krakdown

abstract class Node {

    abstract fun visit(visitor : NodeVisitor) : Unit
}