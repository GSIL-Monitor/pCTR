package application.adCTR.evaluation.adSelectionEvaluator;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-19
 * Time: 下午4:11
 */
public enum LiblinearTypes {
    LOGISTIC,
    SVM;

    public static boolean isLiblinearTypes(String typeName)
    {
        String name = typeName.toUpperCase();
        if(name.equals("LOGISTIC") || name.equals("SVM"))
        {
            return true;
        }
        return false;
    }
}
