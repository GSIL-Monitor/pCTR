package utils.database;

import com.alibaba.druid.pool.DruidDataSource;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-25
 * Time: 下午2:41
 */
public class DataBaseManager {

    private class DatabaseConfigFiles{
        public static final String AD_Config = "conf/databases/AdConfig.properties";
    }

    private static DataBaseManager instance = null;
    private HashMap<DataBaseNames, DruidDataSource> databasePool = null;

    private DataBaseManager(){
        databasePool = new HashMap<DataBaseNames, DruidDataSource>();
    }

    public static DataBaseManager getInstance()
    {
        if(instance == null)
        {
            instance = new DataBaseManager();
        }
        return instance;
    }

    public void init()
    {
        int numberOfDatabases = registerDatabase();
        if(numberOfDatabases > 0)
        {
            System.out.println("Register " + numberOfDatabases + " databases in total");
        }
        else
        {
            System.out.println("Register databases fail");
            System.exit(-1);
        }
    }

    /**
     * get connection from druid data source pool
     * @param dataBaseName
     * @return
     */
    public Connection getConnectionFromPool(DataBaseNames dataBaseName)
    {
        if(!databasePool.containsKey(dataBaseName))
        {
            System.out.println("database" + dataBaseName.name() + " not register or not support");
            return null;
        }
        try
        {
            return databasePool.get(dataBaseName).getConnection();
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public void releaseAllConnection()
    {
        if(databasePool != null)
        {
            for (Map.Entry<DataBaseNames, DruidDataSource> e : databasePool.entrySet()) {
                releaseConnection(e.getValue());
            }
            databasePool.clear();
        }
        databasePool = null;
    }

    private int registerDatabase()
    {
        int numOfDatabases = 0;
        //register ad
        if(registerAdDataBase() < 0)
        {
            return -1;
        }
        numOfDatabases++;

        return numOfDatabases;
    }

    private int registerAdDataBase()
    {
        DataBaseConfig config = new DataBaseConfig(DataBaseNames.AD);
        try{
            config.loadConfigFromFile(DatabaseConfigFiles.AD_Config);
            DruidDataSource dds = getDruidDataSourceInstanceFromConfig(config);
            if(dds == null)//fail
            {
                return -1;
            }
            databasePool.put(DataBaseNames.AD, dds);
        }catch (Exception e)
        {
            System.out.println("loading Ad database config fail");
            return -1;
        }
        return 0;
    }

    private DruidDataSource getDruidDataSourceInstanceFromConfig(DataBaseConfig config)
    {
        if(config.getDataBaseType().toLowerCase().equals("mysql"))
        {
            return ConstructDruidDataSourceFromMysql(config);
        }
        else if(config.getDataBaseType().toLowerCase().equals("oracle"))
        {
            return ConstructDruidDataSourceFromOracle(config);
        }
        else
        {
            System.out.println("unsupported database type as " + config.getDataBaseType() + " in config for " + config.getDataBaseName().name());
            return null;
        }
    }

    private DruidDataSource ConstructDruidDataSourceFromOracle(DataBaseConfig config)
    {
        DruidDataSource dds = null;
        try
        {
            dds = new DruidDataSource();
            dds.setDriverClassName("oracle.jdbc.driver.OracleDriver");
            dds.setInitialSize(3);
            dds.setMaxActive(10);
            dds.setUsername(config.getUserName());
            dds.setPassword(config.getPassword());
            String url = "jdbc:oracle:thin:@" + config.getHost() + ":" + config.getPort() + ":" + config.getSid();
            dds.setUrl(url);

            Connection conn = dds.getConnection();
            conn.close();
        }catch(Exception e)
        {
            System.out.println("Construct connection pool for " + config.getDataBaseName().name() + " fail");
            if(null != dds)
            {
                dds.close();
            }
            dds = null;
        }
        return dds;
    }

    private DruidDataSource ConstructDruidDataSourceFromMysql(DataBaseConfig config)
    {
        DruidDataSource dds = null;
        try
        {
            dds = new DruidDataSource();
            dds.setDriverClassName("com.mysql.jdbc.Driver");
            dds.setInitialSize(1);
            dds.setMaxActive(3);
            dds.setUsername(config.getUserName());
            dds.setPassword(config.getPassword());
            String url = "jdbc:mysql://"+ config.getHost() + ":" + config.getPort() + "/" + config.getSid();
            dds.setUrl(url);

            Connection conn = dds.getConnection();
            conn.close();
        }catch(Exception e)
        {
            System.out.println("Construct connection pool for " + config.getDataBaseName().name() + " fail");
            if(null != dds)
            {
                dds.close();
            }
            dds = null;
        }
        return dds;
    }

    private void releaseConnection(DruidDataSource dds)
    {
        if(dds != null)
        {
            dds.close();
        }
    }
}
