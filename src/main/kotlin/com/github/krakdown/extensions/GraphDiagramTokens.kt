package com.github.krakdown.extensions

abstract class GraphDiagramToken(val tokenTypeName: String, open val lineNum: Int)

abstract class GraphConnectionToken(tokenTypeName: String, lineNum:Int ) : GraphDiagramToken(tokenTypeName, lineNum)

abstract class GraphCardinalityToken(tokenTypeName: String, lineNum:Int ) : GraphDiagramToken(tokenTypeName, lineNum)

enum class GraphVertexType {
     DATABASE ,
     QUEUE ,
     INTERFACE ,
     PROCESS ,
     PREDEFPROCESS ,
     IO ,
     DECISION ,
     CONNECTOR,
     SWIMLANE ,
     PARTICIPANT ,
     TERMINAL
}

data class VertexTypeToken(val type : GraphVertexType, override val lineNum : Int ) : GraphDiagramToken(type.name, lineNum)
data class NameToken(val name:String, override val lineNum : Int ) : GraphDiagramToken ("name", lineNum)
data class LabelToken(val label:String, override val lineNum : Int ) : GraphDiagramToken ("label", lineNum)
data class As(override val lineNum : Int) : GraphDiagramToken("as", lineNum )
data class Cardinality0(override val lineNum : Int) : GraphCardinalityToken("cardinality0", lineNum)
data class Cardinality1(override val lineNum : Int) : GraphCardinalityToken("cardinality1", lineNum)
data class CardinalityN(override val lineNum : Int) : GraphCardinalityToken("cardinality2", lineNum)
data class Comma(override val lineNum : Int) : GraphCardinalityToken("comma", lineNum)
data class Dots(override val lineNum : Int) : GraphConnectionToken("dots", lineNum)
data class Dashes(override val lineNum : Int) : GraphConnectionToken("dashes", lineNum)
data class Backward(override val lineNum : Int) : GraphConnectionToken("backward(<-)", lineNum)
data class BackwardX(override val lineNum : Int) : GraphConnectionToken("backwardx(x-)", lineNum)
data class ForwardX(override val lineNum : Int) : GraphConnectionToken("forward(->)", lineNum)
data class Forward(override val lineNum : Int) : GraphConnectionToken("forwardx(-x)", lineNum)
data class Newline(override val lineNum : Int) : GraphDiagramToken("newline", lineNum)
data class Colon(override val lineNum : Int) : GraphDiagramToken("colon", lineNum)
data class Space(override val lineNum : Int) : GraphDiagramToken("space", lineNum)