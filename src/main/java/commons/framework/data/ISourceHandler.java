package commons.framework.data;

/**
 * Created with IntelliJ IDEA.
 * Author: zhangcen
 * Date: 13-11-13
 * Time: 下午2:58
 */
public interface ISourceHandler {
    public abstract boolean handleSource(String sourceContent, DataInstance dataInstance);
}
