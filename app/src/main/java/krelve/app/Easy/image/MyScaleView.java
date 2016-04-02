package krelve.app.Easy.image;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

/**
 * Created by Me on 2016/3/17 0017.
 */
public class MyScaleView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener,
        ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {

    public static final float SCALE_MAX = 4.0f;
    private static final float SCALE_MID = 2.0f;
    private boolean isAutoScale;
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector detector;
    private Matrix matrix;
    private float[] matrixValue = new float[9];

    private float initScale = 1.0f;
    private float midScale = 2.0f;
    private float maxScale = 10.0f;
    boolean IsOnceLayout = true;

    private int mTouchSlop;

    private float mLastX;
    private float mLastY;

    private boolean isCanDrag;
    private int lastPointerCount;

    private boolean isCheckTopAndBottom = true;
    private boolean isCheckLeftAndRight = true;

    public MyScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //设置图片通过矩阵控制
        super.setScaleType(ScaleType.MATRIX);

        //实例化伸缩手势探测器
        scaleGestureDetector = new ScaleGestureDetector(context, this);

        detector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e)
                    {
                        if (isAutoScale == true)
                            return true;

                        float x = e.getX();
                        float y = e.getY();
                        //Log.e("DoubleTap", getScale() + " , " + initScale);
                        if (getCurrentImageScale() < SCALE_MID)
                        {
                            postDelayed(
                                    new AutoScaleRunnable(SCALE_MID, x, y), 16);
                            isAutoScale = true;
                        } else if (getCurrentImageScale() >= SCALE_MID
                                && getCurrentImageScale() < SCALE_MAX)
                        {
                            postDelayed(
                                    new AutoScaleRunnable(SCALE_MAX, x, y), 16);
                            isAutoScale = true;
                        } else
                        {
                            postDelayed(
                                    new AutoScaleRunnable(initScale, x, y), 16);
                            isAutoScale = true;
                        }

                        return true;
                    }
                });

        matrix = new Matrix();
        setOnTouchListener(this);

    }


    /**
     * 自动缩放的任务
     *
     * @author zhy
     */
    private class AutoScaleRunnable implements Runnable {
        static final float BIGGER = 1.07f;
        static final float SMALLER = 0.93f;
        private float mTargetScale;
        private float tmpScale;

        /**
         * 缩放的中心
         */
        private float x;
        private float y;

        /**
         * 传入目标缩放值，根据目标值与当前值，判断应该放大还是缩小
         *
         * @param targetScale
         */
        public AutoScaleRunnable(float targetScale, float x, float y) {
            this.mTargetScale = targetScale;
            this.x = x;
            this.y = y;
            if (getCurrentImageScale() < mTargetScale) {
                tmpScale = BIGGER;
            } else {
                tmpScale = SMALLER;
            }

        }

        @Override
        public void run() {
            // 进行缩放
            matrix.postScale(tmpScale, tmpScale, x, y);
            checkBorderAndCenterWhenScale();
            setImageMatrix(matrix);

            final float currentScale = getCurrentImageScale();
            // 如果值在合法范围内，继续缩放
            if (((tmpScale > 1f) && (currentScale < mTargetScale))
                    || ((tmpScale < 1f) && (mTargetScale < currentScale))) {
                postDelayed(this, 16);
            } else
            // 设置为目标的缩放比例
            {
                final float deltaScale = mTargetScale / currentScale;
                matrix.postScale(deltaScale, deltaScale, x, y);
                checkBorderAndCenterWhenScale();
                setImageMatrix(matrix);
                isAutoScale = false;
            }

        }
    }


    public MyScaleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public MyScaleView(Context context) {
        this(context, null);
    }

    /**
     * 获取布局参数
     */
    @Override
    public void onGlobalLayout() {
        if (IsOnceLayout) {
            Drawable drawable = getDrawable();
            if (drawable == null) {
                return;
            }
            //获取父控件的宽度
            int parentWidth = getWidth();
            int parentHeight = getHeight();

            //获取图片的宽高
            int drawableHeight = drawable.getIntrinsicHeight();
            int drawableWidth = drawable.getIntrinsicWidth();

            //定义缩放比
            float scale = 1.0f;
            //当图片宽度大于父控件的宽度，当高度小于父控件高度时
            if (drawableWidth > parentWidth && drawableHeight <= parentHeight) {
                scale = parentWidth * 1.0f / drawableWidth;
            }

            //当图片高度高于父控件，但宽度小于父控件时
            if (drawableHeight > parentHeight && drawableWidth <= parentWidth) {
                scale = parentWidth * 1.0f / drawableWidth;
            }

            //当图片宽度和高度都大于父控件时（缩小）
//            if (drawableHeight > parentHeight && drawableWidth > parentWidth) {
//                scale = Math.min(parentHeight * 1.0f / drawableHeight, parentWidth * 1.0f / drawableWidth);
//            }
//
//
            // 当图片宽度和高度都小于父控件（扩大）
            if (drawableHeight < parentHeight && drawableWidth < parentWidth) {
                scale = Math.min(parentHeight * 1.0f / drawableHeight, parentWidth * 1.0f / drawableWidth);
            }

            //将图片移动到父控件中间
            float dx = (parentWidth - drawableWidth) / 2;
            float dy = (parentHeight - drawableHeight) / 2;

            matrix.postTranslate(dx, dy);
            initScale = scale;
            //前2个是x,y的缩放比，后面的是缩放的中心点
            matrix.postScale(scale, scale, parentWidth / 2, parentHeight / 2);
            setImageMatrix(matrix);
            IsOnceLayout = false;
        }

    }

    private float getCurrentImageScale() {
        matrix.getValues(matrixValue);
        return matrixValue[Matrix.MSCALE_X];
    }


    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        //获取图片当前的缩放比
        float currentScalse = getCurrentImageScale();

        //拿到图片将要的缩放比例
        float scaleFactor = detector.getScaleFactor();

        if (getDrawable() == null) {
            return true;
        }

        // 用户将要放大图片或者用户将要缩小图片
        if ((scaleFactor > 1.0f && currentScalse < maxScale) || (scaleFactor < 1.0f && currentScalse > initScale)) {
            // 缩小时当缩小的倍数大于最小状态，则致值为最小
            if (scaleFactor * currentScalse < initScale) {
                scaleFactor = initScale / currentScalse;
            }

            // 放大时当放大的倍数大于最大状态，则致值为最大
            if (scaleFactor * currentScalse > maxScale) {
                scaleFactor = maxScale / currentScalse;
            }
            matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            // 检查边界和中心点
            checkBorderAndCenterWhenScale();
            setImageMatrix(matrix);
        }
        return true;
    }

    /**
     * 当在缩放的时候，对图片的边界和中心进行控制
     */
    private void checkBorderAndCenterWhenScale() {
        // 获取当前缩放过程中的图片的矩形
        RectF rectF = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        // 获取父控件的宽高
        int parentWidth = getWidth();
        int parentHeight = getHeight();

        // 如果宽度大于屏幕宽度
        if (rectF.width() >= parentWidth) {
            if (rectF.left > 0) {// 左边出现了空白
                deltaX = -rectF.left;// 往左移动
            }
            if (rectF.right < parentWidth) {// 右边出现了空白
                deltaX = parentWidth - rectF.right;// 往右移动
            }
        }

        // 如果高度大于屏幕高度
        if (rectF.height() >= parentHeight) {
            if (rectF.top > 0) {// 上边出现了空白
                deltaY = -rectF.top;// 往下移动
            }

            if (rectF.bottom < parentHeight) {// 下面出现了空白
                deltaY = parentHeight - rectF.bottom;// 往下移动
            }
        }
        // 如果宽度小于父控件的宽度
        if (rectF.width() < parentWidth) {// 要基中显示
            deltaX = parentWidth * 0.5f - rectF.right + 0.5f * rectF.width();
        }
        // 如果高度消息小于父控件的高度
        if (rectF.height() < parentHeight) {// 需要基中显示
            deltaY = parentHeight * 0.5f - rectF.bottom + 0.5f * rectF.height();
        }
        // 将图片移动到父控件中心
        matrix.postTranslate(deltaX, deltaY);

    }


    /**
     * 获取图片通过矩阵控制缩放之后的矩形
     *
     * @return
     */
    private RectF getMatrixRectF() {
        Matrix matrix2 = matrix;
        RectF rectF = new RectF();
        Drawable drawable = this.getDrawable();
        if (drawable != null) {
            rectF.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            //经过此方法后rectF里的left，top就是屏幕离图片边界的距离
            //然而我也不知道为什么
            matrix2.mapRect(rectF);
        }
        return rectF;
    }


    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (detector.onTouchEvent(event))
            return true;
        scaleGestureDetector.onTouchEvent(event);
        // 用户缩放手机探测器处理触摸事件
        float x = 0, y = 0;
        // 拿到触摸点的个数

        final int pointerCount = event.getPointerCount();
        // 得到多个触摸点的x与y均值
        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x = x / pointerCount;
        y = y / pointerCount;

        /**
         * 每当触摸点发生变化时，重置mLasX , mLastY
         */
        if (pointerCount != lastPointerCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }

        lastPointerCount = pointerCount;

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mLastX;
                float dy = y - mLastY;

                if (!isCanDrag) {
                    isCanDrag = isCanDrag(dx, dy);
                }
                if (isCanDrag) {
                    RectF rectF = getMatrixRectF();
                    if (getDrawable() != null) {
                        isCheckLeftAndRight = isCheckTopAndBottom = true;
                        // 如果宽度小于屏幕宽度，则禁止左右移动
                        if (rectF.width() < getWidth()) {
                            dx = 0;
                            isCheckLeftAndRight = false;
                        }
                        // 如果高度小雨屏幕高度，则禁止上下移动
                        if (rectF.height() < getHeight()) {
                            dy = 0;
                            isCheckTopAndBottom = false;
                        }
                        matrix.postTranslate(dx, dy);
                        checkMatrixBounds();
                        setImageMatrix(matrix);
                    }
                }
                mLastX = x;
                mLastY = y;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                lastPointerCount = 0;
                break;

        }
        return true;
    }

    /**
     * 移动时，进行边界判断，主要判断宽或高大于屏幕的
     */
    private void checkMatrixBounds() {
        RectF rect = getMatrixRectF();

        float deltaX = 0, deltaY = 0;
        final float viewWidth = getWidth();
        final float viewHeight = getHeight();
        // 判断移动或缩放后，图片显示是否超出屏幕边界
        if (rect.top > 0 && isCheckTopAndBottom) {
            deltaY = -rect.top;
        }
        if (rect.bottom < viewHeight && isCheckTopAndBottom) {
            deltaY = viewHeight - rect.bottom;
        }
        if (rect.left > 0 && isCheckLeftAndRight) {
            deltaX = -rect.left;
        }
        if (rect.right < viewWidth && isCheckLeftAndRight) {
            deltaX = viewWidth - rect.right;
        }
        matrix.postTranslate(deltaX, deltaY);


    }

    /**
     * 是否是推动行为
     *
     * @param dx
     * @param dy
     * @return
     */
    private boolean isCanDrag(float dx, float dy) {
        return Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        // 注册全局布局监听器
        getViewTreeObserver().addOnGlobalLayoutListener(this);

    }


}
