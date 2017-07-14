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
import android.util.Log;
import android.widget.VideoView;


/**
 * Created by Administrator on 2017/6/14 0014.
 */

public class LnVideoView extends VideoView {

    public LnVideoView(Context context) {
        super(context);
    }

    public LnVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LnVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getDefaultSize(0, widthMeasureSpec);
        int height = getDefaultSize(0, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    public void setOnErrorListener(MediaPlayer.OnErrorListener l) {
        super.setOnErrorListener(l);

    }
}
