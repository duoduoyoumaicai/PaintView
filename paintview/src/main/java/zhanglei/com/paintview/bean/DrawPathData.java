package zhanglei.com.paintview.bean;

import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

/**
 * 类名称：DrawPathData
 * 类描述：用于记录绘制路径的对象
 * 创建人：lei.zhang
 * 创建时间：on 2019/6/17
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class DrawPathData extends BaseDrawData {

    public Path mPath;

    public Paint mPaint;

    public DrawPathData(Path path, Paint paint) {
        this.mPath = new Path(path);
        this.mPaint = new Paint(paint);
    }

    @Override
    public DrawDataMemento createDrawDataMemento(int doWhat, DrawDataMemento.onAddIndexListener onAddIndexListener) {
        Log.e("DrawPathData", "create Memoto cur restore data instance is " + this.toString());
        DrawDataMemento memento = new DrawDataMemento(onAddIndexListener);
        memento.setMcTransformData(this);
        return memento;
    }
}
