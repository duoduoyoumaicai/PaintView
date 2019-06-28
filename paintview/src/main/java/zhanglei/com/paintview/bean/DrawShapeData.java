package zhanglei.com.paintview.bean;

import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import zhanglei.com.paintview.DrawTypeEnum;


/**
 * 类名称：DrawShapeData
 * 类描述：这个类封装了几何图形path,用来记录几何图形旋转缩放等逻辑
 * 创建人：lei.zhang
 * 创建时间：on 2019/6/17
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class DrawShapeData extends TransformData {

    public DrawTypeEnum drawType;//记录类型

    public Path drawPath;//画笔路径数据

    public Path srcPath;//初始的路径

    public Paint paint;//笔类

    private DrawDataMemento memento;//备忘录

    @Override
    public DrawDataMemento createDrawDataMemento(int doWhat, DrawDataMemento.onAddIndexListener onAddIndexListener) {
        Log.e("DrawShapeData", "create Memoto cur restore data instance is " + this.toString());
        memento = new DrawDataMemento(onAddIndexListener);
        memento.setDoWhat(doWhat);
        memento.setStartMatrix(this.mMatrix);
        memento.setMcTransformData(this);

        return memento;
    }

}
