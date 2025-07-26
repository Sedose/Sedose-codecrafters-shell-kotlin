package io.codecrafters

import io.codecrafters.command.BuiltinCommandHandler
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import org.jline.reader.impl.DefaultParser
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.system.exitProcess

@Configuration
class Config {
    @Bean
    fun commandHandlerMap(builtinCommandHandlers: List<BuiltinCommandHandler>): Map<String, BuiltinCommandHandler> =
        builtinCommandHandlers.associateBy { it.commandName }

    @Bean
    @ConditionalOnMissingBean(ExitExecutor::class)
    fun realExitExecutor(): ExitExecutor =
        ExitExecutor { status ->
            exitProcess(status)
        }

    @Bean
    fun terminal(): Terminal =
        TerminalBuilder.builder()
            .system(true)
            .build()

    @Bean
    fun lineReader(terminal: Terminal): LineReader =
        LineReaderBuilder.builder()
            .terminal(terminal)
            .parser(DefaultParser())
            .build()
}
