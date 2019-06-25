package zhanglei.com.paintviewdemo;

public interface IPaintColorOrWidthListener {
    void onColorChanged(int paintColor);

    void onPaintWidthChanged(DrawStrokeEnum drawStrokeEnum);
}