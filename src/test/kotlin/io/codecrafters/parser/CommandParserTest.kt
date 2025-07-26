package io.codecrafters.parser

import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommandParserTest {
    private val parser = CommandParser()

    data class Input(
        val line: String,
    )

    data class Output(
        val expectedCommand: String,
        val expectedArgs: List<String>,
    )

    data class Case(
        val description: String,
        val input: Input,
        val output: Output,
    )

    companion object {
        @JvmStatic
        fun cases(): Stream<Case> =
            Stream.of(
                // basic tokenisation
                Case(
                    "no args",
                    Input("""exit"""),
                    Output("exit", emptyList()),
                ),
                Case(
                    "multiple args",
                    Input("""echo hello world"""),
                    Output("echo", listOf("hello", "world")),
                ),
                Case(
                    "leading/trailing whitespace",
                    Input("""   ls   -l    /tmp   """),
                    Output("ls", listOf("-l", "/tmp")),
                ),
                // single-quote handling
                Case(
                    "single quoted token",
                    Input("""echo 'hello world'"""),
                    Output("echo", listOf("hello world")),
                ),
                Case(
                    "multiple single quoted segments",
                    Input("""echo 'hello world' test 'foo bar'"""),
                    Output("echo", listOf("hello world", "test", "foo bar")),
                ),
                Case(
                    "unmatched single quote",
                    Input("""echo 'foo bar"""),
                    Output("echo", listOf("foo bar")),
                ),
                // double-quote handling
                Case(
                    "double quoted token",
                    Input("""echo "hello world""""),
                    Output("echo", listOf("hello world")),
                ),
                Case(
                    "multiple double quoted segments",
                    Input("""echo "hello world" test "foo bar""""),
                    Output("echo", listOf("hello world", "test", "foo bar")),
                ),
                Case(
                    "unmatched double quote",
                    Input("""echo "foo bar"""),
                    Output("echo", listOf("foo bar")),
                ),
                // edge cases
                Case(
                    "empty input",
                    Input(""""""),
                    Output("", emptyList()),
                ),
                Case(
                    "whitespace only input",
                    Input("""        """),
                    Output("", emptyList()),
                ),
                Case(
                    "drop first token",
                    Input("""cmd arg1"""),
                    Output("cmd", listOf("arg1")),
                ),
                // mixed quoting + whitespace stress
                Case(
                    "mixed quote styles: double wraps single",
                    Input("""echo "hello 'inner' world""""),
                    Output("echo", listOf("hello 'inner' world")),
                ),
                Case(
                    "mixed quote styles: single wraps double",
                    Input("""echo 'hello "inner" world'"""),
                    Output("echo", listOf("hello \"inner\" world")),
                ),
                Case(
                    "consecutive blanks no empties",
                    Input("""echo   a    b"""),
                    Output("echo", listOf("a", "b")),
                ),
                Case(
                    "trailing blanks after quoted arg",
                    Input("""echo 'a b'   """),
                    Output("echo", listOf("a b")),
                ),
                Case(
                    "leading blanks before quoted arg",
                    Input("""echo    "foo bar""""),
                    Output("echo", listOf("foo bar")),
                ),
                // back-slash handling
                Case(
                    "backslash escapes space outside quotes",
                    Input("""echo hello\ world"""),
                    Output("echo", listOf("hello world")),
                ),
                Case(
                    "backslash escapes backslash outside quotes",
                    Input("""echo foo\\bar"""),
                    Output("echo", listOf("""foo\bar""")),
                ),
                Case(
                    "backslash inside double quotes is literal",
                    Input("""echo "hello\ world""""),
                    Output("echo", listOf("""hello\ world""")),
                ),
                Case(
                    "backslash inside single quotes is literal",
                    Input("""echo 'hello\ world'"""),
                    Output("echo", listOf("""hello\ world""")),
                ),
                Case(
                    "dangling backslash at end of input kept literal",
                    Input("""echo foo\"""),
                    Output("echo", listOf("""foo\""")),
                ),
                Case(
                    "escaped quote outside quoting",
                    Input("""echo \"quoted\""""),
                    Output("echo", listOf(""""quoted"""")),
                ),
                Case(
                    "escaped backslash in double quotes",
                    Input("""echo "foo\\\\bar""""),  // echo "foo\\bar"
                    Output("echo", listOf("""foo\\bar""")),
                ),
                Case(
                    "escaped quote in double quotes",
                    Input("""echo "she said \"hello\"""""),  // echo "she said \"hello\""
                    Output("echo", listOf("""she said "hello"""")),
                ),
                Case(
                    "escaped dollar in double quotes",
                    Input("""echo "price is \$100""""),  // echo "price is \$100"
                    Output("echo", listOf("""price is $100""")),
                ),
                Case(
                    "backslash before non-special char in double quotes",
                    Input("""echo "foo\qbar""""),  // echo "foo\qbar"
                    Output("echo", listOf("""foo\qbar""")),
                ),
                Case(
                    "escaped quote outside of quotes",
                    Input("""echo \"hello\""""),  // echo "hello"
                    Output("echo", listOf("\"hello\"")),
                ),
            )
    }

    @ParameterizedTest
    @MethodSource("cases")
    fun parseVariants(case: Case) {
        val parsed = parser.parse(case.input.line)
        assertEquals(case.output.expectedCommand, parsed.commandName)
        assertEquals(case.output.expectedArgs, parsed.arguments)
    }
}
