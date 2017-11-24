package com.github.krakdown.extensions

abstract class GraphDiagramToken
abstract class GraphConnectionToken : GraphDiagramToken()

abstract class GraphCardinalityToken : GraphDiagramToken()

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

data class VertexTypeToken(val type : GraphVertexType) : GraphDiagramToken()
data class NameToken(val name:String) : GraphDiagramToken ()
data class LabelToken(val label:String) : GraphDiagramToken ()
object AS : GraphDiagramToken()
object CARDINALITY0 : GraphCardinalityToken()
object CARDINALITY1 : GraphCardinalityToken()
object CARDINALITYN : GraphCardinalityToken()
object COMMA : GraphCardinalityToken()
object DOTS : GraphConnectionToken()
object DASHES : GraphConnectionToken()
object BACKWARD : GraphConnectionToken()
object BACKWARDX : GraphConnectionToken()
object FORWARDX : GraphConnectionToken()
object FORWARD : GraphConnectionToken()
object NEWLINE : GraphDiagramToken()
object COLON : GraphDiagramToken()
object SPACE : GraphDiagramToken()