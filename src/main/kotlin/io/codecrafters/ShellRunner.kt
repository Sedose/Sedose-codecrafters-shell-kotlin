package io.codecrafters

import io.codecrafters.command.CommandHandler
import io.codecrafters.dto.ExternalProgramNotFound
import io.codecrafters.dto.ParsedCommand
import io.codecrafters.external.ExternalProgramExecutor
import io.codecrafters.parser.CommandParser
import io.codecrafters.shared_mutable_state.ShellState
import org.jline.reader.LineReader
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.io.FileOutputStream
import java.io.PrintStream
import java.nio.file.Files
import java.nio.file.Path

@Component
class ShellRunner(
    private val commandHandlerMap: Map<String, CommandHandler>,
    private val lineReader: LineReader,
    private val externalProgramExecutor: ExternalProgramExecutor,
    private val commandParser: CommandParser,
    private val shellState: ShellState,
) : CommandLineRunner {

    override fun run(vararg args: String) {
        while (true) {
            val inputLine = lineReader.readLine("$ ") ?: break
            if (inputLine.isBlank()) continue
            executeCommand(inputLine)
        }
    }

    private fun executeCommand(rawInput: String) {
        val parsedCommand = commandParser.parse(rawInput)
        val handler = commandHandlerMap[parsedCommand.commandName]

        if (parsedCommand.stdoutRedirect != null || parsedCommand.stderrRedirect != null) {
            executeWithRedirection(handler, parsedCommand)
        } else {
            executeDirectly(handler, parsedCommand)
        }
    }

    private fun executeDirectly(handler: CommandHandler?, parsedCommand: ParsedCommand) {
        handler?.handle(parsedCommand.arguments)
            ?: executeExternalCommand(
                parsedCommand.commandName,
                parsedCommand.arguments,
                null,
                null,
            )
    }

    private fun executeWithRedirection(handler: CommandHandler?, parsedCommand: ParsedCommand) {
        val stdoutTarget = parsedCommand.stdoutRedirect?.let(::prepareRedirectTarget)
        val stderrTarget = parsedCommand.stderrRedirect?.let(::prepareRedirectTarget)

        if (handler != null) {
            executeBuiltinWithRedirection(handler, parsedCommand.arguments, stdoutTarget, stderrTarget)
        } else {
            executeExternalCommand(parsedCommand.commandName, parsedCommand.arguments, stdoutTarget, stderrTarget)
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

        val redirectedOut = stdoutTarget?.let { PrintStream(FileOutputStream(it.toFile())) }
        val redirectedErr = stderrTarget?.let { PrintStream(FileOutputStream(it.toFile())) }

        try {
            redirectedOut?.let(System::setOut)
            redirectedErr?.let(System::setErr)
            handler.handle(arguments)
        } finally {
            redirectedOut?.close()
            redirectedErr?.close()
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
