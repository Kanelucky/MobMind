# Welcome to the MobMind contributing guide <!-- omit in toc -->

Thank you for taking the time to contribute to MobMind!

This guide explains the contribution workflow, from opening issues to creating and merging pull requests.

---

# New contributor guide

To understand the project structure and goals, please read the [README](README.md) first.

Useful resources:

- [Finding ways to contribute to open source on GitHub](https://docs.github.com/en/get-started/exploring-projects-on-github/finding-ways-to-contribute-to-open-source-on-github)
- [Set up Git](https://docs.github.com/en/get-started/getting-started-with-git/set-up-git)
- [GitHub flow](https://docs.github.com/en/get-started/using-github/github-flow)
- [Collaborating with pull requests](https://docs.github.com/en/github/collaborating-with-pull-requests)

---

# Getting started

## Issues

### Creating an issue

Before opening a new issue, please search existing issues first to avoid duplicates.

You can open issues for:

- Bugs
- Crashes
- Performance problems
- AI behavior improvements
- Feature requests
- Documentation issues

Please provide as much detail as possible.

---

### Working on issues

Browse existing issues and look for labels such as:

- `good first issue`
- `help wanted`
- `bug`
- `enhancement`

If you want to work on an issue, feel free to open a pull request with your changes.

---

# Development setup

## Requirements

- JDK 25
- Git
- Gradle

---

## Setup

### 1. Fork the repository

Fork the repository to your GitHub account.

### 2. Clone your fork

```bash
git clone https://github.com/YOUR_USERNAME/MobMind.git
```
### 3. Open the project

Open the project in IntelliJ IDEA or your preferred IDE.

### 4. Build the project
`./gradlew build`

# Making changes

Please keep pull requests focused and minimal.

## Guidelines
- Follow existing code style
- Avoid unnecessary dependencies
- Keep commits clean and descriptive
- Test your changes before submitting
- Example commit messages
- feat: improve mob targeting system
- fix: prevent pathfinding crash
- refactor: clean AI scheduler

### Example commit messages
`feat: improve mob targeting system`

`fix: prevent pathfinding crash`

`refactor: clean AI scheduler`

# Pull Requests

When your changes are ready, open a pull request.

## Before submitting
- Make sure the project builds successfully
- Ensure your changes are tested
- Link related issues if applicable

Example:
`Fixes #12`

## Pull request review

Maintainers may:

- Request changes
- Ask questions
- Suggest improvements

Please keep discussions respectful and constructive.

# Contribution philosophy

MobMind aims to provide:

- Clean architecture
- Modern AI systems
- High performance
- Developer-friendly APIs
- Experimental and innovative features

We welcome contributors of all skill levels.

# Your PR got merged 🎉

Congratulations and thank you for contributing to MobMind!

Your contribution is now part of the project and visible to the community.