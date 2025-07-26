package io.codecrafters.command

import io.codecrafters.shared_mutable_state.ShellState
import org.springframework.stereotype.Component

@Component
class PwdCommandHandler(
    private val shellState: ShellState,
) : CommandHandler {
    override val commandName = "pwd"

    override fun handle(arguments: List<String>) {
        println(shellState.currentDirectory)
    }
}
