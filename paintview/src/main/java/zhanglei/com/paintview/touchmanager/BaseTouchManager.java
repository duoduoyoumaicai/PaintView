package zhanglei.com.paintview.touchmanager;

import android.view.MotionEvent;

import zhanglei.com.paintview.PaintView;
import zhanglei.com.paintview.PaintViewDrawDataManager;


/**
 * 类名称：BaseTouchManager
 * 类描述：
 * 创建人：lei.zhang
 * 创建时间：on 2019/6/19
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public abstract class BaseTouchManager {
    protected PaintView mPaintView;
    protected PaintViewDrawDataManager mDataManager;

    protected void attach(PaintView paintView) {
        mPaintView = paintView;
        mDataManager = paintView.getDrawDataManager();
    }

    public void onTouch(MotionEvent event) {
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
    }

    protected abstract void onTouchUp(MotionEvent event);

    protected abstract void onTouchMove(MotionEvent event);

    protected abstract void onTouchDown(MotionEvent event);

    public void detach() {
        mDataManager = null;
        mPaintView = null;
    }
}
