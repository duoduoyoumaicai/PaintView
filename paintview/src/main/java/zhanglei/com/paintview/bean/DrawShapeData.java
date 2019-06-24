package zhanglei.com.paintview.bean;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
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
public class DrawShapeData extends BaseTransformData {

    public DrawTypeEnum drawType;//记录类型

    public Path drawPath;//画笔路径数据

    public Path srcPath;//初始的路径

    public Paint paint;//笔类

    public Matrix matrix = null;//变换图片的Matrix,同时兼职用做判断手指点击位置是否在图片上或几何图形上的作用(逆矩阵判断)

    public RectF RectSrc;

    private DrawDataMemento memento;//备忘录

    @Override
    public DrawDataMemento createDrawDataMemento(int doWhat, DrawDataMemento.AddIndex addIndex) {
        Log.e("DrawShapeData", "create Memoto cur restore data instance is " + this.toString());
        memento = new DrawDataMemento(addIndex);
        memento.setDoWhat(doWhat);
        memento.setStartMatrix(this.matrix);
        memento.setMcTransformData(this);

        return memento;
    }

}
