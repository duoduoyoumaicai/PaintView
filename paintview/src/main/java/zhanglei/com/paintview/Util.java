package zhanglei.com.paintview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

    public static Point getScreenSize(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point out = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(out);
        }else{
            int width = display.getWidth();
            int height = display.getHeight();
            out.set(width, height);
        }
        return out;
    }

    /**
     * 通过uri获取图片并进行压缩
     *
     * @param uri
     * @param activity
     * @return
     * @throws IOException
     */
    public static Bitmap getBitmapFromUri(Uri uri, Activity activity) throws IOException {
        InputStream inputStream = activity.getContentResolver().openInputStream(uri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inDither = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeStream(inputStream, null, options);
        inputStream.close();

        int originalWidth = options.outWidth;
        int originalHeight = options.outHeight;
        if (originalWidth == -1 || originalHeight == -1) {
            return null;
        }

        float height = 800f;
        float width = 480f;
        int be = 1; //be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > width) {
            be = (int) (originalWidth / width);
        } else if (originalWidth < originalHeight && originalHeight > height) {
            be = (int) (originalHeight / height);
        }

        if (be <= 0) {
            be = 1;
        }
        BitmapFactory.Options bitmapOptinos = new BitmapFactory.Options();
        bitmapOptinos.inSampleSize = be;
        bitmapOptinos.inDither = true;
        bitmapOptinos.inPreferredConfig = Bitmap.Config.ARGB_8888;
        inputStream = activity.getContentResolver().openInputStream(uri);

        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, bitmapOptinos);
        inputStream.close();

        return compressImage(bitmap);
    }

    /**
     * 质量压缩方法
     *
     * @param bitmap
     * @return
     */
    public static Bitmap compressImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        int options = 100;
        while (byteArrayOutputStream.toByteArray().length / 1024 > 100) {
            byteArrayOutputStream.reset();
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差 ，第三个参数：保存压缩后的数据的流
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, byteArrayOutputStream);
            options -= 10;
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        Bitmap bitmapImage = BitmapFactory.decodeStream(byteArrayInputStream, null, null);
        return bitmapImage;
    }


}
