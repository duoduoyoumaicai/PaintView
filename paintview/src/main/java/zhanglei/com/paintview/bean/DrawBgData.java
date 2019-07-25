package zhanglei.com.paintview.bean;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * 类名称：DrawBgData
 * 类描述：绘制背景用的数据bean
 * 创建人：lei.zhang
 * 创建时间：on 2019/7/25
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class DrawBgData {
    public Matrix mMatrix = null;//变换图片的Matrix,同时兼职用做判断手指点击位置是否在图片上或几何图形上的作用(逆矩阵判断)
    public Bitmap bitmap = null;
}
