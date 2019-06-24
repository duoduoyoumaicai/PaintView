package zhanglei.com.paintview;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.MotionEvent;

/**
 * 类名称：Util
 * 类描述：
 * 创建人：lei.zhang
 * 创建时间：on 2019/6/18
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class Util {
    /**
     * 对图片进行压缩，用来匹配画布尺寸
     *
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        float bWidth = bitmap.getWidth();
        float bHeight = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = (float) width / bWidth;
        float scaleHeight = (float) height / bHeight;
        matrix.postScale(scaleWidth, scaleHeight);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, (int) bWidth, (int) bHeight, matrix, true);
        return bitmap;
    }

    /**
     * 触摸事件是否是手指
     *
     * @param event
     * @return
     */
    public static boolean isFinger(MotionEvent event) {
        return event.getToolType(0) == MotionEvent.TOOL_TYPE_FINGER;
    }
}
