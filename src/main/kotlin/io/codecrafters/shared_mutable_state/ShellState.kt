package io.codecrafters.shared_mutable_state

import org.springframework.stereotype.Component
import java.nio.file.Path
import java.nio.file.Paths

@Component
class ShellState {
    var currentDirectory: Path =
        Paths.get("").toAbsolutePath().normalize()
}
