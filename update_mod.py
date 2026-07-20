#!/usr/bin/env python3
import os
import shutil

MODULES = ["common", "fabric", "neoforge"]

# ------------------------------------------------------------
# Utility functions
# ------------------------------------------------------------

def read_gradle_properties():
    props = {}
    with open("gradle.properties", "r", encoding="utf-8") as f:
        for line in f:
            line = line.strip()
            if not line or line.startswith("#"):
                continue
            if "=" in line:
                key, value = line.split("=", 1)
                props[key.strip()] = value.strip()
    return props

def detect_old_group():
    base = "common/src/main/kotlin"
    if not os.path.isdir(base):
        return None

    entries = os.listdir(base)
    for prefix in entries:
        prefix_path = os.path.join(base, prefix)
        if os.path.isdir(prefix_path):
            groups = os.listdir(prefix_path)
            for group in groups:
                group_path = os.path.join(prefix_path, group)
                if os.path.isdir(group_path):
                    return f"{prefix}.{group}"
    return None

def detect_old_modid():
    # Try mixins in common
    common_res = "common/src/main/resources"
    if os.path.isdir(common_res):
        for root, dirs, files in os.walk(common_res):
            for f in files:
                if f.endswith(".mixins.json"):
                    return f.replace(".mixins.json", "")

    # Try assets in all modules
    for module in MODULES:
        assets_root = os.path.join(module, "src/main/resources/assets")
        if os.path.isdir(assets_root):
            for root, dirs, files in os.walk(assets_root):
                rel = os.path.relpath(root, assets_root)
                parts = rel.split(os.sep)
                if parts and parts[0] != ".":
                    return parts[0]
    return None

def replace_in_file(path, old, new):
    if not old or not new or old == new:
        return

    binary_ext = (
        ".class", ".jar", ".png", ".jpg", ".jpeg", ".gif",
        ".bin", ".dat", ".kotlin_metadata"
    )
    if any(path.endswith(ext) for ext in binary_ext):
        return

    try:
        with open(path, "r", encoding="utf-8") as f:
            content = f.read()
    except UnicodeDecodeError:
        return

    if old in content:
        content = content.replace(old, new)
        with open(path, "w", encoding="utf-8") as f:
            f.write(content)

def safe_walk(root):
    ignore = {".gradle", ".idea", ".kotlin", "build", "run", "logs", "crash-reports"}
    for dirpath, dirnames, filenames in os.walk(root):
        dirnames[:] = [d for d in dirnames if d not in ignore]
        yield dirpath, dirnames, filenames

# ------------------------------------------------------------
# Main update logic
# ------------------------------------------------------------

def update_mod():
    props = read_gradle_properties()
    new_group = props.get("maven_group")      # e.g. net.marco
    new_modid = props.get("archives_name")    # e.g. rulzframework / backpackmod

    old_group = detect_old_group()            # e.g. net.rulz59
    old_modid = detect_old_modid()            # e.g. rulzframework

    print(f"Old group: {old_group}")
    print(f"New group: {new_group}")
    print(f"Old modid: {old_modid}")
    print(f"New modid: {new_modid}")

    # ------------------------------------------------------------
    # Rename group directories in all modules
    # ------------------------------------------------------------
    if old_group and new_group and old_group != new_group:
        old_prefix, old_name = old_group.split(".", 1)
        new_prefix, new_name = new_group.split(".", 1)

        for module in MODULES:
            old_path = f"{module}/src/main/kotlin/{old_prefix}/{old_name}"
            new_prefix_root = f"{module}/src/main/kotlin/{new_prefix}"
            new_path = f"{new_prefix_root}/{new_name}"

            if os.path.isdir(old_path):
                os.makedirs(new_prefix_root, exist_ok=True)
                shutil.move(old_path, new_path)
                print(f"[{module}] Renamed group directory: {old_path} → {new_path}")

    # ------------------------------------------------------------
    # Rename modid directories under group in all modules
    # ------------------------------------------------------------
    if old_modid and new_modid and old_modid != new_modid and new_group:
        new_group_path = new_group.replace(".", "/")
        for module in MODULES:
            group_path = f"{module}/src/main/kotlin/{new_group_path}"
            old_mod_dir = os.path.join(group_path, old_modid)
            new_mod_dir = os.path.join(group_path, new_modid)
            if os.path.isdir(old_mod_dir):
                shutil.move(old_mod_dir, new_mod_dir)
                print(f"[{module}] Renamed modid directory: {old_mod_dir} → {new_mod_dir}")

    # ------------------------------------------------------------
    # Rename assets folders in all modules
    # ------------------------------------------------------------
    if old_modid and new_modid and old_modid != new_modid:
        for module in MODULES:
            assets_root = os.path.join(module, "src/main/resources/assets")
            old_assets = os.path.join(assets_root, old_modid)
            new_assets = os.path.join(assets_root, new_modid)
            if os.path.isdir(old_assets):
                os.makedirs(assets_root, exist_ok=True)
                shutil.move(old_assets, new_assets)
                print(f"[{module}] Renamed assets: {old_assets} → {new_assets}")

    # ------------------------------------------------------------
    # Rename mixin JSON files in all modules
    # ------------------------------------------------------------
    if old_modid and new_modid and old_modid != new_modid:
        for module in MODULES:
            res_root = os.path.join(module, "src/main/resources")
            if os.path.isdir(res_root):
                for root, dirs, files in os.walk(res_root):
                    for f in files:
                        if f == f"{old_modid}.mixins.json":
                            old_path = os.path.join(root, f)
                            new_path = os.path.join(root, f"{new_modid}.mixins.json")
                            shutil.move(old_path, new_path)
                            print(f"[{module}] Renamed mixin file: {old_path} → {new_path}")

    # ------------------------------------------------------------
    # Replace text inside files in all modules
    # ------------------------------------------------------------
    for module in MODULES:
        for dirpath, dirnames, filenames in safe_walk(module):
            for filename in filenames:
                path = os.path.join(dirpath, filename)
                if os.path.isfile(path):
                    if old_group and new_group and old_group != new_group:
                        replace_in_file(path, old_group, new_group)
                    if old_modid and new_modid and old_modid != new_modid:
                        replace_in_file(path, old_modid, new_modid)

    print("Update complete.")

# ------------------------------------------------------------
# Run
# ------------------------------------------------------------

if __name__ == "__main__":
    update_mod()