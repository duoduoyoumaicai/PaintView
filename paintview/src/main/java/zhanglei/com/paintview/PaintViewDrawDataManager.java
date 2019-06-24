package zhanglei.com.paintview;

import android.graphics.Path;

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

    public PaintViewDrawDataManager() {
        mTempPath = new Path();
        mDrawPhotoList = new CopyOnWriteArrayList<>();
        mDrawShapeList = new CopyOnWriteArrayList<>();
        mUndoList = new CopyOnWriteArrayList<>();
        mDrawPathList = new CopyOnWriteArrayList();
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
