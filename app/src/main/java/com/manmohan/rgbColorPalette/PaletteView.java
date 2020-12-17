package com.manmohan.rgbColorPalette;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


class PaletteView extends View {
    public static int view_margin = 100;
    private static int stroke_width = 100;
    private static int height,width,centerCircleRadius;
    private PaletteListener mListener;
    private int mPaletteDiameter, previousColor;
    private Paint changingColorCirclePaint, cornerCirclePaint, colorPalettePaint;
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

    private Drawable CENTER_IMAGE_DRAWABLE;
    private RectF oval;
    private boolean pointerMoving = false;


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
        Log.e(TAG, "init:  called");
        mContext = context;

        changingColorCirclePaint = new Paint();
        changingColorCirclePaint.setColor(Color.RED);
        changingColorCirclePaint.setStrokeWidth(1);
        changingColorCirclePaint.setStyle(Paint.Style.FILL);
        changingColorCirclePaint.setAntiAlias(true);

        cornerCirclePaint = new Paint();
        cornerCirclePaint.setColor(Color.WHITE);
        cornerCirclePaint.setStrokeWidth(stroke_width / 8f);
        cornerCirclePaint.setStyle(Paint.Style.STROKE);
        cornerCirclePaint.setAntiAlias(true);
        cornerCirclePaint.setShadowLayer(10f, 2.0f, 2.0f, 0x80000000);


        colorPalettePaint = new Paint();
        colorPalettePaint.setStyle(Paint.Style.FILL);
        colorPalettePaint.setAntiAlias(true);
        // oval = getRectangle(palletMargin);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e(TAG, "onSizeChanged: called " + h+" "+w);

        if(w == 0&&h==0){
            width = 100;
            height = 100;
            Log.e(TAG, "onSizeChanged: 1" );
        }else if(h == 0){
            width = height = w;

            Log.e(TAG, "onSizeChanged: 2" );
        }else if(w==0){
            height = width = h;
            Log.e(TAG, "onSizeChanged: 3" );
        }else{
            height = h;
            width = w;
            Log.e(TAG, "onSizeChanged: 4" );
        }
        Log.e(TAG, "onSizeChanged: "+height+" "+width );
        if (width < height) {
            cornerCircleX = width - view_margin;
        } else {
            cornerCircleX = (((width - height) / 2f) + height) - view_margin;
        }

        cornerCircleY = height / 2f;
        oval = getRectangle(view_margin);


    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        Log.e(TAG, "onDraw: called" );
        drawColorPallet(canvas);
        drawColorCircle(canvas);
        drawBulbImage(canvas);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    void drawColorPallet(Canvas canvas) {
        Shader gradient = new SweepGradient(width / 2f, height / 2f, colors, positions);
        colorPalettePaint.setShader(gradient);
        colorPalettePaint.setStyle(Paint.Style.STROKE);
        colorPalettePaint.setStrokeWidth(stroke_width);


        canvas.drawArc(oval, 0f, 360f, true, colorPalettePaint);

        this.setOnTouchListener(paletteTouchListener);
    }


    OnTouchListener paletteTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final float evX = event.getX();
            final float evY = event.getY();
            double clickDistance = Math.sqrt(Math.pow(evX - getPaletteCenterX(), 2) + Math.pow(evY - getPaletteCenterY(), 2));
            float innerRadius = (mPaletteDiameter / 2f) - (stroke_width / 2f);
            float outerRadius = (mPaletteDiameter / 2f) + (stroke_width / 2f);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    if ((clickDistance > innerRadius) && (clickDistance < outerRadius)) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        pointerMoving = true;
                        updateCornerCircleLocation(evX, evY);

                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (pointerMoving) {
                        updateCornerCircleLocation(evX, evY);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    pointerMoving = false;
                    performClick();
                    break;
            }
            return true;
        }
    };

    private void drawColorCircle(Canvas canvas) {

        canvas.drawCircle(cornerCircleX, cornerCircleY, (stroke_width / 2f) - (stroke_width / 10f), cornerCirclePaint);
        canvas.drawCircle(cornerCircleX, cornerCircleY, (stroke_width / 2f) - (stroke_width / 8f), changingColorCirclePaint);
        canvas.drawCircle(width / 2f, height / 2f, centerCircleRadius == 0 ? mPaletteDiameter / 4f : centerCircleRadius, changingColorCirclePaint);
    }

    private void drawBulbImage(Canvas canvas) {
        if (CENTER_IMAGE_DRAWABLE != null) {
            int height = mPaletteDiameter / 4;
            int width = mPaletteDiameter / 4;
            CENTER_IMAGE_DRAWABLE.setBounds(0, 0, width, height);
            canvas.translate(getPaletteCenterX() - (height / 2f), getPaletteCenterY() - (width / 2f));
            CENTER_IMAGE_DRAWABLE.draw(canvas);
        }

    }

    private void changeCircleColorWithXY(int color, float x, float y) {
        changingColorCirclePaint.setColor(color);
        cornerCircleX = x;
        cornerCircleY = y;
        invalidate();
    }


    private void changeCircleXY(float x, float y) {
        cornerCircleX = x;
        cornerCircleY = y;
        invalidate();
    }

    private void updateCornerCircleLocation(double x, double y) {
        findCircleCornerPointAndUpdateListener(getPaletteCenterX(), getPaletteCenterY(), x, y, (mPaletteDiameter / 2f));
    }

    private float getPaletteCenterX() {
        return width / 2f;
    }

    private float getPaletteCenterY() {
        return height / 2f;
    }

    private RectF getRectangle(int margin2) {
        int right, left, top, bottom, margin;
        margin = margin2;
        if (width < height) {
            left = margin;
            right = (width - margin);
            mPaletteDiameter = right - left;
            top = ((height - mPaletteDiameter) / 2);
            bottom = ((mPaletteDiameter + top));
        } else {
            top = margin;
            bottom = (height - margin);
            mPaletteDiameter = bottom - top;
            left = ((width - mPaletteDiameter) / 2);
            right = ((mPaletteDiameter + left));
        }

        return new RectF(left, top, right, bottom);
    }

    private int calculateColor(float angle) {
        float unit = (float) (angle / (2 * Math.PI));
        if (unit < 0) {
            unit += 1;
        }
        if (unit <= 0) {
            return colors[0];
        }
        if (unit >= 1) {

            return colors[colors.length - 1];
        }

        float p = unit * (colors.length - 1);
        int i = (int) p;
        p -= i;

        int c0 = colors[i];
        int c1 = colors[i + 1];
        int a = ave(Color.alpha(c0), Color.alpha(c1), p);
        int r = ave(Color.red(c0), Color.red(c1), p);
        int g = ave(Color.green(c0), Color.green(c1), p);
        int b = ave(Color.blue(c0), Color.blue(c1), p);

        return Color.argb(a, r, g, b);
    }

    private int ave(int s, int d, float p) {
        return s + Math.round(p * (d - s));
    }

    private void findCircleCornerPointAndUpdateListener(double x1, double y1, double x2, double y2, double diatanceOfThirdPoint) {

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
            float touchAngle = (float) Math.atan2(y - y1, x - x1);

            int mCurrentColor = calculateColor(touchAngle);
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


    public void setStrokeWidth(int w) {
        stroke_width = w;
        invalidate();
    }

    public void setCenterCircleRadius(int radius){
        centerCircleRadius = radius;
        invalidate();
    }

    public void setCENTER_IMAGE_DRAWABLE(Drawable drawable) {
        CENTER_IMAGE_DRAWABLE = drawable;
        invalidate();
    }

    public void setListener(PaletteListener listener) {
        mListener = listener;

        // We'll start listening for touches now that the implementer cares about them
        if (listener == null) {
            setOnTouchListener(null);
        }
    }


    public interface PaletteListener {
        void onColorSelected(int color);

        void onSelectXY(double x, double y);

    }
}
