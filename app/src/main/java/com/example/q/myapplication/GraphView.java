package com.example.q.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

import java.util.ArrayList;

public class GraphView extends View {
    private ShapeDrawable addLineShape,delLineShape;
    private Paint addPointPaint,delPointPaint;

    private float mThickness;
    private int[] addPoints, addPointX, addPointY;
    private int[] delPoints, delPointX, delPointY;
    private int mPointSize, mPointRadius, delLineColor, addLineColor, mUnit, mOrigin, mDivide;

    public GraphView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setTypes(context, attrs);
    }

    //그래프 옵션을 받는다
    private void setTypes(Context context, AttributeSet attrs) {
        TypedArray types = context.obtainStyledAttributes(attrs, R.styleable.GraphView);

        addPointPaint = new Paint();
        addPointPaint.setColor(types.getColor(R.styleable.GraphView_pointColor, 0xFF147C02));
        delPointPaint = new Paint();
        delPointPaint.setColor(types.getColor(R.styleable.GraphView_pointColor, 0xFF8C2423));
        mPointSize = (int) types.getDimension(R.styleable.GraphView_pointSize, 30);
        mPointRadius = mPointSize / 2;

        addLineColor = types.getColor(R.styleable.GraphView_lineColor, Color.GREEN);
        delLineColor = types.getColor(R.styleable.GraphView_lineColor, Color.RED);
        mThickness = types.getDimension(R.styleable.GraphView_lineThickness, 3);
    }

    //그래프 정보를 받는다
    public void setPoints(ArrayList<Integer> points1,ArrayList<Integer> points2, int unit, int origin, int divide) {
        int[] points_array1 = new int[points1.size()];
        for(int i=0;i<points1.size();i++)
            points_array1[i] = points1.get(i);
        addPoints = points_array1;   //y축 값 배열
        int[] points_array2 = new int[points2.size()];
        for(int i=0;i<points2.size();i++)
            points_array2[i] = points2.get(i);
        addPoints = points_array1;   //y축 값 배열
        delPoints = points_array2;
        mUnit = unit;       //y축 단위
        mOrigin = origin;   //y축 원점
        mDivide = divide;   //y축 값 갯수
    }

    //그래프를 만든다
    private void draw() {

        int height = getHeight()-200;

        //x축 점 사이의 거리
        float gapx = (float)getWidth() / 6;
        //y축 단위 사이의 거리
        float gapy = (float)((height - mPointSize) / (double)mDivide);
        int length = addPoints.length;

        drawAddGraph(length,gapx,gapy);
        drawDelGraph(length,gapx,gapy);
    }
    protected void drawAddGraph(int length,float gapx,float gapy){
        Path path = new Path();
        int[] points = addPoints;
        addPointX = new int[length];
        addPointY = new int[length];
        int height = getHeight()-200;
        for(int i = 0 ; i < length ; i++) {
            //점 좌표를 구한다
            int x = (int)(gapx/2*3 + (i * gapx));
            int y = (int)(height - mPointRadius - (((points[i] / mUnit) - mOrigin) * gapy));

            addPointX[i] = x;
            addPointY[i] = y;

            //선을 그린다
            if(i == 0)
                path.moveTo(x, y);
            else
                path.lineTo(x, y);
        }

        //그려진 선으로 shape을 만든다
        ShapeDrawable shape = new ShapeDrawable(new PathShape(path, 1, 1));
        shape.setBounds(0, 0, 1, 1);

        Paint paint = shape.getPaint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(addLineColor);
        paint.setStrokeWidth(mThickness);
        paint.setAntiAlias(true);

        addLineShape = shape;
    }
    protected void drawDelGraph(int length,float gapx,float gapy){
        Path path = new Path();
        int[] points = delPoints;
        delPointX = new int[length];
        delPointY = new int[length];
        int height = getHeight()-200;
        for(int i = 0 ; i < length ; i++) {
            //점 좌표를 구한다
            int x = (int)(gapx/2*3 + (i * gapx));
            int y = (int)(height - mPointRadius - (((points[i] / mUnit) - mOrigin) * gapy));

            delPointX[i] = x;
            delPointY[i] = y;

            //선을 그린다
            if(i == 0)
                path.moveTo(x, y);
            else
                path.lineTo(x, y);
        }

        //그려진 선으로 shape을 만든다
        ShapeDrawable shape = new ShapeDrawable(new PathShape(path, 1, 1));
        shape.setBounds(0, 0, 1, 1);

        Paint paint = shape.getPaint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(delLineColor);
        paint.setStrokeWidth(mThickness);
        paint.setAntiAlias(true);

        delLineShape = shape;
    }
    //그래프를 그린다(onCreate 등에서 호출시)
    public void drawForBeforeDrawView() {
        //뷰의 크기를 계산하여 그래프를 그리기 때문에 뷰가 실제로 만들어진 시점에서 함수를 호출해 준다
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                draw();
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //선을 그린다
        if(addLineShape != null)
            addLineShape.draw(canvas);
        if(delLineShape != null)
            delLineShape.draw(canvas);

        //점을 그린다
        if(addPointX != null && addPointY != null) {
            int length = addPointX.length;
            for (int i = 0; i < length; i++) {
                canvas.drawCircle(addPointX[i], addPointY[i], mPointRadius, addPointPaint);
            }
        }
        //점을 그린다
        if(delPointX != null && delPointY != null) {
            int length = delPointX.length;
            for (int i = 0; i < length; i++)
                canvas.drawCircle(delPointX[i], delPointY[i], mPointRadius, delPointPaint);
        }
    }
}
