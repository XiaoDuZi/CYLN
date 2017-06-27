package com.guanghua.ln.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.widget.VideoView;

import com.guanghua.ln.activitys.R;

import static android.R.attr.radius;

/**
 * Created by Administrator on 2017/6/14 0014.
 */

public class LnVideoView extends VideoView {

    private int defalutSize;

    public LnVideoView(Context context) {
        super(context);
    }

    public LnVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LnVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //第二个参数就是我们在styles.xml文件中的<declare-styleable>标签
        //即属性集合的标签，在R文件中名称为R.styleable+name
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyView);
        //第一个参数为属性集合里面的属性，R文件名称：R.styleable+属性集合名称+下划线+属性名称
        //第二个参数为，如果没有设置这个属性，则设置的默认的值
        defalutSize = a.getDimensionPixelSize(R.styleable.MyView_default_size, 100);
        //最后记得将TypedArray对象回收
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint=new Paint();
        paint.setAntiAlias(true);                       //设置画笔为无锯齿
        paint.setColor(Color.BLACK);                    //设置画笔颜色
        canvas.drawColor(Color.WHITE);                  //白色背景
        paint.setStrokeWidth((float) 3.0);              //线宽
        paint.setStyle(Paint.Style.STROKE);                   //空心效果
        int x=getLeft();
        int y=getTop();
        int x1=getLeft()+getMeasuredWidth();
        int y1=getTop()+getMeasuredHeight();

        RectF r2=new RectF(x,y,x1,y1);                           //RectF对象


        canvas.drawRoundRect(r2, 10, 10, paint);

        //调用父View的onDraw函数，因为View这个类帮我们实现了一些
        // 基本的而绘制功能，比如绘制背景颜色、背景图片等
//        super.onDraw(canvas);


//        int r = getMeasuredWidth() / 2;//也可以是getMeasuredHeight()/2,本例中我们已经将宽高设置相等了
//        //圆心的横坐标为当前的View的左边起始位置+半径
//        int centerX = getLeft() + r;
//        //圆心的纵坐标为当前的View的顶部起始位置+半径
//        int centerY = getTop() + r;
//
//        Paint paint = new Paint();
//        paint.setColor(Color.GREEN);
        //开始绘制
//        canvas.drawCircle(centerX, centerY, r, paint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        int width = getDefaultSize(0, widthMeasureSpec);
//        int height = getDefaultSize(0, heightMeasureSpec);
//        setMeasuredDimension(width, height);
        int width = getMySize(defalutSize, widthMeasureSpec);
        int height = getMySize(defalutSize, heightMeasureSpec);

        if (width < height) {
            height = width;
        } else {
            width = height;
        }

        setMeasuredDimension(width, height);
    }

    private int getMySize(int defaultSize, int measureSpec) {
        int mySize = defaultSize;

        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.UNSPECIFIED: {//如果没有指定大小，就设置为默认大小
                mySize = defaultSize;
                break;
            }
            case MeasureSpec.AT_MOST: {//如果测量模式是最大取值为size
                //我们将大小取最大值,你也可以取其他值
                mySize = size;
                break;
            }
            case MeasureSpec.EXACTLY: {//如果是固定的大小，那就不要去改变它
                mySize = size;
                break;
            }
        }
        return mySize;
    }


}
