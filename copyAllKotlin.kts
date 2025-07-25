import java.io.File

val allSourceCode =
    File(".")
        .walkTopDown()
        .filter { it.isFile && it.extension == "kt" }
        .joinToString("\n\n") { it.readText() }

ProcessBuilder("pbcopy")
    .start()
    .outputStream
    .bufferedWriter()
    .use { it.write(allSourceCode) }
