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
import android.view.Window;
import android.widget.ImageView;

import java.io.IOException;

import zhanglei.com.paintview.DrawTypeEnum;
import zhanglei.com.paintview.PaintView;
import zhanglei.com.paintview.Util;

public class MainActivity extends AppCompatActivity implements IPaintColorOrWidthListener {
    private PaintView paintView;
    private final int PHOTO = 0x100;
    private final int ADDBG = 0x010;
    private ToolbarColorSelectPopupWindow colorSelectPopup;
    private ImageView ivUndo;
    private ImageView ivRedo;
    private ImageView iv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        iv = findViewById(R.id.iv);
        paintView = findViewById(R.id.paintView);
        colorSelectPopup = new ToolbarColorSelectPopupWindow(this);
        colorSelectPopup.setPaintColorOrWidthListener(this);
        ivUndo = findViewById(R.id.iv_undo);
        ivRedo = findViewById(R.id.iv_redo);

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
                openImage(PHOTO);
            }
        });
        findViewById(R.id.btn_add_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage(ADDBG);
            }
        });
        findViewById(R.id.btn_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.setDrawType(DrawTypeEnum.SELECT_STATUS);
            }
        });
        findViewById(R.id.btn_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.clear();
            }
        });
        findViewById(R.id.btn_select_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorSelectPopup.showPopupWindow(v);
            }
        });
        findViewById(R.id.btn_get_picture).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = paintView.getPaintViewScreen(Bitmap.Config.ARGB_4444);
                iv.setImageBitmap(bitmap);
            }
        });
        ivUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.undo();
            }
        });
        ivRedo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.redo();
            }
        });
        paintView.setOnReDoUnDoStatusChangedListener(new PaintView.OnReDoUnDoStatusChangedListener() {
            @Override
            public void onReDoUnDoStatusChanged(boolean canReDo, boolean canUnDo) {
                if (canReDo) {
                    ivRedo.setImageResource(R.mipmap.icon_redo);
                } else {
                    ivRedo.setImageResource(R.mipmap.icon_redo_gray);
                }
                if (canUnDo) {
                    ivUndo.setImageResource(R.mipmap.icon_undo);
                } else {
                    ivUndo.setImageResource(R.mipmap.icon_undo_gray);
                }
            }
        });

    }

    private void openImage(int resultCode) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, resultCode);
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
                    paintView.addPhotoByBitmap(bitmap, true);
                }

            } else if (requestCode == ADDBG) {

                Uri imageUri = data.getData();
                Bitmap bitmap = null;
                try {
                    //把URI转换为Bitmap，并将bitmap压缩，防止OOM(out of memory)
                    bitmap = Util.getBitmapFromUri(imageUri, this);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (paintView != null && null != bitmap) {
                    paintView.setPaintViewBg(bitmap);
                }

            }
        }


    }

    @Override
    public void onColorChanged(int paintColor) {
        paintView.setPaintColor(paintColor);
    }

    @Override
    public void onPaintWidthChanged(DrawStrokeEnum drawStrokeEnum) {
        paintView.setPaintWidth(drawStrokeEnum.getPenStroke());
        paintView.setRushPaintWidth(drawStrokeEnum.getEraserStroke());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != paintView) {
            paintView.destroy();
        }
    }
}
