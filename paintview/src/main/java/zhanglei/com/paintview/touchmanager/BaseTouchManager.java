package zhanglei.com.paintview.touchmanager;

import android.view.MotionEvent;

import zhanglei.com.paintview.DrawStepControler;
import zhanglei.com.paintview.PaintView;
import zhanglei.com.paintview.PaintViewDrawDataContainer;
import zhanglei.com.paintview.bean.DrawDataMemento;


/**
 * 类名称：BaseTouchManager
 * 类描述：
 * 创建人：lei.zhang
 * 创建时间：on 2019/6/19
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public abstract class BaseTouchManager implements DrawDataMemento.onAddIndexListener {

    protected PaintView mPaintView;

    protected PaintViewDrawDataContainer mDataContainer;

    protected DrawStepControler mStepControler;

    protected float downX, downY, preX, preY, curX, curY;//手指触摸屏幕的坐标

    protected int[] location = new int[]{0, 0};


    protected void attach(PaintView paintView) {
        mPaintView = paintView;
        mDataContainer = paintView.getDrawDataContainer();
        mStepControler = paintView.getDrawStepControler();
    }

    public void onTouch(MotionEvent event) {
        if (null == mPaintView || null == mDataContainer) return;
        mPaintView.getLocationInWindow(location); //获取在当前窗口内的绝对坐标
        curX = event.getRawX() - location[0];
        curY = event.getRawY() - location[1];
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPaintView.setMove(false);
                onTouchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                mPaintView.setMove(true);
                onTouchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                mPaintView.setMove(false);
                onTouchUp(event);
                break;
        }
        preX = curX;
        preY = curY;
    }

    protected abstract void onTouchUp(MotionEvent event);

    protected abstract void onTouchMove(MotionEvent event);

    protected abstract void onTouchDown(MotionEvent event);

    public void detach() {
        mDataContainer = null;
        mPaintView = null;
    }

    @Override
    public void addIndex() {
        if (null == mDataContainer) return;
        mDataContainer.curIndex++;
    }
}
