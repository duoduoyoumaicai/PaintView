package zhanglei.com.paintview.bean;

/**
 * 类名称：BaseDrawData
 * 类描述：画板数据基类
 * 创建人：lei.zhang
 * 创建时间：on 2019/6/17
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class BaseDrawData {

    /**
     * 创建备忘录的方法
     *
     * @param doWhat
     * @param addIndex
     * @return
     */
    protected DrawDataMemento createDrawDataMemento(int doWhat, DrawDataMemento.AddIndex addIndex) {
        return new DrawDataMemento(addIndex);
    }

}
