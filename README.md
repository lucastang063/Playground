# Playground

A Playground repo for Ducks team to test and evaluate code. This was made in 2026.

## Table of Contents

- [About](#about)
- [Getting Started](#getting-started)
- [Usage](#usage)
- [Reference](#reference)

## About

Everyone in Ducks team can use this repository to host code.
- To contribute, create a new folder with your name, create a new branch and merge into main branch when ready.
- Delete the branch after merge.
- Keep all files in your folder, don't modify other team member's files without permission.


## Getting Started

### Prerequisites

Follow the [instruction](https://docs.github.com/en/authentication/connecting-to-github-with-ssh/adding-a-new-ssh-key-to-your-github-account) to setup SSH key

### Installation

Step-by-step instructions for setting up the project locally.

```bash
# Clone the repo
git clone git@github.com:duck-robotics/Playground.git
cd Playground

```

## Usage

Use pull command to fetch changes from the remote repository and merge into local branch

```bash
git pull
```

Sometimes there are conflicts during merge. Follow this [instruction](https://www.atlassian.com/git/tutorials/using-branches/merge-conflicts) to resolve conflict.

Add a single file
```bash
git add filename.txt
```

Add all changed and new files at once
```bash
git add .
```

Check what is currently staged
```bash
git status
```

Commit local change
```bash
git commit -am "Fix typo in header text"
```

Create a new branch and switch to it
```bash
git checkout -b <branch-name>
```
It is suggested to follow the <user_name>/<feature_name> convention in branch name. For example
```bash
git checkout -b peter/motor_controller
```

Push a local branch to the remote repository
```bash
git push -u origin <branch-name>
```

Checkout a branch
```bash
git switch -c <branch-name>
```

## Reference

https://ftc-docs.firstinspires.org/en/latest/programming_resources/tutorial_specific/android_studio/fork_and_clone_github_repository/Fork-and-Clone-From-GitHub.html
