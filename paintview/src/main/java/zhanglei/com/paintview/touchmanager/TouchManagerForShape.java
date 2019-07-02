package zhanglei.com.paintview.touchmanager;

import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;

import zhanglei.com.paintview.DrawTypeEnum;
import zhanglei.com.paintview.bean.DrawDataMemento;
import zhanglei.com.paintview.bean.DrawShapeData;


/**
 * 类名称：TouchManagerForShape
 * 类描述：处理绘制几何图形的Touch事件
 * 创建人：lei.zhang
 * 创建时间：on 2019/6/19
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class TouchManagerForShape extends BaseTouchManager {

    private float startX = 0;

    private float startY = 0;

    private float endX = 0;

    private float endY = 0;

    public static float SCALE_MIN_LEN = 50;//几何图形外接矩形最小宽高尺寸(px)


    @Override
    protected void onTouchUp(MotionEvent event) {
        Rect rect = getVisibleRect();
        endX = Math.max(Math.min(event.getX(), rect.right), rect.left);
        endY = Math.max(Math.min(event.getY(), rect.bottom), rect.top);
        event.setLocation(endX, endY);
        buildFinalShape(mDataContainer.mCurDrawShape);
        mDataContainer.mDrawShapeList.add(mDataContainer.mCurDrawShape);

        //几何数据添加至备忘录
        if (null != mDataContainer.mCurDrawShape) {
            mStepControler.removeMementoListItemsAfterCurIndex();
            mStepControler.addMemento(mDataContainer.mCurDrawShape.createDrawDataMemento(DrawDataMemento.ADD, this));
            mDataContainer.curIndex = mDataContainer.mMementoList.size() - 1;//更新curIndex至数组末尾
        }
        clearTempShapeData();
    }

    @Override
    protected void onTouchMove(MotionEvent event) {
        mDataContainer.mTempPath.reset();//画笔轨迹清空
        Rect rect = getVisibleRect();
        endX = Math.max(Math.min(event.getX(), rect.right), rect.left);
        endY = Math.max(Math.min(event.getY(), rect.bottom), rect.top);
        event.setLocation(endX, endY);
        buildTempShape(mDataContainer.mTempPath);
    }

    @Override
    protected void onTouchDown(MotionEvent event) {
        clearTempShapeData();
        startX = event.getX();
        startY = event.getY();
        mDataContainer.mCurDrawShape = new DrawShapeData();
        mDataContainer.mCurDrawShape.drawType = mPaintView.getDrawType();
        mDataContainer.mCurDrawShape.drawPath = new Path();
        mDataContainer.mCurDrawShape.srcPath = new Path();
        mDataContainer.mCurDrawShape.mRectSrc = new RectF(startX, startY, startX, startY);
        mDataContainer.mCurDrawShape.paint = new Paint(mPaintView.getPaint());//保存画笔
        mDataContainer.mCurDrawShape.mMatrix = new Matrix();
    }

    /**
     * 构建临时shape
     *
     * @param mPath
     */
    private void buildTempShape(Path mPath) {
        if (mPaintView.getDrawType() == DrawTypeEnum.LINE) {
            mPath.moveTo(startX, startY);
            mPath.lineTo(endX, endY);
        } else if (mPaintView.getDrawType() == DrawTypeEnum.CIRCLE) {
            mPath.addOval(new RectF(Math.min(startX, endX),
                            Math.min(startY, endY),
                            Math.max(startX, endX),
                            Math.max(startY, endY)),
                    Path.Direction.CCW);
        } else if (mPaintView.getDrawType() == DrawTypeEnum.RECT) {
            mPath.addRect(new RectF(Math.min(startX, endX),
                            Math.min(startY, endY),
                            Math.max(startX, endX),
                            Math.max(startY, endY)),
                    Path.Direction.CCW);
        }
    }

    /**
     * 构建最终shape
     *
     * @param mShape
     */
    private void buildFinalShape(DrawShapeData mShape) {
        //根据手指移动确定出来的外接矩形
        RectF rectF = new RectF(Math.min(startX, endX),
                Math.min(startY, endY),
                Math.max(startX, endX),
                Math.max(startY, endY));
        //1.以下代码是为了控制绘制圆或矩形的时候,保证它的外接矩形宽高不小于SCALE_MIN_LEN
        float finalStartXForRectOrCircle, finalEndXForRectOrCircle, finalStartYForRectOrCircle, finalEndYForRectOrCircle;
        if (endX > startX) {
            finalStartXForRectOrCircle = startX;
        } else {
            if (startX - endX > SCALE_MIN_LEN) {
                finalStartXForRectOrCircle = startX;
            } else {
                finalStartXForRectOrCircle = endX + SCALE_MIN_LEN;
            }
        }
        if (endX > startX) {
            if (endX - startX > SCALE_MIN_LEN) {
                finalEndXForRectOrCircle = endX;
            } else {
                finalEndXForRectOrCircle = startX + SCALE_MIN_LEN;
            }
        } else {
            finalEndXForRectOrCircle = endX;
        }
        if (endY > startY) {
            finalStartYForRectOrCircle = startY;
        } else {
            if (startY - endY > SCALE_MIN_LEN) {
                finalStartYForRectOrCircle = startY;
            } else {
                finalStartYForRectOrCircle = endY + SCALE_MIN_LEN;
            }
        }
        if (endY > startY) {
            if (endY - startY > SCALE_MIN_LEN) {
                finalEndYForRectOrCircle = endY;
            } else {
                finalEndYForRectOrCircle = startY + SCALE_MIN_LEN;
            }
        } else {
            finalEndYForRectOrCircle = endY;
        }
        //2.修正后的矩形或者圆外接矩形
        RectF rectFForRectOrCircle = new RectF(Math.min(finalStartXForRectOrCircle, finalEndXForRectOrCircle),
                Math.min(finalStartYForRectOrCircle, finalEndYForRectOrCircle),
                Math.max(finalStartXForRectOrCircle, finalEndXForRectOrCircle),
                Math.max(finalStartYForRectOrCircle, finalEndYForRectOrCircle));

        //1.以下代码是为了控制绘制直线的时候,保证它的外接矩形宽高不小于SCALE_MIN_LEN
        float finalStartXForLine, finalEndXForLine, finalStartYForLine, finalEndYForLine;
        if (rectF.height() < SCALE_MIN_LEN && rectF.width() < SCALE_MIN_LEN) {//直线的外接矩形宽高都小于最小限制
            finalStartXForLine = startX;
            finalStartYForLine = startY;
            float distanceX = Math.abs(endX - startX);
            float distanceY = Math.abs(endY - startY);
            if (distanceX > distanceY) {//直线与x轴夹角小于45度,控制x方向为最小长度
                if (endX > startX) {
                    finalEndXForLine = startX + SCALE_MIN_LEN;
                } else {
                    finalEndXForLine = startX - SCALE_MIN_LEN;
                }
                if (endY > startY) {
                    finalEndYForLine = startY + distanceY / distanceX * SCALE_MIN_LEN;
                } else {
                    finalEndYForLine = startY - distanceY / distanceX * SCALE_MIN_LEN;
                }
            } else {//直线与x轴夹角大于45度,控制y方向为最小长度
                if (endX > startX) {
                    finalEndXForLine = startX + distanceX / distanceY * SCALE_MIN_LEN;
                } else {
                    finalEndXForLine = startX - distanceX / distanceY * SCALE_MIN_LEN;
                }
                if (endY > startY) {
                    finalEndYForLine = startY + SCALE_MIN_LEN;
                } else {
                    finalEndYForLine = startY - SCALE_MIN_LEN;
                }
            }
        } else {//符合最小尺寸要求
            finalStartXForLine = startX;
            finalEndXForLine = endX;
            finalStartYForLine = startY;
            finalEndYForLine = endY;
        }
        //2.修正后的直线外接矩形
        RectF rectFForLine = new RectF(Math.min(finalStartXForLine, finalEndXForLine),
                Math.min(finalStartYForLine, finalEndYForLine),
                Math.max(finalStartXForLine, finalEndXForLine),
                Math.max(finalStartYForLine, finalEndYForLine));

        //以下代码是设置最终需要显示的几何图形
        if (mPaintView.getDrawType() == DrawTypeEnum.LINE) {
            mShape.srcPath.moveTo(finalStartXForLine, finalStartYForLine);
            mShape.srcPath.lineTo(finalEndXForLine, finalEndYForLine);
            mShape.drawPath.set(mShape.srcPath);
            mShape.mRectSrc.set(rectFForLine);
        } else if (mPaintView.getDrawType() == DrawTypeEnum.CIRCLE) {
            mShape.srcPath.addOval(rectFForRectOrCircle, Path.Direction.CCW);
            mShape.drawPath.set(mShape.srcPath);
            mShape.mRectSrc.set(rectFForRectOrCircle);
        } else if (mPaintView.getDrawType() == DrawTypeEnum.RECT) {
            mShape.srcPath.addRect(rectFForRectOrCircle, Path.Direction.CCW);
//            mShape.drawPath.set(mShape.srcPath);//5.1安卓系统这个方法在操作矩形的时候有bug所以采用new的方式
            mShape.drawPath = null;
            mShape.drawPath = new Path(mShape.srcPath);
            mShape.mRectSrc.set(rectFForRectOrCircle);
        }
    }

    private void clearTempShapeData() {
        startX = 0;
        startY = 0;
        endX = 0;
        endY = 0;
        mDataContainer.mTempPath.reset();
    }

    private Rect getVisibleRect() {
        Rect rect = new Rect();
        mPaintView.getLocalVisibleRect(rect);
        return rect;
    }

}
