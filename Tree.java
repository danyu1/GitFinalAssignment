import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;

public class Tree {

    public static List<String> entries = new ArrayList<>();
    private String sha1;
    private String directorySha1;

    public Tree() throws Exception {
        File file = new File("tree");
        if (!file.exists())
            file.createNewFile();
    }

    // add method should accept a fileName, or a tree string
    // tree : HASH : folderName
    public void add(String fileName) throws Exception {
        File fileToAdd = new File(fileName);
        if (fileToAdd.exists()) {
            if (entries.size() == 0) {
                entries.add(fileName);
                Blob.createBlob(fileName);
                updateTreeFileAdd("");
            } else {
                for (int i = 0; i < entries.size(); i++) {
                    if (!entries.get(i).contains(fileName)) {
                        entries.add(fileName);
                        Blob.createBlob(fileName);
                        updateTreeFileAdd("");
                        break;
                    }
                }
            }
        } else {
            if (fileName.contains("Blob : ") || fileName.contains("tree : ")) {
                if (!entries.contains(fileName)) {
                    entries.add(fileName);
                    updateTreeFileAdd(fileName);
                }
            }
        }
    }

    public void remove(String entry) throws Exception {
        updateEntries();
        // to prevent concurrent modification error
        if (entries.size() == 1) {
            if (entries.get(0).contains(entry))
                entries.remove(0);
        } else {
            for (int i = 0; i < entries.size(); i++) {
                if (entries.get(i).contains(entry)) {
                    entries.remove(entries.get(i));
                }
            }
        }
        updateTreeFileRemove(entry);
    }

    public void updateEntries() throws Exception {
        File treeFile = new File("tree");
        if (treeFile.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(treeFile));
            String currentLine = "";
            while (br.ready()) {
                currentLine = br.readLine();
                if (currentLine.contains("tree : ") && !entries.contains(currentLine)) {
                    entries.add(currentLine);
                } else {
                    if (currentLine.contains("Blob : ")
                            && !entries.contains(currentLine.substring(currentLine.lastIndexOf(":") + 2))) {
                        entries.add(currentLine.substring(currentLine.lastIndexOf(":") + 2));
                    }
                }
            }
            br.close();
        }
    }

    public static String addDirectory(String directoryPath) throws Exception {
        Tree childTree = new Tree();
        Path pathToFolder = Paths.get(directoryPath);
        File currentDirectory = new File(pathToFolder.toString());
        File[] files = currentDirectory.listFiles();
        if (files.length == 0) {
            childTree.add("");
            return childTree.getTreeSha();
        }
        if (Files.isDirectory(pathToFolder)) {
            for (File currentFile : files) {
                if (currentFile.isDirectory()) {
                    String subDirectoryPath = directoryPath + "\\" + currentFile.getName();
                    String shaOfDirectoryBlobs = addDirectory(subDirectoryPath);
                    String toAdd = "tree : " + shaOfDirectoryBlobs + " : " + currentFile.getName();
                    childTree.add(toAdd);
                }
                // currentFile is not a directory
                else {
                    String toAdd = "Blob : " + Blob.generateSHA1WithPath(currentFile.getPath()) + " : "
                            + currentFile.getName();
                    childTree.add(toAdd);
                    Blob.createBlobWithPath(currentFile.getPath());
                }
            }
        } else {
            throw new Exception("Invalid path or folder does not exist.");
        }
        childTree.generateBlob();
        return childTree.getTreeSha();
    }

    public void updateTreeFileAdd(String fileName) throws Exception {
        StringBuilder currentTreeFile = new StringBuilder("");
        BufferedReader br = new BufferedReader(
                new FileReader(new File(Paths.get("tree").toString())));
        while (br.ready()) {
            currentTreeFile.append(br.readLine() + "\n");
        }
        br.close();
        if (fileName.contains("Blob : ") || fileName.contains("tree : ")) {
            currentTreeFile.append(fileName);
        } else {
            for (String entry : entries) {
                if (!entry.contains("tree : ") && !entry.contains("Blob : ")) {
                    if (!currentTreeFile.toString().contains(entry))
                        currentTreeFile.append("Blob : " + Blob.generateSHA1(entry)).append(" : " + entry).append("\n");
                } else {
                    if (!currentTreeFile.toString().contains(entry)) {
                        currentTreeFile.append(entry).append("\n");
                    }
                }
            }
        }
        Files.write(Paths.get("tree"), currentTreeFile.toString().getBytes());
        generateTreeSHA();
    }

    public void updateTreeFileRemove(String toRemove) throws Exception {
        StringBuilder currentTreeFile = new StringBuilder("");
        BufferedReader br = new BufferedReader(
                new FileReader(new File(Paths.get("tree").toString())));
        String currentLine = "";
        while (br.ready()) {
            currentLine = br.readLine();
            if (!currentLine.contains(toRemove))
                currentTreeFile.append(currentLine + "\n");
        }

        br.close();
        Files.write(Paths.get("tree"), currentTreeFile.toString().getBytes());
        generateTreeSHA();
    }

    public String getTreeSha() {
        return this.sha1;
    }

    public String generateTreeSHA() throws Exception {
        StringBuilder toHash = new StringBuilder("");
        for (String entry : entries) {
            int lastColonIndex = entry.lastIndexOf(":");
            toHash.append(entry.substring(lastColonIndex + 1).trim());
        }

        // Calculate the SHA-1 hash of the content
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] hashBytes = digest.digest(toHash.toString().getBytes());
        this.sha1 = byteArrayToHex(hashBytes);
        return this.sha1;
    }

    // "save" method
    public void generateBlob() throws Exception {
        // Create a StringBuilder to concatenate all entries
        StringBuilder content = new StringBuilder("");
        File treeFile = new File("tree");
        BufferedReader br = new BufferedReader(new FileReader(treeFile));
        while (br.ready()) {
            content.append(br.readLine() + "\n");
        }
        br.close();
        Path blobPath = Paths.get(Paths.get("objects").toString(), generateTreeSHA());
        File treeObject = new File(blobPath.toString());
        treeObject.createNewFile();
        Files.write(blobPath, content.toString().getBytes());
    }

    // Utility method to convert byte array to hexadecimal string
    private static String byteArrayToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    public List<String> getEntries() {
        return this.entries;
    }

    public String returnAllEntries() {
        StringBuilder sb = new StringBuilder("");
        for (String objects : entries) {
            sb.append(objects);
        }
        return sb.toString();
    }

    public void deleteOrEdit(String entry) throws Exception {
        // you are deleting a file
        File head = new File("head");
        BufferedReader br = new BufferedReader(new FileReader(head));
        String shaOfCurrentCommit = br.readLine();
        br.close();
        File currentCommit = new File(Paths.get(Paths.get("objects").toString(), shaOfCurrentCommit).toString());
        BufferedReader br2 = new BufferedReader(new FileReader(currentCommit));
        String shaOfCurrentTree = br2.readLine();
        br2.close();
        // you are deleting
        if (entry.contains("*deleted*")) {
            traverseTreeAndDelete(entry, shaOfCurrentTree);
            // you are editing a file
        } else if (entry.contains("*edited*")) {
            traverseTreeAndDelete(entry, shaOfCurrentTree);
            String shaOfEntry = Blob.generateSHA1(entry.substring(8));
            String toAdd = "Blob : " + shaOfEntry + " : " + entry.substring(8);
            Blob.createBlob(entry.substring(8));
            add(toAdd);
        } else {
            throw new Exception("You are not editing or deleting a valid file");
        }
    }

    public String traverseTreeAndDelete(String action, String currentTreeSha) throws Exception {
        String removedFile = "NO FILE WAS REMOVED";
        File currentTreeFile = new File(Paths.get(Paths.get("objects").toString(), currentTreeSha).toString());
        BufferedReader br = new BufferedReader(new FileReader(currentTreeFile));
        StringBuilder sb = new StringBuilder();
        while (br.ready()) {
            sb.append(br.readLine() + "\n");
        }
        br.close();
        if (sb.toString().contains(action.substring(9))) {
            remove(action.substring(9));
            removedFile = action.substring(9) + " was removed";
        }
        // must traverse the next tree if file to delete is not found in current tree
        else {
            BufferedReader br2 = new BufferedReader(new FileReader(currentTreeFile));
            while (br2.ready()) {
                String currentLine = br2.readLine();
                if (currentLine.contains("tree : ")) {
                    traverseTreeAndDelete(action, currentLine.substring(7, 47));
                }
            }
            br2.close();
        }
        return removedFile;
    }

    public static boolean containsBlob(String fileName) {
        for (String entry : entries) {
            if (entry.contains(fileName))
                ;
            return true;
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        Tree tree = new Tree();
        // // Add entries to the tree
        // tree.add("testFile1.txt");
        // tree.add("tree : bd1ccec139dead5ee0d8c3a0499b42a7d43ac44b");

        // // Generate and save the tree blob
        // tree.generateBlob();

        // // Check if the tree blob file exists in the 'objects' folder
        // File treeBlobFile = new File(Paths.get(Paths.get("objects").toString(),
        // tree.getTreeSha()).toString());
        // System.out.println(treeBlobFile.exists());

        // System.out.println("Tree SHA1: " + tree.getTreeSha());

        // tree.addDirectory("C:\\Users\\danie\\OneDrive\\Desktop\\Topics
        // Repos\\GitFinalAssignment\\testDirectory1");
        // File parentDirectoryFile = new File(
        // "C:\\Users\\danie\\OneDrive\\Desktop\\Topics
        // Repos\\GitFinalAssignment\\objects\\fb360f9c09ac8c5edb2f18be5de4e80ea4c430d0");
        // System.out.println(parentDirectoryFile.exists());
        // System.out.println(parentDirectoryFile.toString());
        // tree.addDirectory("C:\\Users\\danie\\OneDrive\\Desktop\\Topics
        // Repos\\GitFinalAssignment\\testDirectory2");
        String folderName = "test1";
        File folder1 = new File(folderName);
        folder1.mkdir();
        File file1 = new File(folderName + "/examplefile1.txt");
        File file2 = new File(folderName + "/examplefile2.txt");
        File file3 = new File(folderName + "/examplefile3.txt");
        PrintWriter pw1 = new PrintWriter(file1);
        PrintWriter pw2 = new PrintWriter(file2);
        PrintWriter pw3 = new PrintWriter(file3);
        pw1.print("new contents for file one");
        pw2.print("new contents for file two");
        pw3.print("new contents for file three");
        pw1.close();
        pw2.close();
        pw3.close();

        tree.addDirectory(folderName);

        assertEquals("43b30f483e15a64a6afe4096f805128407574940", tree.getTreeSha());

        Files.delete(Paths.get(file1.getPath()));
        Files.delete(Paths.get(file2.getPath()));
        Files.delete(Paths.get(file3.getPath()));
        Files.delete(Paths.get(folder1.getPath()));
        Files.deleteIfExists(
                Paths.get(Paths.get("objects").toString() + "/" + "43b30f483e15a64a6afe4096f805128407574940"));

    }
}