# Krakdown

Krakdown is a markdown parser written in native Kotlin (no external dependencies). It is meant to be used either 
on the server side (running in a JVM), or to be cross-compiled into Javascript and run in a browser.

## Project status

The project has a small portion of the common-mark specification covered (mostly around lists and blockquotes).

It is not heavily tested yet, and probably contains a large amount of bugs, and potential performance issues.

## Structure

The parser is broken into two parts: a block parser and an inline parser. Both parsers work via chain of responsibility
where rules are evaluated in order on the input.

The inline parser consists of a lexer and a parser. The lexer is fairly heavy, as it evaluates tokens based on current 
context (it performs look-behind).

## Roadmap

### Commonmark compatibility
Next steps here would involve adding the remaining common mark tests to the `src/test/resources/commonmark.testspec` 
specification file and then performing the necessary fixes in the parsers to get this project to 100% commonmark 
compatibility.

### Streaming parsing
Once common mark compatibility is reached, the parser will be updated to support stream parsing, i.e. consume data from
an input stream and produce a stream of parse nodes. This would, ideally, decrease the memory footprint of the parser,
as it would ideally keep at most the necessary memory for processing a single block of text.

### Performance
At present, the performance of the parser is not thoroughly measured. A test suite should be built to measure the performance
of the parser.

### Support for non-standard markdown extensions
Support for tables, todos, footnotes, definition lists, custom block quotes, table of contents, etc.