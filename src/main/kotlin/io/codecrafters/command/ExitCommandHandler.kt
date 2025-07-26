package io.codecrafters.command

import io.codecrafters.ExitExecutor
import org.springframework.stereotype.Component

@Component
class ExitCommandHandler(
    private val exitExecutor: ExitExecutor,
) : CommandHandler {
    override val commandName = "exit"

    override fun handle(arguments: List<String>) {
        exitExecutor.exit(arguments.firstOrNull()?.toIntOrNull() ?: 0)
    }
}
