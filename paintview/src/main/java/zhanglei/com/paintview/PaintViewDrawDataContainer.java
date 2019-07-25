package zhanglei.com.paintview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.graphics.RectF;

import java.util.concurrent.CopyOnWriteArrayList;

import zhanglei.com.paintview.bean.DrawBgData;
import zhanglei.com.paintview.bean.DrawDataMemento;
import zhanglei.com.paintview.bean.DrawPathData;
import zhanglei.com.paintview.bean.DrawPhotoData;
import zhanglei.com.paintview.bean.DrawShapeData;


/**
 * 类名称：PaintViewDrawDataContainer
 * 类描述：负责管理PaintView绘制数据管理,存储所有的绘制数据
 * 创建人：lei.zhang
 * 创建时间：on 2019/6/17
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class PaintViewDrawDataContainer {

    public Path mTempPath;

    public int curIndex = -1;//当前undoList的index索引,记录操作步骤

    public float mCurX, mCurY;//用来做贝塞尔曲线的临时坐标

    public DrawBgData mPaintViewBg;//背景图片

    public CopyOnWriteArrayList<DrawPhotoData> mDrawPhotoList;//图片集合

    public CopyOnWriteArrayList<DrawShapeData> mDrawShapeList;//几何图形集合

    public CopyOnWriteArrayList<DrawPathData> mDrawPathList;//用于记录path的集合路径

    public CopyOnWriteArrayList<DrawDataMemento> mMementoList;//备忘录集合,每一步操作就产生一个备忘录,存储到这个集合里

    public DrawShapeData mCurDrawShape = new DrawShapeData();//当前画出的shape

    public DrawPhotoData mCurSelectPhoto;//当前选中的图片

    public DrawShapeData mCurSelectShape;//当前选中的几何图形

    public Bitmap scaleMarkBM;//缩放图标

    public Bitmap deleteMarkBM;//删除图标

    public Bitmap rotateMarkBM;//旋转图标

    public RectF photoScaleRectLU;//缩放标记边界,矩形大小按照图片原大小2倍,左上

    public RectF photoScaleRectLB;//缩放标记边界,矩形大小按照图片原大小2倍,左下

    public RectF photoDeleteRectRU;//删除标记边界,矩形大小按照图片原大小,右上

    public RectF photoRotateRectRB;//旋转标记边界,矩形大小按照图片原大小,右下

    public RectF shapeScaleRectLU;//缩放标记边界,矩形大小按照图片原大小2倍,左上

    public RectF shapeScaleRectUM;//缩放标记边界,上中

    public RectF shapeRotateRectRB;//旋转标记边界,矩形大小按照图片原大小,右下

    public RectF shapeScaleRectLB;//缩放标记边界,矩形大小按照图片原大小2倍,左下

    public RectF shapeScaleRectRM;//缩放标记边界,右中

    public RectF shapeScaleRectBM;//缩放标记边界,下中

    public RectF shapeScaleRectLM;//缩放标记边界,左中

    public RectF shapeDeleteRectRU;//删除标记边界,矩形大小按照图片原大小,右上

    public static float SCALE_MAX = 4.0f;//最大缩放倍数

    public static float SCALE_MIN = 0.2f;//最小缩放倍数

    public static final float DEFAULT_PHOTO_HEIGHT = 400.00F;//图片默认显示高度

    public static float SCALE_MIN_LEN = 50;//几何图形外接矩形最小宽高尺寸(px)


    public PaintViewDrawDataContainer(PaintView paintView) {
        mTempPath = new Path();
        mDrawPhotoList = new CopyOnWriteArrayList<>();
        mDrawShapeList = new CopyOnWriteArrayList<>();
        mMementoList = new CopyOnWriteArrayList<>();
        mDrawPathList = new CopyOnWriteArrayList();
        scaleMarkBM = BitmapFactory.decodeResource(paintView.getResources(), R.drawable.photo_transform_icon);
        deleteMarkBM = BitmapFactory.decodeResource(paintView.getResources(), R.drawable.photo_delect_icon);
        rotateMarkBM = BitmapFactory.decodeResource(paintView.getResources(), R.drawable.photo_photo_rotate_icon);
        photoScaleRectLU = new RectF(0, 0, scaleMarkBM.getWidth() * 2, scaleMarkBM.getHeight() * 2);
        photoScaleRectLB = new RectF(0, 0, scaleMarkBM.getWidth() * 2, scaleMarkBM.getHeight() * 2);
        photoRotateRectRB = new RectF(0, 0, rotateMarkBM.getWidth(), rotateMarkBM.getHeight());
        photoDeleteRectRU = new RectF(0, 0, deleteMarkBM.getWidth(), deleteMarkBM.getHeight());
        shapeScaleRectLU = new RectF(0, 0, scaleMarkBM.getWidth() * 2, scaleMarkBM.getHeight() * 2);
        shapeRotateRectRB = new RectF(0, 0, rotateMarkBM.getWidth(), rotateMarkBM.getHeight());
        shapeScaleRectLB = new RectF(0, 0, scaleMarkBM.getWidth() * 2, scaleMarkBM.getHeight() * 2);
        shapeScaleRectUM = new RectF(0, 0, scaleMarkBM.getWidth() * 2, scaleMarkBM.getHeight() * 2);
        shapeScaleRectRM = new RectF(0, 0, scaleMarkBM.getWidth() * 2, scaleMarkBM.getHeight() * 2);
        shapeScaleRectBM = new RectF(0, 0, scaleMarkBM.getWidth() * 2, scaleMarkBM.getHeight() * 2);
        shapeScaleRectLM = new RectF(0, 0, scaleMarkBM.getWidth() * 2, scaleMarkBM.getHeight() * 2);
        shapeDeleteRectRU = new RectF(0, 0, deleteMarkBM.getWidth(), deleteMarkBM.getHeight());
    }

    public void clearAndSetNull() {//销毁数据
        clear();
        mTempPath = null;
        mDrawPhotoList = null;
        mDrawShapeList = null;
        mMementoList = null;
        mDrawPathList = null;
        mPaintViewBg.bitmap.recycle();
        mPaintViewBg = null;
    }

    public void clear() {//清空数据,背景图不清空

        if (null != mDrawPhotoList) {
            mDrawPhotoList.clear();
        }
        if (null != mDrawShapeList) {
            mDrawShapeList.clear();
        }
        if (null != mMementoList) {
            mMementoList.clear();
        }
        if (null != mDrawPathList) {
            mDrawPathList.clear();
        }

    }
}
