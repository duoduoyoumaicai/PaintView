package zhanglei.com.paintview.bean;

import android.graphics.Bitmap;
import android.util.Log;

/**
 * 类名称：DrawPhotoData
 * 类描述：这个类封装了图片的Bitmap与Matrix,用来记录图片旋转缩放等逻辑
 * 创建人：lei.zhang
 * 创建时间：on 2019/6/17
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class DrawPhotoData extends TransformData {

    public Bitmap bitmap = null;

    private DrawDataMemento memento;

    @Override
    public DrawDataMemento createDrawDataMemento(int doWhat, DrawDataMemento.AddIndex addIndex) {
        Log.e("DrawPhotoData", "create Memoto cur restore data instance is " + this.toString());
        memento = new DrawDataMemento(addIndex);
        memento.setDoWhat(doWhat);
        memento.setStartMatrix(this.mMatrix);
        memento.setMcTransformData(this);
        return memento;
    }

}
