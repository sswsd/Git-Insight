import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.html.HTMLDocument;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by wangso on 2015/8/11.
 * PACKAGE_NAME
 * Git Insight
 */
public class LogTable
{

    private Git git;
    private String repositoryPath;
    public static Vector<String> title= new Vector<String>();
    private Vector<Vector> v;

    public LogTable(String repository) throws IOException
    {
        this.repositoryPath = repository;
        git = Git.open(new File(repository));
        v = new Vector<Vector>();
    }
    public TableModel getTableModel(String selectedPath) throws GitAPIException
    {
        String relativePath = selectedPath.substring(this.repositoryPath.length()+1,selectedPath.length());
        relativePath = relativePath.replaceAll("\\\\", "/");
        Iterator ts = git.log().addPath(relativePath).call().iterator();
        return generateTableModel(ts);
    }
    public TableModel getTableModel() throws GitAPIException
    {
        Iterator ts = git.log().call().iterator();
        return generateTableModel(ts);
    }
    private TableModel generateTableModel(Iterator ts)
    {
        v.clear();
        //fullDiffLog = "";
        Vector<Vector> tempV = new Vector<Vector>();
        int i = 0;

        progressBar pb = ThreadTask.pb;
        pb.getMyProgressBar().setString("Abstract commit message...");

        // this is ugly, but we need to know ts size
        List<RevCommit> listFromTs = new LinkedList<>();
        while (ts.hasNext())
        {
            listFromTs.add((RevCommit) ts.next());
        }
        pb.getMyProgressBar().setMaximum(listFromTs.size());
        ts = listFromTs.iterator();
        while (ts.hasNext())
        {
            RevCommit el = (RevCommit)ts.next();

            pb.getMyProgressBar().setValue(i++);

            Vector<String> ve = new Vector<String>();
            ve.add(el.getName());
            ve.add(el.getShortMessage());
            ve.add(el.getAuthorIdent().getName());
            ve.add(el.getCommitterIdent().getWhen().toString());
            Vector<String> tempve = (Vector)ve.clone();
            tempV.addElement(tempve);

            ve.add(el.getFullMessage());
            //fullDiffLog += el.getFullMessage()+"\n" + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";
            v.addElement(ve);
        }
        TableModel tmodel = new DefaultTableModel(tempV,title);
        return tmodel;
    }

    public String getFullDiffLog()
    {
        progressBar pb = ThreadTask.pb;
        String fullDiffLog = "";
        Iterator<Vector> itr = v.iterator();
        pb.getMyProgressBar().setString("Abstract commit log...");
        pb.getMyProgressBar().setMaximum(v.size());
        int i = 0;

        while (itr.hasNext())
        {
            Vector temp = itr.next();
            fullDiffLog += temp.elementAt(0).toString()+"\n";
            fullDiffLog += temp.elementAt(4).toString();
            fullDiffLog += "\n~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n";
            pb.getMyProgressBar().setValue(i++);
        }
        return fullDiffLog;
    }
    public String getFullDiffLog(String commitId)
    {
        String fullDiffLog = "";
        Iterator<Vector> itr = v.iterator();
        while (itr.hasNext())
        {
            Vector temp = itr.next();
            if (temp.elementAt(0).toString().equals(commitId))
            {
                fullDiffLog = temp.elementAt(4).toString();
                return fullDiffLog;
            }
        }
        return fullDiffLog;
    }
    public static Vector getTitle()
    {
        title.add("Commit ID");
        title.add("Commit Message");
        title.add("Author");
        title.add("Time");
        return title;
    }
}
