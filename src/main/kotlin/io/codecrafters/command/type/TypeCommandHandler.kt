package io.codecrafters.command.type

import io.codecrafters.command.CommandHandler
import io.codecrafters.dto.ExecutableLookupResult
import org.springframework.beans.factory.ObjectProvider
import org.springframework.stereotype.Component

@Component
class TypeCommandHandler(
    private val executableLocator: ExecutableLocator,
    private val commandHandlerMap: ObjectProvider<Map<String, CommandHandler>>,
) : CommandHandler {
    override val commandName = "type"

    override fun handle(arguments: List<String>) {
        val requestedCommand = arguments.firstOrNull()?.trim() ?: ""
        if (requestedCommand.isEmpty()) {
            println("type: missing operand")
            return
        }

        val builtinCommandNames = commandHandlerMap.getObject().keys
        if (requestedCommand in builtinCommandNames) {
            println("$requestedCommand is a shell builtin")
            return
        }

        when (val lookupResult = executableLocator.findExecutable(requestedCommand)) {
            is ExecutableLookupResult.ExecutableFound -> println("$requestedCommand is ${lookupResult.absolutePath}")
            else -> println("$requestedCommand: not found")
        }
    }
}
