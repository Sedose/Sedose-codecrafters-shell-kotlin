package io.codecrafters.dto

data class ParsedCommand(
    val commandName: String,
    val arguments: List<String>,
    val stdoutRedirect: String? = null,
    val stderrRedirect: String? = null,
)
