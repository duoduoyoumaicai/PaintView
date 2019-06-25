package zhanglei.com.paintview.touchmanager;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.MotionEvent;

import zhanglei.com.paintview.Util;
import zhanglei.com.paintview.bean.DrawPhotoData;
import zhanglei.com.paintview.bean.DrawShapeData;
import zhanglei.com.paintview.bean.TransformData;

import static zhanglei.com.paintview.touchmanager.TouchManagerForShape.SCALE_MIN_LEN;

/**
 * 类名称：TouchManagerForSelectStatus
 * 类描述：处理选择状态下Touch事件,选择图片或几何图形,对他们进行缩放移动等操作
 * 创建人：lei.zhang
 * 创建时间：on 2019/6/19
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class TouchManagerForSelectStatus extends BaseTouchManager {

    private static float SCALE_MAX = 4.0f;//最大缩放倍数

    private static float SCALE_MIN = 0.2f;//最小缩放倍数

    private static final int SHAPE_ACTION_DRAG = 4;

    private static final int SHAPE_ACTION_SCALE = 5;

    private static final int SHAPE_ACTION_ROTATE = 6;

    private static final int SHAPE_ACTION_SCALE_VERTICAL = 7;

    private static final int SHAPE_ACTION_SCALE_HORIZONTAL = 8;

    private static final int PHOTO_ACTION_ROTATE = 3;

    private static final int PHOTO_ACTION_SCALE = 2;

    private static final int PHOTO_ACTION_DRAG = 1;

    private static final int ACTION_NONE = 0;

    private int actionMode;

    @Override
    protected void onTouchUp(MotionEvent event) {

    }

    @Override
    protected void onTouchMove(MotionEvent event) {
        if (actionMode == PHOTO_ACTION_DRAG) {
            onDragAction(mPaintView.getCurDrawPhoto(), (curX - preX), (curY - preY));
        } else if (actionMode == PHOTO_ACTION_ROTATE) {
            onRotateAction(mPaintView.getCurDrawPhoto());
        } else if (actionMode == PHOTO_ACTION_SCALE) {
            onScaleAction(mPaintView.getCurDrawPhoto());
        } else if (actionMode == SHAPE_ACTION_SCALE) {
            onScaleAction(mPaintView.getCurSelectShape());
        } else if (actionMode == SHAPE_ACTION_DRAG) {
            onDragAction(mPaintView.getCurSelectShape(), (curX - preX), (curY - preY));
        } else if (actionMode == SHAPE_ACTION_ROTATE) {
            onRotateAction(mPaintView.getCurSelectShape());
        } else if (actionMode == SHAPE_ACTION_SCALE_HORIZONTAL) {
            onScaleHorizontal(mPaintView.getCurSelectShape());
        } else if (actionMode == SHAPE_ACTION_SCALE_VERTICAL) {
            onScaleVertical(mPaintView.getCurSelectShape());
        }
        preX = curX;
        preY = curY;
        mPaintView.invalidate();
    }

    @Override
    protected void onTouchDown(MotionEvent event) {
        downX = curX;
        downY = curY;
        float[] downPoint = new float[]{downX, downY};
        if (isInMarkRect(downPoint)) {//点中了边角图标
            mPaintView.invalidate();
            return;
        } else {//没有点中图标
            TransformData transformData = selectWhat(downPoint);
            if (null == transformData) {//没有选中图片也没有选中shape
                mPaintView.setCurSelectShape(null);
                mPaintView.setCurDrawPhoto(null);
            } else if (transformData instanceof DrawShapeData) {//平移几何图形
                mPaintView.setCurSelectShape((DrawShapeData) transformData);
                mPaintView.setCurDrawPhoto(null);
            } else if (transformData instanceof DrawPhotoData) {//平移图片
                mPaintView.setCurSelectShape(null);
                mPaintView.setCurDrawPhoto((DrawPhotoData) transformData);
            }
        }
    }

    /**
     * 判断是否点到缩放删除等操作图标，然后做出对应的响应
     *
     * @param downPoint
     * @return
     */
    public boolean isInMarkRect(float[] downPoint) {
        //1.判断是否在当前选中shape缩放图标区域内
        if (mDataManager.shapeScaleRect01.contains(downPoint[0], (int) downPoint[1])
                || mDataManager.shapeScaleRect03.contains(downPoint[0], (int) downPoint[1])) {
            actionMode = SHAPE_ACTION_SCALE;
            return true;
        }

        //几何图形，判断是否上下缩放
        if (mDataManager.shapeScaleRect04.contains(downPoint[0], downPoint[1])
                || mDataManager.shapeScaleRect06.contains(downPoint[0], downPoint[1])) {
            actionMode = SHAPE_ACTION_SCALE_VERTICAL;
            return true;
        }

        //几何图形，是否左右缩放
        if (mDataManager.shapeScaleRect05.contains(downPoint[0], downPoint[1])
                || mDataManager.shapeScaleRect07.contains(downPoint[0], downPoint[1])) {
            actionMode = SHAPE_ACTION_SCALE_HORIZONTAL;
            return true;
        }

        //2.判断是否在当前选中shape删除图标区域内，如果在就做shape删除操作
        if (mDataManager.shapeDeleteRect.contains(downPoint[0], (int) downPoint[1])) {
            mDataManager.mDrawShapeList.remove(mPaintView.getCurSelectShape());
            mPaintView.setCurSelectShape(null);
            actionMode = ACTION_NONE;
            mPaintView.setSelectShape(false);
            return true;
        }
        //3.判断是否在当前选中图片缩放图标区域内
        if (mDataManager.photoScaleRect01.contains(downPoint[0], (int) downPoint[1])
                || mDataManager.photoScaleRect02.contains(downPoint[0], (int) downPoint[1])) {
            actionMode = PHOTO_ACTION_SCALE;
            return true;
        }
        //4.判断是否在当前选中图片删除图标区域内，如果在就做图片删除操作
        if (mDataManager.photoDeleteRect.contains(downPoint[0], (int) downPoint[1])) {
            mDataManager.mDrawPhotoList.remove(mPaintView.getCurDrawPhoto());
            mPaintView.setCurDrawPhoto(null);
            actionMode = ACTION_NONE;
            mPaintView.setSelectPhoto(false);
            return true;
        }
        //5.判断是否在当前选中图片旋转图标区域内
        if (mDataManager.photoRotateRect.contains(downPoint[0], (int) downPoint[1])) {
            actionMode = PHOTO_ACTION_ROTATE;
            return true;
        }
        //6.判断是否在当前选中shape旋转图标区域内
        if (mDataManager.shapeRotateRect.contains(downPoint[0], (int) downPoint[1])) {
            actionMode = SHAPE_ACTION_ROTATE;
            return true;
        }
        return false;
    }

    /**
     * 判断选择了什么
     *
     * @param downPoint
     */
    public TransformData selectWhat(float[] downPoint) {
        //1.判断是否选中当前shape
        if (isInRect(mPaintView.getCurSelectShape(), downPoint)) {
            actionMode = SHAPE_ACTION_DRAG;
            mPaintView.setSelectShape(true);
            return mPaintView.getCurSelectShape();
        }

        //2.遍历shape集合看看选中了哪个shape
        DrawShapeData clickShape = null;
        for (int i = mDataManager.mDrawShapeList.size() - 1; i >= 0; i--) {
            DrawShapeData drawShapeData = mDataManager.mDrawShapeList.get(i);
            if (isInRect(drawShapeData, downPoint)) {
                clickShape = drawShapeData;
                break;
            }
        }
        if (clickShape != null) {
            actionMode = SHAPE_ACTION_DRAG;
            mPaintView.setSelectShape(true);
            return clickShape;
        }

        //3.判断是否选中当前图片
        if (isInRect(mPaintView.getCurDrawPhoto(), downPoint)) {
            actionMode = PHOTO_ACTION_DRAG;
            mPaintView.setSelectPhoto(true);
            return mPaintView.getCurDrawPhoto();
        }
        //4.遍历图片集合看看选中了哪个图片
        DrawPhotoData clickPhoto = null;
        //如果没有选中当前图片就循环判断所有的图片看看选中了哪一个
        for (int i = mDataManager.mDrawPhotoList.size() - 1; i >= 0; i--) {
            DrawPhotoData drawPhotoData = mDataManager.mDrawPhotoList.get(i);
            if (isInRect(drawPhotoData, downPoint)) {
                clickPhoto = drawPhotoData;
                break;
            }
        }
        if (clickPhoto != null) {
            actionMode = PHOTO_ACTION_DRAG;
            mPaintView.setSelectPhoto(true);
            return clickPhoto;
        }
        //5.前面的情况都不符合,没选中shape也没选中图片
        actionMode = ACTION_NONE;
        mPaintView.setSelectPhoto(false);
        mPaintView.setSelectShape(false);
        return null;
    }

    /**
     * 首先利用TransformData对应变换矩阵的逆矩阵将触摸点逆变换，
     * ，由于是图片和触摸点是同样的逆变换，可通过判断该触摸点逆变换后的点是否在TransformData中来判断，当前的触摸点是否在变换后的图片中
     *
     * @param downPoint
     * @return
     */
    protected boolean isInRect(TransformData transformData, float[] downPoint) {
        if (null != transformData) {
            float[] invertPoint = new float[2];//逆变换后的点击点数组
            Matrix invertMatrix = new Matrix();//当前Matrix矩阵的逆矩阵
            transformData.mMatrix.invert(invertMatrix);//通过当前Matrix得到对应的逆矩阵数据
            invertMatrix.mapPoints(invertPoint, downPoint);//通过逆矩阵变化得到逆变换后的点击点
            return transformData.mRectSrc.contains(invertPoint[0], invertPoint[1]);//判断逆变换后的点击点是否在图像初始RectF内
        } else {
            return false;
        }
    }


    /**
     * 移动操作
     *
     * @param distanceX
     * @param distanceY
     */
    public void onDragAction(TransformData transformData, float distanceX, float distanceY) {
        if (transformData == null) return;
        transformData.mMatrix.postTranslate(distanceX, distanceY);
        if (transformData instanceof DrawShapeData) {//shape
            ((DrawShapeData) transformData).drawPath.set(((DrawShapeData) transformData).srcPath);
            ((DrawShapeData) transformData).drawPath.transform(transformData.mMatrix);
        }
    }

    /**
     * 缩放操作
     */
    private void onScaleAction(TransformData transformData) {
        if (transformData == null) return;
        float[] corners = Util.calculateCorners(transformData);
        //放大
        //目前触摸点与图片显示中心距离
        float a = (float) Math.sqrt(Math.pow(curX - corners[8], 2) + Math.pow(curY - corners[9], 2));
        //目前上次图标与图片显示中心距离
        float b = (float) Math.sqrt(Math.pow(corners[4] - corners[8], 2) + Math.pow(corners[5] - corners[9], 2));
        //设置Matrix缩放参数
        double photoLen = Math.sqrt(Math.pow(transformData.mRectSrc.width(), 2) + Math.pow(transformData.mRectSrc.height(), 2));
        if (transformData instanceof DrawShapeData) {
            if (a >= SCALE_MIN_LEN / 2 && a <= Util.getScreenSize(mPaintView.getContext()).x / 2) {
                //这种计算方法可以保持图标坐标与触摸点同步缩放
                float scale = a / b;
                transformData.mMatrix.postScale(scale, scale, corners[8], corners[9]);
                ((DrawShapeData) transformData).drawPath.set(((DrawShapeData) transformData).srcPath);
                ((DrawShapeData) transformData).drawPath.transform(transformData.mMatrix);
            }
        } else {
            if (a >= photoLen / 2 * SCALE_MIN && a >= SCALE_MIN_LEN / 2 && a <= photoLen / 2 * SCALE_MAX) {
                //这种计算方法可以保持图标坐标与触摸点同步缩放
                float scale = a / b;
                transformData.mMatrix.postScale(scale, scale, corners[8], corners[9]);
            }
        }
    }

    /**
     * 横向缩放
     *
     * @param transformData
     */
    private void onScaleHorizontal(TransformData transformData) {
        if (transformData == null) return;
        float[] corners = Util.calculateCorners(transformData);
        float a = (float) Math.abs(curX - corners[8]);//目前触摸点与图片显示中心x方向距离
        float b = Math.abs(corners[2] - corners[0]) / 2;//x方向距中心的长度
        if (transformData instanceof DrawShapeData) {
            if (a >= SCALE_MIN_LEN / 2 && a <= Util.getScreenSize(mPaintView.getContext()).x / 2) {
                float scale = a / b;
                transformData.mMatrix.postScale(scale, 1, corners[8], corners[9]);
                ((DrawShapeData) transformData).drawPath.set(((DrawShapeData) transformData).srcPath);
                ((DrawShapeData) transformData).drawPath.transform(transformData.mMatrix);
            }
        }
    }

    /**
     * 纵向缩放
     *
     * @param transformData
     */
    private void onScaleVertical(TransformData transformData) {
        if (transformData == null) return;
        float[] corners = Util.calculateCorners(transformData);
        float a = (float) Math.abs(curY - corners[9]);//目前触摸点与图片显示中心y方向距离
        float b = Math.abs(corners[7] - corners[1]) / 2;//x方向距中心的长度
        if (transformData instanceof DrawShapeData) {
            if (a >= SCALE_MIN_LEN / 2 && a <= Util.getScreenSize(mPaintView.getContext()).y / 2) {
                float scale = a / b;
                transformData.mMatrix.postScale(1, scale, corners[8], corners[9]);
                ((DrawShapeData) transformData).drawPath.set(((DrawShapeData) transformData).srcPath);
                ((DrawShapeData) transformData).drawPath.transform(transformData.mMatrix);
            }
        }
    }

    /**
     * 旋转操作
     */
    private void onRotateAction(TransformData transformData) {
        if (transformData == null) return;
        float[] corners = Util.calculateCorners(transformData);
        //旋转
        //根据移动坐标的变化构建两个向量，以便计算两个向量角度.
        PointF preVector = new PointF();
        PointF curVector = new PointF();
        preVector.set((preX - corners[8]), preY - corners[9]);//旋转后向量
        curVector.set(curX - corners[8], curY - corners[9]);//旋转前向量
        //计算向量长度
        double preVectorLen = Util.getVectorLength(preVector);
        double curVectorLen = Util.getVectorLength(curVector);
        //计算两个向量的夹角.(余弦值)
        double cosAlpha = (preVector.x * curVector.x + preVector.y * curVector.y)
                / (preVectorLen * curVectorLen);
        //由于计算误差，可能会带来略大于1的cos，例如
        if (cosAlpha > 1.0f) {
            cosAlpha = 1.0f;
        }
        //Math.acos(cosAlpha)得到弧度,然后转化为角度。
        double dAngle = Math.acos(cosAlpha) * 180.0 / Math.PI;
        // 判断顺时针和逆时针.
        //判断方法其实很简单，这里的v1v2其实相差角度很小的。
        //先转换成单位向量
        preVector.x /= preVectorLen;
        preVector.y /= preVectorLen;
        curVector.x /= curVectorLen;
        curVector.y /= curVectorLen;
        //作curVector的逆时针垂直向量。
        PointF verticalVec = new PointF(curVector.y, -curVector.x);

        //判断这个垂直向量和v1的点积，点积>0表示俩向量夹角锐角。=0表示垂直，<0表示钝角
        float vDot = preVector.x * verticalVec.x + preVector.y * verticalVec.y;
        if (vDot > 0) {
            //v2的逆时针垂直向量和v1是锐角关系，说明v1在v2的逆时针方向。
        } else {
            dAngle = -dAngle;
        }
        transformData.mMatrix.postRotate((float) dAngle, corners[8], corners[9]);
        if (transformData instanceof DrawShapeData) {//shape
            ((DrawShapeData) transformData).drawPath.set(((DrawShapeData) transformData).srcPath);
            ((DrawShapeData) transformData).drawPath.transform(transformData.mMatrix);
        }
    }


}
