package zhanglei.com.paintviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import zhanglei.com.paintview.DrawTypeEnum;
import zhanglei.com.paintview.PaintView;

public class MainActivity extends AppCompatActivity {
    private PaintView paintView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        paintView = findViewById(R.id.paintView);


        findViewById(R.id.btn_select_eraser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.setDrawType(DrawTypeEnum.ERASER);
            }
        });
        findViewById(R.id.btn_select_paint).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.setDrawType(DrawTypeEnum.PEN);

            }
        });
        findViewById(R.id.btn_select_rec).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.setDrawType(DrawTypeEnum.RECT);

            }
        });
        findViewById(R.id.btn_select_cir).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.setDrawType(DrawTypeEnum.CIRCLE);

            }
        });
        findViewById(R.id.btn_select_line).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.setDrawType(DrawTypeEnum.LINE);

            }
        });
    }
}
