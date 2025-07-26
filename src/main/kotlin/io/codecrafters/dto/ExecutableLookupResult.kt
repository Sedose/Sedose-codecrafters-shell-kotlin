package io.codecrafters.dto

sealed interface ExecutableLookupResult {
    data class ExecutableFound(
        val absolutePath: String,
    ) : ExecutableLookupResult

    object PathVariableNotFound : ExecutableLookupResult

    object ExecutableNotFound : ExecutableLookupResult
}
