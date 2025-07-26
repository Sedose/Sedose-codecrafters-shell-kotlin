package io.codecrafters.dto

sealed interface ExternalProgramExecutionResult

data class ExternalProgramSuccess(
    val exitCode: Int,
) : ExternalProgramExecutionResult

data class ExternalProgramNotFound(
    val commandName: String,
) : ExternalProgramExecutionResult
