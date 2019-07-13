package zhanglei.com.paintview.touchmanager;

import android.view.MotionEvent;
import android.view.View;

import zhanglei.com.paintview.PaintView;


/**
 * 类名称：PaintViewAttacher
 * 类描述：负责PaintView触摸事件的处理,通过识别触摸事件来构建Path,然后通知PaintView渲染
 * 创建人：lei.zhang
 * 创建时间：on 2019/6/17
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class PaintViewAttacher implements View.OnTouchListener {

    private PaintView mPaintView;
    private TouchManagerForPath touchManagerForPath;
    private TouchManagerForShape touchManagerForShape;
    private TouchManagerForSelectStatus touchManagerForSelectStatus;

    public PaintViewAttacher() {

    }

    public void attach(PaintView paintView) {
        mPaintView = paintView;
        mPaintView.setOnTouchListener(this);
        touchManagerForPath = new TouchManagerForPath();
        touchManagerForPath.attach(mPaintView);
        touchManagerForShape = new TouchManagerForShape();
        touchManagerForShape.attach(mPaintView);
        touchManagerForSelectStatus = new TouchManagerForSelectStatus();
        touchManagerForSelectStatus.attach(mPaintView);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mPaintView == null || mPaintView.isInEditMode() || mPaintView.getPaintCanvas() == null
                || mPaintView.getPaintBitmapRef() == null || mPaintView.getPaintBitmapRef().get() == null
                || mPaintView.getDrawDataContainer() == null) {
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mPaintView.setEdit(true);
        }
        switch (mPaintView.getDrawType()) {
            case PEN:
            case ERASER:
                touchManagerForPath.onTouch(event);
                break;
            case SELECT_STATUS:
                touchManagerForSelectStatus.onTouch(event);
                break;
            default:
                touchManagerForShape.onTouch(event);
                break;
        }
        mPaintView.invalidate();
        return true;
    }

    public void detach() {
        mPaintView = null;
        if (null != touchManagerForPath) {
            touchManagerForPath.detach();
        }
        if (null != touchManagerForShape) {
            touchManagerForShape.detach();
        }
        if (null != touchManagerForSelectStatus) {
            touchManagerForSelectStatus.detach();
        }
    }

}
