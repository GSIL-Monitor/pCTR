package application.adCTR.evaluation.adSelector;

import application.adCTR.data.CTRDataInstance;
import application.adCTR.evaluation.creative.Creative;
import application.adCTR.evaluation.evaluator.MetricTypes;
import application.adCTR.sample.CTRSampleConfig;
import application.adCTR.sample.CreativeBasedSampleMaker;
import application.adCTR.sample.CreativeIrrelevantSampleMaker;
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

    private CreativeBasedSampleMaker creativeBasedSampleMaker = CreativeBasedSampleMaker.getInstance();
    private CreativeIrrelevantSampleMaker creativeIrrelevantSampleMaker = CreativeIrrelevantSampleMaker.getInstance();

    private double logisticFunction(double value)
    {
        return 1/(1 + Math.exp(0 - value));
    }

    private ArrayList<Creative> selectAdFromPoolForCTR(DataInstance dataInstance, Creative[] creativePool) {
        if(creativePool.length < 1)
        {
            return null;
        }
        CTRDataInstance ctrDataInstance = (CTRDataInstance) dataInstance;
        //first we have a base score, which is irrelevant to the creative information
        double baseScore = calcBaseScore(ctrDataInstance);
        //now we process creative based score
        Creative bestCreative = null;
        double bestScore = -1;
        for(int i = 0; i < creativePool.length; i++)
        {
            Creative creative = creativePool[i];
            double score = logisticFunction(calcScore(ctrDataInstance, creative) + baseScore);
            creative.setScore(score);
            if(score > bestScore)
            {
                bestCreative = creative;
                bestScore = score;
            }
        }
        ArrayList<Creative> returnCreativeList = new ArrayList<Creative>(1);
        returnCreativeList.add(bestCreative);
        return returnCreativeList;
    }

    private ArrayList<Creative> selectAdFromPoolForIndex(DataInstance dataInstance, Creative[] creativePool) {
        if(creativePool.length < 1)
        {
            return null;
        }
        ArrayList<Creative> returnCreativeList = new ArrayList<Creative>(creativePool.length);
        CTRDataInstance ctrDataInstance = (CTRDataInstance) dataInstance;
        //first we have a base score, which is irrelevant to the creative information
        double baseScore = calcBaseScore(ctrDataInstance);
        for(int i = 0; i < creativePool.length; i++)
        {
            Creative creative = creativePool[i];
            double score = calcScore(ctrDataInstance, creative) + baseScore;
            creative.setScore(score);
            returnCreativeList.add(creative);
        }
        Collections.sort(returnCreativeList);
        return returnCreativeList;
    }

    private double calcBaseScore(CTRDataInstance dataInstance)
    {
        Sample sample = new Sample(SampleType.TEST, dataInstance.getDataInstanceId(), dataInstance.getTargetValue());
        creativeIrrelevantSampleMaker.fillSample(sample, dataInstance);
        return getPrediction(sample);
    }

    private double calcScore(CTRDataInstance dataInstance, Creative creative)
    {
        dataInstance.setCreativeId(creative.getCreativeId());
        dataInstance.setCastId(creative.getCastId());

        Sample sample = new Sample(SampleType.TEST, dataInstance.getDataInstanceId(), dataInstance.getTargetValue());
        creativeBasedSampleMaker.fillSample(sample, dataInstance);
        return getPrediction(sample);
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
    public ArrayList<Creative> selectAdFromPool(DataInstance dataInstance, Creative[] creativePool, MetricTypes metricType) {
        switch (metricType)
        {
            case CTR:
                return selectAdFromPoolForCTR(dataInstance, creativePool);
            case INDEX:
                return selectAdFromPoolForIndex(dataInstance, creativePool);
            default:
                return null;
        }
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
         * start to add all kinds of feature handlers, here we need to decide which kind of feature is creative
         * based and which are creative irrelevant
         */
        //device handler
        if(config.isDeviceTypeHandlerSwitch())
        {
            DeviceTypeHandler deviceTypeHandler = new DeviceTypeHandler();
            initFeatureId = creativeIrrelevantSampleMaker.registerFeatureHandler(deviceTypeHandler, initFeatureId);
            System.out.println("device type handler register succeed");
        }
        //os handler
        if(config.isOsTypeHandlerSwitch())
        {
            OsTypeHandler osTypeHandler = new OsTypeHandler();
            initFeatureId = creativeIrrelevantSampleMaker.registerFeatureHandler(osTypeHandler, initFeatureId);
            System.out.println("os type handler register succeed");
        }
        //product type handler
        if(config.isProductTypeHandlerSwitch())
        {
            ProductTypeHandler productTypeHandler = new ProductTypeHandler();
            initFeatureId = creativeIrrelevantSampleMaker.registerFeatureHandler(productTypeHandler, initFeatureId);
            System.out.println("product type handler register succeed");
        }
        //category
        if(config.isCategoryHandlerSwitch())
        {
            CategoryHandler categoryHandler = new CategoryHandler();
            initFeatureId = creativeIrrelevantSampleMaker.registerFeatureHandler(categoryHandler, initFeatureId);
            System.out.println("category handler register succeed");
        }
        //cast id
        if(config.isCastIDHandlerSwitch())
        {
            CastIDHandler castIDHandler = new CastIDHandler();
            initFeatureId = creativeBasedSampleMaker.registerFeatureHandler(castIDHandler, initFeatureId);
            System.out.println("cast id handler register succeed");
        }
        //creative id
        if(config.isCreativeIDHandlerSwitch())
        {
            CreativeIDHandler creativeIDHandler = new CreativeIDHandler();
            initFeatureId = creativeBasedSampleMaker.registerFeatureHandler(creativeIDHandler, initFeatureId);
            System.out.println("creative id handler register succeed");
        }
        //creative channel
        if(config.isCreativeChannelHandlerSwitch())
        {
            CreativeChannelHandler creativeChannelHandler = new CreativeChannelHandler();
            initFeatureId = creativeBasedSampleMaker.registerFeatureHandler(creativeChannelHandler, initFeatureId);
            System.out.println("creative channel handler register succeed");
        }
        //client type
        if(config.isClientTypeHandlerSwitch())
        {
            ClientTypeHandler clientTypeHandler = new ClientTypeHandler();
            initFeatureId = creativeIrrelevantSampleMaker.registerFeatureHandler(clientTypeHandler, initFeatureId);
            System.out.println("client type handler register succeed");
        }
        //clicked ip
        if(config.isIpHandlerSwitch())
        {
            IpHandler ipHandler = new IpHandler();
            initFeatureId = creativeIrrelevantSampleMaker.registerFeatureHandler(ipHandler, initFeatureId);
            System.out.println("ip handler register succeed");
        }
        //app version
        if(config.isAppVersionHandlerSwitch())
        {
            AppVersionHandler appVersionHandler = new AppVersionHandler();
            initFeatureId = creativeIrrelevantSampleMaker.registerFeatureHandler(appVersionHandler, initFeatureId);
            System.out.println("app version handler register succeed");
        }
        //sub category
        if(config.isSubCategoryHandlerSwitch())
        {
            SubCategoryHandler subCategoryHandler = new SubCategoryHandler();
            initFeatureId = creativeIrrelevantSampleMaker.registerFeatureHandler(subCategoryHandler, initFeatureId);
            System.out.println("sub category handler register succeed");
        }
        //program id
        if(config.isProgramIDHandlerSwitch())
        {
            ProgramIDHandler programIDHandler = new ProgramIDHandler();
            initFeatureId = creativeIrrelevantSampleMaker.registerFeatureHandler(programIDHandler, initFeatureId);
            System.out.println("program id handler register succeed");
        }
        //video id
        if(config.isVideoIDHandlerSwitch())
        {
            VideoIDHandler videoIDHandler = new VideoIDHandler();
            initFeatureId = creativeIrrelevantSampleMaker.registerFeatureHandler(videoIDHandler, initFeatureId);
            System.out.println("video id handler register succeed");
        }
        //islong
        if(config.isIsLongHandlerSwitch())
        {
            IsLongHandler isLongHandler = new IsLongHandler();
            initFeatureId = creativeIrrelevantSampleMaker.registerFeatureHandler(isLongHandler, initFeatureId);
            System.out.println("isLong handler register succeed");
        }
        //city id
        if(config.isCityIDHandlerSwitch())
        {
            CityIDHandler cityIDHandler = new CityIDHandler();
            initFeatureId = creativeIrrelevantSampleMaker.registerFeatureHandler(cityIDHandler, initFeatureId);
            System.out.println("city id handler register succeed");
        }
        //more feature handlers here
        return initFeatureId;
    }
}


