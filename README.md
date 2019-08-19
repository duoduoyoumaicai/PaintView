# PaintView
* 一个很好用的绘图板
* 可以绘制曲线、橡皮擦功能（可以擦除曲线）、直线、矩形、圆、添加图片
* 可以对直线、矩形、圆、图片进行移动缩放旋转

<img src="https://github.com/duoduoyoumaicai/picture/blob/master/paintview_readme02.jpg" width="30%">

# 如何集成
Project的build.gradle文件中加上

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
  Module的build.gradle文件中加上
  ```
  implementation 'com.github.duoduoyoumaicai:PaintView:1.8'
  ```
  * 1.2之前的版本为预发布版本,部分功能无法使用,请直接引用1.2版本
  * 1.3版本增加撤销重做功能
  * 1.4版本增加添加全屏图片功能
    **1.4.1如果高度先撑满,就按高度等比缩放
    **1.4.2如果宽度先撑满,就按宽度等比缩放
    **1.4.3对图片的放大倍数不能大于4倍,如果大于了4倍则不按全屏倍数方大而按4倍等比缩放
  * 1.5版本增加isEdit()方法,判断画板是否编辑过
  * 1.8版本增加添加背景图功能(画板的背景图不能删除和撤销,不能对背景图进行变换)
  # 使用方式
   * 在xml布局文件中引入PaintView
 
```
<zhanglei.com.paintview.PaintView
        android:id="@+id/paintView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```
* 设置为画笔模式,可以自由涂鸦
```
paintView.setDrawType(DrawTypeEnum.PEN);
```
* 设置为橡皮模式,可以擦除自由涂鸦的曲线
```
paintView.setDrawType(DrawTypeEnum.ERASER);
```
* 设置为矩形模式,可以绘制矩形
```
paintView.setDrawType(DrawTypeEnum.RECT);
```
* 设置为圆模式,可以绘制椭圆
```
paintView.setDrawType(DrawTypeEnum.CIRCLE);
```
* 设置为直线模式,可以绘制直线
```
paintView.setDrawType(DrawTypeEnum.LINE);
```
* 设置为画笔颜色
```
 paintView.setPaintColor(paintColor);//paintColor类型为Color
```
* 设置为画笔/橡皮粗细
```
 paintView.setPaintWidth(2);
 paintView.setRushPaintWidth(30);
```
* 添加图片
```
 paintView.addPhotoByBitmap(bitmap);
 paintView.addPhotoByBitmap(bitmap,true);//添加全屏图片
```
* 撤销
```
 paintView.undo();
```
* 重做
```
 paintView.redo();
```
* 设置为选择模式,这个模式可以选择几何图形和图片,对他们进行移动缩放旋转
```
 paintView.setDrawType(DrawTypeEnum.SELECT_STATUS);
```
* 清空画板
```
 paintView.clear();
```
* 获取画板截图
```
 paintView.getPaintViewScreen();
```
* 不再使用PaintView的时候别忘了资源回收,可以在Activity的onDestroy方法调用
```
 paintView.destroy();
```
