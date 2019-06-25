package zhanglei.com.paintview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.graphics.RectF;

import java.util.concurrent.CopyOnWriteArrayList;

import zhanglei.com.paintview.bean.DrawDataMemento;
import zhanglei.com.paintview.bean.DrawPathData;
import zhanglei.com.paintview.bean.DrawPhotoData;
import zhanglei.com.paintview.bean.DrawShapeData;


/**
 * 类名称：PaintViewDrawDataManager
 * 类描述：负责管理PaintView绘制数据管理,存储所有的绘制数据
 * 创建人：lei.zhang
 * 创建时间：on 2019/6/17
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class PaintViewDrawDataManager {

    public Path mTempPath;

    public float mCurX, mCurY;//用来做贝塞尔曲线的临时坐标

    public CopyOnWriteArrayList<DrawPhotoData> mDrawPhotoList;//图片集合

    public CopyOnWriteArrayList<DrawShapeData> mDrawShapeList;//几何图形集合

    public CopyOnWriteArrayList<DrawPathData> mDrawPathList;//用于记录path的集合路径

    public CopyOnWriteArrayList<DrawDataMemento> mUndoList;//回退集合,每一步操作就产生一个备忘录,存储到这个集合里

    public Bitmap scaleMarkBM;//缩放图标

    public Bitmap deleteMarkBM;//删除图标

    public Bitmap rotateMarkBM;//旋转图标

    public RectF photoScaleRect01;//缩放标记边界,矩形大小按照图片原大小2倍,左上

    public RectF photoScaleRect02;//缩放标记边界,矩形大小按照图片原大小2倍,左下

    public RectF photoDeleteRect;//删除标记边界,矩形大小按照图片原大小,右上

    public RectF photoRotateRect;//旋转标记边界,矩形大小按照图片原大小,右下

    public RectF shapeScaleRect01;//缩放标记边界,矩形大小按照图片原大小2倍,左上

    public RectF shapeRotateRect;//旋转标记边界,矩形大小按照图片原大小,右下

    public RectF shapeScaleRect03;//缩放标记边界,矩形大小按照图片原大小2倍,左下

    public RectF shapeScaleRect04;//缩放标记边界,上中

    public RectF shapeScaleRect05;//缩放标记边界,右中

    public RectF shapeScaleRect06;//缩放标记边界,下中

    public RectF shapeScaleRect07;//缩放标记边界,左中

    public RectF shapeDeleteRect;//删除标记边界,矩形大小按照图片原大小,右上

    public PaintViewDrawDataManager(PaintView paintView) {
        mTempPath = new Path();
        mDrawPhotoList = new CopyOnWriteArrayList<>();
        mDrawShapeList = new CopyOnWriteArrayList<>();
        mUndoList = new CopyOnWriteArrayList<>();
        mDrawPathList = new CopyOnWriteArrayList();
        scaleMarkBM = BitmapFactory.decodeResource(paintView.getResources(), R.drawable.photo_transform_icon);
        deleteMarkBM = BitmapFactory.decodeResource(paintView.getResources(), R.drawable.photo_delect_icon);
        rotateMarkBM = BitmapFactory.decodeResource(paintView.getResources(), R.drawable.photo_photo_rotate_icon);
        photoScaleRect01 = new RectF(0, 0, scaleMarkBM.getWidth() * 2, scaleMarkBM.getHeight() * 2);
        photoScaleRect02 = new RectF(0, 0, scaleMarkBM.getWidth() * 2, scaleMarkBM.getHeight() * 2);
//        photoScaleRect03 = new RectF(0, 0, scaleMarkBM.getWidth() * 2, scaleMarkBM.getHeight() * 2);
        photoRotateRect = new RectF(0, 0, rotateMarkBM.getWidth(), rotateMarkBM.getHeight());
        photoDeleteRect = new RectF(0, 0, deleteMarkBM.getWidth(), deleteMarkBM.getHeight());
        shapeScaleRect01 = new RectF(0, 0, scaleMarkBM.getWidth() * 2, scaleMarkBM.getHeight() * 2);
        shapeRotateRect = new RectF(0, 0, scaleMarkBM.getWidth() * 2, scaleMarkBM.getHeight() * 2);
        shapeScaleRect03 = new RectF(0, 0, scaleMarkBM.getWidth() * 2, scaleMarkBM.getHeight() * 2);
        shapeScaleRect04 = new RectF(0, 0, scaleMarkBM.getWidth() * 2, scaleMarkBM.getHeight() * 2);
        shapeScaleRect05 = new RectF(0, 0, scaleMarkBM.getWidth() * 2, scaleMarkBM.getHeight() * 2);
        shapeScaleRect06 = new RectF(0, 0, scaleMarkBM.getWidth() * 2, scaleMarkBM.getHeight() * 2);
        shapeScaleRect07 = new RectF(0, 0, scaleMarkBM.getWidth() * 2, scaleMarkBM.getHeight() * 2);
        shapeDeleteRect = new RectF(0, 0, deleteMarkBM.getWidth(), deleteMarkBM.getHeight());
    }

    public void clearAndSetNull() {
        clear();
        mTempPath = null;
        mDrawPhotoList = null;
        mDrawShapeList = null;
        mUndoList = null;
        mDrawPathList = null;
    }

    public void clear() {
        if (null != mDrawPhotoList) {
            mDrawPhotoList.clear();
        }
        if (null != mDrawShapeList) {
            mDrawShapeList.clear();
        }
        if (null != mUndoList) {
            mUndoList.clear();
        }
        if (null != mDrawPathList) {
            mDrawPathList.clear();
        }
    }
}
