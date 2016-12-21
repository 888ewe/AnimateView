package activity.xiaobao.com.zhua;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.HashMap;

/**
 * Created by song on 2016/11/29.
 * 作者:沉默
 * QQ:823925783
 */
public class ZhuaView extends ViewGroup {

    public final static int STYLE_NONE_TEXT = 0;

    public final static int STYLE_ONE_TEXT_3 = 3;
    public final static int STYLE_ONE_TEXT_4 = 4;

    public final static int STYLE_TWO_TEXT_3 = 12;
    public final static int STYLE_TWO_TEXT_4 = 13;

    public final static int STYLE_THREE_TEXT_3 = 22;
    public final static int STYLE_THREE_TEXT_4 = 23;

    /**
     * view的宽
     */
    private int width;
    /**
     * view的高
     */
    private int height;
    public String text1;
    private String text2;
    private String text3;
    /**
     * 白色实心圆的半径
     */
    int r = 8;
    /**
     * 外圈的圆半径
     */
    int outerR = (int) (1.5 * r);

    private ImageView dot;

    public int currStyle = STYLE_NONE_TEXT;

    public int outerCircleColor = Color.LTGRAY;
    public int centerColor = Color.WHITE;
    public int lineColor = Color.WHITE;
    public int textColor = Color.WHITE;

    public float textSize=5f;
    int centerX;//中心点X的坐标
    int centerY;//中心点Y的坐标


    //直线的宽度
    int lineWidth = 2;
    int defaultTextsize = 15;
    int defaultTextPadding = 5;
    int defaultTextMarginBottom = 5;
    //显示的第一个字符串
    String displayText1;
    String displayText2;
    String displayText3;

    /**
     * text1的线条的折点
     */
    Point pointLine1Corner;
    /**
     * text1的线条的端点
     */
    Point pointLine1End;

    Point pointLine2Corner;
    Point pointLine2End;

    Point pointLine3End;
    Point pointLine3Corner;


    Rect centerRect;


    int displayTextCount = 0;
    private Paint mPaint;
    private Rect mBound;

    /**
     * 斜线L1的长度是5*r
     */
    int slantLength = 8 * r;

    int text1Width;
    int text1Height;
    int text2Width;
    int text2Height;
    int text3Width;
    int text3Height;


    private LabelInfo info;

    private Rect mTouchCenterRect;
    private HashMap<Integer, Rect> mTouchTextRectMap;

    public ZhuaView(Context context) {
        this(context, null);
    }

    public ZhuaView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);

        /**
         * 注意!!!这里一定要调用setWillNotDraw(false);
         * 否则不设置背景,viewgroup不会调用onDraw();
         */
        setWillNotDraw(false);
        initDot();
        info = new LabelInfo();
        mTouchTextRectMap = new HashMap<>();
    }

    /**
     * ######### Measure相关[END] #########
     */

    private void initDot() {
        dot = new ImageView(getContext());
        dot.setLayoutParams(new LayoutParams(2 * r, 2 * r));
        Bitmap bm = Bitmap.createBitmap(r * 2, r * 2, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStrokeWidth(r);
        p.setColor(Color.WHITE);
        c.drawCircle(r, r, r, p);
        dot.setImageBitmap(bm);
        this.addView(dot);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        switch (currStyle) {
            //以下是没text的
            case STYLE_NONE_TEXT:
                drawNoText(canvas);
                break;
            //以下是1个text的
            case STYLE_ONE_TEXT_3:
                drawOneText3(canvas);
                break;
            case STYLE_ONE_TEXT_4:
                drawOneText4(canvas);
                break;

            //以下是两个text的
            case STYLE_TWO_TEXT_3:
                drawTwoText3(canvas);
                break;
            case STYLE_TWO_TEXT_4:
                drawTwoText4(canvas);
                break;

            //以下是三个text的
            case STYLE_THREE_TEXT_3:
                drawThreeText3(canvas);
                break;
            case STYLE_THREE_TEXT_4:
                drawThreeText4(canvas);
                break;
        }
        //记录圆圈的位置
        recordRect(canvas);
    }


    /***
     * ### 显示效果[BEGIN] ###
     */
    private static final int ANIMATION_EACH_OFFSET = 400;
    private static final float ANIMATION_SCALE_MULTIPLE = 2.5f;

    public void alwaysWave() {
        AnimationSet as = new AnimationSet(true);
        ScaleAnimation sa = new ScaleAnimation(1f, ANIMATION_SCALE_MULTIPLE, 1f, ANIMATION_SCALE_MULTIPLE, ScaleAnimation.RELATIVE_TO_SELF,
                0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(ANIMATION_EACH_OFFSET * 3);
        sa.setRepeatCount(Animation.INFINITE);// 设置循环
        AlphaAnimation aniAlp = new AlphaAnimation(1, 0.1f);
        aniAlp.setRepeatCount(Animation.INFINITE);// 设置循环
        as.setDuration(ANIMATION_EACH_OFFSET * 3);
        as.addAnimation(sa);
        as.addAnimation(aniAlp);
        dot.startAnimation(as);
    }

    private OnTextClickListener onTextClickListener;

    public interface OnTextClickListener {
        //        void onOuterClick(AnimateLabelView v);
        void onTextClick(int position);
    }

    public void setOnTextClickListener(OnTextClickListener l) {
        this.onTextClickListener = l;
    }

    public void onTextClick(int position) {
//       Toast.makeText(getContext(), "text", Toast.LENGTH_SHORT).show();

        if (this.onTextClickListener != null) {
//            onOuterSideClickListener.onOuterClick(this);
            onTextClickListener.onTextClick(position);
        }
    }

    // TODO: 16/11/6  计算点击区域是否在重新点区域内或者是否在文字区域内
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isInView(event, mTouchCenterRect)) {
                    Log.e("onTouchEvent===", "isInView====");
                    handled = true;
                } else {
                    int hasTouch = -1;
                    for (int i = 0; i < mTouchTextRectMap.size(); i++) {
                        if (isInView(event, mTouchTextRectMap.get(i))) {
                            hasTouch = i;
                            onTextClick(i);
                        }
                    }
                    Log.e("onTouchEvent===", "outInView====hasTouch==" + hasTouch);

                    handled = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return handled;
    }

    private boolean isInView(MotionEvent event, Rect rect) {
        if (rect != null) {
            int left = rect.left;
            int right = rect.right;
            int top = rect.top;
            int bottom = rect.bottom;
            return event.getX(0) >= left && event.getX(0) <= right && event.getY(0) >= top && event.getY(0) <= bottom;

        }
        return false;
    }

    private void recordRect(Canvas canvas) {
        //记录圆的区域
        mTouchCenterRect = new Rect(centerX - outerR * 3, centerY - outerR * 3, centerX + outerR * 3, centerY + outerR * 3);
        // TODO: 16/11/6  以下记录每个文字的矩形区域，便于在onTouchEvent里面针对每个文字的矩形区域进行判断
        //以下矩形区域按照其他代码的写法感觉不正确，你自己在重新整理计算吧，这里面的代码实在太乱了
        switch (currStyle) {
            //以下是没text的
            case STYLE_NONE_TEXT:
//                drawNoText(canvas);
                break;
            //以下是1个text的
            case STYLE_ONE_TEXT_3:
                if (!TextUtils.isEmpty(displayText1)) {
                    Rect TextRect0 = new Rect(pointLine1End.x - defaultTextPadding, pointLine1End.y - defaultTextMarginBottom-text1Height,
                            pointLine1End.x - defaultTextPadding+text1Width, pointLine1End.y - defaultTextMarginBottom);
                    mTouchTextRectMap.put(0, TextRect0);

                }
                break;
            case STYLE_ONE_TEXT_4:
                if (!TextUtils.isEmpty(displayText1)) {
                    Rect TextRect0 = new Rect(centerX + outerR + defaultTextPadding, pointLine1End.y - defaultTextMarginBottom- text1Height,
                            centerX + outerR + defaultTextPadding + text1Width, pointLine1End.y - defaultTextMarginBottom);
                    mTouchTextRectMap.put(0, TextRect0);
                }
                break;

            //以下是两个text的
            case STYLE_TWO_TEXT_3:
                if (!TextUtils.isEmpty(displayText1)) {
                    Rect TextRect0 = new Rect(pointLine1End.x - defaultTextPadding, pointLine1End.y - defaultTextMarginBottom- text1Height,
                            pointLine1End.x + defaultTextPadding - text1Width, pointLine1Corner.y - defaultTextMarginBottom);
                    mTouchTextRectMap.put(0, TextRect0);

                }
                if(!TextUtils.isEmpty(displayText2)) {
                    Rect TextRect1 = new Rect(pointLine2End.x - defaultTextPadding, pointLine2End.y - defaultTextMarginBottom- text2Height,
                            pointLine2End.x - defaultTextPadding + text2Width, pointLine2End.y - defaultTextMarginBottom );

                    mTouchTextRectMap.put(0, TextRect1);
                }
                break;
            case STYLE_TWO_TEXT_4:
                if (!TextUtils.isEmpty(displayText1)) {
                    Rect TextRect0 = new Rect(pointLine1Corner.x + defaultTextPadding, pointLine1Corner.y - defaultTextMarginBottom-text1Height,
                            pointLine1Corner.x + defaultTextPadding + text1Width, pointLine1Corner.y - defaultTextMarginBottom);

                    mTouchTextRectMap.put(0, TextRect0);

                }
                if(!TextUtils.isEmpty(displayText2)) {
                    Rect TextRect1 = new Rect(pointLine2Corner.x + defaultTextPadding, pointLine2Corner.y - defaultTextMarginBottom-text2Height,
                            pointLine2Corner.x + defaultTextPadding + text2Width, pointLine2Corner.y - defaultTextMarginBottom );
                    mTouchTextRectMap.put(1, TextRect1);

                }
                break;

            //以下是三个text的
            case STYLE_THREE_TEXT_3:
                if (!TextUtils.isEmpty(displayText1)) {
                    Rect TextRect0 = new Rect(pointLine3End.x - defaultTextPadding, pointLine3End.y - defaultTextMarginBottom- text3Height,
                            pointLine3End.x - defaultTextPadding + text3Width, pointLine3End.y - defaultTextMarginBottom );
                    mTouchTextRectMap.put(0, TextRect0);
                }
                if(!TextUtils.isEmpty(displayText2)) {
                    Rect TextRect1 = new Rect(pointLine2End.x - defaultTextPadding, pointLine2End.y - defaultTextMarginBottom - text2Height,
                            pointLine2End.x - defaultTextPadding + text2Width, pointLine2End.y - defaultTextMarginBottom );
                    mTouchTextRectMap.put(1, TextRect1);
                }
                if(!TextUtils.isEmpty(displayText3)) {
                    Rect TextRect2 = new Rect(pointLine1End.x - defaultTextPadding, pointLine1End.y - defaultTextMarginBottom-text1Height,
                            pointLine1End.x - defaultTextPadding+ text1Width,  pointLine1End.y - defaultTextMarginBottom );
                    mTouchTextRectMap.put(2, TextRect2);
                }
                break;
            case STYLE_THREE_TEXT_4:
                if (!TextUtils.isEmpty(displayText1)) {
                    Rect TextRect0 = new Rect(pointLine1Corner.x + defaultTextPadding, pointLine1Corner.y - defaultTextMarginBottom-text1Height,
                            pointLine1Corner.x + defaultTextPadding + text1Width, pointLine1Corner.y - defaultTextMarginBottom);

                    mTouchTextRectMap.put(0, TextRect0);

                }
                if(!TextUtils.isEmpty(displayText2)) {
                    Rect TextRect1 = new Rect(centerX + defaultTextPadding, centerY - defaultTextMarginBottom-text2Height,
                            centerX + defaultTextPadding+ text2Width,  centerY - defaultTextMarginBottom );

                    mTouchTextRectMap.put(1, TextRect1);


                }
                if(!TextUtils.isEmpty(displayText3)) {
                    Rect TextRect2 = new Rect(pointLine3Corner.x + defaultTextPadding, pointLine3Corner.y - defaultTextMarginBottom- text3Height,
                            pointLine3Corner.x + defaultTextPadding+ text3Width,  pointLine3Corner.y - defaultTextMarginBottom );
                    mTouchTextRectMap.put(2, TextRect2);
                }
                break;
        }

    }

    private void drawThreeText4(Canvas canvas) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStrokeWidth(r);
        //外圆
        p.setColor(outerCircleColor);
        canvas.drawCircle(centerX, centerY, outerR, p);

        //内圆
        p.setColor(centerColor);
        canvas.drawCircle(centerX, centerY, r, p);

        //text1
        p.setStrokeWidth(lineWidth);
        p.setColor(lineColor);
        p.setTextSize(textSize);
        //右下斜线
        canvas.drawLine(centerX, centerY, pointLine1Corner.x, pointLine1Corner.y, p);
        //右下横线
        canvas.drawLine(pointLine1Corner.x, pointLine1Corner.y, pointLine1End.x, pointLine1End.y, p);
        //右下文字
        p.setColor(textColor);
        p.setTextSize(defaultTextsize);
        canvas.drawText(displayText1, pointLine1Corner.x + defaultTextPadding, pointLine1Corner.y - defaultTextMarginBottom, p);

        //text2
        p.setStrokeWidth(lineWidth);
        p.setColor(lineColor);
        p.setTextSize(textSize);
        //右横线
        canvas.drawLine(centerX, centerY, pointLine2End.x, pointLine2End.y, p);
        //右文字
        p.setColor(textColor);
        p.setTextSize(defaultTextsize);
        canvas.drawText(displayText2, centerX + defaultTextPadding, centerY - defaultTextMarginBottom, p);

        //text3
        p.setStrokeWidth(lineWidth);
        p.setColor(lineColor);
        p.setTextSize(textSize);
        //右上斜线
        canvas.drawLine(centerX, centerY, pointLine3Corner.x, pointLine3Corner.y, p);
        //右上横线
        canvas.drawLine(pointLine3Corner.x, pointLine3Corner.y, pointLine3End.x, pointLine3End.y, p);
        //右上文字
        p.setColor(textColor);
        p.setTextSize(defaultTextsize);
        canvas.drawText(displayText3, pointLine3Corner.x + defaultTextPadding, pointLine3Corner.y - defaultTextMarginBottom, p);

    }

    private void drawThreeText3(Canvas canvas) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStrokeWidth(r);
        //外圆
        p.setColor(outerCircleColor);
        canvas.drawCircle(centerX, centerY, outerR, p);

        //内圆
        p.setColor(centerColor);
        canvas.drawCircle(centerX, centerY, r, p);

        //text1
        p.setStrokeWidth(lineWidth);
        p.setColor(lineColor);
        p.setTextSize(textSize);
        //左下斜线
        canvas.drawLine(centerX, centerY, pointLine1Corner.x, pointLine1Corner.y, p);
        //左下横线
        canvas.drawLine(pointLine1Corner.x, pointLine1Corner.y, pointLine1End.x, pointLine1End.y, p);
        //左下文字
        p.setColor(textColor);
        p.setTextSize(defaultTextsize);
        canvas.drawText(displayText1, pointLine1End.x + defaultTextPadding, pointLine1End.y - defaultTextMarginBottom, p);

        //text2
        p.setStrokeWidth(lineWidth);
        p.setColor(lineColor);
        p.setTextSize(textSize);
        //左横线
        canvas.drawLine(centerX, centerY, pointLine2End.x, pointLine2End.y, p);
        //左文字
        p.setColor(textColor);
        p.setTextSize(defaultTextsize);
        canvas.drawText(displayText2, pointLine2End.x + defaultTextPadding, pointLine2End.y - defaultTextMarginBottom, p);

        //text3
        p.setStrokeWidth(lineWidth);
        p.setColor(lineColor);
        p.setTextSize(textSize);
        //左上斜线
        canvas.drawLine(centerX, centerY, pointLine3Corner.x, pointLine3Corner.y, p);
        //左上横线
        canvas.drawLine(pointLine3Corner.x, pointLine3Corner.y, pointLine3End.x, pointLine3End.y, p);
        //左上文字
        p.setColor(textColor);
        p.setTextSize(defaultTextsize);
        canvas.drawText(displayText3, pointLine3End.x + defaultTextPadding, pointLine3End.y - defaultTextMarginBottom, p);
    }

    private void drawTwoText4(Canvas canvas) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStrokeWidth(r);
        //外圆
        p.setColor(outerCircleColor);
        canvas.drawCircle(centerX, centerY, outerR, p);

        //内圆
        p.setColor(centerColor);
        canvas.drawCircle(centerX, centerY, r, p);

        //text1
        p.setStrokeWidth(lineWidth);
        p.setColor(lineColor);
        p.setTextSize(textSize);
        //右上斜线
        canvas.drawLine(centerX, centerY, pointLine1Corner.x, pointLine1Corner.y, p);
        //右上横线
        canvas.drawLine(pointLine1Corner.x, pointLine1Corner.y, pointLine1End.x, pointLine1End.y, p);
        //右上文字
        p.setColor(textColor);
        p.setTextSize(defaultTextsize);
        canvas.drawText(displayText1, pointLine1Corner.x + defaultTextPadding, pointLine1Corner.y - defaultTextMarginBottom, p);

        //text2
        p.setStrokeWidth(lineWidth);
        p.setColor(lineColor);
        p.setTextSize(textSize);
        //左下斜线
        canvas.drawLine(centerX, centerY, pointLine2Corner.x, pointLine2Corner.y, p);
        //左下横线
        canvas.drawLine(pointLine2Corner.x, pointLine2Corner.y, pointLine2End.x, pointLine2End.y, p);
        //左下文字
        p.setColor(textColor);
        p.setTextSize(defaultTextsize);
        canvas.drawText(displayText2, pointLine2Corner.x + defaultTextPadding, pointLine2Corner.y - defaultTextMarginBottom, p);
    }

    private void drawTwoText3(Canvas canvas) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStrokeWidth(r);
        //外圆
        p.setColor(outerCircleColor);
        canvas.drawCircle(centerX, centerY, outerR, p);

        //内圆
        p.setColor(centerColor);
        canvas.drawCircle(centerX, centerY, r, p);

        //text1
        p.setStrokeWidth(lineWidth);
        p.setColor(lineColor);
        p.setTextSize(textSize);
        //左上斜线
        canvas.drawLine(centerX, centerY, pointLine1Corner.x, pointLine1Corner.y, p);
        //左上横线
        canvas.drawLine(pointLine1Corner.x, pointLine1Corner.y, pointLine1End.x, pointLine1End.y, p);
        //左上文字
        p.setColor(textColor);
        p.setTextSize(defaultTextsize);
        canvas.drawText(displayText1, pointLine1End.x + defaultTextPadding, pointLine1End.y - defaultTextMarginBottom, p);

        //text2
        p.setStrokeWidth(lineWidth);
        p.setColor(lineColor);
        p.setTextSize(textSize);
        //左下斜线
        canvas.drawLine(centerX, centerY, pointLine2Corner.x, pointLine2Corner.y, p);
        //左下横线
        canvas.drawLine(pointLine2Corner.x, pointLine2Corner.y, pointLine2End.x, pointLine2End.y, p);
        //左下文字
        p.setColor(textColor);
        p.setTextSize(defaultTextsize);
        canvas.drawText(displayText2, pointLine2End.x + defaultTextPadding, pointLine2End.y - defaultTextMarginBottom, p);
    }

    private void drawOneText4(Canvas canvas) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStrokeWidth(r);
        //外圆
        p.setColor(outerCircleColor);
        canvas.drawCircle(centerX, centerY, outerR, p);

        //内圆
        p.setColor(centerColor);
        canvas.drawCircle(centerX, centerY, r, p);

        //单线样式2:右下斜线__/
        p.setStrokeWidth(lineWidth);
        p.setColor(lineColor);
        p.setTextSize(textSize);
        //左线
        canvas.drawLine(centerX, centerY, pointLine1End.x, pointLine1End.y, p);
        //文字
        p.setColor(textColor);
        p.setTextSize(defaultTextsize);
        canvas.drawText(displayText1, centerX + outerR + defaultTextPadding, pointLine1End.y - defaultTextMarginBottom, p);


    }

    private void drawOneText3(Canvas canvas) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStrokeWidth(r);
        //外圆
        p.setColor(outerCircleColor);
        canvas.drawCircle(centerX, centerY, outerR, p);

        //内圆
        p.setColor(centerColor);
        canvas.drawCircle(centerX, centerY, r, p);

        //单线样式2:右下斜线__/
        p.setStrokeWidth(lineWidth);
        p.setColor(lineColor);
        p.setTextSize(textSize);
        //左线
        canvas.drawLine(centerX, centerY, pointLine1End.x, pointLine1End.y, p);
        //文字
        p.setColor(textColor);
        p.setTextSize(defaultTextsize);
        canvas.drawText(displayText1, pointLine1End.x + defaultTextPadding, pointLine1End.y - defaultTextMarginBottom, p);

    }

    private void drawNoText(Canvas canvas) {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStrokeWidth(r);
        //外圆
        p.setColor(outerCircleColor);
        canvas.drawCircle(centerX, centerY, outerR, p);

        //内圆
        p.setColor(centerColor);
        canvas.drawCircle(centerX, centerY, r, p);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int right, int b) {
        int dotLeft = centerX - r;
        int dotTop = centerY - r;
        int dotRight = dotLeft + 2 * r;
        int dotBottom = dotTop + 2 * r;
        dot.layout(dotLeft, dotTop, dotRight, dotBottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureCore(widthMeasureSpec, heightMeasureSpec);

    }

    private void measureCore(int widthMeasureSpec, int heightMeasureSpec) {
        //设置默认padding
        setPadding(20, 20, 20, 20);
        checkDisplayText();
        switch (currStyle) {
            case STYLE_NONE_TEXT:
                measureStyleNoText();
                break;
            case STYLE_ONE_TEXT_3:
                measureStyleOneText3();
                break;
            case STYLE_ONE_TEXT_4:
                measureStyleOneText4();
                break;

            //以下是两个text
            case STYLE_TWO_TEXT_3:
                measureStyleTwoText3();
                break;
            case STYLE_TWO_TEXT_4:
                measureStyleTwoText4();
                break;

            //以下是三个text
            case STYLE_THREE_TEXT_3:
                measureStyleThreeText3();
                break;
            case STYLE_THREE_TEXT_4:
                measureStyleThreeText4();
                break;
        }
        setMeasuredDimension(width, height);

        //保持point的位置
        setPointAt(pointAtX, pointAtY);

        //在改变view大小后,调用parent的layout,改变view在视图中的大小
        if (getLayoutParams() != null) {
            getLayoutParams().width = width;
            getLayoutParams().height = height;
        }
        if (getParent() != null) {
            getParent().requestLayout();
        }
    }

    private void measureStyleThreeText4() {
        text1Width = getTextWidth(displayText1);
        text1Height = getTextHeight(displayText1);

        text2Width = getTextWidth(displayText2);
        text2Height = getTextHeight(displayText2);

        text3Width = getTextWidth(displayText3);
        text3Height = getTextHeight(displayText3);

        int maxTextWidth = Math.max(text1Width, Math.max(text2Width, text3Width));

        height = getPaddingTop() + text3Height + slantLength * 2 + getPaddingBottom();
        width = getPaddingLeft() + maxTextWidth + outerR + getPaddingRight();

        centerX = getPaddingLeft() + outerR;
        centerY = getPaddingTop() + text3Height + slantLength;

        pointLine1Corner = new Point();
        pointLine1Corner.x = centerX;
        pointLine1Corner.y = centerY + slantLength;

        pointLine1End = new Point();
        pointLine1End.x = pointLine1Corner.x + text1Width;
        pointLine1End.y = pointLine1Corner.y;

        pointLine2End = new Point();
        pointLine2End.x = centerX + text2Width;
        pointLine2End.y = centerY;

        pointLine3Corner = new Point();
        pointLine3Corner.x = centerX;
        pointLine3Corner.y = centerY - slantLength;

        pointLine3End = new Point();
        pointLine3End.x = pointLine3Corner.x + text3Width;
        pointLine3End.y = pointLine3Corner.y;

        setCenterRect(centerX, centerY);
    }

    private void measureStyleThreeText3() {
        text1Width = getTextWidth(displayText1);
        text1Height = getTextHeight(displayText1);

        text2Width = getTextWidth(displayText2);
        text2Height = getTextHeight(displayText2);

        text3Width = getTextWidth(displayText3);
        text3Height = getTextHeight(displayText3);

        int maxTextWidth = Math.max(text1Width, Math.max(text2Width, text3Width));

        height = getPaddingTop() + text3Height + slantLength * 2 + getPaddingBottom();
        width = getPaddingLeft() + maxTextWidth + outerR + getPaddingRight();

        centerX = getPaddingLeft() + maxTextWidth;
        centerY = getPaddingTop() + text3Height + slantLength;

        pointLine1Corner = new Point();
        pointLine1Corner.x = centerX;
        pointLine1Corner.y = centerY + slantLength;

        pointLine1End = new Point();
        pointLine1End.x = pointLine1Corner.x - text1Width;
        pointLine1End.y = pointLine1Corner.y;

        pointLine2End = new Point();
        pointLine2End.x = centerX - text2Width;
        pointLine2End.y = centerY;

        pointLine3Corner = new Point();
        pointLine3Corner.x = centerX;
        pointLine3Corner.y = centerY - slantLength;

        pointLine3End = new Point();
        pointLine3End.x = pointLine3Corner.x - text3Width;
        pointLine3End.y = pointLine3Corner.y;

        setCenterRect(centerX, centerY);
    }

    private void measureStyleTwoText4() {
        text1Width = getTextWidth(displayText1);
        text1Height = getTextHeight(displayText1);

        text2Width = getTextWidth(displayText2);
        text2Height = getTextHeight(displayText2);

        height = getPaddingTop() + text1Height + slantLength * 2 + getPaddingBottom();
        width = getPaddingLeft() + Math.max(text1Width, text2Width) + outerR + getPaddingRight();

        centerX = getPaddingLeft() + outerR;
        centerY = getPaddingTop() + text1Height + slantLength;


        pointLine1Corner = new Point();
        pointLine1Corner.x = centerX;
        pointLine1Corner.y = centerY - slantLength;

        pointLine1End = new Point();
        pointLine1End.x = pointLine1Corner.x + text1Width;
        pointLine1End.y = pointLine1Corner.y;

        pointLine2Corner = new Point();
        pointLine2Corner.x = centerX;
        pointLine2Corner.y = centerY + slantLength;

        pointLine2End = new Point();
        pointLine2End.x = pointLine2Corner.x + text2Width;
        pointLine2End.y = pointLine2Corner.y;

        setCenterRect(centerX, centerY);
    }

    private void measureStyleTwoText3() {
        text1Width = getTextWidth(displayText1);
        text1Height = getTextHeight(displayText1);

        text2Width = getTextWidth(displayText2);
        text2Height = getTextHeight(displayText2);

        height = getPaddingTop() + text1Height + slantLength * 2 + getPaddingBottom();
        width = getPaddingLeft() + Math.max(text1Width, text2Width) + outerR + getPaddingRight();

        centerX = getPaddingLeft() + Math.max(text1Width, text2Width);
        centerY = getPaddingTop() + text1Height + slantLength;


        pointLine1Corner = new Point();
        pointLine1Corner.x = centerX;
        pointLine1Corner.y = centerY - slantLength;

        pointLine1End = new Point();
        pointLine1End.x = pointLine1Corner.x - text1Width;
        pointLine1End.y = pointLine1Corner.y;

        pointLine2Corner = new Point();
        pointLine2Corner.x = centerX;
        pointLine2Corner.y = centerY + slantLength;

        pointLine2End = new Point();
        pointLine2End.x = pointLine2Corner.x - text2Width;
        pointLine2End.y = pointLine2Corner.y;

        setCenterRect(centerX, centerY);
    }

    private void measureStyleOneText4() {
        text1Width = getTextWidth(displayText1);
        text1Height = getTextHeight(displayText1);

        centerX = getPaddingLeft() + outerR;
        centerY = getPaddingTop() + text1Height;

        width = centerX + outerR + text1Width + getPaddingRight();
        height = centerY + outerR + getPaddingBottom();

        pointLine1End = new Point();
        pointLine1End.x = centerX + outerR + text1Width;
        pointLine1End.y = centerY;

        setCenterRect(centerX, centerY);
    }

    private void measureStyleOneText3() {
        text1Width = getTextWidth(displayText1);
        text1Height = getTextHeight(displayText1);

        centerX = getPaddingLeft() + text1Width + outerR;
        centerY = getPaddingTop() + text1Height;

        width = centerX + outerR + getPaddingRight();
        height = centerY + outerR + getPaddingBottom();

        pointLine1End = new Point();
        pointLine1End.x = centerX - outerR - text1Width;
        pointLine1End.y = centerY;

        setCenterRect(centerX, centerY);
    }

    //测量的过程中,首先确定了多个关键的绘图的点
    private void measureStyleNoText() {
        centerX = getPaddingLeft() + outerR;
        centerY = getPaddingTop() + outerR;
        width = getPaddingLeft() + outerR * 2 + getPaddingRight();
        height = getPaddingTop() + outerR * 2 + getPaddingBottom();

        setCenterRect(centerX, centerY);
    }

    private void setCenterRect(int centerX, int centerY) {
        //中心范围int left, int top, int right, int bottom
        centerRect = new Rect(centerX - outerR, centerY - outerR, centerX + outerR, centerY + outerR);

        /**
         * 获得绘制文本的宽和高
         */
    }

    /**
     * 返回text的宽度+左右padding
     *
     * @param text
     * @return
     */
    private int getTextWidth(String text) {
        if (isNull(text)) {
            return defaultTextPadding * 2;
        }

        Rect rect = new Rect();
        Paint p = new Paint();
        p.setTextSize(defaultTextsize);
        p.getTextBounds(text, 0, text.length(), rect);
        float textWidth = rect.width();
        int desired = (int) (defaultTextPadding + textWidth + defaultTextPadding);
        return desired;
    }

    /**
     * 返回text的高度+默认离底部的距离
     *
     * @param text
     * @return
     */
    private int getTextHeight(String text) {
        Rect rect = new Rect();
        Paint p = new Paint();
        p.setTextSize(defaultTextsize);
        p.getTextBounds(text, 0, text.length(), rect);
        float textHeight = rect.height();
        int desired = (int) (textHeight + defaultTextMarginBottom);
        return desired;
    }

    /**
     * 返回在父容器中pointAt
     *
     * @return
     */
    private Point getPointAt() {
        int x = (int) getX() + centerX;
        int y = (int) getY() + centerY;

        Log.d("pointAt:", x + "/" + y);

        //???10.22屏蔽
        //setPointAt(pointAtX, pointAtY);
        return new Point(x, y);
    }

    /**
     * 中心指示的位置(在父容器中的像素坐标)
     */
    public int pointAtX, pointAtY;

    public void setPointAt(final float x, final float y) {
        pointAtX = (int) x;
        pointAtY = (int) y;

        info.pxX = x;
        info.pxY = y;
        int centerXinParent = (int) getX() + centerX;
        int centerYinParent = (int) getY() + centerY;

        //因为添加了pcX, pcY,更新位置后需要更新
        updatePcXandPcY(centerXinParent, centerYinParent);

        setX(getX() + x - centerXinParent);
        setY(getY() + y - centerYinParent);
    }


    /**
     * 因为添加了pcX, pcY,更新位置后需要更新
     *
     * @param pxX
     * @param pxY
     */
    private void updatePcXandPcY(float pxX, float pxY) {
        if (getParent() != null) {
            float pw = ((ViewGroup) getParent()).getWidth();
            float ph = ((ViewGroup) getParent()).getHeight();

            if (pw > 0 && ph > 0) {
                info.pcX = pxX / pw;
                info.pcY = pxY / ph;
            }
        }
    }

    /**
     * 查找应该显示的text,当所有的text为空时候返回false
     *
     * @return 是否有该显示的字符?
     */
    private boolean checkDisplayText() {
        /**
         * 修改前显示的text数目
         */
        int oldDisplayTextCount = displayTextCount;

        displayTextCount = 0;
        displayText1 = null;
        displayText2 = null;
        displayText3 = null;

        if (!isNull(text1)) {
            displayText1 = text1;
        }

        if (!isNull(text2)) {
            if (displayText1 == null) {
                displayText1 = text2;
            } else {
                displayText2 = text2;
            }
        }

        if (!isNull(text3)) {
            if (displayText1 == null) {
                displayText1 = text3;
            } else {
                if (displayText2 == null) {
                    displayText2 = text3;
                } else {
                    displayText3 = text3;
                }
            }
        }

        if (displayText1 == null) {
            displayTextCount = 0;
        } else if (displayText2 == null) {
            displayTextCount = 1;
        } else if (displayText3 == null) {
            displayTextCount = 2;
        } else {
            displayTextCount = 3;
        }

        if (displayTextCount < 1) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * ############# Draw相关[END] #############
     */

    private boolean isNull(String text) {
        if (text == null || "".equals(text.trim())) {
            return true;
        }

        return false;
    }


    public LabelInfo getLabelInfo() {
        info.pxX = getPointAt().x;
        info.pxY = getPointAt().y;
        return info;
    }

    /**
     * 直接设置
     */
    public void setLabelInfo(LabelInfo info) {
        if (info == null) {
            return;
        }
        this.info = info;

        //保留一下会改变的pxX/Y
        float pxX = info.pxX;
        float pxY = info.pxY;

        /**
         * settext会改变info的pxX
         */
        setText1(combineTitleNInput(info.title1Text, info.input1Text));
        setText2(combineTitleNInput(info.title2Text, info.input2Text));
        setText3(combineTitleNInput(info.title3Text, info.input3Text));

        if (info.pcX > 0 && info.pcY > 0) {
            setPointAtPercent(info.pcX, info.pcY);
        } else if (pxX > 0 && pxY > 0) {
            setPointAt(pxX, pxY);
        }

    }

    /**
     * 优先使用比例的位置
     *
     * @param xpc 水平方向上的百分比
     * @param ypc 垂直方向上的百分比
     */
    public void setPointAtPercent(final float xpc, final float ypc) {
        info.pcX = xpc;
        info.pcY = ypc;

        post(new PointAtPercentRunnable(xpc, ypc));
    }

    class PointAtPercentRunnable implements Runnable {
        private float xpc, ypc;

        public PointAtPercentRunnable(float xpc, float ypc) {
            this.xpc = xpc;
            this.ypc = ypc;
        }

        @Override
        public void run() {
            if (getParent() == null) {
                return;
            }

            if (getParent() instanceof RelativeLayout) {
                RelativeLayout p = (RelativeLayout) getParent();

                float pxX = p.getWidth() * xpc;
                float pxY = p.getHeight() * ypc;

                setPointAt(pxX, pxY);
            }
        }
    }

    public String combineTitleNInput(String t, String i) {
        if (isNull(t) && isNull(i)) {
            return null;
        } else {
            if (isNull(t)) {
                return i;
            } else if (isNull(i)) {
                return t;
            } else {
                return t + " " + i;
            }
        }
    }

    public void setText1(String text) {
        this.text1 = text;
        checkDisplayText();
        updateViewAfterSetText();
    }

    public void setText2(String text) {
        this.text2 = text;
        checkDisplayText();
        updateViewAfterSetText();
    }

    public void setText3(String text) {
        this.text3 = text;
        checkDisplayText();
        updateViewAfterSetText();
    }

    public void updateViewAfterSetText() {
        //如果只是显示内容改变,也会改变视图的大小
        measureCore(currStyle, currStyle);
        invalidate();
    }

}
