package io.codecrafters

import io.codecrafters.command.CommandHandler
import io.codecrafters.dto.ExternalProgramNotFound
import io.codecrafters.dto.ParsedCommand
import io.codecrafters.external.ExternalProgramExecutor
import io.codecrafters.parser.CommandParser
import io.codecrafters.shared_mutable_state.ShellState
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.io.FileOutputStream
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Path

@Component
class ShellRunner(
    private val commandHandlerMap: Map<String, CommandHandler>,
    private val externalProgramExecutor: ExternalProgramExecutor,
    private val commandParser: CommandParser,
    private val shellState: ShellState,
) : CommandLineRunner {

    override fun run(vararg args: String) {
        while (true) {
            val input = readUserInput() ?: break
            if (input.isBlank()) continue

            executeCommand(input)
        }
    }

    private fun readUserInput(): String? {
        print("$ ")
        return readLine()?.trim()
    }

    private fun executeCommand(input: String) {
        val parsed = commandParser.parse(input)
        val handler = commandHandlerMap[parsed.commandName]

        if (parsed.stdoutRedirect != null || parsed.stderrRedirect != null) {
            executeWithRedirection(handler, parsed)
        } else {
            executeDirectly(handler, parsed)
        }
    }

    private fun executeDirectly(handler: CommandHandler?, parsed: ParsedCommand) {
        handler?.handle(parsed.arguments)
            ?: executeExternalCommand(parsed.commandName, parsed.arguments, null, null)
    }

    private fun executeWithRedirection(
        handler: CommandHandler?,
        parsed: ParsedCommand,
    ) {
        val stdoutTarget = parsed.stdoutRedirect?.let(::prepareRedirectTarget)
        val stderrTarget = parsed.stderrRedirect?.let(::prepareRedirectTarget)

        if (handler != null) {
            executeBuiltinWithRedirection(handler, parsed.arguments, stdoutTarget, stderrTarget)
        } else {
            executeExternalCommand(parsed.commandName, parsed.arguments, stdoutTarget, stderrTarget)
        }
    }

    private fun prepareRedirectTarget(redirectPath: String): Path {
        val target = shellState.currentDirectory.resolve(redirectPath).normalize()
        Files.createDirectories(target.parent)
        return target
    }

    private fun executeBuiltinWithRedirection(
        handler: CommandHandler,
        arguments: List<String>,
        stdoutTarget: Path?,
        stderrTarget: Path?,
    ) {
        val originalOut = System.out
        val originalErr = System.err

        val newOut = stdoutTarget?.let { PrintStream(FileOutputStream(it.toFile())) }
        val newErr = stderrTarget?.let { PrintStream(FileOutputStream(it.toFile())) }

        try {
            newOut?.let(System::setOut)
            newErr?.let(System::setErr)
            handler.handle(arguments)
        } finally {
            newOut?.close()
            newErr?.close()
            System.setOut(originalOut)
            System.setErr(originalErr)
        }
    }

    private fun executeExternalCommand(
        commandName: String,
        arguments: List<String>,
        stdoutRedirect: Path?,
        stderrRedirect: Path?,
    ) {
        val result = externalProgramExecutor.execute(commandName, arguments, stdoutRedirect, stderrRedirect)
        if (result is ExternalProgramNotFound) {
            println("$commandName: not found")
        }
    }
}
