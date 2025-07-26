import pyperclip
from pathlib import Path

all_kotlin_code = "\n\n".join(file.read_text() for file in Path(".").rglob("*.kt"))
pyperclip.copy(all_kotlin_code)
