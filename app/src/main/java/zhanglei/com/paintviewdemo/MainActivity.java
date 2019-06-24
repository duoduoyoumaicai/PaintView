package zhanglei.com.paintviewdemo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.IOException;

import zhanglei.com.paintview.DrawTypeEnum;
import zhanglei.com.paintview.PaintView;
import zhanglei.com.paintview.Util;

public class MainActivity extends AppCompatActivity {
    private PaintView paintView;
    private final int PHOTO = 0x100;


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
        findViewById(R.id.btn_add_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });


    }

    private void openImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == PHOTO) {

                Uri imageUri = data.getData();
                Bitmap bitmap = null;
                try {
                    //把URI转换为Bitmap，并将bitmap压缩，防止OOM(out of memory)
                    bitmap = Util.getBitmapFromUri(imageUri, this);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (paintView != null && null != bitmap) {
                    paintView.addPhotoByBitmap(bitmap);
                }
            }
        }


    }

}
