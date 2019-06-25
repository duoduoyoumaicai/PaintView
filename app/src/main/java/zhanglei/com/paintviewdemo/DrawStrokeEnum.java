package zhanglei.com.paintviewdemo;


public enum DrawStrokeEnum {
    LEVEL1(2, 30), LEVEL2(4, 50), LEVEL3(8, 70);


    private int penStroke;

    private int eraserStroke;

    DrawStrokeEnum(int penStroke, int eraserStroke) {
        this.penStroke = penStroke;
        this.eraserStroke = eraserStroke;
    }

    public int getEraserStroke() {
        return eraserStroke;
    }

    public int getPenStroke() {
        return penStroke;
    }

}
