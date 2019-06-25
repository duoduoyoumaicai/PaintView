# PaintView
一个很好用的绘图板
可以绘制曲线、直线、矩形、圆、橡皮擦功能（可以擦除曲线）添加图片,可以对直线、矩形、圆、图片进行移动缩放旋转;

# 使用方式
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
  implementation 'com.github.duoduoyoumaicai:PaintView:1.0'
  ```
