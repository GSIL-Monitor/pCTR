package application.adCTR.sample;

import utils.FileUtils;
import utils.NumericalUtils;

import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-19
 * Time: 下午2:43
 */
public class CTRSampleConfig {
    private static CTRSampleConfig instance = null;
    private CTRSampleConfig(){}

    public static CTRSampleConfig getInstance()
    {
        if(instance == null)
        {
            instance = new CTRSampleConfig();
        }
        return instance;
    }

    private boolean castIDHandlerSwitch = false;
    private boolean categoryHandlerSwitch = false;
    private boolean clientTypeHandlerSwitch = false;
    private boolean creativeChannelHandlerSwitch = false;
    private boolean creativeIDHandlerSwitch = false;
    private boolean deviceTypeHandlerSwitch = false;
    private boolean osTypeHandlerSwitch = false;
    private boolean productTypeHandlerSwitch = false;
    private boolean ipHandlerSwitch = false;
    private boolean appVersionHandlerSwitch = false;

    private final String conf = "conf/CTRControl.properties";

    public void loadConfig()
    {
        try{
            Properties properties = new Properties();
            properties.load(FileUtils.getStreamFromFile(conf));
            this.setParameters(properties);
        }catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("Fail to Load Config " + conf);
            System.exit(-1);
        }

        System.out.println("Config Loaded...");
    }

    private void setParameters(Properties properties) throws Exception
    {
        this.castIDHandlerSwitch = NumericalUtils.toInteger(properties.getProperty("CastIDHandler")) > 0;
        this.categoryHandlerSwitch = NumericalUtils.toInteger(properties.getProperty("CategoryHandler")) > 0;
        this.clientTypeHandlerSwitch = NumericalUtils.toInteger(properties.getProperty("ClientTypeHandler")) > 0;
        this.creativeChannelHandlerSwitch = NumericalUtils.toInteger(properties.getProperty("CreativeChannelHandler")) > 0;
        this.creativeIDHandlerSwitch = NumericalUtils.toInteger(properties.getProperty("CreativeIDHandler")) > 0;
        this.deviceTypeHandlerSwitch = NumericalUtils.toInteger(properties.getProperty("DeviceTypeHandler")) > 0;
        this.osTypeHandlerSwitch = NumericalUtils.toInteger(properties.getProperty("OsTypeHandler")) > 0;
        this.productTypeHandlerSwitch = NumericalUtils.toInteger(properties.getProperty("ProductTypeHandler")) > 0;
        this.ipHandlerSwitch = NumericalUtils.toInteger(properties.getProperty("IpHandler")) > 0;
        this.appVersionHandlerSwitch = NumericalUtils.toInteger(properties.getProperty("AppVersionHandler")) > 0;
    }

    public boolean isCastIDHandlerSwitch() {
        return castIDHandlerSwitch;
    }

    public boolean isCategoryHandlerSwitch() {
        return categoryHandlerSwitch;
    }

    public boolean isClientTypeHandlerSwitch() {
        return clientTypeHandlerSwitch;
    }

    public boolean isCreativeChannelHandlerSwitch() {
        return creativeChannelHandlerSwitch;
    }

    public boolean isCreativeIDHandlerSwitch() {
        return creativeIDHandlerSwitch;
    }

    public boolean isDeviceTypeHandlerSwitch() {
        return deviceTypeHandlerSwitch;
    }

    public boolean isOsTypeHandlerSwitch() {
        return osTypeHandlerSwitch;
    }

    public boolean isProductTypeHandlerSwitch() {
        return productTypeHandlerSwitch;
    }

    public boolean isIpHandlerSwitch()
    {
        return ipHandlerSwitch;
    }

    public boolean isAppVersionHandler()
    {
        return appVersionHandlerSwitch;
    }
}
