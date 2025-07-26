package io.codecrafters.command

interface BuiltinCommandHandler {
    val commandName: String

    fun handle(arguments: List<String>)
}
