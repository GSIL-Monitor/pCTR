package application.adCTR.data;

import commons.framework.data.DataInstance;
import commons.framework.data.DataInstanceMaker;
import commons.framework.data.ISourceHandler;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-13
 * Time: 下午3:09
 */
public class CTRDataInstanceMaker extends DataInstanceMaker {
    private static CTRDataInstanceMaker instance = null;
    private CTRDataInstanceMaker(){
        this.sourceHandlerArrayList = new ArrayList<ISourceHandler>();
    }

    public static CTRDataInstanceMaker getInstance()
    {
        if(instance == null)
        {
            instance = new CTRDataInstanceMaker();
        }
        return instance;
    }

    public void registerSourceHandler(CTRSourceHandler ctrSourceHandler)
    {
        super.registerSourceHandler(ctrSourceHandler);
    }

    @Override
    public void fillDataInstance(String sourceContent, DataInstance dataInstance) {
        if(dataInstance instanceof  CTRDataInstance)
        {
            for(int i = 0, len = sourceHandlerArrayList.size(); i < len; i++)
            {
                dataInstance.setCorrectlyFilled(sourceHandlerArrayList.get(i).handleSource(sourceContent, dataInstance));
            }
        }
    }
}
