package application.adCTR.data;

import commons.framework.data.DataInstance;
import commons.framework.data.ISourceHandler;
import utils.NumericalUtils;
import utils.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-13
 * Time: 下午3:33
 */
public class CTRSourceHandler implements ISourceHandler{
    private final int numOfFields = 28;
    @Override
    public boolean handleSource(String sourceContent, DataInstance dataInstance) {
        try{
            String[] contents = sourceContent.split("\t");
            if(contents.length != numOfFields)
            {
                System.out.println("numOfFields not correct");
                return false;
            }
            CTRDataInstance localInstance = (CTRDataInstance)dataInstance;
            setupDataInstance(localInstance, contents);
            if(localInstance.getTargetValue() != Double.MIN_VALUE)// a valid targetValue;
            {
                return true;
            }
            System.out.println("target value not correct");
            return false;
        }catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    private void setupDataInstance(CTRDataInstance dataInstance, String[] fields)
    {
        dataInstance.setVersion(NumericalUtils.toInteger(fields[0]));
        dataInstance.setTimeStamp(NumericalUtils.toLong(fields[1]));
        dataInstance.setIp(fields[2]);
        dataInstance.setCastId(NumericalUtils.toInteger(fields[3]));
        dataInstance.setCreativeId(NumericalUtils.toInteger(fields[4]));
        dataInstance.setCategory(fields[5]);
        dataInstance.setSubCategory(fields[6]);
        dataInstance.setVideoId(fields[7]);
        dataInstance.setUserId(fields[8]);
        //Pl is dispatched
        dataInstance.setCookie(fields[10]);
        dataInstance.setAdLength(NumericalUtils.toInteger(fields[11]));
        dataInstance.setLong(NumericalUtils.isTrue(fields[12]));
        dataInstance.setProgramId(fields[13]);
        dataInstance.setTotalDown(fields[14]);
        dataInstance.setVideoLength(NumericalUtils.toDouble(fields[15]));
        dataInstance.setAdPosition(NumericalUtils.toInteger(fields[16]));
        dataInstance.setSessionId(fields[17]);
        dataInstance.setCopyright(NumericalUtils.isTrue(fields[18]));
        dataInstance.setOrderId(fields[19]);
        dataInstance.setTvb(NumericalUtils.isTrue(fields[20]));
        dataInstance.setProductType(NumericalUtils.toInteger(fields[21]));
        dataInstance.setAppVersion(fields[22]);
        dataInstance.setDisplayQuality(fields[23]);
        dataInstance.setDeviceType(NumericalUtils.toInteger(fields[24]));
        dataInstance.setOsType(NumericalUtils.toInteger(fields[25]));
        dataInstance.setClientType(fields[26]);
        //dataInstance.setSdkId(fields[27]);
        //ignore reserved fields
        dataInstance.setTargetValue(NumericalUtils.toDouble(fields[27]));
    }
}
