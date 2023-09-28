import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;

public class Tree {
    static String pathToWorkSpace = "C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\GitFinalAssignment";

    private List<String> entries = new ArrayList<>();
    private String sha1;
    private String directorySha1;

    public void add(String entry) {
        entries.add(entry);
    }

    public void remove(String entry) {
        entries.remove(entry);
    }

    public String addDirectory(String directoryPath) throws Exception {
        // create new tree instance and call upon it if necessary
        Tree childTree = new Tree();
        File directoryFile = new File(directoryPath);
        Path p = Paths.get(directoryPath);
        // if path exists enter
        if (Files.isDirectory(p)) {
            File[] filesInDirectory = directoryFile.listFiles();
            if (filesInDirectory.length != 0) {
                // loop through the files in directory
                for (File currentFile : filesInDirectory) {
                    if (currentFile.isDirectory()) {
                        // call addDirectory again with current subdirectory
                        addDirectory(currentFile.getPath());
                        StringBuilder sb = new StringBuilder("");
                        File[] filesInSubDirectory = currentFile.listFiles();
                        for (File fileNames : filesInSubDirectory)
                            sb.append(fileNames.getName());
                        // add this directory with a sha1 of its contents to the child tree of current
                        // working directory
                        childTree.add("tree : " + Blob.generateSHA(sb.toString()) + " : " + currentFile.getName());
                    } else {
                        // if file is not a sub directory add a blob to the child tree of current
                        // working directory
                        childTree.add(
                                "Blob : " + Blob.generateSHA(currentFile.getName()) + " : " + currentFile.getName());
                    }
                }
                // enter the else if there were no files found in passed folder
            } else {
                childTree.add("No files were found in passed folder.");
            }
        } else {
            throw new Exception(
                    "You did not provide a valid path to a directory. Try using the absolute path if you didn't already.");
        }
        // generate childTree of current working directory
        childTree.generateBlob();
        // add the current tree to "entries" arraylist
        add("tree : " + childTree.getSha1() + " : " + directoryFile.getName());
        this.directorySha1 = childTree.getSha1();
        // updates index/tree file in the workspace
        updateTreeFile();
        return this.directorySha1;
    }

    // in this case "tree" file is called index due to previous choices made in
    // other classes
    public void updateTreeFile() throws Exception {
        Path pathToTreeFile = Paths.get("C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\GitFinalAssignment\\index");
        StringBuilder sb = new StringBuilder("");
        for (String entry : entries) {
            sb.append(entry).append("\n");
        }
        Files.write(pathToTreeFile, sb.toString().getBytes());
    }

    public String getDirectorySha() {
        return this.directorySha1;
    }

    // "save" method
    public void generateBlob() throws IOException, NoSuchAlgorithmException {

        // Create a StringBuilder to concatenate all entries
        StringBuilder content = new StringBuilder();
        StringBuilder toHash = new StringBuilder();
        for (String entry : entries) {
            int lastColonIndex = entry.lastIndexOf(":");
            toHash.append(entry.substring(lastColonIndex + 1).trim());
            content.append(entry).append("\n");
        }

        // Calculate the SHA-1 hash of the content
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] hashBytes = digest.digest(toHash.toString().getBytes());
        sha1 = byteArrayToHex(hashBytes);

        // Create the blob file in the 'objects' folder
        Path blobPath = Paths.get(pathToWorkSpace + "\\objects\\", sha1);
        Files.write(blobPath, content.toString().getBytes());
    }

    public String getSha1() {
        return sha1;
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

    public static void main(String[] args) throws Exception {
        Tree tree = new Tree();

        // Add entries to the tree
        tree.add("blob : 81e0268c84067377a0a1fdfb5cc996c93f6dcf9f : file1.txt");
        tree.add("blob : 01d82591292494afd1602d175e165f94992f6f5f : file2.txt");
        tree.add("blob : f1d82236ab908c86ed095023b1d2e6ddf78a6d83 : file3.txt");
        tree.add("tree : bd1ccec139dead5ee0d8c3a0499b42a7d43ac44b");
        tree.add("tree : e7d79898d3342fd15daf6ec36f4cb095b52fd976");

        // Generate and save the tree blob
        tree.generateBlob();

        System.out.println("Tree SHA1: " + tree.getSha1());

        tree.addDirectory("C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\GitFinalAssignment\\testDirectory1");
        File parentDirectoryFile = new File(
                "C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\GitFinalAssignment\\objects\\fb360f9c09ac8c5edb2f18be5de4e80ea4c430d0");
        System.out.println(parentDirectoryFile.exists());
        System.out.println(parentDirectoryFile.toString());
        tree.addDirectory("C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\GitFinalAssignment\\testDirectory2");

    }
}