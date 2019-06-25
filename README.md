# PaintView
* 一个很好用的绘图板
* 可以绘制曲线、橡皮擦功能（可以擦除曲线）、直线、矩形、圆、添加图片
* 可以对直线、矩形、圆、图片进行移动缩放旋转

！[image]（https://github.com/duoduoyoumaicai/picture/blob/master/paintview_readme/paintview_readme.jpg）

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
  implementation 'com.github.duoduoyoumaicai:PaintView:1.2'
  ```
  * 1.2之前的版本为预发布版本,部分功能无法使用,请直接引用1.2及其以上版本
  
  # 使用方式
   * 在xml布局文件中引入PaintView
 
```
<zhanglei.com.paintview.PaintView
        android:id="@+id/paintView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```
* 选择画笔模式,可以自由涂鸦
```
paintView.setDrawType(DrawTypeEnum.PEN);
```
* 选择橡皮模式,可以擦除自由涂鸦的曲线
```
paintView.setDrawType(DrawTypeEnum.ERASER);
```
* 选择矩形模式,可以绘制矩形
```
paintView.setDrawType(DrawTypeEnum.RECT);
```
* 选择圆模式,可以绘制椭圆
```
paintView.setDrawType(DrawTypeEnum.CIRCLE);
```
* 选择直线模式,可以绘制直线
```
paintView.setDrawType(DrawTypeEnum.LINE);
```
* 选择画笔颜色
```
 paintView.setPaintColor(paintColor);//paintColor类型为Color
```
* 选择画笔/橡皮粗细
```
 paintView.setPaintWidth(2);
 paintView.setRushPaintWidth(30);
```
* 添加图片
```
 paintView.addPhotoByBitmap(bitmap);
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
