package io.codecrafters

import io.codecrafters.command.CommandHandler
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.system.exitProcess

@Configuration
class Config {
    @Bean
    fun commandHandlerMap(commandHandlers: List<CommandHandler>): Map<String, CommandHandler> =
        commandHandlers.associateBy { it.commandName }

    @Bean
    @ConditionalOnMissingBean(ExitExecutor::class)
    fun realExitExecutor(): ExitExecutor =
        ExitExecutor { status ->
            exitProcess(status)
        }
}
