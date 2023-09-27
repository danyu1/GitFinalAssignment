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
        File directoryFile = new File(directoryPath);
        Path p = Paths.get(directoryPath);
        if (Files.isDirectory(p)) {
            File[] filesInDirectory = directoryFile.listFiles();
            for (File currentFile : filesInDirectory) {
                // current file is also a directory call addDirectory again
                if (currentFile.isDirectory()) {
                    // at this point you must create a new tree instance called childTree and use
                    // the previous tree to create an entry in the form "Tree: <Sha1> : folderName"
                    addDirectory(currentFile.getPath());
                }
                // current file is not a directory
                else {
                    BufferedReader br = new BufferedReader(new FileReader(currentFile));
                    StringBuilder fileContents = new StringBuilder("");
                    while (br.ready()) {
                        // at this point you have the name of the file, you must generate a hash with
                        // that file and add the proper format to the tree with this file and all the
                        // files after
                        fileContents.append(br.readLine());
                    }

                    br.close();
                }
            }
        } else {
            throw new Exception("directory path is not valid, please use full path written out.");
        }
        return "";
    }

    public String getSHA1OfDirectory() {
        return this.directorySha1;
    }

    // "save" method
    public void generateBlob() throws IOException, NoSuchAlgorithmException {

        // Create a StringBuilder to concatenate all entries
        StringBuilder content = new StringBuilder();
        for (String entry : entries) {
            content.append(entry).append("\n");
        }

        // Calculate the SHA-1 hash of the content
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        byte[] hashBytes = digest.digest(content.toString().getBytes());
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

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
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

        // test how listFiles () method functions
        File directoryFile = new File(
                "C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\GitFinalAssignment\\testDirectory");
        File[] lists = directoryFile.listFiles();
        for (File file : lists) {
            System.out.println(file.getName());
        }
    }
}