package commons.framework.data;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-13
 * Time: 下午3:01
 */
public abstract class DataInstanceMaker {
    protected ArrayList<ISourceHandler> sourceHandlerArrayList;

    protected void registerSourceHandler(ISourceHandler handler)
    {
        this.sourceHandlerArrayList.add(handler);
    }
    public abstract void fillDataInstance(String sourceContent, DataInstance dataInstance);
}
