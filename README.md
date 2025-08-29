# 🧬 Hiigh Level Git Version Control (Java)

> A simplified Java-based simulation of Git’s core functionality, including blobs, commits, trees, and a basic staging/index mechanism.

---

## 📁 Repository Structure

```
.
├── Blob.java              # Represents file contents
├── Commit.java            # Handles commit metadata and tree references
├── Tree.java              # Represents directory-like structure of blobs/trees
├── Index.java             # Tracks staged changes
├── GitTest.java           # Integration tests for Git simulation
├── *.Test.java            # Unit tests for core classes
├── PredictionScript.java  # (If present) handles future prediction logic
├── *.png                  # Visuals showing different commit states
├── .gitignore             # Ignore rules
├── README.md              # This file
```

---

## ⚙️ Features

- 📦 **Blobs**: Store file content and generate SHA1 hashes.
- 🌲 **Trees**: Represent directories that hold blobs or other trees.
- 🧾 **Commits**: Store metadata (message, parent, timestamp) and point to a root tree.
- 🗃️ **Index**: Stage added/deleted files before commit.
- 🔁 **Reconstruction**: Rebuild prior versions from commit history.
- 📷 **Visuals**: PNG images demonstrate the state of the repository at different commit stages.
- 🧪 **JUnit Tests**: Comprehensive testing for Blobs, Commits, Trees, Indexes.

---

## 🚀 Getting Started

1. Clone the repo:
   ```bash
   git clone https://github.com/<your-username>/<repo-name>.git
   ```

2. Compile the project:
   ```bash
   javac *.java
   ```

3. Run tests (using JUnit or your IDE):
   ```bash
   java org.junit.runner.JUnitCore CommitTest
   ```

---

## 🧪 Testing

This project uses basic unit tests for each component:

- `BlobTest.java`
- `CommitTest.java`
- `TreeTest.java`
- `IndexTest.java`
- `GitTest.java`

Each test file validates the core logic and structure, ensuring correct SHA generation, file tracking, and commit reconstruction.

---

## 🖼️ Visual Output

The following PNGs show commit evolution and structure:

- `firstcommit.png`
- `secondcommit.png`
- `thirdcommit.png`
- `lastcommit.png`

Each corresponds to a simulated snapshot of the repository.

---

## 🙌 Acknowledgments

Project by `dany1`. Originally created to simulate low-level Git internals using pure Java.
