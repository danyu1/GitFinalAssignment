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

    // // // due to trash programming addDirectory gets complicated, comments added
    // to
    // // // help with readibility
    // public String addDirectory(String directoryPath) throws Exception {
    // Tree tree = new Tree();
    // File directoryFile = new File(directoryPath);
    // Path p = Paths.get(directoryPath);
    // // edge case when the passed directory is empty
    // if (Files.isDirectory(p) && (directoryFile.listFiles().length == 0)) {
    // tree.add("tree : " + Blob.generateSHA(directoryFile.getName()) + " : " +
    // directoryFile.getName());
    // } else if (Files.isDirectory(p)) {
    // File[] filesInDirectory = directoryFile.listFiles();
    // for (File currentFile : filesInDirectory) {
    // // current file is also a directory enter following code
    // if (currentFile.isDirectory()) {
    // addDirectory(currentFile.getPath());
    // Tree childTree = new Tree();
    // Path pathToSubDirectory = Paths.get(currentFile.getPath());
    // File subDirectoryFile = new File(pathToSubDirectory.toString());
    // File[] filesInSubDirectory = subDirectoryFile.listFiles();
    // for (File currentSubFile : filesInSubDirectory) {
    // // check if the currentSubFile is not a directory
    // if (!currentSubFile.isDirectory()) {
    // BufferedReader br = new BufferedReader(new FileReader(currentSubFile));
    // StringBuilder subFileContents = new StringBuilder("");
    // while (br.ready())
    // subFileContents.append(br.readLine());

    // childTree.add("Blob : " + Blob.generateSHA(subFileContents.toString()) + " :
    // "
    // + currentSubFile.getName());
    // br.close();
    // // if the currentSubFile is a directory hash it with only its name
    // } else {
    // childTree.add("tree : " + Blob.generateSHA(currentSubFile.getName()) + " : "
    // + currentSubFile.getName());
    // }
    // }
    // // generate the child tree as its own blob
    // childTree.generateBlob();
    // // add the hash of the child tree to the parent tree
    // tree.add("tree : " + childTree.getSha1() + " : " +
    // subDirectoryFile.getName());
    // }

    // // current file is not a directory within the passed directory
    // else {
    // BufferedReader br = new BufferedReader(new FileReader(currentFile));
    // StringBuilder fileContents = new StringBuilder("");
    // while (br.ready())
    // fileContents.append(br.readLine());

    // tree.add("Blob : " + Blob.generateSHA(fileContents.toString()) + " : " +
    // currentFile.getName());
    // br.close();
    // }
    // }
    // // edge case when the passed "directory" is just a file
    // } else if (Files.exists(p) && !Files.isDirectory(p)) {
    // BufferedReader br = new BufferedReader(new FileReader(directoryFile));
    // StringBuilder fileContents = new StringBuilder("");
    // while (br.ready())
    // fileContents.append(br.readLine());

    // tree.add("Blob : " + Blob.generateSHA(fileContents.toString()) + " : " +
    // directoryFile.getName());
    // br.close();
    // } else {
    // throw new Exception("directory path is not valid, a possible solution is to
    // use the absolute path.");
    // }
    // // generate the parent tree as a blob
    // tree.generateBlob();
    // this.directorySha1 = tree.getSha1();
    // return directorySha1;
    // }

    public String addDirectory(String directoryPath) throws Exception {
        Tree childTree = new Tree();
        File directoryFile = new File(directoryPath);
        Path p = Paths.get(directoryPath);
        if (Files.isDirectory(p)) {
            File[] filesInDirectory = directoryFile.listFiles();
            if (filesInDirectory.length != 0) {
                for (File currentFile : filesInDirectory) {
                    if (currentFile.isDirectory()) {
                        addDirectory(currentFile.getPath());
                        StringBuilder sb = new StringBuilder("");
                        File[] filesInSubDirectory = currentFile.listFiles();
                        for (File fileNames : filesInSubDirectory)
                            sb.append(fileNames.getName());
                        childTree.add("tree : " + Blob.generateSHA(sb.toString()) + " : " + currentFile.getName());
                    } else {
                        childTree.add(
                                "Blob : " + Blob.generateSHA(currentFile.getName()) + " : " + currentFile.getName());
                    }
                }
            }
        } else {
            throw new Exception(
                    "You did not provide a valid path to a directory. Try using the absolute path if you didn't already.");
        }
        childTree.generateBlob();
        add("tree : " + childTree.getSha1() + " : " + directoryFile.getName());
        this.directorySha1 = childTree.getSha1();
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

    }
}