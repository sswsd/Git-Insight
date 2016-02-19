import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Properties;

public class GitRepository
{
    private String recentRepositoryPath;
    private Properties prop;

    public GitRepository()
    {
        prop = new Properties();
    }

    public String getRecentReposityroPath()
    {
        URL url = this.getClass().getResource("config\\config.properties");
        String popPath = null;
        try
        {
            if (url != null)
                popPath = URLDecoder.decode(url.getFile(), "UTF-8");
            else
                popPath = null;
        } catch (UnsupportedEncodingException e1)
        {
            e1.printStackTrace();
        }

        try
        {
            // read properties file
            InputStream in = new BufferedInputStream(new FileInputStream(popPath));
            prop.load(in);
            Iterator<String> it = prop.stringPropertyNames().iterator();
            while (it.hasNext())
            {
                String key = it.next();
                recentRepositoryPath = prop.getProperty(key);
            }
            in.close();
        }
        catch (Exception e1)
        {
            System.out.println(e1);
        }
        return this.recentRepositoryPath;
    }
    public void updateRepositoryPath(String latestRepositoryPath)
    {
        URL url = this.getClass().getResource("config\\config.properties");
        String popPath = null;
        try
        {
            if (url != null)
                popPath = URLDecoder.decode(url.getFile(), "UTF-8");
            else
                popPath = null;
        } catch (UnsupportedEncodingException e1)
        {
            e1.printStackTrace();
        }

        File targetFile = null;

        if (popPath == null)
        {
            try
            {
                popPath = URLDecoder.decode(MainForm.class.getResource("").getFile(), "UTF-8") + "\\config";
                File targetFolder = new File(popPath);
                if (!targetFolder.exists())
                    targetFolder.mkdirs();
                targetFile = new File(targetFolder, "config.properties");
                targetFile.createNewFile();
            }
            catch (UnsupportedEncodingException e1)
            {
                e1.printStackTrace();
            } catch (IOException e1)
            {
                e1.printStackTrace();
            }
        }
        else
        {
            targetFile = new File(popPath);
        }
        try
        {
            FileOutputStream oFile = new FileOutputStream(targetFile);
            prop.setProperty("Recent Repository", latestRepositoryPath);
            prop.store(oFile,"");
            oFile.close();
        } catch (FileNotFoundException e1)
        {
            e1.printStackTrace();
        } catch (IOException e1)
        {
            e1.printStackTrace();
        }
    }
}
