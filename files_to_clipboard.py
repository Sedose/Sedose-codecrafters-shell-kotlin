import pyperclip
from pathlib import Path

if __name__ == "__main__":
    root = Path(".")
    pom = (root / "pom.xml").read_text() if (root / "pom.xml").exists() else ""
    sources = "\n\n".join(f.read_text() for f in root.rglob("*.kt"))
    pyperclip.copy(f"{pom}\n\n{sources}")
