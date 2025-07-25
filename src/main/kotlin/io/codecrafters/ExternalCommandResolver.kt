package io.codecrafters

import org.springframework.shell.Availability
import org.springframework.shell.command.CommandContext
import org.springframework.shell.command.CommandRegistration
import org.springframework.shell.command.CommandResolver
import org.springframework.stereotype.Component
import java.io.File

@Component
class ExternalCommandResolver : CommandResolver {
    private data class ExternalExecutable(
        val name: String,
        val path: String,
    )

    override fun resolve(): List<CommandRegistration> {
        val builtInCommands = setOf("echo", "exit", "type")

        return System
            .getenv("PATH")
            .orEmpty()
            .split(':')
            .asSequence()
            .map(::File)
            .filter { it.isDirectory && it.canRead() }
            .flatMap { it.listFiles().orEmpty().asSequence() }
            .filter { it.canExecute() }
            .map { file -> ExternalExecutable(name = file.name, path = file.absolutePath) }
            .distinctBy(ExternalExecutable::name)
            .filterNot { it.name in builtInCommands }
            .map(::toCommandRegistration)
            .toList()
    }

    private fun toCommandRegistration(executable: ExternalExecutable): CommandRegistration =
        CommandRegistration
            .builder()
            .command(executable.name)
            .description("external executable ${executable.path}")
            .availability { Availability.available() }
            .withTarget()
            .consumer { ctx -> executeExternal(ctx, executable) }
            .and()
            .build()

    private fun executeExternal(
        ctx: CommandContext,
        executable: ExternalExecutable,
    ) {
        val args = ctx.rawArgs.drop(1)
        ProcessBuilder(listOf(executable.name) + args)
            .inheritIO()
            .start()
            .waitFor()
    }
}
