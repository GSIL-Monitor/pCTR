package commons.framework.data;
/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-13
 * Time: 下午2:14
 */
public abstract class DataInstance {
    protected long dataInstanceId;
    protected boolean correctlyFilled = false;

    public long getDataInstanceId()
    {
        return dataInstanceId;
    }

    public void setCorrectlyFilled(boolean correctlyFilled)
    {
        this.correctlyFilled = correctlyFilled;
    }

    public boolean isCorrectlyFilled()
    {
        return correctlyFilled;
    }
}
