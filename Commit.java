import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Commit {
    Tree tree = null;
    String treeHash = "";
    String prevCommit = "";
    String nextCommit = "";
    String author;
    String date;
    String summary;

    StringBuilder toPrint;
    String pathToCommit;

    // constructor for any commit after the first ever commit
    public Commit(String shaOfPrevCommit, String author, String summary) throws Exception {
        createTree();
        prevCommit = shaOfPrevCommit;
        this.nextCommit = "";
        this.author = author;
        this.date = getDate();
        this.summary = summary;

        // update toPrint to be ready to use when writing to a file
        toPrint = new StringBuilder();
        toPrint.append(treeHash + "\n");
        toPrint.append(this.prevCommit + "\n");
        toPrint.append("\n");
        toPrint.append(this.author + "\n");
        toPrint.append(this.date + "\n");
        toPrint.append(summary);
        this.pathToCommit = Paths.get(Paths.get("objects").toString(), prevCommit).toString();
    }

    // constructor for the first commit with no parent or next
    public Commit(String author, String summary) throws Exception {
        createTree();
        prevCommit = "";
        this.nextCommit = "";
        this.author = author;
        this.date = getDate();
        this.summary = summary;

        // update toPrint to be ready to use when writing to a file
        toPrint = new StringBuilder();
        toPrint.append(treeHash + "\n");
        toPrint.append(this.prevCommit + "\n");
        toPrint.append(this.nextCommit + "\n");
        toPrint.append(this.author + "\n");
        toPrint.append(this.date + "\n");
        toPrint.append(summary);

        // create the head file
        File head = new File("head");
        if (!head.exists())
            head.createNewFile();

        // create the commit file
        File commit = new File("commit");
        if (!commit.exists())
            commit.createNewFile();

    }

    public void save() throws Exception {
        // generate new hash for the new tree
        this.tree.generateBlob();
        this.treeHash = tree.getTreeSha();
        // must update toPrint stringbuilder
        Path temp = Paths.get(Paths.get("objects").toString(), prevCommit);
        this.pathToCommit = temp.toString();
        // if this is the intial commit there is no need to update the "next" commit of
        // its previous commit since it didn't have a prevCommit
        if (!this.prevCommit.equals(""))
            updatePrevNextCommit();
        this.toPrint = new StringBuilder("");
        toPrint.append(this.treeHash + "\n" + this.prevCommit + "\n" + this.nextCommit + "\n" + this.author + "\n"
                + this.date + "\n" + this.summary);
        // Create the commit file in the 'objects' folder
        Path commitPath = Paths.get(Paths.get("objects").toString(), generateSha1());
        Files.write(commitPath, toPrint.toString().getBytes());
        // write to the commit file
        Files.write(Paths.get("commit"), toPrint.toString().getBytes());
        // update the head file
        File head = new File("head");
        if (head.exists())
            Files.write(Paths.get("head"), generateSha1().getBytes());
    }

    public String getTreeHash() {
        return this.treeHash;
    }

    public String generateSha1() throws Exception {
        StringBuilder forSHA = new StringBuilder("");
        // add all the file contents except for the next commit
        forSHA.append(treeHash + "\n" + this.prevCommit + "\n" + this.author + "\n" + this.date + "\n"
                + this.summary);
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] hash = md.digest(forSHA.toString().getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // code taken from javatpoint.com
    public String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }

    // create a tree and generate a base sha1 for an empty file
    public String createTree() throws Exception {
        this.tree = new Tree();
        // read index contents and add them all to the tree
        File indexFile = new File("index");
        BufferedReader br = new BufferedReader(new FileReader(indexFile));
        while (br.ready()) {
            String toAdd = br.readLine();
            if (!toAdd.contains("*edited*") && !toAdd.contains("*deleted*")) {
                if (toAdd.contains("\n")) {
                    toAdd.substring(0, toAdd.length() - 2);
                }
                tree.add(toAdd);
            }
            // the current line is an edited or deleted file
            else {
                tree.traverseTreeAndDelete(toAdd.substring(toAdd.lastIndexOf(":") + 2), prevCommit);
            }
        }
        br.close();
        String toOverride = "";
        Files.write(Paths.get(indexFile.getName()), toOverride.getBytes());
        this.tree.generateBlob();
        this.treeHash = tree.getTreeSha();
        // if the current commit has a previous then update index with the previous tree
        if (!prevCommit.equals("")) {
            File prevCommitFile = new File(Paths.get(Paths.get("objects").toString(), prevCommit).toString());
            BufferedReader br2 = new BufferedReader(new FileReader(prevCommitFile));
            Files.write(Paths.get(Paths.get("objects").toString(), prevCommit), ("tree : " + br.readLine()).getBytes());
            br2.close();
        }
        String toPrint = "tree : " + treeHash;
        Files.write(Paths.get(indexFile.getName()), toPrint.getBytes());
        return this.treeHash;
    }

    public String getCommitTree(String commitSHA1) throws Exception {
        Path pathToCommit = Paths.get("objects", commitSHA1);
        String commitTreeHash = "A commit was never found";
        if (Files.exists(pathToCommit)) {
            File commit = new File(Paths.get("objects", commitSHA1).toString());
            BufferedReader br = new BufferedReader(new FileReader(commit));
            commitTreeHash = br.readLine();
            br.close();
        } else {
            throw new Exception("Commit blob couldn't be found, try another hash.");
        }
        return commitTreeHash;
    }

    public void addToTree(String fileName) throws Exception {
        this.tree.add(fileName);
    }

    // read the parentCommit that has the updated "next" value and update this
    // objects next value
    public void updatePrevNextCommit() throws Exception {
        File filePathToPrevCommit = new File(pathToCommit);
        BufferedReader br = new BufferedReader(new FileReader(filePathToPrevCommit));
        StringBuilder contents = new StringBuilder("");
        for (int i = 0; i < 6; i++) {
            if (i == 2) {
                contents.append(generateSha1());
            } else {
                contents.append(br.readLine() + "\n");
            }
        }
        contents.append(br.readLine());
        Files.write(Paths.get(Paths.get("objects").toString(), prevCommit), contents.toString().getBytes());
        br.close();
    }

    public static void main(String[] args) throws Exception {
        Utils.deleteAllTestFile();
    }
}
