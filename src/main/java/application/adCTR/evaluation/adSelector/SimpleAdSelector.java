package application.adCTR.evaluation.adSelector;

import application.adCTR.data.CTRDataInstance;
import application.adCTR.evaluation.creative.Creative;
import application.adCTR.sample.CTRSampleConfig;
import application.adCTR.sample.CTRSampleMaker;
import application.adCTR.sample.handlers.*;
import commons.framework.data.DataInstance;
import commons.framework.sample.Feature;
import commons.framework.sample.Sample;
import commons.framework.sample.SampleType;
import utils.FileUtils;
import utils.NumericalUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-26
 * Time: 上午10:38
 */
public class SimpleAdSelector implements IAdSelector{
    private ArrayList<Double> weightList;
    private long weightSize;
    private static SimpleAdSelector instance;
    private SimpleAdSelector(){
    }

    public static SimpleAdSelector getInstance()
    {
        if(instance == null)
        {
            instance = new SimpleAdSelector();
        }
        return instance;
    }

    private CTRSampleMaker sampleMaker = CTRSampleMaker.getInstance();

    private double logisticFunction(double value)
    {
        return 1/(1 + Math.exp(0 - value));
    }

    @Override
    /**
     * in this method, we only return one result
     */
    public ArrayList<Creative> selectAdFromPool(DataInstance dataInstance, Creative[] creativePool) {
        if(creativePool.length < 1)
        {
            return null;
        }
        CTRDataInstance ctrDataInstance = (CTRDataInstance) dataInstance;
        Creative bestCreative = null;
        double bestScore = -1;
        for(int i = 0; i < creativePool.length; i++)
        {
            Creative creative = creativePool[i];
            double score = calcScore(ctrDataInstance, creative);
            if(score > bestScore)
            {
                bestCreative = creative;
                bestScore = score;
                bestCreative.setScore(score);
            }
        }
        ArrayList<Creative> returnCreativeList = new ArrayList<Creative>(1);
        returnCreativeList.add(bestCreative);
        return returnCreativeList;
    }

    private double calcScore(CTRDataInstance dataInstance, Creative creative)
    {
        dataInstance.setCreativeId(creative.getCreativeId());
        dataInstance.setCastId(creative.getCastId());

        Sample sample = new Sample(SampleType.TEST, dataInstance.getDataInstanceId(), dataInstance.getTargetValue());
        sampleMaker.fillSample(sample, dataInstance);
        return logisticFunction(getPrediction(sample));
    }

    private double calcPrediction(ArrayList<Feature> features)
    {
        double value = 0;
        for(int i = 0, len = features.size(); i < len; i++)
        {
            int index = features.get(i).getFeatureId();
            double featureWeight = NumericalUtils.toDouble(features.get(i).getFeatureValue());
            if(index == Integer.MIN_VALUE || Double.isNaN(featureWeight))
            {
                continue;
            }
            if(index > weightSize)
            {
                continue;
            }
            value += weightList.get(index - 1) * featureWeight;
        }
        return value;
    }

    private double getPrediction(Sample sample)
    {
        ArrayList<Feature> features = sample.getFeatureArrayList();
        return calcPrediction(features);
    }

    @Override
    public long loadModel(String modelFile) {
        this.weightList = new ArrayList<Double>();
        try{
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(FileUtils.getStreamFromFile(modelFile)));
            String line;
            boolean tag = false;
            while((line = bufferedReader.readLine()) != null)
            {
                if(line.trim().equals("w"))
                {
                    tag = true;
                    continue;
                }
                if(!tag)
                {
                    continue;
                }
                double value = Double.parseDouble(line.trim());
                this.weightList.add(value);
            }
            bufferedReader.close();
        }catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("Loading weight file wrong");
            return -1;
        }
        weightSize = weightList.size();
        return weightSize;
    }

    @Override
    public int registerSampleHandlers() {
        /**
         * init config first
         */
        CTRSampleConfig config = CTRSampleConfig.getInstance();
        config.loadConfig();
        int initFeatureId = 0;//because all localId starts from 1, so we set init all featureId as 1 here
        /**
         * start to add all kinds of feature handlers
         */
        //device handler
        if(config.isDeviceTypeHandlerSwitch())
        {
            DeviceTypeHandler deviceTypeHandler = new DeviceTypeHandler();
            initFeatureId = sampleMaker.registerFeatureHandler(deviceTypeHandler, initFeatureId);
            System.out.println("device type handler register succeed");
        }
        //os handler
        if(config.isOsTypeHandlerSwitch())
        {
            OsTypeHandler osTypeHandler = new OsTypeHandler();
            initFeatureId = sampleMaker.registerFeatureHandler(osTypeHandler, initFeatureId);
            System.out.println("os type handler register succeed");
        }
        //product type handler
        if(config.isProductTypeHandlerSwitch())
        {
            ProductTypeHandler productTypeHandler = new ProductTypeHandler();
            initFeatureId = sampleMaker.registerFeatureHandler(productTypeHandler, initFeatureId);
            System.out.println("product type handler register succeed");
        }
        //category
        if(config.isCategoryHandlerSwitch())
        {
            CategoryHandler categoryHandler = new CategoryHandler();
            initFeatureId = sampleMaker.registerFeatureHandler(categoryHandler, initFeatureId);
            System.out.println("category handler register succeed");
        }
        //cast id
        if(config.isCastIDHandlerSwitch())
        {
            CastIDHandler castIDHandler = new CastIDHandler();
            initFeatureId = sampleMaker.registerFeatureHandler(castIDHandler, initFeatureId);
            System.out.println("cast id handler register succeed");
        }
        //creative id
        if(config.isCreativeIDHandlerSwitch())
        {
            CreativeIDHandler creativeIDHandler = new CreativeIDHandler();
            initFeatureId = sampleMaker.registerFeatureHandler(creativeIDHandler, initFeatureId);
            System.out.println("creative id handler register succeed");
        }
        //creative channel
        if(config.isCreativeChannelHandlerSwitch())
        {
            CreativeChannelHandler creativeChannelHandler = new CreativeChannelHandler();
            initFeatureId = sampleMaker.registerFeatureHandler(creativeChannelHandler, initFeatureId);
            System.out.println("creative channel handler register succeed");
        }
        //client type
        if(config.isClientTypeHandlerSwitch())
        {
            ClientTypeHandler clientTypeHandler = new ClientTypeHandler();
            initFeatureId = sampleMaker.registerFeatureHandler(clientTypeHandler, initFeatureId);
            System.out.println("client type handler register succeed");
        }
        //clicked ip
        if(config.isIpHandlerSwitch())
        {
            IpHandler ipHandler = new IpHandler();
            initFeatureId = sampleMaker.registerFeatureHandler(ipHandler, initFeatureId);
            System.out.println("ip handler register succeed");
        }
        //app version
        if(config.isAppVersionHandlerSwitch())
        {
            AppVersionHandler appVersionHandler = new AppVersionHandler();
            initFeatureId = sampleMaker.registerFeatureHandler(appVersionHandler, initFeatureId);
            System.out.println("app version handler register succeed");
        }
        //sub category
        if(config.isSubCategoryHandlerSwitch())
        {
            SubCategoryHandler subCategoryHandler = new SubCategoryHandler();
            initFeatureId = sampleMaker.registerFeatureHandler(subCategoryHandler, initFeatureId);
            System.out.println("sub category handler register succeed");
        }
        //program id
        if(config.isProgramIDHandlerSwitch())
        {
            ProgramIDHandler programIDHandler = new ProgramIDHandler();
            initFeatureId = sampleMaker.registerFeatureHandler(programIDHandler, initFeatureId);
            System.out.println("program id handler register succeed");
        }
        //video id
        if(config.isVideoIDHandlerSwitch())
        {
            VideoIDHandler videoIDHandler = new VideoIDHandler();
            initFeatureId = sampleMaker.registerFeatureHandler(videoIDHandler, initFeatureId);
            System.out.println("video id handler register succeed");
        }
        //islong
        if(config.isIsLongHandlerSwitch())
        {
            IsLongHandler isLongHandler = new IsLongHandler();
            initFeatureId = sampleMaker.registerFeatureHandler(isLongHandler, initFeatureId);
            System.out.println("isLong handler register succeed");
        }
        //city id
        if(config.isCityIDHandlerSwitch())
        {
            CityIDHandler cityIDHandler = new CityIDHandler();
            initFeatureId = sampleMaker.registerFeatureHandler(cityIDHandler, initFeatureId);
            System.out.println("city id handler register succeed");
        }
        //more feature handlers here
        return initFeatureId;
    }
}


