package zijing.com.lottery_demo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class LuckyPanView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder mHolder;
    /**
     * 与SurfaceHolder绑定的Canvas
     */
    private Canvas mCanvas;
    /**
     * 用于绘制的线程
     */
    private Thread t;
    /**
     * 线程的控制开关
     */
    private boolean isRunning;

    /**
     * 抽奖的文字
     */
    private String[] mStrs = new String[]{"单反相机", "IPAD", "恭喜发财", "IPHONE", "妹子一只", "恭喜发财"};
    /**
     * 每个盘块的颜色
     */
    private int[] mColors = new int[]{0xFFFFC300, 0xFFF17E01, 0xFFFFC300, 0xFFFFC433, 0xFFFFC300, 0xFFF17E01};
    /**
     * 与文字对应的图片
     */
    private int[] mImgs = new int[]{R.drawable.bulb, R.drawable.camera,
            R.drawable.circle_bg, R.drawable.computer, R.drawable.cup,
            R.drawable.diamond};

    /**
     * 与文字对应图片的bitmap数组
     */
    private Bitmap[] mImgsBitmap;
    /**
     * 盘块的个数
     */
    private int mItemCount = 6;

    /**
     * 绘制盘块的范围
     */
    private RectF mRange = new RectF();
    /**
     * 圆的直径
     */
    private int mRadius;
    /**
     * 绘制盘快的画笔
     */
    private Paint mArcPaint;

    /**
     * 绘制文字的画笔
     */
    private Paint mTextPaint;

    /**
     * 滚动的速度
     */
    private double mSpeed;
    private volatile float mStartAngle = 0;
    /**
     * 是否点击了停止
     */
    private boolean isShouldEnd;

    /**
     * 控件的中心位置
     */
    private int mCenter;
    /**
     * 控件的padding，这里我们认为4个padding的值一致，以paddingleft为标准
     */
    private int mPadding;

    /**
     * 背景图的bitmap
     */
    private Bitmap mBgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.circle_bg);
    /**
     * 文字的大小
     */
    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics());

    public LuckyPanView(Context context) {
        this(context, (AttributeSet)null);
    }

    public LuckyPanView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.mHolder = this.getHolder();
        this.mHolder.addCallback(this);
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        this.setKeepScreenOn(true);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = Math.min(this.getMeasuredWidth(), this.getMeasuredHeight());
        this.mRadius = width - this.getPaddingLeft() - this.getPaddingRight();
        this.mPadding = this.getPaddingLeft();
        this.mCenter = width / 2;
        this.setMeasuredDimension(width, width);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        this.mArcPaint = new Paint();
        this.mArcPaint.setAntiAlias(true);
        this.mArcPaint.setDither(true);
        this.mTextPaint = new Paint();
        this.mTextPaint.setColor(-1);
        this.mTextPaint.setTextSize(this.mTextSize);
        this.mRange = new RectF((float)this.getPaddingLeft(), (float)this.getPaddingLeft(), (float)(this.mRadius + this.getPaddingLeft()), (float)(this.mRadius + this.getPaddingLeft()));
        this.mImgsBitmap = new Bitmap[this.mItemCount];

        for(int i = 0; i < this.mItemCount; ++i) {
            this.mImgsBitmap[i] = BitmapFactory.decodeResource(this.getResources(), this.mImgs[i]);
        }

        this.isRunning = true;
        this.t = new Thread(this);
        this.t.start();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.isRunning = false;
    }

    public void run() {
        while(this.isRunning) {
            long start = System.currentTimeMillis();
            this.draw();
            long end = System.currentTimeMillis();

            try {
                if(end - start < 50L) {
                    Thread.sleep(50L - (end - start));
                }
            } catch (InterruptedException var6) {
                var6.printStackTrace();
            }
        }

    }

    private void draw() {
        try {
            this.mCanvas = this.mHolder.lockCanvas();
            if(this.mCanvas != null) {
                this.drawBg();
                float e = this.mStartAngle;
                float sweepAngle = (float)(360 / this.mItemCount);

                for(int i = 0; i < this.mItemCount; ++i) {
                    this.mArcPaint.setColor(this.mColors[i]);
                    this.mCanvas.drawArc(this.mRange, e, sweepAngle, true, this.mArcPaint);
                    this.drawText(e, sweepAngle, this.mStrs[i]);
                    this.drawIcon(e, i);
                    e += sweepAngle;
                }

                this.mStartAngle = (float)((double)this.mStartAngle + this.mSpeed);
                if(this.isShouldEnd) {
                    --this.mSpeed;
                }

                if(this.mSpeed <= 0.0D) {
                    this.mSpeed = 0.0D;
                    this.isShouldEnd = false;
                }

                this.calInExactArea(this.mStartAngle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(this.mCanvas != null) {
                this.mHolder.unlockCanvasAndPost(this.mCanvas);
            }

        }

    }

    private void drawBg() {
        this.mCanvas.drawColor(-1);
        this.mCanvas.drawBitmap(this.mBgBitmap, (Rect)null, new Rect(this.mPadding / 2, this.mPadding / 2, this.getMeasuredWidth() - this.mPadding / 2, this.getMeasuredWidth() - this.mPadding / 2), (Paint)null);
    }

    public void calInExactArea(float startAngle) {
        float rotate = startAngle + 90.0F;
        rotate = (float)((double)rotate % 360.0D);

        for(int i = 0; i < this.mItemCount; ++i) {
            float from = (float)(360 - (i + 1) * (360 / this.mItemCount));
            float to = from + 360.0F - (float)(i * (360 / this.mItemCount));
            if(rotate > from && rotate < to) {
                Log.d("TAG", this.mStrs[i]);
                return;
            }
        }

    }

    private void drawIcon(float startAngle, int i) {
        int imgWidth = this.mRadius / 8;
        float angle = (float)((double)(30.0F + startAngle) * 0.017453292519943295D);
        int x = (int)((double)this.mCenter + (double)(this.mRadius / 2 / 2) * Math.cos((double)angle));
        int y = (int)((double)this.mCenter + (double)(this.mRadius / 2 / 2) * Math.sin((double)angle));
        Rect rect = new Rect(x - imgWidth / 2, y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2);
        this.mCanvas.drawBitmap(this.mImgsBitmap[i], (Rect)null, rect, (Paint)null);
    }

    private void drawText(float startAngle, float sweepAngle, String string) {
        Path path = new Path();
        path.addArc(this.mRange, startAngle, sweepAngle);
        float textWidth = this.mTextPaint.measureText(string);
        float hOffset = (float)((double)this.mRadius * 3.141592653589793D / (double)this.mItemCount / 2.0D - (double)(textWidth / 2.0F));
        float vOffset = (float)(this.mRadius / 2 / 6);
        this.mCanvas.drawTextOnPath(string, path, hOffset, vOffset, this.mTextPaint);
    }

    public void luckyStart(int luckyIndex) {
        float angle = (float)(360 / this.mItemCount);
        float from = 270.0F - (float)(luckyIndex + 1) * angle;
        float to = from + angle;
        float targetFrom = 1440.0F + from;
        float v1 = (float)(Math.sqrt((double)(1.0F + 8.0F * targetFrom)) - 1.0D) / 2.0F;
        float targetTo = 1440.0F + to;
        float v2 = (float)(Math.sqrt((double)(1.0F + 8.0F * targetTo)) - 1.0D) / 2.0F;
        this.mSpeed = (double)((float)((double)v1 + Math.random() * (double)(v2 - v1)));
        this.isShouldEnd = false;
    }

    public void luckyEnd() {
        this.mStartAngle = 0.0F;
        this.isShouldEnd = true;
    }

    public boolean isStart() {
        return this.mSpeed != 0.0D;
    }

    public boolean isShouldEnd() {
        return this.isShouldEnd;
    }
}