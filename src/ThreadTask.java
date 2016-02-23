import org.eclipse.jgit.api.errors.GitAPIException;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;

/**
 * Created by wangso on 2015/12/7.
 * PACKAGE_NAME
 * Git Insight
 */
public class ThreadTask extends Thread
{
    public static final int taskFlgRepSelect = 1;
    public static final int taskFlgTreeSelectionChange = 2;
    public static final int taskFlgCommitMsgChange = 3;

    private int taskFlg;
    private LogTable lgt;
    private JTable panelMidTopScrollPaneTableCommitMsg;
    private JTextPane panelMidBottomScrollPaneTextPaneFullLog;
    private JTextField topJPanelTextFieldRepositoryPath;
    private JTree fileTree;
    private FolderNode selectedNode;

    public static progressBar pb;

    public ThreadTask(int taskFlg)
    {
        //JProgressBar pb = new JProgressBar();
        pb = new progressBar();
        this.taskFlg = taskFlg;
    }
    public void setLogTable(LogTable lgt)
    {
        this.lgt = lgt;
    }
    public void setTableCommitMsg(JTable jt)
    {
        this.panelMidTopScrollPaneTableCommitMsg = jt;
    }
    public void setTextPaneFullLog(JTextPane jtp)
    {
        this.panelMidBottomScrollPaneTextPaneFullLog = jtp;
    }
    public void setTextFieldRepositoryPath(JTextField jtf)
    {
        this.topJPanelTextFieldRepositoryPath = jtf;
    }
    public void setFileTree(JTree jtree)
    {
        this.fileTree = jtree;
    }
    public void setSelectedNode(FolderNode selectedNode)
    {
        this.selectedNode = selectedNode;
    }

    public void cleanTaskFlg()
    {
        this.taskFlg = 0;
    }
    public void run()
    {
        switch (this.taskFlg)
        {
            case taskFlgRepSelect:
            {
                pb.show();
                pb.getMyProgressBar().setString("Fetch git repository...");
                try
                {
                    TableModel tablem = lgt.getTableModel();
                    panelMidTopScrollPaneTableCommitMsg.setModel(tablem);
                    panelMidBottomScrollPaneTextPaneFullLog.setText(lgt.getFullDiffLog());

                } catch (GitAPIException e1)
                {
                    e1.printStackTrace();
                }

                String inputPath = topJPanelTextFieldRepositoryPath.getText();
                File inputPathF = new File(inputPath);
                FolderNode fn = new FolderNode(inputPathF);
                FileSystemModel fsm = new FileSystemModel(fn);
                fileTree.setModel(fsm);
                pb.closeFrame();
            }
            break;
            case taskFlgTreeSelectionChange:
            {
                try
                {
                    TableModel tablem = lgt.getTableModel(selectedNode.getNodePath());
                    panelMidTopScrollPaneTableCommitMsg.setModel(tablem);
                    panelMidBottomScrollPaneTextPaneFullLog.setText(lgt.getFullDiffLog());
                } catch (GitAPIException e1)
                {
                    e1.printStackTrace();
                }
            }
            break;

            case taskFlgCommitMsgChange:
            {
                int row = panelMidTopScrollPaneTableCommitMsg.getSelectedRow();
                String value = panelMidTopScrollPaneTableCommitMsg.getValueAt(row, 0).toString().trim();
                panelMidBottomScrollPaneTextPaneFullLog.setText(lgt.getFullDiffLog(value));
            }
            break;
            default:
                break;

        }
        cleanTaskFlg();
    }
}
