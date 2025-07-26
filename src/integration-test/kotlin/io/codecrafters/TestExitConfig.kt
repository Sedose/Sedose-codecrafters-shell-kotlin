package io.codecrafters

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

@TestConfiguration
class TestExitConfig {
    @Bean
    @Primary
    fun testExitExecutor(): ExitExecutor =
        ExitExecutor { status ->
            throw ExitInterceptedException(status)
        }
}
