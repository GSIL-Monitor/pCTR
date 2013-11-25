package application.adCTR.evaluation.evaluator;

import application.adCTR.evaluation.creative.CreativeInventory;
import utils.FileUtils;
import utils.NumericalUtils;
import utils.database.DataBaseManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-20
 * Time: 上午9:42
 */
public class AdSelectionEvaluator {
    //creative inventory
    private CreativeInventory inventory = null;
    private DataBaseManager dataBaseManager = null;
    private String datetime = null;

    private ArrayList<Double> weightList;
    private int weightSize = 0;
    private double threshold = 0.5;

    private long pos_pos; //positive sample with positive prediction
    private long pos_neg; //positive sample with negative prediction
    private long neg_pos; //negative sample with positive prediction
    private long neg_neg; //negative sample with negative prediction

    public AdSelectionEvaluator(String datetime)
    {
        this.datetime = datetime;
    }

    public void initAdSelector()
    {
        dataBaseManager = DataBaseManager.getInstance();
        dataBaseManager.init();
        System.out.println("init database manager succeed");
        System.out.println("======================================================");

        inventory = CreativeInventory.getInstance();
        inventory.init(datetime);
    }

    public void checkCreative()
    {
        inventory.printCreative();
    }

    public static void main(String[] args)
    {
        String datetime = "2013-11-16";
        System.out.println("prepare evaluation for date : " + datetime);
        AdSelectionEvaluator evaluator = new AdSelectionEvaluator(datetime);

        evaluator.initAdSelector();
        evaluator.checkCreative();
        System.out.println("init done");
    }
}
