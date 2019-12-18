package com.freak.marqueeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FuckView extends View {
    private Paint mPaint;
    private Path path;
    private Paint outPaint;
    private Paint inPaint;
    private Paint textPaint;
    private Path outPath;
    private Path inPath;
    private int width;
    private int height;
    private int strokeWidth = 20;
    private int outColor = Color.BLACK;
    private int inColor = Color.BLACK;
    private int textColor = Color.BLACK;
    private int textSize = 14;

    public FuckView(Context context) {
        this(context, null);
    }

    public FuckView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FuckView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mPaint = new Paint();
        inPaint = new Paint();
        outPaint = new Paint();
        textPaint = new Paint();
        path = new Path();
        outPath = new Path();
        inPath = new Path();
        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.FuckView);
        for (int i = 0; i < typedArray.getIndexCount(); i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.FuckView_FuckViewStrokeWidth) {
                strokeWidth = (int) typedArray.getDimension(attr, FuckViewUtils.dip2px(context,strokeWidth));
            } else if (attr == R.styleable.FuckView_FuckViewInColor) {
                inColor = typedArray.getColor(attr, inColor);
            } else if (attr == R.styleable.FuckView_FuckViewOutColor) {
                outColor = typedArray.getColor(attr, outColor);
            } else if (attr == R.styleable.FuckView_FuckViewTextColor) {
                textColor = typedArray.getColor(attr, textColor);
            } else if (attr == R.styleable.FuckView_FuckViewTextSize) {
                textSize = (int) typedArray.getDimension(attr, FuckViewUtils.px2sp(context,textSize));

            }
        }
        Log.e("TAG","textSize "+textSize);
        Log.e("TAG","strokeWidth "+strokeWidth);
        inPaint.setColor(inColor);
        inPaint.setStyle(Paint.Style.STROKE);
        outPaint.setStyle(Paint.Style.STROKE);
        outPaint.setColor(outColor);
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        mPaint.setStrokeWidth(3);
        typedArray.recycle();
    }

    private String text = "history";
    private List<Map<String, Integer>> mListPoint = new ArrayList<Map<String, Integer>>();


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //这个功能的重要点 其实是先画一个轨迹 然后再把文字画到这个轨迹上 所以 想要什么样的扭曲，就画什么样的轨迹，下面就是画一个轨迹
        //画一个弧形 也可以是椭圆  上面的矩形就是这个图形的区域大小了 后面两个参数记住 一个是起始位置 一个是所画角度而不是终点位置
        mPaint.setStyle(Paint.Style.STROKE);
        outPath.addCircle(width / 2, height / 2, width / 2, Path.Direction.CW);
        inPath.addCircle(width / 2, height / 2, width / 2 - strokeWidth, Path.Direction.CW);
        outPath.addPath(inPath);

        mPaint.setColor(textColor);
        //去锯齿的 不多说
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(textSize);
        canvas.drawPath(outPath, mPaint);

        // 下面就是绘制文本了
        mPaint.setStyle(Paint.Style.FILL);
        //把传进来的string转成char【】类型 下面要用到
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char[] chars1 = new char[1];
            chars1[0] = chars[i];
            /**
             * 参数说明
             * 第一：要绘制的文本永远不能为该值null。
             * 第二：要绘制的文本中的起始索引
             * 第三：从索引开始，要绘制的字符数
             * 第四：文本应以其基线为基准的路径null。此值绝不能为。
             * 第五：沿着路径添加到文本的起始位置的距离
             * 第六：位于文本上方的路径上方（-）或下方（+）的距离
             * 第七：用于文本的绘画（例如颜色，大小，样式）此值绝不能为null。
             */
            canvas.drawTextOnPath(chars1, 0, 1, outPath, i * (strokeWidth/3 + i), strokeWidth/2 + i, textPaint);
        }
    }

    public void setFuckText(String text) {
        this.text = text;
        invalidate();
    }
}
