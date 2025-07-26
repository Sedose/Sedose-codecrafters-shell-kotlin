package io.codecrafters.command

import org.springframework.stereotype.Component

@Component
class EchoCommandHandler : CommandHandler {
    override val commandName = "echo"

    override fun handle(arguments: List<String>) {
        println(arguments.joinToString(" "))
    }
}
