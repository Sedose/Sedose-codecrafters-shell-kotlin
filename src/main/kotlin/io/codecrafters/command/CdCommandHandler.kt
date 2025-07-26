package io.codecrafters.command

import io.codecrafters.shared_mutable_state.ShellState
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Component
class CdCommandHandler(
    private val shellState: ShellState,
) : CommandHandler {
    override val commandName = "cd"

    override fun handle(arguments: List<String>) {
        val rawPath = arguments.firstOrNull()?.trim().orEmpty()

        if (rawPath.isEmpty()) {
            println("cd: missing operand")
            return
        }

        val resolvedPath = resolveTargetPath(rawPath) ?: return

        if (!Files.isDirectory(resolvedPath)) {
            println("cd: $rawPath: No such file or directory")
            return
        }

        shellState.currentDirectory = resolvedPath
    }

    private fun resolveTargetPath(input: String): Path? {
        if (input == "~") {
            val home = System.getenv("HOME")
            if (home.isNullOrEmpty()) {
                println("cd: HOME environment variable not set")
                return null
            }
            return Paths.get(home).normalize()
        }

        val path = Paths.get(input)
        return if (path.isAbsolute) {
            path.normalize()
        } else {
            shellState.currentDirectory.resolve(path).normalize()
        }
    }
}
