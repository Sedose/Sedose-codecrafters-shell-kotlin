package io.codecrafters

import com.github.sedose.kream.withRedirects
import io.codecrafters.command.BuiltinCommandHandler
import io.codecrafters.dto.ExternalProgramNotFound
import io.codecrafters.dto.ExternalProgramSuccess
import io.codecrafters.external.ExternalProgramExecutor
import io.codecrafters.parser.CommandParser
import io.codecrafters.shared_mutable_state.ShellState
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path

@Component
class ShellRunner(
    private val builtinCommandHandlers: Map<String, BuiltinCommandHandler>,
    private val externalProgramExecutor: ExternalProgramExecutor,
    private val commandParser: CommandParser,
    private val shellState: ShellState,
) : CommandLineRunner {

    override fun run(vararg args: String) {
        while (true) {
            print("$ ")
            System.out.flush()
            val input = readLine() ?: break
            val trimmed = input.trim()
            if (trimmed.isNotBlank()) {
                handleInput(trimmed)
            }
        }
    }

    private fun handleInput(rawLine: String) {
        val (commandName, commandArgs, stdoutRedirect, stderrRedirect) = commandParser.parse(rawLine)
        val stdoutTarget = stdoutRedirect?.toPathWithin(shellState.currentDirectory)
        val stderrTarget = stderrRedirect?.toPathWithin(shellState.currentDirectory)
        if (builtinCommandHandlers.containsKey(commandName)) {
            withRedirects(stdoutTarget, stderrTarget) {
                builtinCommandHandlers.getValue(commandName)
                    .handle(commandArgs)
            }
            return
        }

        when (externalProgramExecutor.execute(commandName, commandArgs, stdoutTarget, stderrTarget)) {
            is ExternalProgramNotFound -> println("$commandName: not found")
            is ExternalProgramSuccess -> { /* no op, it's fine */ }
        }
    }

    private fun String.toPathWithin(base: Path): Path =
        base.resolve(this)
            .normalize()
            .also { Files.createDirectories(it.parent) }
}
