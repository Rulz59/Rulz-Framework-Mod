# RulzFramework – Cross‑Loader Minecraft Mod Template
<div align="center">
  <a href="https://github.com/Rulz59">
    <img src="https://img.shields.io/badge/GitHub-Rulz59-181717?logo=github" alt="GitHub Profile">
  </a>
</div>

---

## Overview

**RulzFramework** is a multi‑loader Minecraft mod template using **Architectury** and **Kotlin** for mod development targeting both **Fabric** and **NeoForge**.

The repository is structured for clean development, cross‑loader compatibility, and long‑term expansion.

---

## Project Information

### Author
**Rulz59**

### Mod ID
`rulzframework`

### Mod Group
`net.rulz59`

### Loaders
- Fabric
- NeoForge

### Language
Kotlin (JVM)

### Description
**RulzFramework** is a mod template that uses a shared codebase (`common`) consumed by both **Fabric** and **NeoForge**, enabling easier cross‑loader mod development.  
It is prepared for development in Kotlin.

---

## Repository Structure

```
Rulz-Framework-Mod/
│
├── common/        # Shared code used by both loaders
├── fabric/        # Fabric-specific entrypoints & integrations
├── neoforge/      # NeoForge-specific entrypoints & integrations
│
├── gradle/        # Gradle wrapper files
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── update_mod.py  # Automatic modid/group refactoring script
└── README.md
```

This structure follows the official Architectury multi‑loader pattern.

---

## Automatic Mod ID & Group Refactoring (update_mod.py)

Changing the **modid** or **mod group** normally requires manually renaming multiple files and directories.

This is error‑prone and time‑consuming so, to solve this, the template includes **`update_mod.py`**, a fully automated refactoring script.

### How it works

1. Edit the following fields in `gradle.properties`:
   ```
   maven_group = net.example
   archives_name = mycoolmod
   ```

2. Run the script:
   ```
   python update_mod.py
   ```

The script will:

- Detect the old modid and group
- Rename all relevant directories
- Update Kotlin package paths
- Rename assets folders
- Rename mixin config files
- Update all references inside source files
- Apply changes across **common**, **fabric**, and **neoforge**

This ensures the entire project updates cleanly and consistently with **zero manual renaming**.

## Development Workflow

### Working with the Main Branch

The `main` branch is the primary development branch.

```bash
git add .
git commit -m "message"
git push
```

---

### Creating a New Branch

```bash
git checkout -b branch-name
# OR
git checkout branch-name
```

Push the branch (optional):

```bash
git push
```

---

### Keeping Other Branches Updated

```bash
git checkout branch-name
git pull origin main
```

Resolve conflicts if needed:

```bash
git add resolved-file
git commit
```

---

## General Tips

- Pull from `main` regularly to avoid large merge conflicts.
- Branch naming follows Minecraft versions:
    - main → stable template
    - 1.21.1 → current development
    - 1.22.x → future updates  
      (Temporary feature branches may be used locally, e.g., `1.21.1_registry-layer`.)

- Write clear commit messages for better tracking.
- Keep common code loader‑agnostic; loader‑specific logic belongs in `fabric/` or `neoforge/`.

---

## License
MIT License

Copyright (c) 2026 Rulz59

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the “Software”), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
