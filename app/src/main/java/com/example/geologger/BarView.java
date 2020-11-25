package com.example.geologger;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by hkf on 2016/8/9.
 * 自定义BarView柱状图
 * 横轴随数据变化自动变化，超出屏幕可横移
 */

public class BarView extends View {
    //数据源
	/* 纵轴信噪比数据 */
    private double[] Snr;
    /* 横轴卫星号数据 */
    private int[] Prn;

    //卫星总数
    private int totalSize;

    //view 的宽与高
    private int width;
    private int height;

    //屏幕的宽、高
    private int screen_width;
    private int screen_height;

    //单个柱状图的间隔与宽度
    private int bar_space;
    private int bar_width;

    //柱状图上边距和下边距
    private int top_space;
    private int bottom_space;

    //矩形背景的画笔
    private Paint mPaint_bg;

    //单个柱子的颜色
    private Paint mPaintBarColor;

    //文本的画笔
    private Paint mPaint_text;
    private Paint.FontMetrics mMetrics;

    //虚线的画笔
    private Paint vPaint;
    private PathEffect effects;
    private Path path;

    public BarView(Context context) {
        super(context);
        init(context);
    }

    public BarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     *初始化Prn，Snr
     * @param _Prn 卫星号数组
     * @param _Snr 信噪比数组
     */
    public void setData(int[] _Prn, double[] _Snr) {
        this.Prn = _Prn;
        this.Snr = _Snr;
        //统计卫星总数
        if (Prn != null) {
            int j = 0;
            for (int i = 0; i < Prn.length; i++) {
                if (Snr[i] > 0) {
                    j++;
                }
            }
            totalSize = j;
        }
        requestLayout();  // 相当于调用onMeasure方法
        invalidate();
    }

    /**
     * 初始化绘制参数
     * @param context
     */
    public void init(Context context) {
        //获取屏幕高度
//        screen_width = ScreenUtils.getScreenWidth(context);
//        screen_height = ScreenUtils.getScreenHeight(context);
//
//        //调用了dp转px的方法，引入ZHD-Utils Module，设置上下以及间隔宽度
//        top_space = DisplayUtils.dip2px(context, 10);
//        bottom_space = DisplayUtils.dip2px(context, 20);
//        bar_width = DisplayUtils.dip2px(context, 10);
//        bar_space = DisplayUtils.dip2px(context, 30);

        //柱状图的背景颜色(灰色的背景)
        mPaint_bg = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint_bg.setColor(Color.GRAY);

        //设置bar的颜色(蓝色)
        mPaintBarColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBarColor.setColor(Color.BLUE);
        mPaintBarColor.setStrokeCap(Paint.Cap.ROUND);
        mPaintBarColor.setStrokeJoin(Paint.Join.ROUND);
        mPaintBarColor.setStrokeWidth(bar_width);

        //设置字体
        mPaint_text = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint_text.setColor(Color.BLACK);
        mPaint_text.setTextSize(bar_width);
        mMetrics = mPaint_text.getFontMetrics();

        //虚线画笔
        vPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        vPaint.setStyle(Paint.Style.STROKE);
        vPaint.setColor(Color.BLACK);
        vPaint.setStrokeWidth(1);
        path = new Path();
        effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
        vPaint.setPathEffect(effects);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //没有数据时画布宽度为屏幕宽度
        if (Prn == null) {
            width = screen_width;
        } else {
            //根据数据的多少确定绘制的宽度
            width = (bar_space) * (totalSize + 1);
            if (width < screen_width) {
                width = screen_width;
            }
        }
        height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画布的灰色背景
        //canvas.drawColor(Color.GRAY);
        //画布灰色矩形区域，上下各自留出一定空白区域
        //canvas.drawRect(0, height - bottom_space, width, top_space, mPaint_bg);

        //横纵轴Label
        canvas.drawText("SNR", 0, top_space, mPaint_text);
        canvas.drawText("PRN", 0, height, mPaint_text);

        //绘制虚线（纵坐标20,40,60,80时虚线标记）
        path.moveTo(0, height - top_space - drawHeight(20));
        path.lineTo(width, height - top_space - drawHeight(20));
        canvas.drawPath(path, vPaint);
        path.moveTo(0, height - top_space - drawHeight(40));
        path.lineTo(width, height - top_space - drawHeight(40));
        canvas.drawPath(path, vPaint);
        path.moveTo(0, height - top_space - drawHeight(60));
        path.lineTo(width, height - top_space - drawHeight(60));
        canvas.drawPath(path, vPaint);
        path.moveTo(0, height - top_space - drawHeight(80));
        path.lineTo(width, height - top_space - drawHeight(80));
        canvas.drawPath(path, vPaint);

        if (Prn != null) {
            //开始绘制
            //绘制顶部百分数文本
            int j = 0;
            for (int i = 0; i < Prn.length; i++) {
                if (Snr[i] > 0) {
                    canvas.drawText("" + (int) Snr[i], bar_space * (j + 1) - bar_width / 2, height - 2 * top_space - drawHeight(Snr[i]), mPaint_text);
                    if((Prn[i]>=33)&&(Prn[i]<=64)) {//SBAS  +87处理
                        canvas.drawText("" + (Prn[i]+87), bar_space * (j + 1) - bar_width / 2, height, mPaint_text);
                    }else if((Prn[i]>=101)&&(Prn[i]<=135)){//BD2  +60处理
                        canvas.drawText("" + (Prn[i]+60), bar_space * (j + 1) - bar_width / 2, height, mPaint_text);
                    }
                    else{
                        canvas.drawText("" + Prn[i], bar_space * (j + 1) - bar_width / 2, height, mPaint_text);
                    }
                    canvas.drawLine(bar_space * (j + 1), height - bottom_space, bar_space * (j + 1), height - top_space - drawHeight(Snr[i]), mPaintBarColor);
                    j++;
                }
            }
        }
        canvas.save();
    }

    /**
     *计算绘制高度
     * @param number 信噪比
     * @return 绘制高度
     */
    private float drawHeight(double number) {
        return (float) (number / 90 * (height - bottom_space - top_space));
    }

}
