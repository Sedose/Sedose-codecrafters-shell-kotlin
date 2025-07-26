package io.codecrafters.external

import io.codecrafters.dto.ExternalProgramExecutionResult
import io.codecrafters.dto.ExternalProgramNotFound
import io.codecrafters.dto.ExternalProgramSuccess
import io.codecrafters.shared_mutable_state.ShellState
import org.springframework.stereotype.Component
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path

@Component
class ExternalProgramExecutor(
    private val shellState: ShellState,
) {
    fun execute(
        commandName: String,
        arguments: List<String>,
        stdoutRedirect: Path? = null,
        stderrRedirect: Path? = null,
    ): ExternalProgramExecutionResult {

        val builder = ProcessBuilder(listOf(commandName) + arguments)
            .directory(shellState.currentDirectory.toFile())

        if (stdoutRedirect != null) {
            stdoutRedirect.parent?.let(Files::createDirectories)
            builder.redirectOutput(stdoutRedirect.toFile())
        }

        if (stderrRedirect != null) {
            stderrRedirect.parent?.let(Files::createDirectories)
            builder.redirectError(stderrRedirect.toFile())
        }

        return try {
            val process = builder.start()

            if (stdoutRedirect == null) {
                process.inputStream.bufferedReader().forEachLine(::println)
            }
            if (stderrRedirect == null) {
                process.errorStream.bufferedReader().forEachLine(System.err::println)
            }

            ExternalProgramSuccess(process.waitFor())
        } catch (_: IOException) {
            ExternalProgramNotFound(commandName)
        }
    }
}
