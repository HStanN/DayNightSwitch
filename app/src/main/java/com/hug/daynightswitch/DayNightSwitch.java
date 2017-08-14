package com.hug.daynightswitch;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Checkable;

/**
 * Created by HStan on 2017/8/9.
 * <p>
 * a Day and Night style SwitchButton
 */

public class DayNightSwitch extends View implements Checkable {
    private Context mContext;

    private Bitmap mCloud;
    private RectF cloudRectF;

    private float cloudWid;
    private float cloudHeight;

    private Paint mBoaderPaint;
    private Paint mBodyPaint;
    private Paint mSpotPaint;
    private Paint mSpotOutPaint;
    private Paint mMoonPaint;
    private Paint mCloudPaint;
    private Paint mStarPaint;


    private Path mOvalPath;
    private Path mOvalInPath;
    private Path mSpotPath;

    private float mRadius;
    private float mSpotRadius;
    private float mBoaderWidth = 5;

    private Point spotPoint;
    private Point spotShadowPoint;

    private float star1R;
    private float star2R;
    private float star3R;
    private float star4R;
    private float star5R;
    private float star6R;
    private float star7R;


    private static final float STAR1R = 3;
    private static final float STAR2R = 4;
    private static final float STAR3R = 3;
    private static final float STAR4R = 2;
    private static final float STAR5R = 3;
    private static final float STAR6R = 2;
    private static final float STAR7R = 4;


    //colors
    private int mBoaderColor;
    private int mBodyColor;
    private int mSpotOnColor;
    private int mSpotOnColorOut;

    private int mWidth;
    private int mHeight;

    private int mDuration = 400;
    //default size
    private static final int DEFAULT_SWITCH_WIDTH = 110;
    private static final int DEFAULT_SWITCH_HEIGHT = 60;

    //default colors
    private static final int SWITCH_ON_COLOR = 0xFF9EE3FB;
    private static final int SWITCH_ON_COLOR_OUT = 0xFF86C3D7;
    private static final int SWITCH_OFF_COLOR = 0xFF3C4145;
    private static final int SWITCH_OFF_COLOR_OUT = 0xFF1C1C1C;
    private static final int SPOT_ON_COLOR = 0xFFFFDF6D;
    private static final int SPOT_ON_COLOR_OUT = 0xFFE1C348;
    private static final int SPOT_OFF_COLOR = 0xFFFFFFFF;
    private static final int SPOT_OFF_COLOR_OUT = 0xFFEFF4C6;

    //status
    public static final int DAY = 0;
    public static final int NIGHT = 1;

    private static final int SHADOW_DELAY = 50;

    private int mStatus;
    private boolean mChecked;
    private boolean isAnimating = false;

    private ValueAnimator spotMoveAnimator;
    private ValueAnimator spotShadowMoveAnimator;

    private OnDayNightChangeListener listener;

    public interface OnDayNightChangeListener{
        void onCheck(int status, boolean checked);
    }

    public DayNightSwitch(Context context) {
        this(context, null);
    }

    public DayNightSwitch(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DayNightSwitch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DayNightSwitch, 0, 0);
        mStatus = ta.getInt(R.styleable.DayNightSwitch_status, DAY);

        mCloud = BitmapFactory.decodeResource(getResources(), R.mipmap.cloud);
        star1R = STAR1R;
        star2R = STAR2R;
        star3R = STAR3R;
        star4R = STAR4R;
        star5R = STAR5R;
        star6R = STAR6R;
        star7R = STAR7R;
        if (mStatus == DAY) {
            mChecked = true;
            mBoaderColor = SWITCH_ON_COLOR_OUT;
            mBodyColor = SWITCH_ON_COLOR;
            mSpotOnColor = SPOT_ON_COLOR;
            mSpotOnColorOut = SPOT_ON_COLOR_OUT;
        } else if (mStatus == NIGHT) {
            mChecked = false;
            mBoaderColor = SWITCH_OFF_COLOR_OUT;
            mBodyColor = SWITCH_OFF_COLOR;
            mSpotOnColor = SPOT_OFF_COLOR;
            mSpotOnColorOut = SPOT_OFF_COLOR_OUT;
        }

        mContext = context;
        mMoonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMoonPaint.setStyle(Paint.Style.STROKE);
        mMoonPaint.setColor(SPOT_OFF_COLOR_OUT);
        mMoonPaint.setStrokeWidth(5);
        mMoonPaint.setStrokeCap(Paint.Cap.BUTT);

        mStarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mStarPaint.setStyle(Paint.Style.FILL);
        mStarPaint.setColor(Color.WHITE);

        mCloudPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCloudPaint.setFilterBitmap(true);

        mSpotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSpotPaint.setStyle(Paint.Style.FILL);
        mSpotPaint.setColor(mSpotOnColor);

        mSpotOutPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mSpotOutPaint.setStyle(Paint.Style.FILL);
        mSpotOutPaint.setColor(mSpotOnColorOut);

        mBoaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBoaderPaint.setStyle(Paint.Style.FILL);
        mBoaderPaint.setColor(mBoaderColor);

        mBodyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBodyPaint.setStyle(Paint.Style.FILL);
        mBodyPaint.setColor(mBodyColor);
        mOvalPath = new Path();
        mOvalInPath = new Path();
        mSpotPath = new Path();
        setClickable(true);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(mOvalPath, mBoaderPaint);
        canvas.drawPath(mOvalInPath, mBodyPaint);
        drawSpot(canvas);
        if (mStatus == DAY) {
            drawCloud(canvas);
        }
        if (mStatus == NIGHT) {
            drawStars(canvas);
        }
    }

    private void drawStars(final Canvas canvas) {
        canvas.drawCircle(mWidth * 0.75f, mRadius * 0.35f, star1R, mStarPaint);
        canvas.drawCircle(mWidth * 0.80f, mRadius * 0.78f, star2R, mStarPaint);
        canvas.drawCircle(mWidth * 0.63f, mRadius * 0.53f, star3R, mStarPaint);
        canvas.drawCircle(mWidth * 0.55f, mRadius * 1.58f, star4R, mStarPaint);
        canvas.drawCircle(mWidth * 0.65f, mRadius * 0.90f, star5R, mStarPaint);
        canvas.drawCircle(mWidth * 0.85f, mRadius * 1.30f, star6R, mStarPaint);
        canvas.drawCircle(mWidth * 0.73f, mRadius * 1.60f, star7R, mStarPaint);

    }

    private void drawSpot(Canvas canvas) {
        mSpotPath = new Path();
        mSpotPath.addCircle(spotPoint.x, spotPoint.y, mSpotRadius + 5, Path.Direction.CW);
        mSpotPath.addCircle(spotShadowPoint.x, spotShadowPoint.y, mSpotRadius + 5, Path.Direction.CW);
        mSpotPath.moveTo(spotPoint.x, spotPoint.y - (mSpotRadius + 5));
        mSpotPath.lineTo(spotShadowPoint.x, spotShadowPoint.y - (mSpotRadius + 5));
        mSpotPath.moveTo(spotPoint.x, spotPoint.y + mSpotRadius + 5);
        mSpotPath.lineTo(spotShadowPoint.x, spotShadowPoint.y + mSpotRadius + 5);
        mSpotPath.close();
        canvas.drawPath(mSpotPath, mSpotOutPaint);
        canvas.drawCircle(spotPoint.x, spotPoint.y, mSpotRadius, mSpotPaint);

        if (mStatus == NIGHT){
            canvas.drawCircle(spotPoint.x + mSpotRadius - 7,spotPoint.y - 7,4,mMoonPaint);
            canvas.drawCircle(spotPoint.x - mSpotRadius + 15,spotPoint.y + 12,3,mMoonPaint);
            canvas.drawCircle(spotPoint.x - mSpotRadius + 5,spotPoint.y - 10 ,2,mMoonPaint);
        }

        mSpotPath = null;
    }

    private void drawCloud(Canvas canvas) {
        cloudRectF = new RectF(mWidth / 2, mRadius, mWidth / 2 + cloudWid, mRadius +
                cloudHeight);
        canvas.drawBitmap(mCloud, null, cloudRectF, mCloudPaint);
        cloudRectF = null;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int widMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widMode != MeasureSpec.AT_MOST) {
            mWidth = width;
        }else{
            mWidth = DEFAULT_SWITCH_WIDTH;
        }
        if (heightMode != MeasureSpec.AT_MOST) {
            mHeight = height;
        }else{
            mHeight = DEFAULT_SWITCH_HEIGHT;
        }
        setMeasuredDimension(width, height);
        mRadius = mHeight / 2;
        mSpotRadius = mRadius - mBoaderWidth * 2 - 2;

        if (mStatus == DAY) {
            cloudWid = (mHeight/2 - 15)  * 1.44f;
            cloudHeight = mHeight/2 - 15;
            spotPoint = new Point((int) (mWidth - mRadius), (int) mRadius);
            spotShadowPoint = new Point((int) (mWidth - mRadius), (int) mRadius);
        } else if (mStatus == NIGHT) {
            spotPoint = new Point((int) mRadius, (int) mRadius);
            spotShadowPoint = new Point((int) mRadius, (int) mRadius);
        }
        drawSwitchBody();
    }

    private void drawSwitchBody() {
        RectF rectF = new RectF(0, 0, mWidth, mHeight);
        RectF in = new RectF(mBoaderWidth, mBoaderWidth, mWidth - mBoaderWidth, mHeight - mBoaderWidth);
        mOvalInPath.addRoundRect(in, mRadius - mBoaderWidth, mRadius - mBoaderWidth, Path.Direction.CW);
        mOvalInPath.close();
        mOvalPath.addRoundRect(rectF, mRadius, mRadius, Path.Direction.CW);
        mOvalPath.close();
    }

    /**
     * @param state DAY and NIGHT
     */
    private void setAnimatedState(int state) {
        if (state == DAY) {
            mStatus = DAY;
            mChecked = true;
            spotShadowMoveAnimator = getSpotMoveAnimator(true);
            spotMoveAnimator = getSpotMoveAnimator(true);
        } else {
            mStatus = NIGHT;
            mChecked = false;
            spotShadowMoveAnimator = getSpotMoveAnimator(false);
            spotMoveAnimator = getSpotMoveAnimator(false);
        }
        spotShadowMoveAnimator.setDuration(mDuration);
        spotShadowMoveAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        spotShadowMoveAnimator.setStartDelay(SHADOW_DELAY);
        spotShadowMoveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float d = (float) animation.getAnimatedValue();
                spotShadowPoint.x = (int) (d * (mWidth - 2*mRadius) + mRadius);
                invalidate();
            }
        });
        spotShadowMoveAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                isAnimating = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        spotMoveAnimator.setDuration(mDuration);
        spotMoveAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        spotMoveAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float d = (float) animation.getAnimatedValue();
                spotPoint.x = (int)(d * (mWidth - 2*mRadius) + mRadius);
                mBoaderPaint.setColor(translateColor(d,SWITCH_OFF_COLOR_OUT, SWITCH_ON_COLOR_OUT));
                mBodyPaint.setColor(translateColor(d,SWITCH_OFF_COLOR, SWITCH_ON_COLOR));
                mSpotOutPaint.setColor(translateColor(d,SPOT_OFF_COLOR_OUT, SPOT_ON_COLOR_OUT));
                mSpotPaint.setColor(translateColor(d,SPOT_OFF_COLOR, SPOT_ON_COLOR));
                if (mStatus == DAY) {
                    cloudWid = (d) * (mHeight/2 - 15) * 1.44f;
                    cloudHeight = (d) * (mHeight/2 - 15);
                }else if (mStatus == NIGHT) {
                    star1R = (1-d) * STAR1R;
                    star2R = (1-d) * STAR2R;
                    star3R = (1-d) * STAR3R;
                    star4R = (1-d) * STAR4R;
                    star5R = (1-d) * STAR5R;
                    star6R = (1-d) * STAR6R;
                    star7R = (1-d) * STAR7R;
                }
                invalidate();
            }
        });
        spotMoveAnimator.start();
        spotShadowMoveAnimator.start();
    }

    private int translateColor(float percent, int startColor, int endColor) {
        return (Integer) new ArgbEvaluator().evaluate(percent, startColor, endColor);
    }

    private ValueAnimator getSpotMoveAnimator(boolean checkState) {
        if (checkState) {
            return ValueAnimator.ofFloat(0,1);
        } else {
            return ValueAnimator.ofFloat(1,0);
        }
    }

    public void setOnDayNightChangeListener(OnDayNightChangeListener listener){
        this.listener = listener;
    }

    @Override
    public void setChecked(boolean checked) {
        if (isAnimating) {
            return;
        }
        if (mStatus == DAY) {
            setAnimatedState(NIGHT);
        } else if (mStatus == NIGHT) {
            setAnimatedState(DAY);
        }
        if (listener != null){
            listener.onCheck(mStatus,mChecked);
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
    }

    @Override
    public boolean performClick() {
        if (mChecked){
            setChecked(false);
        }else{
            setChecked(true);
        }
        return super.performClick();
    }

    public int dp2px(float dpValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
