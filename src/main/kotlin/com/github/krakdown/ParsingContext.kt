package com.github.krakdown

data class ParsingContext (val linkReferences: MutableList<LinkReference>)
data class LinkReference(val linkReferenceId: String, val uri: String)