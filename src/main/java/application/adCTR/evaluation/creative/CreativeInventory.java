package application.adCTR.evaluation.creative;

import utils.NumericalUtils;
import utils.database.DataBaseManager;
import utils.database.DataBaseNames;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

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
    private HashMap<Integer, HashSet<Integer>> provinceToCitySet = null;
    private HashSet<Integer> nationalSet = null;
    private HashMap<String, Creative[]> contextToCreativeArrayMap = null;

    public void init(String datetime)
    {
        loadProvinceFromDB();
        int numOfCreative = loadCreativeFromDB(datetime);
        if(numOfCreative > 0)
        {
            System.out.println("Creative inventory loaded, there are " + numOfCreative + " creative in stock");
        }
        else
        {
            System.out.println("Init creative fail");
            System.exit(-1);
        }
    }

    private void loadProvinceFromDB()
    {
        try{
            Connection connection = DataBaseManager.getInstance().getConnectionFromPool(DataBaseNames.AD);
            String sql = "select id, province_id from city_name";
            int fetchSize = 1024;
            PreparedStatement preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setFetchSize(fetchSize);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs == null) {
                System.out.println("executing sql fail");
                System.exit(-1);
            }
            this.provinceToCitySet = new HashMap<Integer, HashSet<Integer>>();
            this.nationalSet = new HashSet<Integer>();
            while(rs.next())
            {
                int cityId = rs.getInt(1);
                int provinceId = rs.getInt(2);
                HashSet<Integer> set = provinceToCitySet.containsKey(provinceId) ? provinceToCitySet.get(provinceId) : new HashSet<Integer>();
                set.add(cityId);

                provinceToCitySet.put(provinceId, set);
                nationalSet.add(cityId);
            }

            preparedStatement.close();
            rs.close();
            connection.close();

            System.out.println("load province and city done, there are " + provinceToCitySet.size() + " provinces and " + nationalSet.size() + " cities");
        }catch (Exception e)
        {
            System.out.println("load province information fail");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private int loadCreativeFromDB(String datetime)
    {
        try{
            //get connection from ad
            Connection connection = DataBaseManager.getInstance().getConnectionFromPool(DataBaseNames.AD);
            //fill creative stock
            String sql = String.format("select t3.idea_id idea_id, t3.cast_id cast_id, t3.province province, " +
                    "t3.city city, t4.channel_id channel, t4.sub_channel_id subchannel " +
                    "from(select t1.idea_id idea_id, t1.cast_id cast_id, t2.province_id province, t2.city_id city, " +
                    "t1.config_id config_id from(select a.id idea_id, a.cast_id cast_id, " +
                    "b.order_id order_id, b.product_type type, c.cpm cpm, d.id config_id " +
                    "from idea a, ad_cast b, idea_cpm c, vp_config d where a.id = c.idea_id and a.cast_id = b.id " +
                    "and b.product_type = 100 and b.isVidLimit = -1 and c.target_date = '%s' " +
                    "and b.order_id = d.order_id) t1 left join vp_city t2 on t1.config_id = t2.config_id) t3 " +
                    "left join vp_channel t4 on t3.config_id = t4.config_id", datetime);
            System.out.printf("Sql = [%s]\n", sql);
            int fetchSize = 1024;
            PreparedStatement preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setFetchSize(fetchSize);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs == null) {
                System.out.println("executing sql fail");
                System.exit(-1);
            }

            this.creativeStock = new HashMap<Integer, Creative>();
            while(rs.next())
            {
                int creativeId = rs.getInt(1);
                int castId = rs.getInt(2);
                int province = NumericalUtils.toInteger(rs.getString(3));
                int city = NumericalUtils.toInteger(rs.getString(4));
                String channel = rs.getString(5);
                String subChannel = rs.getString(6);

                Creative creative = creativeStock.containsKey(creativeId) ? creativeStock.get(creativeId) : new Creative(creativeId, castId);
                //deal with target cities
                if(province != Integer.MIN_VALUE)//has province
                {
                    HashSet<Integer> citySet = provinceToCitySet.containsKey(province) ? provinceToCitySet.get(province) : new HashSet<Integer>();
                    creative.addTargetCities(citySet);
                }
                if(city != Integer.MIN_VALUE)
                {
                    creative.addTargetCity(city);
                }
                if(city == Integer.MIN_VALUE && province == Integer.MIN_VALUE)//national
                {
                    creative.addTargetCities(nationalSet);
                }

                //deal with target channels
                if(!channel.equals("-1"))
                {
                    creative.addTargetChannel(channel);
                }
                if(!subChannel.equals("-1"))
                {
                    creative.addTargetChannel(subChannel);
                }

                //put it back
                creativeStock.put(creativeId, creative);
            }

            preparedStatement.close();
            rs.close();
            connection.close();
        }catch (Exception e)
        {
            e.printStackTrace();
            return -1;
        }

        //process target city and channel
        createTargetConditions();
        return creativeStock.size();
    }

    private void createTargetConditions()
    {
        this.contextToCreativeArrayMap = new HashMap<String, Creative[]>();
        HashMap<String, HashSet<Creative>> contextToCreativeMap = new HashMap<String, HashSet<Creative>>();
        Iterator<Creative> creativeIterator = creativeStock.values().iterator();
        while(creativeIterator.hasNext())
        {
            Creative creative = creativeIterator.next();
            ArrayList<String> contextList = creative.getTargetContext();

            for(int i = 0, len = contextList.size(); i < len; i++)
            {
                String context = contextList.get(i);
                HashSet<Creative>  creativeHashSet = contextToCreativeMap.containsKey(context) ? contextToCreativeMap.get(context) : new HashSet<Creative>();
                creativeHashSet.add(creative);

                contextToCreativeMap.put(context, creativeHashSet);
            }
        }

        fillContextToCreativeArrayMap(contextToCreativeMap);
        System.out.println("There are " + contextToCreativeArrayMap.size() + " context");
    }

    private void fillContextToCreativeArrayMap(HashMap<String, HashSet<Creative>> contextToCreativeMap)
    {
        Iterator<Map.Entry<String, HashSet<Creative>>> iterator = contextToCreativeMap.entrySet().iterator();
        while(iterator.hasNext())
        {
            Map.Entry<String, HashSet<Creative>> entry = iterator.next();
            Creative[] creativeArray = convertHashSetToArray(entry.getValue());
            this.contextToCreativeArrayMap.put(entry.getKey(), creativeArray);
        }
    }

    private Creative[] convertHashSetToArray(HashSet<Creative> set)
    {
        Creative[] array = new Creative[set.size()];
        Iterator<Creative> iterator = set.iterator();
        int index = 0;
        while(iterator.hasNext())
        {
            array[index] = iterator.next();
            index++;
        }

        return array;
    }

    public boolean isCreativeSubstitutable(int creativeID)
    {
        return creativeStock.containsKey(creativeID);
    }

    /**
     * select creative candidates by context, it should be the job of atm in online environment
     * @param context
     * @return
     */
    public Creative[] getCreativeSetByContext(String context)
    {
        return contextToCreativeArrayMap.containsKey(context) ? contextToCreativeArrayMap.get(context) : new Creative[0];
    }
}
