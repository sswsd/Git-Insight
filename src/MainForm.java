import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RepositoryNotFoundException;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * Created by wangso on 2015/8/6.
 * PACKAGE_NAME
 * Git Insight
 */
public class MainForm
{
    private static JFrame frame;
    public JPanel JPanelMy;

    private JButton buttonRepSel;
    private JTree fileTree;
    private JScrollPane LeftScrollPane;
    private JTextField topJPanelTextFieldRepositoryPath;
    private JPanel TopJPanel;
    private JTextPane gitRepositoryTextPane;
    //private JList list1;
    //private JList list2;
    private JList list3;
    private JPanel panelMid;

    private JTable panelMidTopScrollPaneTableCommitMsg;
    private JTextPane panelMidBottomScrollPaneTextPaneFullLog;
    private JSplitPane splitPaneMid;
    private JScrollPane topScrollPane;
    private JScrollPane bottomScrollPane;

    private JPopupMenu pop = new JPopupMenu();
    private JMenuItem copy = new JMenuItem("Copy");
    private JMenuItem cut = new JMenuItem("Cut");

    LogTable lgt = null;

    private int clickNumber;

    public static void main(String[] args)
    {
        frame = new JFrame("MainForm");
        frame.setContentPane(new MainForm().JPanelMy);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.setLocationRelativeTo(null);

        ImageIcon i = new ImageIcon(MainForm.class.getResource("logo.jpg"));
        frame.setIconImage(i.getImage());

        frame.setVisible(true);
    }

    private void initComponent()
    {
        copy.setAccelerator(KeyStroke.getKeyStroke('C', InputEvent.CTRL_MASK));
        pop.add(copy);
        pop.add(cut);
        copy.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                panelMidBottomScrollPaneTextPaneFullLog.copy();
            }
        });

        JPanelMy.setPreferredSize(new Dimension(500, 500));
        //LeftScrollPane.setPreferredSize(new Dimension(100, 300));
        fileTree.setModel(null);

        TableModel tmodel = new DefaultTableModel(null, LogTable.getTitle());

        panelMidTopScrollPaneTableCommitMsg.setModel(tmodel);
    }

    public MainForm()
    {
        initComponent();

        buttonRepSel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                super.mouseClicked(e);
                // read recent git repository from properties file
                GitRepository gitRepository = new GitRepository();
                String recentRepositoryPath = gitRepository.getRecentReposityroPath();
                JFileChooser jfc = new JFileChooser(recentRepositoryPath);
                jfc.setDialogTitle("Please choose git repository");
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                int returnVal = jfc.showOpenDialog(null);
                if (jfc.APPROVE_OPTION == returnVal)
                {
                    String inputPath = jfc.getSelectedFile().getAbsolutePath();
                    //------------ check git repository
                    try
                    {
                        lgt = new LogTable(inputPath);
                    } catch (IOException e1)
                    {
                        if (e1 instanceof RepositoryNotFoundException)
                        {
                            JOptionPane.showMessageDialog(null, "This is not a Git Repository", "Error", 0);
                            return;
                        }
                    }
                    topJPanelTextFieldRepositoryPath.setText(inputPath);

                    ThreadTask threadTask = new ThreadTask(ThreadTask.taskFlgRepSelect);
                    threadTask.setFileTree(fileTree);
                    threadTask.setLogTable(lgt);
                    threadTask.setTableCommitMsg(panelMidTopScrollPaneTableCommitMsg);
                    threadTask.setTextFieldRepositoryPath(topJPanelTextFieldRepositoryPath);
                    threadTask.setTextPaneFullLog(panelMidBottomScrollPaneTextPaneFullLog);
                    threadTask.start();

                    // Once repository read is successful, store this repository path for further reference
                    gitRepository.updateRepositoryPath(topJPanelTextFieldRepositoryPath.getText());
                }
            }
        });

        fileTree.addTreeSelectionListener(new TreeSelectionListener()
        {
            @Override
            public void valueChanged(TreeSelectionEvent e)
            {
                TreePath selectedPath = fileTree.getSelectionPath();
                if (selectedPath == null) return;

                FolderNode selectedNode = (FolderNode) selectedPath.getLastPathComponent();
                if (lgt != null)
                {
                    ThreadTask threadTask = new ThreadTask(ThreadTask.taskFlgTreeSelectionChange);
                    threadTask.setLogTable(lgt);
                    threadTask.setTableCommitMsg(panelMidTopScrollPaneTableCommitMsg);
                    threadTask.setTextPaneFullLog(panelMidBottomScrollPaneTextPaneFullLog);
                    threadTask.setSelectedNode(selectedNode);
                    threadTask.start();
                } else
                {
                    JOptionPane.showMessageDialog(null, selectedNode.getNodePath() + "\n" + lgt.toString());
                }
            }
        });

        panelMidTopScrollPaneTableCommitMsg.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                super.mouseClicked(e);
                ThreadTask threadTask = new ThreadTask(ThreadTask.taskFlgCommitMsgChange);
                threadTask.setLogTable(lgt);
                threadTask.setTableCommitMsg(panelMidTopScrollPaneTableCommitMsg);
                threadTask.setTextPaneFullLog(panelMidBottomScrollPaneTextPaneFullLog);
                threadTask.start();
            }
        });

        panelMidTopScrollPaneTableCommitMsg.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased(KeyEvent e)
            {
                super.keyReleased(e);
                if ((e.getKeyCode() == 38) ||   // up
                    (e.getKeyCode() == 40) ||   // down
                    (e.getKeyCode() == 33) ||   // pg up
                    (e.getKeyCode() == 34) ||   // pg dn
                    (e.getKeyCode() == 36) ||   // home
                    (e.getKeyCode() == 35))     // end
                {
                    ThreadTask threadTask = new ThreadTask(ThreadTask.taskFlgCommitMsgChange);
                    threadTask.setLogTable(lgt);
                    threadTask.setTableCommitMsg(panelMidTopScrollPaneTableCommitMsg);
                    threadTask.setTextPaneFullLog(panelMidBottomScrollPaneTextPaneFullLog);
                    threadTask.start();
                }
            }
        });
        panelMidBottomScrollPaneTextPaneFullLog.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent mouseEvent)
            {
                super.mouseClicked(mouseEvent);
                if (mouseEvent.getButton() == 3)
                {
                    copy.setEnabled(isCanCopy());
                    pop.show(panelMidBottomScrollPaneTextPaneFullLog, mouseEvent.getX(),mouseEvent.getY());
                }
            }
        });
    }

    public boolean isCanCopy() {
        boolean b = false;
        int start = panelMidBottomScrollPaneTextPaneFullLog.getSelectionStart();
        int end = panelMidBottomScrollPaneTextPaneFullLog.getSelectionEnd();
        if (start != end)
            b = true;
        return b;
    }
}
