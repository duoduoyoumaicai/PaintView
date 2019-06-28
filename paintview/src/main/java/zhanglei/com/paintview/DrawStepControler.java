package zhanglei.com.paintview;

/**
 * 类名称：DrawStepControler
 * 类描述：绘制步骤控制器
 * 创建人：lei.zhang
 * 创建时间：on 2019/6/28
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class DrawStepControler {

    private PaintView mPaintView;

    private PaintViewDrawDataContainer mDrawDataContainer;

    public DrawStepControler(PaintView mPaintView) {
        this.mPaintView = mPaintView;
        this.mDrawDataContainer = mPaintView.getDrawDataContainer();
    }

    public void undo() {

    }

    public void redo() {

    }

}
