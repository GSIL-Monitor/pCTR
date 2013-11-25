package application.adCTR.evaluation.creative;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-15
 * Time: 上午10:10
 */
public class Creative {
    private int creativeId;
    private int castId;
    private HashSet<Integer> targetCities = null;
    private HashSet<String> targetChannels = null;

    public Creative(int creativeId, int castId)
    {
        this.creativeId = creativeId;
        this.castId = castId;

        targetCities = new HashSet<Integer>();
        targetChannels = new HashSet<String>();
    }

    public int getCreativeId() {
        return creativeId;
    }

    public int getCastId() {
        return castId;
    }

    public HashSet<Integer> getTargetCities() {
        return targetCities;
    }

    public HashSet<String> getTargetChannels() {
        return targetChannels;
    }

    public void addTargetCity(int cityId)
    {
        targetCities.add(cityId);
    }

    public void addTargetCities(HashSet<Integer> citySet)
    {
        targetCities.addAll(citySet);
    }

    public void addTargetChannel(String channel)
    {
        targetChannels.add(channel);
    }

    public ArrayList<String> getTargetContext()
    {
        ArrayList<String> contextList = new ArrayList<String>();
        Iterator<Integer> cityIter = targetCities.iterator();
        while (cityIter.hasNext())
        {
            int cityId = cityIter.next();
            Iterator<String> channelIter = targetChannels.iterator();
            while(channelIter.hasNext())
            {
                String context = String.valueOf(cityId) + "_" + channelIter.next();
                contextList.add(context);
            }
        }

        return contextList;
    }
}
