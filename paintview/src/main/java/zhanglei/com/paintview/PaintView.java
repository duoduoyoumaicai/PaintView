package zhanglei.com.paintview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

import zhanglei.com.paintview.bean.DrawShapeData;
import zhanglei.com.paintview.touchmanager.PaintViewAttacher;

import static zhanglei.com.paintview.DrawTypeEnum.ERASER;


/**
 * 类名称：PaintView
 * 类描述：负责View的渲染
 * 创建人：lei.zhang
 * 创建时间：on 2019/6/17
 * 修改人：
 * 修改时间：
 * 修改备注：
 */

public class PaintView extends View implements ViewTreeObserver.OnGlobalLayoutListener {

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

    protected Canvas mPaintCanvas;

    protected Reference<Bitmap> mPaintBitmapRef;

    protected Bitmap.Config mConfig = Bitmap.Config.ARGB_4444;

    private PaintViewDrawDataManager mDataManager;

    private PaintViewAttacher mPaintViewAttacher;//处理PaintView的触摸事件

    private boolean isMove = false;

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
        mDataManager = new PaintViewDrawDataManager();

        //初始化绘制图形的画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);// 抗锯齿
        mPaint.setColor(mPaintColor);
        mPaint.setAlpha((int) (mPaintAlpha * 255));// 设置透明度为0
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);// 圆头
        mPaint.setStrokeJoin(Paint.Join.ROUND);// 结合方式，平滑
        mPaint.setStrokeWidth(mPaintWidth);// 设置空心边框宽,绘制图形画笔宽度

        setRushBitmap(BitmapFactory.decodeResource(getResources(),
                R.drawable.icon_rush_bg).copy(mConfig, true), (int) mRushPaintWidth);

        mPaintViewAttacher = new PaintViewAttacher();
        mPaintViewAttacher.attach(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (null == mDataManager) return;

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
        if ((mPaintBitmapRef == null || mPaintBitmapRef.get() == null)) {
            initPaintCanvas();
        }
    }

    public void initPaintCanvas() {

        if (this.mWidth > 0 && this.mHeight > 0) {
            Bitmap bitmap = Bitmap.createBitmap(this.mWidth, this.mHeight, this.mConfig);
            this.mPaintBitmapRef = new SoftReference<>(bitmap);
        }

        if (this.mPaintBitmapRef != null && mPaintBitmapRef.get() != null) {
            try {
                mPaintCanvas = new Canvas(mPaintBitmapRef.get());
            } catch (Exception e) {
                Log.e(TAG, "mPaintBitmapRef.get() == null");
            }
            this.invalidate();
        }

    }

    /**
     * 画笔移动过程中绘制Path或几何图形
     *
     * @param canvas
     */
    private void drawTemp(Canvas canvas) {
        if (mDataManager.mTempPath != null && !mDataManager.mTempPath.isEmpty() && mDrawType != ERASER) {
            canvas.drawPath(mDataManager.mTempPath, mPaint);
        }
    }

    /**
     * 绘制shape和shape的边线所有的内容
     *
     * @param canvas
     */
    private void drawFinalShape(Canvas canvas) {
        if (mDataManager.drawShapeList != null) {
            for (DrawShapeData drawShapeData : mDataManager.drawShapeList) {
                if (drawShapeData != null) {
                    canvas.drawPath(drawShapeData.drawPath, drawShapeData.paint);
                }
            }
        }
    }

    /**
     * 将Path生成的Bitmap绘制到画布上(包括橡皮擦)
     *
     * @param canvas
     */
    private void drawFinalPath(Canvas canvas) {
        if (mPaintBitmapRef != null && mPaintBitmapRef.get() != null) {
            canvas.drawBitmap(mPaintBitmapRef.get(), mBaseMatrix, null);
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
        values[Matrix.MTRANS_X] = mDataManager.mCurX - (mRushIconBitmap.getWidth() * values[Matrix.MSCALE_X] / 2);
        values[Matrix.MTRANS_Y] = mDataManager.mCurY - (mRushIconBitmap.getHeight() * values[Matrix.MSCALE_X] / 2);
        values[Matrix.MSCALE_X] = 1;
        values[Matrix.MSCALE_Y] = 1;
        mRushMatrix.setValues(values);
    }

    public float getMatrixScale() {
        float[] values = new float[9];
        mBaseMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    /**
     * 获取PaintView的整体截图
     *
     * @return
     */
    public Bitmap getPaintViewScreen() {
        Bitmap res = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(res);
        this.draw(canvas);
        return res;
    }


    //.....................................................各种set/get........................................................

    public DrawTypeEnum getDrawType() {
        return mDrawType;
    }

    public void setDrawType(DrawTypeEnum drawType) {
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
        if (mPaint != null) {
            mPaint.setStrokeWidth(paintWidth);
        }
    }

    public float getRushPaintWidth() {
        return mRushPaintWidth;
    }

    public void setRushPaintWidth(float rushPaintWidth) {
        this.mRushPaintWidth = rushPaintWidth;
        if (mPaint != null) {
            mPaint.setStrokeWidth(rushPaintWidth);
        }
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
        return mPaintBitmapRef;
    }

    public PaintViewDrawDataManager getDrawDataManager() {
        return mDataManager;
    }

    //.....................................................各种set/get........................................................

    public void destroy() {
        if (null != mPaintViewAttacher) {
            mPaintViewAttacher.detach();
        }
        if (null != mDataManager) {
            mDataManager.clearAndSetNull();
        }
    }

}
