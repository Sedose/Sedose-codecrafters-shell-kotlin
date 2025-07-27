package io.codecrafters.command.type

import io.codecrafters.command.BuiltinCommandHandler
import io.codecrafters.dto.ExecutableLookupResult
import org.apache.logging.log4j.util.Supplier
import org.springframework.stereotype.Component

@Component
class TypeBuiltinCommandHandler(
    private val executableLocator: ExecutableLocator,
    private val builtinCommandHandlers: Supplier<Map<String, BuiltinCommandHandler>>,
) : BuiltinCommandHandler {
    override val commandName = "type"

    override fun handle(arguments: List<String>) {
        val requestedCommand = arguments.firstOrNull()?.trim() ?: ""
        if (requestedCommand.isEmpty()) {
            println("type: missing operand")
            return
        }
        val builtinCommandNames = builtinCommandHandlers.get().keys
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
