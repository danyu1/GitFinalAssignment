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
    String treeHash = null;
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
        this.toPrint = new StringBuilder("");
        createTree();
        this.prevCommit = shaOfPrevCommit;
        if (nextCommit == null)
            nextCommit = "";

        this.author = author;
        this.date = getDate();
        this.summary = summary;

        toPrint.append(this.treeHash + "\n" + this.prevCommit + "\n\n" + this.nextCommit + "\n" + this.author + "\n"
                + this.date + "\n" + this.summary);
        save();

        // add updated commit contents in a stringbuilder
        File fileToPrevCommit = new File(pathToWorkSpace + "\\objects\\" + shaOfPrevCommit);
        StringBuilder tempSB = new StringBuilder("");
        BufferedReader br = new BufferedReader(new FileReader(fileToPrevCommit));
        for (int i = 0; i < 5; i++) {
            if (i != 2)
                tempSB.append(br.readLine() + "\n");
            else {
                br.readLine();
                tempSB.append(generateSha1() + "\n");
            }
        }
        tempSB.append(br.readLine());
        br.close();

        // update the second most recently created commits "next" value
        PrintWriter pw = new PrintWriter(pathToWorkSpace + "\\objects\\" + shaOfPrevCommit);
        pw.print(tempSB.toString());
        pw.close();
        pathToCommit = pathToWorkSpace + "\\objects\\" + generateSha1();
    }

    // constructor for the first commit with no parent or next
    public Commit(String author, String summary) throws Exception {
        this.toPrint = new StringBuilder("");
        createTree();
        if (prevCommit == null)
            prevCommit = "";
        if (nextCommit == null)
            nextCommit = "";

        this.author = author;
        this.date = getDate();
        this.summary = summary;

        // toPrint will be printed out to a commit file in the objects folder
        toPrint.append(this.treeHash + "\n" + this.prevCommit + "\n" + this.nextCommit + "\n" + this.author + "\n"
                + this.date + "\n" + this.summary);
        save();
        pathToCommit = pathToWorkSpace + "\\objects\\" + generateSha1();
    }

    public void save() throws Exception {
        // Create the commit file in the 'objects' folder
        Path commitPath = Paths.get(pathToWorkSpace + "\\objects", generateSha1());
        Files.write(commitPath, toPrint.toString().getBytes());
        Files.write(Paths.get("commit"), toPrint.toString().getBytes());
    }

    public String generateSha1() throws Exception {
        StringBuilder forSHA = new StringBuilder("");
        // add all the file contents except for the next commit
        forSHA.append(tree.returnAllEntries() + "\n" + this.prevCommit + "\n" + this.author + "\n" + this.date + "\n"
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

    public void addToTree(String fileName) throws Exception {
        this.tree.add(fileName);
        // generate new hash for the new tree
        this.tree.generateBlob();
        this.treeHash = tree.getSha1();
        // must update toPrint stringbuilder
        updateNextCommit();
        this.toPrint = new StringBuilder("");
        toPrint.append(this.treeHash + "\n" + this.prevCommit + "\n" + this.nextCommit + "\n" + this.author + "\n"
                + this.date + "\n" + this.summary);

    }

    // read the parentCommit that has the updated "next" value and update this
    // objects next value
    public void updateNextCommit() throws Exception {
        File filePathToCommit = new File(pathToCommit);
        BufferedReader br = new BufferedReader(new FileReader(filePathToCommit));
        br.readLine();
        br.readLine();
        this.nextCommit = br.readLine();
        br.close();
    }

    public static void main(String[] args) throws Exception {
        Commit c1 = new Commit("paco", "initial commit");
        Commit c2 = new Commit(c1.generateSha1(), "paco", "second ever commit");
        c1.addToTree("test.txt");
        c1.save();
        System.out.println(c1.getDate());
        System.out.println(c2.getDate());
    }
}
