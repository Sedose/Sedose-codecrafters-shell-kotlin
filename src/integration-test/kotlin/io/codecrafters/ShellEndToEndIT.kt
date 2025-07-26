package io.codecrafters

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.PrintStream
import java.nio.file.Paths

class ShellEndToEndIT {
    @Test
    fun echoCommandPrintsSuppliedArguments() {
        val result =
            runSession(
                """
                echo Hello, Kotlin End-to-End Test
                exit
            """,
            )
        assertTrue("Hello, Kotlin End-to-End Test" in result.consoleOutput)
    }

    @Test
    fun echoCommandPrintsSuppliedArguments2() {
        val result =
            runSession(
                """
                echo asdasdasd
                exit
            """,
            )
        assertTrue("asdasdasd" in result.consoleOutput)
    }

    @Test
    fun typeCommandRecognisesBuiltins() {
        val result =
            runSession(
                """
                type echo
                type exit
                type type
                exit
            """,
            )
        val expectedBuiltins =
            listOf(
                "echo is a shell builtin",
                "exit is a shell builtin",
                "type is a shell builtin",
            )
        for (builtinStatement in expectedBuiltins) {
            assertTrue(builtinStatement in result.consoleOutput)
        }
    }

    @Test
    fun typeCommandReportsUnknownExecutable() {
        val result =
            runSession(
                """
                type foobar
                exit
            """,
            )
        assertTrue("foobar: not found" in result.consoleOutput)
    }

    @Test
    fun exitCommandExitsWithExplicitStatusCode() {
        val result = runSession("exit 42")
        assertEquals(42, result.exitStatus)
    }

    @Test
    fun emptyInput() {
        val result = runSession("")
        assertTrue(result.consoleOutput == "$ $ ")
        assertTrue(result.exitStatus == 0)
    }

    @Test
    fun handleExternalCommand() {
        val osName = System.getProperty("os.name").lowercase()
        val (externalCommand, uniqueMessage) =
            if (osName.lowercase().contains("win")) {
                "cmd /c echo" to "cross-platform-ok"
            } else {
                "/bin/echo" to "cross-platform-ok"
            }
        val result =
            runSession(
                """
            $externalCommand $uniqueMessage
            exit
            """,
            )
        assertTrue(uniqueMessage in result.consoleOutput)
        assertEquals(0, result.exitStatus)
    }

    @Test
    fun printsCurrentDirectory() {
        val expected =
            Paths
                .get("")
                .toAbsolutePath()
                .normalize()
                .toString()
        val result =
            runSession(
                """
            pwd
            exit
            """,
            )
        assertTrue(expected in result.consoleOutput)
    }

    @Test
    fun invalidCommandPrintsErrorMessage() {
        val result =
            runSession(
                """
            invalid_command
            exit
        """,
            )
        assertTrue("invalid_command: not found" in result.consoleOutput)
    }

    fun runSession(script: String): SessionResult {
        val originalInputStream: InputStream = System.`in`
        val originalPrintStream: PrintStream = System.out
        val capturedOutput = ByteArrayOutputStream()

        System.setIn(ByteArrayInputStream((script.trimIndent() + "\n").toByteArray()))
        System.setOut(PrintStream(capturedOutput))

        var ctx: ConfigurableApplicationContext? = null
        val status =
            try {
                ctx = SpringApplication.run(arrayOf(Main::class.java, TestExitConfig::class.java), emptyArray<String>())
                0
            } catch (intercepted: ExitInterceptedException) {
                intercepted.status
            } finally {
                System.setIn(originalInputStream)
                System.setOut(originalPrintStream)
                ctx?.close()
            }

        return SessionResult(capturedOutput.toString(), status)
    }
}

data class SessionResult(
    val consoleOutput: String,
    val exitStatus: Int,
)
