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

    public CopyOnWriteArrayList<DrawPhotoData> drawPhotoList;//图片集合

    public CopyOnWriteArrayList<DrawShapeData> drawShapeList;//几何图形集合

    public CopyOnWriteArrayList<DrawPathData> drawPathList;//用于记录path的集合路径

    public CopyOnWriteArrayList<DrawDataMemento> undoList;//回退集合,每一步操作就产生一个备忘录,存储到这个集合里

    public PaintViewDrawDataManager() {
        mTempPath = new Path();
        drawPhotoList = new CopyOnWriteArrayList<>();
        drawShapeList = new CopyOnWriteArrayList<>();
        undoList = new CopyOnWriteArrayList<>();
        drawPathList = new CopyOnWriteArrayList();
    }

    public void clearAndSetNull() {
        clear();
        mTempPath = null;
        drawPhotoList = null;
        drawShapeList = null;
        undoList = null;
        drawPathList = null;
    }

    public void clear() {
        if (null != drawPhotoList) {
            drawPhotoList.clear();
        }
        if (null != drawShapeList) {
            drawShapeList.clear();
        }
        if (null != undoList) {
            undoList.clear();
        }
        if (null != drawPathList) {
            drawPathList.clear();
        }
    }
}
