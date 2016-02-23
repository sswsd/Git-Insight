import javax.swing.*;
import javax.swing.event.TreeModelListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.util.*;

/**
 * Created by wangso on 2015/8/6.
 * PACKAGE_NAME
 * Git Insight
 */
class FileSystemModel implements TreeModel
{
    char fileType = FolderNode.ALL;
    FolderNode theRoot;
    public FileSystemModel(FolderNode fn)
    {
        theRoot = fn;
    }
    @Override
    public Object getRoot()
    {
        return theRoot;
    }

    @Override
    public Object getChild(Object parent, int index)
    {
        return ((FolderNode)parent).getChild(fileType, index);
    }

    @Override
    public int getChildCount(Object parent)
    {
        return ((FolderNode)parent).getChildCount(fileType);
    }

    @Override
    public boolean isLeaf(Object node)
    {
        return ((FolderNode)node).isLeaf(fileType);
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue)
    {

    }

    @Override
    public int getIndexOfChild(Object parent, Object child)
    {
        return ((FolderNode)parent).getIndexOfChild(fileType, child);
    }

    @Override
    public void addTreeModelListener(TreeModelListener l)
    {

    }

    @Override
    public void removeTreeModelListener(TreeModelListener l)
    {

    }
}

class FolderNode
{
    final public static char DIRECTORY = 'D';
    final public static char FILE = 'F';
    final public static char ALL = 'A';
    private FileSystemView fsView;
    private File the1stFile;
    private Vector<File> all = new Vector<File>();
    private Vector<File> folders = new Vector<File>();

    public FolderNode()
    {
        fsView = FileSystemView.getFileSystemView();
        the1stFile = fsView.getHomeDirectory();
        prepareChildren();
    }
    public FolderNode(File file)
    {
        fsView = FileSystemView.getFileSystemView();
        the1stFile = file;
        prepareChildren();
    }
    public Icon getIcon()
    {
        return fsView.getSystemIcon(the1stFile);
    }
    public String toString()
    {
        return fsView.getSystemDisplayName(the1stFile);
    }
    private void prepareChildren()
    {
        File[] files = fsView.getFiles(the1stFile, false);
        List filesList = Arrays.asList(files);

        /*
        Comparator tempC = new Comparator<File>()
        {
            @Override
            public int compare(File f1, File f2)
            {
                if (f1.isDirectory() && f2.isFile())
                {
                    //Collections.sort(Arrays.asList(f1.listFiles()),tempC);
                    return -1;
                }
                if (f1.isFile() && f2.isDirectory()) return 1;
                return f1.getName().compareTo(f2.getName());
            }
        };*/
        /*Collections.sort(filesList, new Comparator<File>(){
            @Override
            public int compare(File f1, File f2)
            {
                if (f1.isDirectory() && f2.isFile())
                {

                    return -1;
                }
                if (f1.isFile() && f2.isDirectory()) return 1;
                return f1.getName().compareTo(f2.getName());
            }
        });*/
        Collections.sort(filesList);
        for (int i=0; i<files.length; i++)
        {
            all.add(files[i]);
            if (files[i].isDirectory() && !files[i].toString().toLowerCase().endsWith(".lnk"))
            {
                folders.add(files[i]);
            }
        }
    }
    public String getNodePath()
    {
        return the1stFile.getAbsolutePath();
    }
    public FolderNode getChild(char fileType, int index)
    {
        FolderNode retFN;
        switch (fileType)
        {
            case DIRECTORY:
                retFN = new FolderNode(folders.get(index));
                break;
            case ALL:
                retFN = new FolderNode(all.get(index));
                break;
            case FILE:
            default:
                retFN = null;
                break;
        }
        return retFN;
    }


    public int getChildCount(char fileType)
    {
        int retCC;
        switch (fileType)
        {
            case DIRECTORY:
                retCC = this.folders.size();
                break;
            case ALL:
                retCC = this.all.size();
                break;
            case FILE:
            default:
                retCC = -1;
                break;
        }
        return retCC;
    }

    public int getIndexOfChild(char fileType, Object child)
    {
        int retVal = -1;
        if (child instanceof FolderNode)
        {
            switch (fileType)
            {
                case DIRECTORY:
                    retVal = this.folders.indexOf(((FolderNode)child).the1stFile);
                    break;
                case ALL:
                    retVal = this.all.indexOf(((FolderNode)child).the1stFile);
                    break;
                case FILE:
                default:
                    retVal = -1;
                    break;
            }
        }
        else
        {
            retVal = -1;
        }
        return retVal;
    }
    public boolean isLeaf(char fileType)
    {
        //return folders.size()== 0;
        return all.size() == 0;
    }
}


/*interface FolderNode {

    final public static char DIRECTORY = 'D';

    final public static char FILE = 'F';

    final public static char ALL = 'A';

    //public Icon getIcon();

    public FolderNode getChild(char fileType, int index);

    public int getChildCount(char fileType);

    public boolean isLeaf(char fileType);

    public int getIndexOfChild(char fileType, Object child);

}*/