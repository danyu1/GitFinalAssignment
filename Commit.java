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
    String prevCommit = null;
    String nextCommit = null;
    String author;
    String date;
    String summary;

    StringBuilder toPrint;
    static String pathToWorkSpace = "C:\\Users\\danie\\OneDrive\\Desktop\\Topics Repos\\GitFinalAssignment";
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
        toPrint.append(this.nextCommit + "\n");
        toPrint.append(this.author + "\n");
        toPrint.append(this.date + "\n");
        toPrint.append(summary);
        this.pathToCommit = Paths.get(Paths.get("objects").toString(), prevCommit).toString();
        // update the next line of the previous commit
        // updatePrevNextCommit();
        save();
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
        save();
    }

    public void save() throws Exception {
        // Create the commit file in the 'objects' folder
        Path commitPath = Paths.get(Paths.get("objects").toString(), generateSha1());
        Files.write(commitPath, toPrint.toString().getBytes());
        // write to the commit file
        Files.write(Paths.get("commit"), toPrint.toString().getBytes());
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
        this.tree.generateBlob();
        this.treeHash = tree.getSha1();
        return this.treeHash;
    }

    // just in case typa method
    public void setNextCommit(Commit commit) throws Exception {
        this.nextCommit = commit.getTreeHash();
        toPrint = new StringBuilder();
        toPrint.append(treeHash + "\n");
        toPrint.append(this.prevCommit + "\n");
        toPrint.append(this.nextCommit + "\n");
        toPrint.append(this.author + "\n");
        toPrint.append(this.date + "\n");
        toPrint.append(summary);
        save();
    }

    public void addToTree(String fileName) throws Exception {
        this.tree.add(fileName);
        // generate new hash for the new tree
        this.tree.generateBlob();
        this.treeHash = tree.getSha1();
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
        save();
    }

    // read the parentCommit that has the updated "next" value and update this
    // objects next value
    public void updatePrevNextCommit() throws Exception {
        File filePathToPrevCommit = new File(pathToCommit);
        BufferedReader br = new BufferedReader(new FileReader(filePathToPrevCommit));
        StringBuilder contents = new StringBuilder("");
        for (int i = 0; i < 5; i++) {
            if (i == 2) {
                contents.append(generateSha1() + "\n");
            }
            contents.append(br.readLine() + "\n");
        }
        contents.append(br.readLine());
        Files.write(Paths.get(Paths.get("objects").toString(), prevCommit), contents.toString().getBytes());
        br.close();
    }

    public static void main(String[] args) throws Exception {
        Commit c1 = new Commit("paco", "initial commit");
        Commit c2 = new Commit(c1.generateSha1(), "paco", "second commit");
        Commit c3 = new Commit(c2.generateSha1(), "paco", "final commit");
        c1.addToTree("test.txt");
        c2.addToTree("test_input.txt");
        c3.addToTree("input.txt");
        System.out.println(c1.getDate());
    }
}
