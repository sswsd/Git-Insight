import javax.swing.*;
import java.awt.*;

/**
 * Created by danwang on 15/12/6.
 */
public class progressBar{
    private JProgressBar myProgressBar;
    private JPanel panel1;
    private static JFrame frame;

    public JProgressBar getMyProgressBar()
    {
        return myProgressBar;
    }
    public progressBar()
    {
        frame = new JFrame("progressBar");
        frame.setContentPane(panel1);
        panel1.setPreferredSize(new Dimension(250, 30));

        frame.pack();
        frame.setLocationRelativeTo(null);
    }
    public void show()
    {
        frame.setVisible(true);
    }

    public void closeFrame()
    {
        frame.dispose();
    }
}
