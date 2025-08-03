package io.codecrafters.parser

import io.codecrafters.dto.ParsedCommand
import org.springframework.stereotype.Component

@Component
class CommandParser {

    fun parse(line: String): ParsedCommand {
        val tokens = splitRespectingSingleQuotes(line)
        var stdoutRedirect: String? = null
        var stderrRedirect: String? = null
        val cleaned = mutableListOf<String>()

        var i = 0
        while (i < tokens.size) {
            when (tokens[i]) {
                ">", "1>" -> {
                    stdoutRedirect = tokens.getOrNull(i + 1)
                    i += 2
                }
                "2>" -> {
                    stderrRedirect = tokens.getOrNull(i + 1)
                    i += 2
                }
                else -> {
                    cleaned += tokens[i]
                    i++
                }
            }
        }

        return ParsedCommand(
            commandName = cleaned.firstOrNull().orEmpty(),
            arguments = cleaned.drop(1),
            stdoutRedirect = stdoutRedirect,
            stderrRedirect = stderrRedirect,
        )
    }

    private fun splitRespectingSingleQuotes(source: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var insideSingle = false

        for (ch in source) {
            when {
                ch == '\'' -> insideSingle = !insideSingle
                ch.isWhitespace() && !insideSingle -> {
                    if (current.isNotEmpty()) {
                        result += current.toString()
                        current.clear()
                    }
                }
                else -> current.append(ch)
            }
        }
        if (current.isNotEmpty()) result += current.toString()
        return result
    }
}
