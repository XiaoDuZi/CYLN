package com.guanghua.ln.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.guanghua.ln.R;


/**
 * Created by Administrator on 2017/8/2 0002.
 */

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS=new int[]{
            android.R.attr.listDivider
    };

    public static final int HORIZONTAL_LIST= LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST=LinearLayoutManager.VERTICAL;

    private Drawable mDivider;
    private int mOrientation;

    public DividerItemDecoration(Context context,int orientation) {
        final TypedArray array=context.obtainStyledAttributes(ATTRS);
        mDivider=array.getDrawable(0);
        array.recycle();
        setOrientation(orientation);
//        mOrientation = orientation;
    }

    public void setOrientation(int orientation) {
        if (orientation!=HORIZONTAL_LIST&&orientation!=VERTICAL_LIST){
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation=orientation;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        
        if (mOrientation==VERTICAL_LIST){
            drawVertival(c,parent);
        }else {
            drawHorizontal(c,parent);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (mOrientation==HORIZONTAL_LIST){
            outRect.set(0,0,0,mDivider.getIntrinsicHeight());
        }
    }

    private void drawHorizontal(Canvas c, RecyclerView parent) {
        final int left=parent.getPaddingLeft();
        final int right=parent.getWidth()-parent.getPaddingRight();

        final int childCount=parent.getChildCount();
        for (int i=0;i<childCount;i++){
            final View child=parent.getChildAt(i);
            RecyclerView v=new RecyclerView(parent.getContext());
            final RecyclerView.LayoutParams params= (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top=child.getBottom()+params.bottomMargin;
            final int bottom=top+mDivider.getIntrinsicHeight();
            mDivider.setBounds(left,top,right,bottom);
            mDivider.draw(c);
        }
    }

    private void drawVertival(Canvas c, RecyclerView parent) {
        final int left=parent.getPaddingLeft();
        final int right=parent.getWidth()-parent.getPaddingRight();

        final int childCount=parent.getChildCount();
        for (int i=0;i<childCount;i++){
            final View child=parent.getChildAt(i);
            RecyclerView v=new RecyclerView(parent.getContext());
            final RecyclerView.LayoutParams params= (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top=child.getBottom()+params.bottomMargin;
            final int bottom=top+mDivider.getIntrinsicHeight();
            mDivider.setBounds(left,top,right,bottom);
            mDivider.draw(c);
        }
    }
}
