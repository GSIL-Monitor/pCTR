package commons.framework.data;

import utils.FileUtils;

import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-13
 * Time: 下午2:48
 */
public abstract class DataSource {
    protected String sourceName;
    protected InputStream inputStream;

    protected void registerInputStream(String fileName)
    {
        this.inputStream = FileUtils.getStreamFromFile(fileName);
    }
    public abstract String readLine();
    public abstract void close();
    public String getSourceName()
    {
        return sourceName;
    }


}
