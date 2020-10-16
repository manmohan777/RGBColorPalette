package com.manmohan.rgbColorPalette;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

class PaletteView extends View {
    public static int VIEW_MARGIN = 100;
    private int STROKE_WIDTH = 100;
    private static Bitmap imgbmp;
    private PaletteListener mListener;
    private View mView;
    private int mPaletteDiameter, previousColor;
    private Paint circlePaint, centerCirclePaint, colorPalettePaint;
    private float cornerCircleX, cornerCircleY;
    private float[] positions = {0.0f,
            1 / 6f, 2 / 6f, 3 / 6f,
            4 / 6f, 5 / 6f, 1.0f};

    final int RED = Color.rgb(255, 0, 0);
    final int YELLOW = Color.rgb(255, 255, 0);
    final int GREEN = Color.rgb(0, 255, 0);
    final int TEAL = Color.rgb(0, 255, 255);
    final int BLUE = Color.rgb(0, 0, 255);
    final int VIOLET = Color.rgb(255, 0, 255);

    private int[] colors = {RED, VIOLET, BLUE, TEAL, GREEN, YELLOW, RED};
    private static boolean bitmapFlag = false;

    private Drawable CENTER_IMAGE_DRAWABLE;
    private RectF oval;


    private static final String TAG = "PaletteView";
    Context mContext;

    public PaletteView(Context context) {
        super(context);
        init(context);
    }

    public PaletteView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public PaletteView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    private void init(Context context) {
        Log.e(TAG, "init:  called" );
        mContext = context;
        mView = this;
        bitmapFlag = false;

        circlePaint = new Paint();
        circlePaint.setColor(Color.RED);
        circlePaint.setStrokeWidth(1);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        circlePaint.setAntiAlias(true);
        if (STROKE_WIDTH >= 80)
            circlePaint.setShadowLayer(10f, 0.0f, 0.0f, 0x80000000);

//        CENTER_IMAGE_DRAWABLE = ContextCompat.getDrawable(mContext, R.drawable.bulb);

        centerCirclePaint = new Paint();
        centerCirclePaint.setColor(Color.WHITE);
        centerCirclePaint.setStrokeWidth(STROKE_WIDTH / 10f);
        centerCirclePaint.setStyle(Paint.Style.STROKE);
        centerCirclePaint.setAntiAlias(true);
        if (STROKE_WIDTH >= 80)
            centerCirclePaint.setShadowLayer(10f, 2.0f, 2.0f, 0x80000000);


        colorPalettePaint = new Paint();
        colorPalettePaint.setStyle(Paint.Style.FILL);
        colorPalettePaint.setAntiAlias(true);
        // oval = getRectangle(palletMargin);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e(TAG, "onSizeChanged: called" );
        if (w < h) {
            cornerCircleX = w - VIEW_MARGIN;
            cornerCircleY = h / 2f;
        } else {
            cornerCircleX = (((w - h) / 2f) + h) - VIEW_MARGIN;
            cornerCircleY = h / 2f;
        }
        oval = getRectangle(VIEW_MARGIN);

        if(!bitmapFlag) {
            mView.setDrawingCacheEnabled(true);
            try {
                imgbmp = mView.getDrawingCache() != null ? Bitmap.createBitmap(mView.getDrawingCache()) : imgbmp;
            } catch (Exception e) {
                Log.e(TAG, "onSizeChanged: ", e);
            }
            mView.setDrawingCacheEnabled(false);
            bitmapFlag = true;
            invalidate();
        }
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        drawColorPallet(canvas);
        if (bitmapFlag) {
            drawColorCircle(canvas);
            drawBulbImage(canvas);


        }
    }

    @SuppressLint("ClickableViewAccessibility")
    void drawColorPallet(Canvas canvas) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Shader gradient = new SweepGradient(this.getWidth() / 2f, this.getHeight() / 2f, colors, positions);
            colorPalettePaint.setShader(gradient);
            colorPalettePaint.setStyle(Paint.Style.STROKE);
            colorPalettePaint.setStrokeWidth(STROKE_WIDTH);


            canvas.drawArc(oval, 0f, 360f, true, colorPalettePaint);

            this.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final float evX = event.getX();
                    final float evY = event.getY();


                    try {
                        double clickDistance = Math.sqrt(Math.pow(evX - getPaletteCenterX(), 2) + Math.pow(evY - getPaletteCenterY(), 2));
                        float innerRadius = (mPaletteDiameter / 2f) - (STROKE_WIDTH / 2f);
                        float outerRadius = (mPaletteDiameter / 2f) + (STROKE_WIDTH / 2f);
                        if ((clickDistance > innerRadius) && (clickDistance < outerRadius)) {
                            updateCornerCircleLocation(evX, evY);
                        }
                    } catch (Exception ignore) {
                    }

                    return true;
                }


            });
        }
    }

    private void drawColorCircle(Canvas canvas) {

        canvas.drawCircle(cornerCircleX, cornerCircleY, (STROKE_WIDTH / 2f) - (STROKE_WIDTH / 10f), centerCirclePaint);
        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, mPaletteDiameter / 6f, circlePaint);
    }

    private void drawBulbImage(Canvas canvas) {
        if (CENTER_IMAGE_DRAWABLE != null) {
            int height = mPaletteDiameter / 6;
            int width = mPaletteDiameter / 6;
            CENTER_IMAGE_DRAWABLE.setBounds(0, 0, width, height);
            canvas.translate(getPaletteCenterX() - (height / 2f), getPaletteCenterY() - (width / 2f));
            CENTER_IMAGE_DRAWABLE.draw(canvas);
        }

    }

    private void changeCircleColorWithXY(int color, float x, float y) {
        circlePaint.setColor(color);
        cornerCircleX = x;
        cornerCircleY = y;
        invalidate();
    }


    private void changeCircleXY(float x, float y) {
        cornerCircleX = x;
        cornerCircleY = y;
        invalidate();
    }


    public void setStrokeWidth(int w) {
        STROKE_WIDTH = w;
        invalidate();
    }

    public void setCENTER_IMAGE_DRAWABLE(Drawable drawable) {
        CENTER_IMAGE_DRAWABLE = drawable;
        invalidate();
    }

    private void updateCornerCircleLocation(double x, double y) {
        findCircleCornerPointAndUpdateListener(getPaletteCenterX(), getPaletteCenterY(), x, y, (mPaletteDiameter / 2f));
    }

    public void findCircleCornerPointAndUpdateListener(double x1, double y1, double x2, double y2, double diatanceOfThirdPoint) {

//    calculate distance between the two points
        double diatanceOfTwoPoints = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
        double x;
        double y;
        double T = diatanceOfThirdPoint / diatanceOfTwoPoints;
        // Math.toRadians(30);
        // finding point C coordinate
        x = (1 - T) * x1 + T * x2;
        y = (1 - T) * y1 + T * y2;
//        for getting color of given x and y
        try {
//            Log.e(TAG, "findCircleCornerPointAndUpdateListener: angle" + Math.toDegrees(Math.atan2(y - y1, x - x1)));
//            Log.e(TAG, "findCircleCornerPointAndUpdateListener: (x,y) , center(x2,y2), (" + x + " ," + y + "), (" + x1 + " ," + y1 + "), " + diatanceOfThirdPoint);

            int mCurrentColor = imgbmp.getPixel((int) x, (int) y);
            if (mCurrentColor != previousColor) {
                // changeCircleColor(mCurrentColor);
                mListener.onSelectXY(x, y);
                if (mCurrentColor != Color.WHITE && (Color.red(mCurrentColor) == 255
                        || Color.green(mCurrentColor) == 255
                        || Color.blue(mCurrentColor) == 255)) {
                    changeCircleColorWithXY(mCurrentColor, (float) x, (float) y);
                    mListener.onColorSelected(mCurrentColor);
                    previousColor = mCurrentColor;
                } else {
                    changeCircleXY((float) x, (float) y);
                }
            }
        } catch (Exception avoid) {
            Log.e(TAG, "findCircleCornerPointAndUpdateListener: " + avoid);
        }
    }


    public void setListener(PaletteListener listener) {
        mListener = listener;

        // We'll start listening for touches now that the implementer cares about them
        if (listener == null) {
            setOnTouchListener(null);
        }
    }




    public float getPaletteRadius() {
        return mPaletteDiameter / 2f;
    }

    public float getPaletteCenterX() {
        return mView.getWidth() / 2f;
    }

    public float getPaletteCenterY() {
        return mView.getHeight() / 2f;
    }


    private RectF getRectangle(int margin2) {
        int right, left, top, bottom, margin;
        margin = margin2;
        if (this.getWidth() < this.getHeight()) {
            left = margin;
            right = (this.getWidth() - margin);
            mPaletteDiameter = right - left;
            top = ((this.getHeight() - mPaletteDiameter) / 2);
            bottom = ((mPaletteDiameter + top));
        } else {
            top = margin;
            bottom = (this.getHeight() - margin);
            mPaletteDiameter = bottom - top;
            left = ((this.getWidth() - mPaletteDiameter) / 2);
            right = ((mPaletteDiameter + left));
        }

        return new RectF(left, top, right, bottom);
    }


    public interface PaletteListener {
        void onColorSelected(int color);
        void onSelectXY(double x, double y);

    }
}
