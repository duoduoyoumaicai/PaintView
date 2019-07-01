package zhanglei.com.paintview.bean;

import android.graphics.Matrix;

/**
 * 类名称：DrawDataMemento
 * 类描述：画板画的各种图形的数据备忘录类，记录用户操作了哪个对象，对这个对象做了什么操作
 * 创建人：lei.zhang
 * 创建时间：on 2019/6/17
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class DrawDataMemento {

    public static final int ADD = 1;//添加

    public static final int DELETE = 2;//删除

    public static final int TRANSFORM = 3;//变换

    private BaseDrawData mBaseDrawData;//操作了哪个对象

    private int doWhat = -1;//本次做了什么操作，默认什么都没做

    private Matrix startMatrix = null;//图片变换前矩阵

    private Matrix endMatrix = null;//图片变换后矩阵

    public DrawDataMemento(onAddIndexListener onAddIndexListener) {
        this.onAddIndexListener = onAddIndexListener;
        this.onAddIndexListener.addIndex();//创建一条备忘录就调用一次addIndex()
        this.onAddIndexListener = null;
    }

    private onAddIndexListener onAddIndexListener;

    public interface onAddIndexListener {
        void addIndex();
    }

    public Matrix getEndMatrix() {
        return endMatrix;
    }

    /**
     * 设置endMatrix,TouchUp的时候设置
     *
     * @param endMatrix
     */
    public void setEndMatrix(Matrix endMatrix) {
        this.endMatrix = new Matrix();
        this.endMatrix.set(endMatrix);
    }

    public Matrix getStartMatrix() {
        return startMatrix;
    }

    /**
     * 设置startMatrix,TouchDown的时候设置
     *
     * @param matrix
     */
    public void setStartMatrix(Matrix matrix) {
        this.startMatrix = new Matrix();
        this.startMatrix.set(matrix);
    }

    public BaseDrawData getBaseDrawData() {
        return mBaseDrawData;
    }

    public void setBaseDrawData(BaseDrawData mBaseDrawData) {
        this.mBaseDrawData = mBaseDrawData;
    }

    public int getDoWhat() {
        return doWhat;
    }

    public void setDoWhat(int doWhat) {
        this.doWhat = doWhat;
    }


}
