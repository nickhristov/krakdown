package com.github.krakdown.extensions

/**
 *
 * # Diagram syntax
 *
 *## Common diagram syntax
 *
 *`markdown.online` will support a common diagram syntax across a variety of diagram styles. Most of the diagrams which are supported express relationships between objects: these objects for a graph of objects, and, therefore can be modeled as a graph of objects.
 *
 *## Structure of a diagram definition
 *
 *A diagram definition will consists of an optional heading which contains a set of definitions, and a list of associations, or rules. The definitions in the heading express the role of every object in the diagram. The rules of the diagram express the associations between the objects in the diagram.
 *
 *## Abstract grammar
 *
 *The formal grammar of a diagram follows:
 *
 *Productions:
 *
 *```
 *diagram: heading? associations
 *heading: definition*
 *definition: role NAME (AS (LABEL|NAME))? NEWLINE
 *role: (PARTICIPANT|DATABASE|QUEUE|INTERFACE|PROCESS|PREDEFPROCESS|IO|DECISION|TERMINAL|CONNECTOR|SWIMLANE|ACTIVITY|STATE|COMPONENT)
 *associations: association+
 *node: (NAME|START|END)
 *association: node connection node (COMMA node)* messageblock? NEWLINE
 *connection: (DASHES|FORWARD|BACKWARD|FORWARDX|BACKWARDX|DOTS) cardinality?
 *cardinality: FOR_ZERO|FOR_ONE|FOR_N
 *messageblock: COLON LABEL
 *```
 *
 *Terminals:
 *
 *```
 *COMMA: ','
 *DASHES: '--'
 *FORWARD: '->'
 *BACKWARD: '<-'
 *FORWARDX: '-x'
 *BACKWARDX: 'x-'
 *DOTS: '..'
 *FOR_ZERO: 'for 0'
 *FOR_ONE: 'for 1'
 *FOR_N: 'for N'
 *COLON : ':'
 *AS : 'as'
 *LABEL : '".+"'
 *NEWLINE : \n
 *PARTICIPANT : 'participant'
 *DATABASE : 'database'
 *QUEUE : 'queue'
 *INTERFACE : 'interface'
 *PROCESS : 'process'
 *PREDEFPROCESS : 'predefprocess'
 *IO : 'io'
 *DECISION : 'decision'
 *TERMINAL : 'terminal'
 *CONNECTOR : 'connector'
 *SWIMLANE : 'swimlane'
 *ACTIVITY : 'activity'
 *START : 'start'
 *END : 'end'
 *```
 *
 *
 */
open class GraphDiagramParser {

    private val emptyResult = GraphMatchResult(0, emptyList())

    fun parse(input:String) : RootGraphNode {
        val tokens = GraphDiagramLexer.process(input)
        return diagram(tokens)
    }

    private fun diagram(tokens: List<GraphDiagramToken>): RootGraphNode {
        return if (tokens.isNotEmpty()) {
            val result = orderedMatch(tokens, this::heading, this::associations)
            val vertices = result.nodes.filter { it is GraphVertex }.map { it as GraphVertex }.toMutableList()

            val verticesMap = vertices.associateBy { it.name }.toMutableMap()

            val internal = result.nodes.filter { it is InternalConnectionNode }.map { it as InternalConnectionNode }

            val connections = internal.map {
                var start = verticesMap[it.start]
                if (start == null) {
                    start = GraphVertex(it.start, it.start, GraphVertexType.PARTICIPANT)
                    vertices.add(start)
                    verticesMap.put(it.start, start)
                }
                var end = verticesMap[it.end]
                if (end == null) {
                    end = GraphVertex(it.end, it.end, GraphVertexType.PARTICIPANT)
                    vertices.add(end)
                    verticesMap.put(it.end, end)
                }
                GraphConnection(start, end, it.type, it.style, it.label)
            }

            RootGraphNode(vertices, connections)
        } else {
            RootGraphNode(emptyList(), emptyList())
        }
    }

    private fun heading(tokens: List<GraphDiagramToken>): GraphMatchResult {
        val result = mutableListOf<GraphNode>()
        var count = 0
        var idx = 0
        while(idx < tokens.size) {
            val token = tokens[idx]
            if (token is Newline) {
                // skip it
            } else if (token is VertexTypeToken) {

                // FIXME: this can be broken down into smaller functions utilizing lambda dispatch and GraphMatchResult

                if (tokens.size > idx + 4) {
                    val nametoken = tokens[idx+1]
                    val astoken = tokens[idx+2]
                    val labeltoken = tokens[idx+3]
                    if (nametoken is NameToken) {
                        if (astoken is As || astoken is Newline) {
                            if (astoken is As) {
                                if (labeltoken is LabelToken) {
                                    idx += 3
                                    result.add(GraphVertex(nametoken.name, labeltoken.label, token.type))
                                } else if (labeltoken is NameToken) {
                                    idx += 3
                                    result.add(GraphVertex(nametoken.name, labeltoken.name, token.type))
                                } else {
                                    throw GraphSyntaxException("Unexpected token ${labeltoken.tokenTypeName} at line ${labeltoken.lineNum}")
                                }
                            } else {
                                idx++   // we consumed an extra name token here
                                result.add(GraphVertex(nametoken.name, nametoken.name, token.type))
                            }
                        } else {
                            throw GraphSyntaxException("Unexpected token ${astoken.tokenTypeName} at line ${astoken.lineNum}")
                        }
                    } else {
                        throw GraphSyntaxException("Unexpected token ${nametoken.tokenTypeName} at line ${nametoken.lineNum}")
                    }
                } else if (tokens.size > idx + 2) {
                    val nextidx = idx + 1
                    val nextToken = tokens[nextidx]
                    if (nextToken is NameToken) {
                        result.add(GraphVertex(nextToken.name, nextToken.name, token.type))
                    } else {
                        throw GraphSyntaxException("Unexpected token ${nextToken.tokenTypeName} at line ${nextToken.lineNum}")
                    }
                    ++idx
                } else {
                    // we just have a vertex at the end of stream. just ignore it
                }
            } else {
                break
            }
            count = idx
            ++idx
        }
        return GraphMatchResult(count, result)
    }

    class GraphSyntaxException(s: String) : Exception(s)

    private fun associations(tokens: List<GraphDiagramToken>): GraphMatchResult =
            matchUntilMismatch(tokens, this::association)

    private fun association(tokens: List<GraphDiagramToken>) :GraphMatchResult {
        if (tokens.isNotEmpty() && tokens[0] is Newline) {
            return GraphMatchResult(1, emptyList())
        }
        if (tokens.size > 2) {
            var totalConsumed = 0
            val leftName = tokens[0] as? NameToken ?: return emptyResult
            val connection = tokens[1] as? GraphConnectionToken ?: return emptyResult
            val cardinality = if (tokens[2] is GraphCardinalityToken) tokens[2] else null
            val sublistidx = if (cardinality == null) 2 else 3

            totalConsumed += sublistidx

            var sublist = tokens.subList(sublistidx, tokens.size)

            // match names
            val nameTokens = mutableListOf<NameToken>()
            while (sublist.isNotEmpty()) {
                val token = sublist[0]
                if (token is Comma || token is NameToken) {
                    sublist = sublist.subList(1, sublist.size)
                    totalConsumed++
                    if (token is NameToken) {
                        nameTokens.add(token)
                    }
                } else {
                    break
                }
            }
            if (nameTokens.isEmpty()) {
                return emptyResult
            }
            var labelToken = LabelToken("", tokens[0].lineNum)
            if (sublist.size > 1 && sublist[0] is Colon && sublist[1] is LabelToken) {
                totalConsumed+=2
                labelToken = sublist[1] as LabelToken
                sublist = sublist.subList(2, sublist.size)
            }
            if (sublist.isNotEmpty() && sublist[0] is Newline) {
                totalConsumed++ // consume the newline as well
            }
            val connectionType = connectionType(connection)
            val connectionStyle = connectionStyle(connection)
            val reverse = isReverse(connection)

            val associations = nameTokens.map {
                val start = if (reverse) it else leftName
                val end = if (reverse) leftName else it
                InternalConnectionNode(start.name, end.name, connectionType, connectionStyle, labelToken.label)
            }

            return GraphMatchResult(totalConsumed, associations)
        } else {
            return emptyResult
        }
    }

    private fun connectionType(connection: GraphConnectionToken): ConnectionType =
            if (connection is Dots || connection is Dashes) ConnectionType.UNDIRECTED else ConnectionType.DIRECTIONAL

    private fun connectionStyle(connection: GraphConnectionToken) : ConnectionStyle {
        if (connection is BackwardX || connection is ForwardX) {
            return ConnectionStyle.FAILURE
        }
        if (connection is Dots) {
            return ConnectionStyle.DOTS
        }
        return ConnectionStyle.NORMAL
    }

    private fun isReverse(connection: GraphConnectionToken) : Boolean =
            connection is BackwardX || connection is Backward

    /**
     * Technically this breaks the grammar definition, but we do it in the name of user fr
     */
    private fun matchUntilMismatch(tokens: List<GraphDiagramToken>, consumer: (List<GraphDiagramToken>) -> GraphMatchResult): GraphMatchResult {
        var idx = 0
        val resultNodeList = mutableListOf<GraphNode>()
        while (idx < tokens.size) {
            val (count, resultList) = consumer(tokens.subList(idx, tokens.size))
            resultNodeList.addAll(resultList)
            idx += count
            if (count < 1) {
                break
            }
        }
        return GraphMatchResult(tokens.size, resultNodeList)
    }

    private fun orderedMatch(tokens: List<GraphDiagramToken>, vararg matchers: (List<GraphDiagramToken>) -> GraphMatchResult) : GraphMatchResult {
        var index = 0
        val nodes = mutableListOf<GraphNode>()
        for (matcher in matchers) {
            if (index >= tokens.size) {
                break
            }
            val sublist = tokens.subList(index, tokens.size)
            val result = matcher(sublist)
            index += result.count
            nodes.addAll(result.nodes)
        }

        return GraphMatchResult(index, nodes)
    }

    data class GraphMatchResult(val count: Int, val nodes: List<GraphNode>)

    private data class InternalConnectionNode(val start: String, val end: String, val type: ConnectionType, val style: ConnectionStyle, val label: String) : GraphNode()
}

abstract class GraphNode
abstract class GraphExternalNode : GraphNode()

enum class ConnectionType {
    DIRECTIONAL, UNDIRECTED
}

enum class ConnectionStyle {
    NORMAL, FAILURE, DOTS
}

data class GraphVertex( val name :String, val label: String, val vertexType: GraphVertexType) : GraphExternalNode()

// FIXME: spelling of vertexes (it's vertices, stupid)
data class RootGraphNode(val vertexes: List<GraphVertex>, val connections: List<GraphConnection>) : GraphExternalNode()

data class GraphConnection(val start: GraphVertex, val end: GraphVertex,
                           val type: ConnectionType, val style: ConnectionStyle,
                           val label: String) : GraphExternalNode()
