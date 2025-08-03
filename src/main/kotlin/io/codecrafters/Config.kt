package io.codecrafters

import io.codecrafters.command.BuiltinCommandHandler
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import kotlin.system.exitProcess

@Configuration
class Config {
    @Bean
    fun builtinCommandHandlers(builtinCommandHandlers: List<BuiltinCommandHandler>): Map<String, BuiltinCommandHandler> =
        builtinCommandHandlers.associateBy { it.commandName }

    @Bean
    @ConditionalOnMissingBean(ExitExecutor::class)
    fun realExitExecutor(): ExitExecutor =
        ExitExecutor { status ->
            exitProcess(status)
        }
}
