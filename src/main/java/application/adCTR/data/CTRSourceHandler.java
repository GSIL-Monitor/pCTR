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
    private final int numOfFields = 34;
    @Override
    public boolean handleSource(String sourceContent, DataInstance dataInstance) {
        try{
            String[] contents = sourceContent.split("\t");
            if(contents.length != numOfFields)
            {
                System.out.println("numOfFields not correct, now the fields number is " + contents.length);
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
        dataInstance.setTargetValue(NumericalUtils.toDouble(fields[0]));
        dataInstance.setVersion(NumericalUtils.toInteger(fields[1]));
        dataInstance.setTimeStamp(NumericalUtils.toLong(fields[2]));
        dataInstance.setIp(fields[3]);
        dataInstance.setCastId(NumericalUtils.toInteger(fields[4]));
        dataInstance.setCreativeId(NumericalUtils.toInteger(fields[5]));
        dataInstance.setCategory(fields[6]);
        dataInstance.setSubCategory(fields[7]);
        dataInstance.setVideoId(fields[8]);
        dataInstance.setUserId(fields[9]);
        //Pl is dispatched
        dataInstance.setCookie(fields[11]);
        dataInstance.setAdLength(NumericalUtils.toInteger(fields[12]));
        dataInstance.setLong(NumericalUtils.isTrue(fields[13]));
        dataInstance.setProgramId(fields[14]);
        dataInstance.setTotalDown(fields[15]);
        dataInstance.setVideoLength(NumericalUtils.toDouble(fields[16]));
        dataInstance.setAdPosition(NumericalUtils.toInteger(fields[17]));
        dataInstance.setSessionId(fields[18]);
        dataInstance.setCopyright(NumericalUtils.isTrue(fields[19]));
        dataInstance.setOrderId(fields[20]);
        dataInstance.setTvb(NumericalUtils.isTrue(fields[21]));
        dataInstance.setProductType(NumericalUtils.toInteger(fields[22]));
        dataInstance.setAppVersion(fields[23]);
        dataInstance.setDisplayQuality(fields[24]);
        dataInstance.setDeviceType(NumericalUtils.toInteger(fields[25]));
        dataInstance.setOsType(NumericalUtils.toInteger(fields[26]));
        dataInstance.setClientType(fields[27]);
        dataInstance.setSdkId(fields[28]);
        //ignore reserved fields
        dataInstance.setCityId(NumericalUtils.toInteger(fields[31]));
        dataInstance.setFullScreen(StringUtils.isTrue(fields[32]));
        dataInstance.setKeywords(fields[33]);
    }
}
