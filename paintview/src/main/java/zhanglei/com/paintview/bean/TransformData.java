package zhanglei.com.paintview.bean;

import android.graphics.Matrix;
import android.graphics.RectF;

/**
 * 类名称：TransformData
 * 类描述：
 * 创建人：lei.zhang
 * 创建时间：on 2018/9/18
 * 修改人：
 * 修改时间：
 * 修改备注：
 */


public class TransformData extends BaseDrawData {
    public Matrix mMatrix = null;//变换图片的Matrix,同时兼职用做判断手指点击位置是否在图片上或几何图形上的作用(逆矩阵判断)
    public RectF mRectSrc;

    @Override
    protected DrawDataMemento createDrawDataMemento(int doWhat, DrawDataMemento.AddIndex addIndex) {
        return super.createDrawDataMemento(doWhat, addIndex);
    }
}
