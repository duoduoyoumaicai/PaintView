package zhanglei.com.paintview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.concurrent.CopyOnWriteArrayList;

import zhanglei.com.paintview.bean.DrawBgData;
import zhanglei.com.paintview.bean.DrawDataMemento;
import zhanglei.com.paintview.bean.DrawPhotoData;
import zhanglei.com.paintview.bean.DrawShapeData;
import zhanglei.com.paintview.touchmanager.PaintViewAttacher;

import static zhanglei.com.paintview.DrawTypeEnum.ERASER;
import static zhanglei.com.paintview.DrawTypeEnum.PEN;
import static zhanglei.com.paintview.DrawTypeEnum.SELECT_STATUS;
import static zhanglei.com.paintview.PaintViewDrawDataContainer.DEFAULT_PHOTO_HEIGHT;
import static zhanglei.com.paintview.PaintViewDrawDataContainer.SCALE_MAX;


/**
 * 类名称：PaintView
 * 类描述：负责View的渲染
 * 创建人：lei.zhang
 * 创建时间：on 2019/6/17
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class PaintView extends View implements ViewTreeObserver.OnGlobalLayoutListener, DrawDataMemento.onAddIndexListener {

    private final String TAG = getClass().getSimpleName();

    private DrawTypeEnum mDrawType = DrawTypeEnum.PEN;//画板当前的绘制类型

    private int mWidth, mHeight;//当前view的宽度和高度

    private Paint mPaint;//绘制曲线几何图形的画笔

    private float mPaintWidth = 2F;//笔的宽度

    private float mRushPaintWidth = 30F;//橡皮笔的宽度

    private int mPaintColor = Color.BLACK;//笔的色值

    private float mPaintAlpha = 1.0f; // 画笔的透明度

    private Bitmap mRushIconBitmap;//橡皮擦样式的bitmap

    private Matrix mBaseMatrix = new Matrix(); //绘制曲线几何图形的基础矩阵

    private Matrix mRushMatrix = new Matrix(); //绘制橡皮图标矩阵

    private float alpha = 1.0f; // 画笔的透明度

    private Canvas mPaintCanvas;

    private Reference<Bitmap> mPaintViewBitmapRef;//绘制内容

    private int left = 0; // 背景的left

    private int top = 0; // 背景的top

    private Bitmap.Config mConfig = Bitmap.Config.ARGB_4444;

    private PaintViewDrawDataContainer mDataContainer;

    private PaintViewAttacher mPaintViewAttacher;//处理PaintView的触摸事件

    private boolean isMove = false;

    private boolean isSelectPhoto;//选择了图片

    private boolean isSelectShape;//选择了几何图形

    private Paint mBoardPaint = null;//画图片的边线

    private DrawStepControler mStepControler;

    /* 标识，是否进行过绘制 */
    private boolean isEdit = false;

    public PaintView(Context context) {
        this(context, null);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PaintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDataContainer = new PaintViewDrawDataContainer(this);
        mStepControler = new DrawStepControler(this);

        //初始化绘制图形的画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);// 抗锯齿
        mPaint.setColor(mPaintColor);
        mPaint.setAlpha((int) (mPaintAlpha * 255));// 设置透明度为0
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);// 圆头
        mPaint.setStrokeJoin(Paint.Join.ROUND);// 结合方式，平滑
        mPaint.setStrokeWidth(mPaintWidth);// 设置空心边框宽,绘制图形画笔宽度

        mBoardPaint = new Paint();
        mBoardPaint.setColor(Color.parseColor("#019982"));
        mBoardPaint.setStrokeWidth(5);
        mBoardPaint.setStyle(Paint.Style.STROKE);

        setRushBitmap(BitmapFactory.decodeResource(getResources(),
                R.drawable.icon_rush_bg).copy(mConfig, true), (int) mRushPaintWidth);

        mPaintViewAttacher = new PaintViewAttacher();
        mPaintViewAttacher.setOnDeleteListener(mOnDeleteListener);
        mPaintViewAttacher.attach(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (null == mDataContainer) return;
        drawBg(canvas);//画背景

        drawPhoto(canvas);//画添加到画板中的图片

        drawTemp(canvas);//画笔移动过程的曲线和几何图形

        drawFinalPath(canvas);//画最终曲线和橡皮擦

        drawFinalShape(canvas);//画最终几何图形

        drawRushIcon(canvas);//绘制橡皮移动过程中的图标
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @SuppressLint("NewApi")
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    @SuppressLint("NewApi")
    @Override
    public void onGlobalLayout() {
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
        if (this.mWidth == 0 || this.mHeight == 0) {
            this.mWidth = this.getWidth();
            this.mHeight = this.getHeight();
        }
        if ((mPaintViewBitmapRef == null || mPaintViewBitmapRef.get() == null)) {
            initPaintCanvas();
        }
    }

    private void initPaintCanvas() {

        if (this.mWidth > 0 && this.mHeight > 0) {
            Bitmap bitmap = Bitmap.createBitmap(this.mWidth, this.mHeight, this.mConfig);
            this.mPaintViewBitmapRef = new SoftReference<>(bitmap);
        }

        if (this.mPaintViewBitmapRef != null && mPaintViewBitmapRef.get() != null) {
            try {
                mPaintCanvas = new Canvas(mPaintViewBitmapRef.get());
            } catch (Exception e) {
                Log.e(TAG, "mPaintViewBitmapRef.get() == null");
            }
            this.invalidate();
        }

    }

    /**
     * 绘制背景
     *
     * @param canvas
     */
    private void drawBg(Canvas canvas) {
        if (null != mDataContainer.mPaintViewBg && null != mDataContainer.mPaintViewBg.bitmap) {
            canvas.drawBitmap(mDataContainer.mPaintViewBg.bitmap, mDataContainer.mPaintViewBg.mMatrix, null);
        }
    }

    /**
     * 将Bitmap封装成MCDrawPhoto
     *
     * @param sampleBM
     */
    public void addPhotoByBitmap(Bitmap sampleBM) {
        addPhotoByBitmap(sampleBM, false, null);
    }

    public void addPhotoByBitmap(Bitmap sampleBM, Object flag) {
        addPhotoByBitmap(sampleBM, false, flag);
    }

    /**
     * 将Bitmap封装成MCDrawPhoto,
     *
     * @param sampleBM
     * @param isFullScreen 是否全屏
     */
    public void addPhotoByBitmap(Bitmap sampleBM, boolean isFullScreen) {
        addPhotoByBitmap(sampleBM, isFullScreen, null);
    }

    public void addPhotoByBitmap(Bitmap sampleBM, boolean isFullScreen, Object flag) {
        setDrawType(SELECT_STATUS);
        if (sampleBM != null) {
            DrawPhotoData newRecord = initDrawPhoto(sampleBM, isFullScreen);
            newRecord.flag = flag;
            setCurSelectPhoto(newRecord);
        }
    }

    /**
     * 将图片Bitmap封装成MCDrawPhoto
     *
     * @param bitmap
     * @param isFullScreen 是否全屏
     * @return
     */
    private DrawPhotoData initDrawPhoto(Bitmap bitmap, boolean isFullScreen) {
        final DrawPhotoData drawPhoto = new DrawPhotoData();
        drawPhoto.bitmap = bitmap;
        drawPhoto.mRectSrc = new RectF(0, 0, drawPhoto.bitmap.getWidth(), drawPhoto.bitmap.getHeight());
        drawPhoto.mMatrix = new Matrix();
        //将图片调整到合适大小
        float scale;
        if (isFullScreen) {
            float scaleH = (mWidth - 50) / Float.valueOf(drawPhoto.bitmap.getWidth());//横向满屏边距25px,需要方大倍数
            float scaleV = (mHeight - 50) / Float.valueOf(drawPhoto.bitmap.getHeight());//竖向满屏边距25px,需要方大倍数
            scale = scaleH > scaleV ? scaleV : scaleH;
        } else {
            scale = DEFAULT_PHOTO_HEIGHT / drawPhoto.bitmap.getHeight();
        }
        scale = scale > SCALE_MAX ? SCALE_MAX : scale;//如果计算出的放大系数大于了最大放大系数,就取最大放大系数为实际放大系数
        drawPhoto.mMatrix.postScale(scale, scale);
        drawPhoto.mMatrix.postTranslate(mWidth / 2 - drawPhoto.bitmap.getWidth() * scale / 2,
                mHeight / 2 - drawPhoto.bitmap.getHeight() * scale / 2);
        //添加一条备忘录
        mStepControler.removeMementoListItemsAfterCurIndex();
        mStepControler.addMemento(drawPhoto.createDrawDataMemento(DrawDataMemento.ADD, this));
        mDataContainer.curIndex = mDataContainer.mMementoList.size() - 1;//重置curIndex
        if (getOnIndexChangedListener() != null) {
            getOnIndexChangedListener().onIndexChanged(mDataContainer.curIndex, mDataContainer.mMementoList.size());
        }
        return drawPhoto;
    }

    /**
     * 设置当前图片
     *
     * @param drawPhotoData
     */
    public void setCurSelectPhoto(DrawPhotoData drawPhotoData) {
        if (null != drawPhotoData) {
            isSelectPhoto = true;
            setCurSelectShape(null);
        } else {
            isSelectPhoto = false;
        }
        mDataContainer.mDrawPhotoList.remove(drawPhotoData);
        mDataContainer.mDrawPhotoList.add(drawPhotoData);
        mDataContainer.mCurSelectPhoto = drawPhotoData;
        invalidate();
    }


    /**
     * 绘制图片和图片的边线所有的内容
     *
     * @param canvas
     */
    private void drawPhoto(Canvas canvas) {
        if (mDataContainer.mDrawPhotoList != null) {
            for (DrawPhotoData record : mDataContainer.mDrawPhotoList) {
                if (record != null) {
                    canvas.drawBitmap(record.bitmap, record.mMatrix, null);
                }
            }
            if (!isSelectShape && isSelectPhoto && mDataContainer.mCurSelectPhoto != null) {
                float[] photoCorners = Util.calculateCorners(mDataContainer.mCurSelectPhoto);//计算图片四个角点和中心点
                drawBoard(canvas, photoCorners);//绘制图形边线
                drawPhotoMarks(canvas, photoCorners);//绘制边角图片
            }
        }
    }

    /**
     * 绘制图像边线（由于图形旋转的时候导致边线不一定是矩形，所以用Path绘制边线）
     *
     * @param canvas
     * @param photoCorners
     */
    private void drawBoard(Canvas canvas, float[] photoCorners) {
        Path photoBorderPath = new Path();
        photoBorderPath.moveTo(photoCorners[0], photoCorners[1]);
        photoBorderPath.lineTo(photoCorners[2], photoCorners[3]);
        photoBorderPath.lineTo(photoCorners[4], photoCorners[5]);
        photoBorderPath.lineTo(photoCorners[6], photoCorners[7]);
        photoBorderPath.lineTo(photoCorners[0], photoCorners[1]);
        canvas.drawPath(photoBorderPath, mBoardPaint);
    }

    /**
     * 绘制边角操作图标
     *
     * @param canvas
     * @param photoCorners
     */
    private void drawPhotoMarks(Canvas canvas, float[] photoCorners) {
        float x;
        float y;
        x = photoCorners[0] - mDataContainer.photoScaleRectLU.width() / 4;
        y = photoCorners[1] - mDataContainer.photoScaleRectLU.height() / 4;
        mDataContainer.photoScaleRectLU.offsetTo(x, y);//偏移到x,y坐标
        canvas.drawBitmap(mDataContainer.scaleMarkBM, x, y, null);//左上

        x = photoCorners[2] - mDataContainer.photoDeleteRectRU.width() / 2;
        y = photoCorners[3] - mDataContainer.photoDeleteRectRU.height() / 2;
        mDataContainer.photoDeleteRectRU.offsetTo(x, y);
        canvas.drawBitmap(mDataContainer.deleteMarkBM, x, y, null);//右上

        x = photoCorners[4] - mDataContainer.photoRotateRectRB.width() / 2;
        y = photoCorners[5] - mDataContainer.photoRotateRectRB.height() / 2;
        mDataContainer.photoRotateRectRB.offsetTo(x, y);//偏移到x,y坐标
        canvas.drawBitmap(mDataContainer.rotateMarkBM, x, y, null);//右下

        x = photoCorners[6] - mDataContainer.photoScaleRectLB.width() / 4;
        y = photoCorners[7] - mDataContainer.photoScaleRectLB.height() / 4;
        mDataContainer.photoScaleRectLB.offsetTo(x, y);//偏移到x,y坐标
        canvas.drawBitmap(mDataContainer.scaleMarkBM, x, y, null);//左下
    }

    /**
     * 画笔移动过程中绘制Path或几何图形
     *
     * @param canvas
     */
    private void drawTemp(Canvas canvas) {
        if (mDataContainer.mTempPath != null && !mDataContainer.mTempPath.isEmpty() && mDrawType != ERASER) {
            canvas.drawPath(mDataContainer.mTempPath, mPaint);
        }
    }

    public void setCurSelectShape(DrawShapeData drawShapeData) {
        if (null != drawShapeData) {
            isSelectShape = true;
            setCurSelectPhoto(null);
        } else {
            isSelectShape = false;
        }
        mDataContainer.mDrawShapeList.remove(drawShapeData);
        mDataContainer.mDrawShapeList.add(drawShapeData);
        mDataContainer.mCurSelectShape = drawShapeData;
        invalidate();
    }

    /**
     * 绘制shape和shape的边线所有的内容
     *
     * @param canvas
     */
    private void drawFinalShape(Canvas canvas) {
        if (mDataContainer.mDrawShapeList != null) {
            for (DrawShapeData drawShapeData : mDataContainer.mDrawShapeList) {
                if (drawShapeData != null) {
                    canvas.drawPath(drawShapeData.drawPath, drawShapeData.paint);
                }
            }
        }

        if (!isSelectPhoto && isSelectShape && mDataContainer.mCurSelectShape != null) {
            float[] photoCorners = Util.calculateCorners(mDataContainer.mCurSelectShape);//计算图片四个角点和中心点
            drawBoard(canvas, photoCorners);//绘制shape边线
            drawShapeMarks(canvas, photoCorners);//绘制边角图片
        }
    }

    /**
     * 绘制shape边角操作图标
     *
     * @param canvas
     * @param photoCorners
     */
    private void drawShapeMarks(Canvas canvas, float[] photoCorners) {
        float xLeftTop, yLeftTop, xRightTop, yRightTop, xRightBottom, yRightBottom, xLeftBottom, yLeftBottom;
//        float yMiddleTop, xMiddleTop, xMiddleRight, yMiddleRight, xMiddleBottom, yMiddleBottom, xMidlleLeft, yMiddleLeft;
        xLeftTop = photoCorners[0] - mDataContainer.shapeScaleRectLU.width() / 4;
        yLeftTop = photoCorners[1] - mDataContainer.shapeScaleRectLU.height() / 4;
        mDataContainer.shapeScaleRectLU.offsetTo(xLeftTop, yLeftTop);//偏移到x,y坐标
        canvas.drawBitmap(mDataContainer.scaleMarkBM, xLeftTop, yLeftTop, null);//左上

        xRightTop = photoCorners[2] - mDataContainer.shapeDeleteRectRU.width() / 2;
        yRightTop = photoCorners[3] - mDataContainer.shapeDeleteRectRU.height() / 2;
        mDataContainer.shapeDeleteRectRU.offsetTo(xRightTop, yRightTop);
        canvas.drawBitmap(mDataContainer.deleteMarkBM, xRightTop, yRightTop, null);//右上

        xRightBottom = photoCorners[4] - mDataContainer.shapeRotateRectRB.width() / 2;
        yRightBottom = photoCorners[5] - mDataContainer.shapeRotateRectRB.height() / 2;
        mDataContainer.shapeRotateRectRB.offsetTo(xRightBottom, yRightBottom);//偏移到x,y坐标
        canvas.drawBitmap(mDataContainer.rotateMarkBM, xRightBottom, yRightBottom, null);//右下

        xLeftBottom = photoCorners[6] - mDataContainer.shapeScaleRectLB.width() / 4;
        yLeftBottom = photoCorners[7] - mDataContainer.shapeScaleRectLB.height() / 4;
        mDataContainer.shapeScaleRectLB.offsetTo(xLeftBottom, yLeftBottom);//偏移到x,y坐标
        canvas.drawBitmap(mDataContainer.scaleMarkBM, xLeftBottom, yLeftBottom, null);//左下

        //几何图形的横向竖向缩放在旋转后感觉很奇怪,暂时将横竖向缩放禁用
//        xMiddleTop = (photoCorners[2] + photoCorners[0]) / 2 - mDataContainer.shapeScaleRectUM.width() / 4;
//        yMiddleTop = (photoCorners[3] + photoCorners[1]) / 2 - mDataContainer.shapeScaleRectUM.height() / 4;
//        mDataContainer.shapeScaleRectUM.offsetTo(xMiddleTop, yMiddleTop);
//        canvas.drawBitmap(mDataContainer.scaleMarkBM, xMiddleTop, yMiddleTop, null);//上中
//
//        xMiddleRight = (photoCorners[4] + photoCorners[2]) / 2 - mDataContainer.shapeScaleRectRM.width() / 4;
//        yMiddleRight = (photoCorners[5] + photoCorners[3]) / 2 - mDataContainer.shapeScaleRectRM.height() / 4;
//        mDataContainer.shapeScaleRectRM.offsetTo(xMiddleRight, yMiddleRight);
//        canvas.drawBitmap(mDataContainer.scaleMarkBM, xMiddleRight, yMiddleRight, null);//右中
//
//        xMiddleBottom = (photoCorners[4] + photoCorners[6]) / 2 - mDataContainer.shapeScaleRectBM.width() / 4;
//        yMiddleBottom = (photoCorners[5] + photoCorners[7]) / 2 - mDataContainer.shapeScaleRectBM.height() / 4;
//        mDataContainer.shapeScaleRectBM.offsetTo(xMiddleBottom, yMiddleBottom);
//        canvas.drawBitmap(mDataContainer.scaleMarkBM, xMiddleBottom, yMiddleBottom, null);//下中
//
//        xMidlleLeft = (photoCorners[6] + photoCorners[0]) / 2 - mDataContainer.shapeScaleRectLM.width() / 4;
//        yMiddleLeft = (photoCorners[7] + photoCorners[1]) / 2 - mDataContainer.shapeScaleRectLM.height() / 4;
//        mDataContainer.shapeScaleRectLM.offsetTo(xMidlleLeft, yMiddleLeft);
//        canvas.drawBitmap(mDataContainer.scaleMarkBM, xMidlleLeft, yMiddleLeft, null);//左中
    }

    /**
     * 清除画图板中图形的选中状态
     */
    private void clearSelected() {
        isSelectPhoto = false;
        isSelectShape = false;
        mDataContainer.mCurSelectPhoto = null;
        mDataContainer.mCurSelectShape = null;
        invalidate();
    }

    /**
     * 将Path生成的Bitmap绘制到画布上(包括橡皮擦)
     *
     * @param canvas
     */
    private void drawFinalPath(Canvas canvas) {
        if (mPaintViewBitmapRef != null && mPaintViewBitmapRef.get() != null) {
            canvas.drawBitmap(mPaintViewBitmapRef.get(), mBaseMatrix, null);
        }
    }

    /**
     * 绘制橡皮移动过程中的图标
     *
     * @param canvas
     */
    private void drawRushIcon(Canvas canvas) {
        if (mDrawType == ERASER && mRushIconBitmap != null && isMove) {
            changeRushMatrix();
            canvas.drawBitmap(mRushIconBitmap, mRushMatrix, null);
        }
    }

    /**
     * 修正橡皮图标的大小和位置
     */
    private void changeRushMatrix() {
        float[] values = new float[9];
        mRushMatrix.getValues(values);
        values[Matrix.MTRANS_X] = mDataContainer.mCurX - (mRushIconBitmap.getWidth() * values[Matrix.MSCALE_X] / 2);
        values[Matrix.MTRANS_Y] = mDataContainer.mCurY - (mRushIconBitmap.getHeight() * values[Matrix.MSCALE_X] / 2);
        values[Matrix.MSCALE_X] = 1;
        values[Matrix.MSCALE_Y] = 1;
        mRushMatrix.setValues(values);
    }


    /**
     * 获取PaintView的整体截图
     *
     * @return
     */
    public Bitmap getPaintViewScreen() {
        return getPaintViewScreen(mConfig);
    }

    /**
     * 获取PaintView的整体截图
     *
     * @param config
     * @return
     */
    public Bitmap getPaintViewScreen(Bitmap.Config config) {
        clearSelected();
        Bitmap res = Bitmap.createBitmap(this.getWidth(), this.getHeight(), config);
        Canvas canvas = new Canvas(res);
        this.draw(canvas);
        return res;
    }

    public void undo() {
        clearSelected();
        mStepControler.undo();
    }

    public void redo() {
        clearSelected();
        mStepControler.redo();
    }

    private PaintViewAttacher.OnDeleteListener mOnDeleteListener;

    public void setOnDeleteListener(PaintViewAttacher.OnDeleteListener mOnDeleteListener) {
        this.mOnDeleteListener = mOnDeleteListener;
    }
    //.....................................................各种set/get......start..................................................

    public DrawTypeEnum getDrawType() {
        return mDrawType;
    }

    public void setDrawType(DrawTypeEnum drawType) {
        clearSelected();
        this.mDrawType = drawType;
        switch (drawType) {
            case ERASER:
                mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                mPaint.setStrokeWidth(mRushPaintWidth);
                break;
            case RECT:
            case CIRCLE:
            case LINE:
            case PEN:
                mPaint.setXfermode(null);
                mPaint.setStrokeWidth(mPaintWidth);
                mPaint.setColor(mPaintColor);
                mPaint.setAlpha((int) (alpha * 255));
                break;
            default:
                break;
        }

    }

    public float getPaintWidth() {
        return mPaintWidth;
    }

    public void setPaintWidth(float paintWidth) {
        this.mPaintWidth = paintWidth;
        if (getDrawType() != ERASER) {
            mPaint.setStrokeWidth(mPaintWidth);
        }
    }

    public float getRushPaintWidth() {
        return mRushPaintWidth;
    }

    public void setRushPaintWidth(float rushPaintWidth) {
        this.mRushPaintWidth = rushPaintWidth;
        if (getDrawType() == ERASER) {
            mPaint.setStrokeWidth(rushPaintWidth);
        }
        //改变橡皮图标的大小
        setRushBitmap(BitmapFactory.decodeResource(getResources(),
                R.drawable.icon_rush_bg).copy(mConfig, true), (int) rushPaintWidth);
    }

    /**
     * 设置橡皮的图标图片
     *
     * @param rushBitmap
     * @param rushWidth
     */
    public void setRushBitmap(Bitmap rushBitmap, int rushWidth) {
        this.mRushIconBitmap = Util.zoomBitmap(rushBitmap, rushWidth, rushWidth);
    }

    public int getPaintColor() {
        return mPaintColor;
    }

    public void setPaintColor(int paintColor) {
        this.mPaintColor = paintColor;
        this.mPaint.setColor(paintColor);
        this.mPaint.setAlpha((int) (alpha * 255));
    }

    public float getPaintAlpha() {
        return mPaintAlpha;
    }

    public void setPaintAlpha(float paintAlpha) {
        this.mPaintAlpha = paintAlpha;
        this.mPaint.setAlpha((int) (alpha * 255));
    }

    public Matrix getBaseMatrix() {
        return mBaseMatrix;
    }

    public void setBaseMatrix(Matrix matrix) {
        this.mBaseMatrix = matrix;
    }

    public Matrix getRushMatrix() {
        return mRushMatrix;
    }

    public void setRushMatrix(Matrix rushMatrix) {
        this.mRushMatrix = rushMatrix;
    }

    public boolean isMove() {
        return isMove;
    }

    public void setMove(boolean move) {
        isMove = move;
    }

    public Paint getPaint() {
        return mPaint;
    }

    public Canvas getPaintCanvas() {
        return mPaintCanvas;
    }

    public Reference<Bitmap> getPaintBitmapRef() {
        return mPaintViewBitmapRef;
    }

    public PaintViewDrawDataContainer getDrawDataContainer() {
        return mDataContainer;
    }

    public void setSelectPhoto(boolean selectPhoto) {
        isSelectPhoto = selectPhoto;
    }

    public void setSelectShape(boolean selectShape) {
        isSelectShape = selectShape;
    }

    /**
     * 得到画板可绘制宽度
     */
    public int getPaintBitmapWidth() {
        return mWidth;
    }

    /**
     * 得到画板可绘制高度
     */
    public int getPaintBitmapHeight() {
        return mHeight;
    }

    public DrawStepControler getDrawStepControler() {
        return mStepControler;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public void setPaintViewBg(Bitmap mPaintBg) {
        DrawBgData drawBgData = new DrawBgData();
        drawBgData.bitmap = mPaintBg;
        drawBgData.mMatrix = new Matrix();
        mDataContainer.mPaintViewBg = drawBgData;

        //将图片调整到合适大小
        float scaleH = (mWidth - 50) / Float.valueOf(drawBgData.bitmap.getWidth());//横向满屏边距25px,需要方大倍数
        float scaleV = (mHeight - 50) / Float.valueOf(drawBgData.bitmap.getHeight());//竖向满屏边距25px,需要方大倍数
        float scale = scaleH > scaleV ? scaleV : scaleH;
        scale = scale > SCALE_MAX ? SCALE_MAX : scale;//如果计算出的放大系数大于了最大放大系数,就取最大放大系数为实际放大系数
        drawBgData.mMatrix.postScale(scale, scale);
        drawBgData.mMatrix.postTranslate(mWidth / 2 - drawBgData.bitmap.getWidth() * scale / 2,
                mHeight / 2 - drawBgData.bitmap.getHeight() * scale / 2);

        invalidate();
    }

    public CopyOnWriteArrayList<DrawPhotoData> getDrawPhotoData(){
        return mDataContainer.mDrawPhotoList;
    }
    //.....................................................各种set/get.........end...............................................

    public void clear() {
        isEdit = false;
        mDataContainer.curIndex = -1;
        isSelectPhoto = false;
        isSelectShape = false;
        mDataContainer.mCurSelectPhoto = null;
        mDataContainer.mCurSelectShape = null;
        setDrawType(PEN);
        mDataContainer.clear();
        renewPaintView();
        if (getOnIndexChangedListener() != null) {
            getOnIndexChangedListener().onIndexChanged(mDataContainer.curIndex, mDataContainer.mMementoList.size());
        }
        invalidate();
    }

    /**
     * 重新创建空的画板/清空画板
     */
    public void renewPaintView() {
        if (getPaintBitmapWidth() == 0) {
            return;
        }

        if (mPaintViewBitmapRef != null && mPaintViewBitmapRef.get() != null) {
            if (mPaintCanvas == null) {
                try {
                    mPaintCanvas = new Canvas(mPaintViewBitmapRef.get());
                } catch (Exception e) {
                    Log.e(TAG, "mPaintViewBitmapRef.get() == null");
                }
            }
            if (mPaintCanvas != null)
                mPaintCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        } else {
            Bitmap bitmap = Bitmap.createBitmap(getPaintBitmapWidth(), getPaintBitmapHeight(), mConfig);
            if (null == mPaintViewBitmapRef) {
                mPaintViewBitmapRef = new SoftReference<>(bitmap);
            } else {
                mPaintViewBitmapRef.clear();
                mPaintViewBitmapRef = new SoftReference<>(bitmap);
            }
            try {
                mPaintCanvas = new Canvas(mPaintViewBitmapRef.get());
            } catch (Exception e) {
                Log.e(TAG, "mPaintViewBitmapRef.get() == null");
            }
        }
    }

    /**
     * 回收所有的bitmap
     */
    private void recycleAllBitmap() {
        mPaintCanvas = null;
        if (mPaintViewBitmapRef != null) {
            mPaintViewBitmapRef.clear();
            mPaintViewBitmapRef = null;
        }
        if (mRushIconBitmap != null) {
            mRushIconBitmap.recycle();
            mRushIconBitmap = null;
        }
    }

    public void destroy() {
        if (null != mPaintViewAttacher) {
            mPaintViewAttacher.detach();
        }
        if (null != mDataContainer) {
            mDataContainer.clearAndSetNull();
        }
        recycleAllBitmap();
    }

    @Override
    public void addIndex() {
        mDataContainer.curIndex++;
        if (getOnIndexChangedListener() != null) {
            getOnIndexChangedListener().onIndexChanged(mDataContainer.curIndex, mDataContainer.mMementoList.size());
        }
    }

    private OnIndexChangedListener mOnIndexChangedListener = new OnIndexChangedListener() {
        @Override
        public void onIndexChanged(int curIndex, int listSize) {
            if (null != mOnReDoUnDoStatusChangedListener) {
                boolean canReDo, canUnDo;
                if (listSize > 0) {
                    if (curIndex < listSize - 1) {//可以重做
                        canReDo = true;
                    } else {//不可以重做
                        canReDo = false;
                    }
                    if (curIndex >= 0) {//可以撤销
                        canUnDo = true;
                    } else {//不可以撤销
                        canUnDo = false;
                    }
                } else {//不可以撤销也不可以重做
                    canReDo = false;
                    canUnDo = false;
                }

                mOnReDoUnDoStatusChangedListener.onReDoUnDoStatusChanged(canReDo, canUnDo);
            }
        }
    };


    public OnIndexChangedListener getOnIndexChangedListener() {
        return mOnIndexChangedListener;
    }

    public interface OnIndexChangedListener {
        void onIndexChanged(int curIndex, int listSize);
    }

    private OnReDoUnDoStatusChangedListener mOnReDoUnDoStatusChangedListener;

    public void setOnReDoUnDoStatusChangedListener(OnReDoUnDoStatusChangedListener mOnReDoUnDoStatusChangedListener) {
        this.mOnReDoUnDoStatusChangedListener = mOnReDoUnDoStatusChangedListener;
    }

    public interface OnReDoUnDoStatusChangedListener {
        void onReDoUnDoStatusChanged(boolean canReDo, boolean canUnDo);
    }
}
