package application.adCTR.evaluation.creative;

import org.apache.hadoop.util.hash.Hash;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-15
 * Time: 上午10:16
 */
public class CreativeInventory {
    private static CreativeInventory instance = null;
    private CreativeInventory(){
        creativeStock = new HashMap<Integer, Creative>();
    }
    public static CreativeInventory getInstance()
    {
        if(instance == null)
        {
            instance = new CreativeInventory();
        }
        return instance;
    }

    private HashMap<Integer, Creative> creativeStock = null;

    public int loadCreativeFromDB()
    {
        return -1;
    }

    public Creative getCreativeById(int creativeID)
    {
        return creativeStock.containsKey(creativeID)?creativeStock.get(creativeID):null;
    }

    /**
     * select creative candidates by context, it should be the job of atm in online environment
     * @param context
     * @return
     */
    public Creative getCreativeByContext(String context)
    {
        //TODO finish this method
        return null;
    }
}
