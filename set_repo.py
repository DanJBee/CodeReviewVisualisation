#!/usr/bin/env python3
"""
Script to update the repository owner and name in settings.ini

Usage:
    python3 set_repo.py <owner> <repo>
    
"""

import sys
import os
import subprocess
from configparser import ConfigParser


def run_command(command: list[str], cwd: str, description: str) -> bool:
    """Run a command and return True if successful"""
    print(f"\n{'='*60}")
    print(f"Running: {description}")
    print(f"Command: {' '.join(command)}")
    print(f"Directory: {cwd}")
    print('='*60 + "\n")
    
    result = subprocess.run(command, cwd=cwd)
    
    if result.returncode != 0:
        print(f"\nError: {description} failed with exit code {result.returncode}")
        return False
    
    print(f"\n✓ {description} completed successfully")
    return True


def update_settings(owner: str, repo: str) -> str:
    """Update the OWNER and REPO in settings.ini"""
    
    # Get the directory where this script is located
    script_dir = os.path.dirname(os.path.abspath(__file__))
    settings_file = os.path.join(script_dir, "settings.ini")
    
    if not os.path.exists(settings_file):
        print(f"Error: settings.ini not found at {settings_file}")
        sys.exit(1)
    
    # Read the current settings
    config = ConfigParser()
    config.read(settings_file)
    
    # Get old values for display
    old_owner = config.get("DATABASE", "OWNER", fallback="(not set)")
    old_repo = config.get("DATABASE", "REPO", fallback="(not set)")
    
    # Update the values
    config.set("DATABASE", "OWNER", owner)
    config.set("DATABASE", "REPO", repo)
    
    # Write back to file
    with open(settings_file, "w") as f:
        config.write(f)
    
    print(f"Updated settings.ini:")
    print(f"  OWNER: {old_owner} -> {owner}")
    print(f"  REPO:  {old_repo} -> {repo}")
    print()
    print(f"Target: https://github.com/{owner}/{repo}")
    
    return script_dir


def main():
    if len(sys.argv) != 3:
        print("Usage: python set_repo.py <owner> <repo>")
        print()
        print("Examples:")
        print("  python set_repo.py microsoft typescript")
        print("  python set_repo.py facebook react")
        print("  python set_repo.py torvalds linux")
        sys.exit(1)
    
    owner = sys.argv[1]
    repo = sys.argv[2]
    
    # Step 0: Update settings.ini
    script_dir = update_settings(owner, repo)
    
    crawler_dir = os.path.join(script_dir, "crawler")
    migrator_dir = os.path.join(script_dir, "migrator")
    crawler_python = os.path.join(crawler_dir, "venv", "bin", "python")
    migrator_python = os.path.join(migrator_dir, "venv", "bin", "python")
    
    # Step 1: Run crawler
    if not run_command([crawler_python, "crawl.py"], crawler_dir, "Crawling GitHub repository"):
        sys.exit(1)
    
    # Step 2: Run split
    if not run_command([crawler_python, "split.py"], crawler_dir, "Splitting crawled data"):
        sys.exit(1)
    
    # Step 3: Run migrator
    if not run_command([migrator_python, "migrate.py"], migrator_dir, "Migrating data to database"):
        sys.exit(1)
    
    print(f"\n{'='*60}")
    print("✓ All steps completed successfully!")
    print(f"Repository {owner}/{repo} has been crawled and migrated.")
    print('='*60)


if __name__ == "__main__":
    main()
