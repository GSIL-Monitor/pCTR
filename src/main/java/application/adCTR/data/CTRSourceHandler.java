package application.adCTR.data;

import commons.framework.data.DataInstance;
import commons.framework.data.ISourceHandler;
import utils.StringUtils;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-13
 * Time: 下午3:33
 */
public class CTRSourceHandler implements ISourceHandler{
    private final int numOfFields = 31;
    @Override
    public boolean handleSource(String sourceContent, DataInstance dataInstance) {
        try{
            String[] contents = StringUtils.splitStr(sourceContent,'\t');
            if(contents.length != numOfFields)
            {
                return false;
            }
            CTRDataInstance instance = (CTRDataInstance)dataInstance;
            //TODO to finish, it is only a simple example
            instance.setDeviceType(Integer.parseInt(contents[24]));
            return true;
        }catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
