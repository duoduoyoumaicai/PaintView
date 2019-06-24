package zhanglei.com.paintview.touchmanager;

import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;

import zhanglei.com.paintview.DrawTypeEnum;
import zhanglei.com.paintview.bean.DrawPathData;


/**
 * 类名称：TouchManagerForPath
 * 类描述：处理绘制曲线,实现橡皮功能的Touch事件
 * 创建人：lei.zhang
 * 创建时间：on 2019/6/19
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class TouchManagerForPath extends BaseTouchManager {

    private DrawPathData mCurDrawPathData = new DrawPathData();

    @Override
    protected void onTouchUp(MotionEvent event) {
        mDataManager.mTempPath.lineTo(event.getX(), event.getY());
        //将最终的path画到bitmap中
        mPaintView.getPaintCanvas().drawPath(mDataManager.mTempPath, mPaintView.getPaint());
        //重置mPath
        mDataManager.mTempPath.reset();
        //将当前的path保存的数据集
        mDataManager.mDrawPathList.add(mCurDrawPathData);
    }

    @Override
    protected void onTouchMove(MotionEvent event) {
        //构建临时Path
        mDataManager.mTempPath.quadTo(mDataManager.mCurX, mDataManager.mCurY,
                (event.getX() + mDataManager.mCurX) / 2.0F, (event.getY() + mDataManager.mCurY) / 2.0F);
        //构建当前Path,用于存储
        mCurDrawPathData.mPath.quadTo(mDataManager.mCurX, mDataManager.mCurY, (event.getX() + mDataManager.mCurX) / 2.0F,
                (event.getY() + mDataManager.mCurY) / 2.0F);
        //橡皮轨迹绘制到bitmap(没有在PaintView的onDraw方法绘制是因为:在这里绘制橡皮擦可以防止画出黑色轨迹)
        if (mPaintView.getDrawType() == DrawTypeEnum.ERASER) {
            mPaintView.getPaintCanvas().drawPath(mDataManager.mTempPath, mPaintView.getPaint());
        }

        mDataManager.mCurX = event.getX();
        mDataManager.mCurY = event.getY();
    }

    @Override
    protected void onTouchDown(MotionEvent event) {

        mDataManager.mTempPath.moveTo(event.getX(), event.getY());

        mDataManager.mCurX = event.getX();
        mDataManager.mCurY = event.getY();

        mCurDrawPathData = new DrawPathData();
        mCurDrawPathData.mPaint = new Paint(mPaintView.getPaint());//保存画笔
        mCurDrawPathData.mPath = new Path();
        mCurDrawPathData.mPath.moveTo(event.getX(), event.getY());
    }

}
