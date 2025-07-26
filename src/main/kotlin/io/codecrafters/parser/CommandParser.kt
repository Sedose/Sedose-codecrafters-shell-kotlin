package io.codecrafters.parser

import io.codecrafters.dto.ParsedCommand
import org.jline.reader.impl.DefaultParser
import org.springframework.stereotype.Component

@Component
class CommandParser(
    private val jLineParser: DefaultParser = DefaultParser(),
) {
    fun parse(line: String): ParsedCommand {
        val words = jLineParser.parse(line, line.length).words()
        var stdoutRedirect: String? = null
        var stderrRedirect: String? = null
        val cleaned = mutableListOf<String>()
        var i = 0
        while (i < words.size) {
            when (words[i]) {
                ">", "1>" -> {
                    stdoutRedirect = words.getOrNull(i + 1)
                    i += 2
                }
                "2>" -> {
                    stderrRedirect = words.getOrNull(i + 1)
                    i += 2
                }
                else -> {
                    cleaned += words[i]
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
}
