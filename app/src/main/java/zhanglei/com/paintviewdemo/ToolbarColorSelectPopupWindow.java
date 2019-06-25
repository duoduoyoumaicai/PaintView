package zhanglei.com.paintviewdemo;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;


/**
 * 颜色选择与画笔粗细选择
 */

public class ToolbarColorSelectPopupWindow extends PopupWindow implements View.OnClickListener {


    private View view;

    private ImageView mPaintBlack;

    private ImageView mPaintBlue;

    private ImageView mPaintYellow;

    private ImageView mPaintGreen;

    private ImageView mPaintRed;

    private ImageView mPaintWidthSmall;

    private ImageView mPaintWidthMiddle;

    private ImageView mPaintWidthLarge;

    private IPaintColorOrWidthListener paintColorOrWidthListener;

    public void setPaintColorOrWidthListener(IPaintColorOrWidthListener paintColorOrWidthListener) {
        this.paintColorOrWidthListener = paintColorOrWidthListener;
    }

    public ToolbarColorSelectPopupWindow(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.popwindow_color_select, null);
        mPaintBlack = (ImageView) view.findViewById(R.id.black);
        mPaintRed = (ImageView) view.findViewById(R.id.red);
        mPaintBlue = (ImageView) view.findViewById(R.id.blue);
        mPaintYellow = (ImageView) view.findViewById(R.id.yellow);
        mPaintGreen = (ImageView) view.findViewById(R.id.green);
        mPaintWidthMiddle = (ImageView) view.findViewById(R.id.paint_width_middle);
        mPaintWidthSmall = (ImageView) view.findViewById(R.id.paint_width_small);
        mPaintWidthLarge = (ImageView) view.findViewById(R.id.paint_width_big);
        initListener();
        setContentView(view);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(0x000000));
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void initListener() {
        mPaintBlack.setOnClickListener(this);
        mPaintBlue.setOnClickListener(this);
        mPaintGreen.setOnClickListener(this);
        mPaintRed.setOnClickListener(this);
        mPaintYellow.setOnClickListener(this);

        mPaintWidthLarge.setOnClickListener(this);
        mPaintWidthSmall.setOnClickListener(this);
        mPaintWidthMiddle.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.black) {
            if (null != paintColorOrWidthListener) {
                paintColorOrWidthListener.onColorChanged(Color.BLACK);
            }
            mPaintBlack.setBackgroundResource(R.drawable.shape_toobar_select_bg);
            mPaintYellow.setBackgroundResource(R.drawable.shape_toobar_unselect_bg);
            mPaintBlue.setBackgroundResource(R.drawable.shape_toobar_unselect_bg);
            mPaintGreen.setBackgroundResource(R.drawable.shape_toobar_unselect_bg);
            mPaintRed.setBackgroundResource(R.drawable.shape_toobar_unselect_bg);
            dismissPopupWindow();
        } else if (id == R.id.blue) {
            if (null != paintColorOrWidthListener) {
                paintColorOrWidthListener.onColorChanged(Color.BLUE);
            }
            mPaintBlue.setBackgroundResource(R.drawable.shape_toobar_select_bg);
            mPaintBlack.setBackgroundResource(R.drawable.shape_toobar_unselect_bg);
            mPaintYellow.setBackgroundResource(R.drawable.shape_toobar_unselect_bg);
            mPaintGreen.setBackgroundResource(R.drawable.shape_toobar_unselect_bg);
            mPaintRed.setBackgroundResource(R.drawable.shape_toobar_unselect_bg);
            dismissPopupWindow();
        } else if (id == R.id.red) {
            if (null != paintColorOrWidthListener) {
                paintColorOrWidthListener.onColorChanged(Color.RED);
            }
            mPaintRed.setBackgroundResource(R.drawable.shape_toobar_select_bg);
            mPaintBlue.setBackgroundResource(R.drawable.shape_toobar_unselect_bg);
            mPaintBlack.setBackgroundResource(R.drawable.shape_toobar_unselect_bg);
            mPaintYellow.setBackgroundResource(R.drawable.shape_toobar_unselect_bg);
            mPaintGreen.setBackgroundResource(R.drawable.shape_toobar_unselect_bg);
            dismissPopupWindow();
        } else if (id == R.id.yellow) {
            if (null != paintColorOrWidthListener) {
                paintColorOrWidthListener.onColorChanged(Color.YELLOW);
            }
            mPaintYellow.setBackgroundResource(R.drawable.shape_toobar_select_bg);
            mPaintRed.setBackgroundResource(R.drawable.shape_toobar_unselect_bg);
            mPaintBlue.setBackgroundResource(R.drawable.shape_toobar_unselect_bg);
            mPaintBlack.setBackgroundResource(R.drawable.shape_toobar_unselect_bg);
            mPaintGreen.setBackgroundResource(R.drawable.shape_toobar_unselect_bg);
            dismissPopupWindow();
        } else if (id == R.id.green) {
            if (null != paintColorOrWidthListener) {
                paintColorOrWidthListener.onColorChanged(Color.GREEN);
            }
            mPaintGreen.setBackgroundResource(R.drawable.shape_toobar_select_bg);
            mPaintYellow.setBackgroundResource(R.drawable.shape_toobar_unselect_bg);
            mPaintRed.setBackgroundResource(R.drawable.shape_toobar_unselect_bg);
            mPaintBlue.setBackgroundResource(R.drawable.shape_toobar_unselect_bg);
            mPaintBlack.setBackgroundResource(R.drawable.shape_toobar_unselect_bg);
            dismissPopupWindow();
        } else if (id == R.id.paint_width_small) {
            if (null != paintColorOrWidthListener) {
                paintColorOrWidthListener.onPaintWidthChanged(DrawStrokeEnum.LEVEL1);
            }
            mPaintWidthSmall.setBackgroundResource(R.drawable.shape_toobar_select_bg);
            mPaintWidthMiddle.setBackgroundResource(R.drawable.shape_toobar_unselect_bg);
            mPaintWidthLarge.setBackgroundResource(R.drawable.shape_toobar_unselect_bg);
            dismissPopupWindow();
        } else if (id == R.id.paint_width_middle) {
            if (null != paintColorOrWidthListener) {
                paintColorOrWidthListener.onPaintWidthChanged(DrawStrokeEnum.LEVEL2);
            }
            mPaintWidthSmall.setBackgroundResource(R.drawable.shape_toobar_unselect_bg);
            mPaintWidthMiddle.setBackgroundResource(R.drawable.shape_toobar_select_bg);
            mPaintWidthLarge.setBackgroundResource(R.drawable.shape_toobar_unselect_bg);
            dismissPopupWindow();
        } else if (id == R.id.paint_width_big) {
            if (null != paintColorOrWidthListener) {
                paintColorOrWidthListener.onPaintWidthChanged(DrawStrokeEnum.LEVEL3);
            }
            mPaintWidthLarge.setBackgroundResource(R.drawable.shape_toobar_select_bg);
            mPaintWidthSmall.setBackgroundResource(R.drawable.shape_toobar_unselect_bg);
            mPaintWidthMiddle.setBackgroundResource(R.drawable.shape_toobar_unselect_bg);
            dismissPopupWindow();
        }

    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void showPopupWindow(View parent) {
        if (!isShowing()) {
            int[] location = new int[2];
            parent.getLocationOnScreen(location);
            showAtLocation(parent, Gravity.NO_GRAVITY, location[0] + parent.getWidth(), location[1] - 10);
        } else {
            dismissPopupWindow();
        }
    }

    private void dismissPopupWindow() {
        dismiss();
    }


}
