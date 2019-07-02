package zhanglei.com.paintview;

import zhanglei.com.paintview.bean.BaseDrawData;
import zhanglei.com.paintview.bean.DrawDataMemento;
import zhanglei.com.paintview.bean.DrawPathData;
import zhanglei.com.paintview.bean.DrawPhotoData;
import zhanglei.com.paintview.bean.DrawShapeData;

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

    private PaintViewDrawDataContainer mDataContainer;

    public DrawStepControler(PaintView mPaintView) {
        this.mPaintView = mPaintView;
        this.mDataContainer = mPaintView.getDrawDataContainer();
    }

    /**
     * 撤销
     */
    public void undo() {
        if (null != mDataContainer.mMementoList && mDataContainer.mMementoList.size() > 0
                && mDataContainer.curIndex >= 0 && mDataContainer.curIndex < mDataContainer.mMementoList.size()) {//curIndex=-1代表无法回退
            DrawDataMemento memento = mDataContainer.mMementoList.get(mDataContainer.curIndex);//得到当前索引的备忘录
            BaseDrawData baseDrawData = memento.getBaseDrawData();

            if (baseDrawData instanceof DrawShapeData) {//回退几何图形
                if (memento.getDoWhat() == DrawDataMemento.ADD) {//备忘录中记录添加，回退是之前的操作，也就是把添加的操作移除
                    mDataContainer.mDrawShapeList.remove((DrawShapeData) baseDrawData);
                } else if (memento.getDoWhat() == DrawDataMemento.DELETE) {//删除和添加同理，将删除的操作移除
                    mDataContainer.mDrawShapeList.add((DrawShapeData) baseDrawData);
                } else if (memento.getDoWhat() == DrawDataMemento.TRANSFORM) {
                    //回退变换操作的原理是将图形对象的matrix设置为原来的matrix
                    for (DrawShapeData shape : mDataContainer.mDrawShapeList) {
                        if (memento.getBaseDrawData().equals(shape)) {//找到与备忘录中相等的数据源，并修改
                            if (null != memento.getStartMatrix()) {
                                shape.mMatrix.set(memento.getStartMatrix());//设置为落笔的时候记录的matrix,图片和几何图形原理相同
                                shape.drawPath.set(shape.srcPath);
                                shape.drawPath.transform(shape.mMatrix);
                            }
                        }
                    }
                }
            } else if (baseDrawData instanceof DrawPhotoData) {//回退图片
                if (memento.getDoWhat() == DrawDataMemento.ADD) {
                    mDataContainer.mDrawPhotoList.remove((DrawPhotoData) baseDrawData);
                } else if (memento.getDoWhat() == DrawDataMemento.DELETE) {
                    mDataContainer.mDrawPhotoList.add((DrawPhotoData) baseDrawData);
                } else if (memento.getDoWhat() == DrawDataMemento.TRANSFORM) {
                    //回退变换操作的原理是将图形对象的matrix设置为原来的matrix
                    for (DrawPhotoData photo : mDataContainer.mDrawPhotoList) {
                        if (memento.getBaseDrawData().equals(photo)) {
                            if (null != memento.getStartMatrix()) {
                                photo.mMatrix.set(memento.getStartMatrix());
                            }
                        }
                    }
                }
            } else if (baseDrawData instanceof DrawPathData) {//回退曲线,橡皮擦
                mPaintView.renewPaintView();
                for (int i = 0; i < mDataContainer.curIndex; i++) {//遍历备忘录中curIndex之前(不包含当前)的所有画笔数据
                    BaseDrawData baseDrawData1 = mDataContainer.mMementoList.get(i).getBaseDrawData();
                    if (baseDrawData1 instanceof DrawPathData) {//筛选出所有的画笔数据画出来
                        mPaintView.getPaintCanvas().drawPath(((DrawPathData) baseDrawData1).mPath, ((DrawPathData) baseDrawData1).mPaint);
                    }
                }
            }
            mPaintView.invalidate();
            mDataContainer.curIndex--;

        }
    }

    /**
     * 重做
     */
    public void redo() {
        int maxMementoListIndex = mDataContainer.mMementoList.size() - 1;
        if (null != mDataContainer.mMementoList && mDataContainer.mMementoList.size() > 0
                && mDataContainer.curIndex < maxMementoListIndex && mDataContainer.curIndex >= -1) {//curIndex必须小于最大备忘录数组的index
            DrawDataMemento memento = mDataContainer.mMementoList.get(mDataContainer.curIndex + 1);//重做操作始终先获取
            BaseDrawData baseDrawData = memento.getBaseDrawData();
            if (baseDrawData instanceof DrawShapeData) {//重做几何图形
                if (memento.getDoWhat() == DrawDataMemento.ADD) {
                    mDataContainer.mDrawShapeList.add((DrawShapeData) baseDrawData);
                } else if (memento.getDoWhat() == DrawDataMemento.DELETE) {//备忘录里面记录删除，那么就删除
                    mDataContainer.mDrawShapeList.remove((DrawShapeData) baseDrawData);
                } else if (memento.getDoWhat() == DrawDataMemento.TRANSFORM) {
                    for (DrawShapeData shape : mDataContainer.mDrawShapeList) {
                        if (memento.getBaseDrawData().equals(shape)) {
                            if (null != memento.getEndMatrix()) {
                                shape.mMatrix.set(memento.getEndMatrix());
                                shape.drawPath.set(shape.srcPath);
                                shape.drawPath.transform(shape.mMatrix);
                            }
                        }
                    }
                }
            } else if (baseDrawData instanceof DrawPhotoData) {//重做图片
                if (memento.getDoWhat() == DrawDataMemento.ADD) {
                    mDataContainer.mDrawPhotoList.add((DrawPhotoData) baseDrawData);
                } else if (memento.getDoWhat() == DrawDataMemento.DELETE) {
                    mDataContainer.mDrawPhotoList.remove((DrawPhotoData) baseDrawData);
                } else if (memento.getDoWhat() == DrawDataMemento.TRANSFORM) {
                    //前进变换操作的原理是将图形对象的matrix设置为变换后的matrix
                    for (DrawPhotoData photo : mDataContainer.mDrawPhotoList) {
                        if (memento.getBaseDrawData().equals(photo)) {
                            if (null != memento.getEndMatrix()) {
                                photo.mMatrix.set(memento.getEndMatrix());
                            }
                        }
                    }
                }
            } else if (baseDrawData instanceof DrawPathData) {//重做曲线
                mPaintView.renewPaintView();
                for (int i = 0; i <= mDataContainer.curIndex + 1; i++) {//curIndex表示当前的index,redo操作要画出curIndex之后一步所有的画笔
                    BaseDrawData baseDrawData1 = mDataContainer.mMementoList.get(i).getBaseDrawData();
                    if (baseDrawData1 instanceof DrawPathData) {//筛选出所有的画笔数据画出来
                        mPaintView.getPaintCanvas().drawPath(((DrawPathData) baseDrawData1).mPath, ((DrawPathData) baseDrawData1).mPaint);
                    }
                }

            }

            mPaintView.invalidate();
            mDataContainer.curIndex++;//处理shape ,photo的备忘录索引逻辑
        }

    }

    /**
     * 移除curIndex之后的所有undoList的 item
     */
    public void removeUndoListItemsAfterCurIndex() {
        int maxUndoListIndex = mDataContainer.mMementoList.size() - 1;
        if (mDataContainer.curIndex != maxUndoListIndex) {//移除curIndex之后的所有历史记录
            int index = maxUndoListIndex;
            while (index > mDataContainer.curIndex) {
                mDataContainer.mMementoList.remove(index);
                index--;
            }
        }

    }

    /**
     * 添加一条备忘录
     *
     * @param drawDataMemento
     */
    public void addMemento(DrawDataMemento drawDataMemento) {
        mDataContainer.mMementoList.add(drawDataMemento);
    }
}
