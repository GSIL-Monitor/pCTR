package utils.database;

import utils.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-25
 * Time: 下午2:39
 */
public class DataBaseConfig {
    private DataBaseNames dataBaseName = null;
    private String dataBaseType = null;
    private String port = null;
    private String sid = null;
    private String host = null;
    private String userName = null;
    private String password = null;

    public DataBaseConfig(DataBaseNames dataBaseName) {
        this.dataBaseName = dataBaseName;
    }

    public void loadConfigFromFile(String configFile) throws IOException
    {
        Properties properties = new Properties();
        InputStream is = FileUtils.getStreamFromFile(configFile);
        properties.load(is);

        this.dataBaseType = properties.getProperty("database_type");
        this.host = properties.getProperty("host");
        this.port = properties.getProperty("port");
        this.sid = properties.getProperty("sid");
        this.userName = properties.getProperty("user");
        this.password = properties.getProperty("password");

        is.close();
    }

    public DataBaseNames getDataBaseName(){
        return dataBaseName;
    }

    public String getDataBaseType() {
        return dataBaseType;
    }

    public String getPort() {
        return port;
    }

    public String getSid() {
        return sid;
    }

    public String getHost() {
        return host;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }
}
