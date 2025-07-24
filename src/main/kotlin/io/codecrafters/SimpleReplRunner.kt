package io.codecrafters

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class SimpleReplRunner : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        print("$ ")
        System.out.flush()

        val line = readlnOrNull()?.trim() ?: return
        if (line.isNotBlank()) {
            val cmd = line.split(Regex("\\s+")).first()
            println("$cmd: command not found")
        }
    }
}
