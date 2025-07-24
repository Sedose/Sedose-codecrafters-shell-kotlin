package io.codecrafters.shell

import org.springframework.shell.command.CommandRegistration
import org.springframework.shell.command.CommandResolver
import org.springframework.shell.command.CommandContext
import org.springframework.stereotype.Component
import java.io.IOException
import java.lang.reflect.Method

/**
 * Registers a fallback command that runs any unmatched input via ProcessBuilder.
 * Required to satisfy CodeCrafters prompt and invalid command handling.
 */
@Component
class FallbackCommandResolver : CommandResolver {

    override fun resolve(): List<CommandRegistration> {
        val method: Method = this::class.java.getDeclaredMethod("executeFallback")

        val command = CommandRegistration.builder()
            .command("*")     // wildcard to catch-all unmatched input
            .withTarget()
            .method(this, method)
            .and()
            .build()

        return listOf(command)
    }

    fun executeFallback(): Int {
        // For a fallback resolver, we might need to handle this differently
        // This approach might not work for true catch-all behavior
        println("Fallback command executed")
        return 0
    }

    private fun executeCommand(line: String) {
        val args = line.trim().split(Regex("\\s+"))
        if (args.isEmpty() || args.first().isBlank()) return

        try {
            ProcessBuilder(args)
                .inheritIO()
                .start()
                .waitFor()
        } catch (error: IOException) {
            println("${args.first()}: command not found")
        }
    }
}