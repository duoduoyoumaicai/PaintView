package zhanglei.com.paintview.touchmanager;

import android.view.MotionEvent;

import zhanglei.com.paintview.DrawTypeEnum;
import zhanglei.com.paintview.bean.DrawDataMemento;
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

    @Override
    protected void onTouchUp(MotionEvent event) {
        mDataContainer.mTempPath.lineTo(event.getX(), event.getY());
        //将最终的path画到bitmap中
        mPaintView.getPaintCanvas().drawPath(mDataContainer.mTempPath, mPaintView.getPaint());

        DrawPathData drawPathData = new DrawPathData(mDataContainer.mTempPath, mPaintView.getPaint());
        //将当前的path保存的数据集
        mDataContainer.mDrawPathList.add(drawPathData);

        //添加一条备忘录
        mStepControler.addMemento(drawPathData.createDrawDataMemento(DrawDataMemento.ADD, this));
        //重置mPath
        mDataContainer.mTempPath.reset();
    }

    @Override
    protected void onTouchMove(MotionEvent event) {
        //构建临时Path
        mDataContainer.mTempPath.quadTo(mDataContainer.mCurX, mDataContainer.mCurY,
                (event.getX() + mDataContainer.mCurX) / 2.0F, (event.getY() + mDataContainer.mCurY) / 2.0F);
        //橡皮轨迹绘制到bitmap(没有在PaintView的onDraw方法绘制是因为:在这里绘制橡皮擦可以防止画出黑色轨迹)
        if (mPaintView.getDrawType() == DrawTypeEnum.ERASER) {
            mPaintView.getPaintCanvas().drawPath(mDataContainer.mTempPath, mPaintView.getPaint());
        }

        mDataContainer.mCurX = event.getX();
        mDataContainer.mCurY = event.getY();
    }

    @Override
    protected void onTouchDown(MotionEvent event) {

        mDataContainer.mTempPath.moveTo(event.getX(), event.getY());
        mDataContainer.mCurX = event.getX();
        mDataContainer.mCurY = event.getY();

    }

}
