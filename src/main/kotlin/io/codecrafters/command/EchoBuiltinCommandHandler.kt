package io.codecrafters.command

import org.springframework.stereotype.Component

@Component
class EchoBuiltinCommandHandler : BuiltinCommandHandler {
    override val commandName = "echo"

    override fun handle(arguments: List<String>) {
        println(arguments.joinToString(" "))
    }
}
