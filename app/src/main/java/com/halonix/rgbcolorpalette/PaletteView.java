package com.halonix.rgbcolorpalette;

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
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

class PaletteView extends View {
    private int stroke_width = 100;
    private Bitmap imgbmp;
    private PaletteListener mListener;
    private View mView;
    private int right, left, top, bottom, margin, mWidth, mCurrentColor, previousColor, palletMargin = 100;
    private Paint circlePaint;
    private float cornerCircleX, cornerCircleY;
    private float[] positions = {0.0f,
            1 / 6f, 2 / 6f, 3 / 6f,
            4 / 6f, 5 / 6f, 1.0f};
    final int RED = Color.rgb(255, 0, 0);
    final int YELLOW = Color.rgb(255, 255, 0);
    final int GREEN = Color.rgb(0, 255, 0);
    final int TEAL = Color.rgb(128, 255, 255);
    final int BLUE = Color.rgb(0, 0, 255);
    final int VIOLET = Color.rgb(255, 0, 255);

    private int[] colors = {RED, VIOLET, BLUE, TEAL, GREEN, YELLOW, RED};
    Drawable mDrawable;


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
        mContext = context;
        mView = this;
        circlePaint = new Paint();
        circlePaint.setColor(Color.RED);
        circlePaint.setStrokeWidth(1);
        circlePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        circlePaint.setAntiAlias(true);
        circlePaint.setShadowLayer(10f, 0.0f, 0.0f, 0x80000000);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDrawable = mContext.getDrawable(R.drawable.bulb);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        cornerCircleX = w - palletMargin;
        cornerCircleY = h / 2;
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        drawColorPallet(canvas);
        drawColorCircle(canvas);
        drawBulbImage(canvas);
    }

    @SuppressLint("ClickableViewAccessibility")
    void drawColorPallet(Canvas canvas) {
        final Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);

//      to make the entire canvas white
//      paint.setColor(Color.WHITE);
//      canvas.drawPaint(paint);

        paint.setAntiAlias(true);
        paint.setColor(Color.GREEN);
        //  Log.e(TAG, "onDraw: "+this.getX() );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            paint.setColor(Color.RED);
            Shader gradient = new SweepGradient(this.getWidth() / 2, this.getHeight() / 2, colors, positions);
            paint.setShader(gradient);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(stroke_width);
            RectF oval = getRectangle(palletMargin);
            canvas.drawArc(oval, 0f, 360f, true, paint);

            this.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final float evX = event.getX();
                    final float evY = event.getY();

                    mView.setDrawingCacheEnabled(true);
                    imgbmp = Bitmap.createBitmap(mView.getDrawingCache());
                    mView.setDrawingCacheEnabled(false);
                    try {
                        Double clickDistance = Math.sqrt(Math.pow(evX - getPaletteCenterX(), 2) + Math.pow(evY - getPaletteCenterY(), 2));
                        float innerRadis = (mWidth / 2) - (stroke_width / 2);
                        float outerRadius = (mWidth / 2) + (stroke_width / 2);
                        if (clickDistance > innerRadis && clickDistance < outerRadius) {
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
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(stroke_width / 10);
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        if (stroke_width >= 80)
            paint.setShadowLayer(10f, 2.0f, 2.0f, 0x80000000);

        canvas.drawCircle(cornerCircleX, cornerCircleY, (stroke_width / 2) - (stroke_width / 10), paint);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, getWidth() / 6, circlePaint);
    }

    private void drawBulbImage(Canvas canvas){
        if(mDrawable!=null){
            int height =300;
            int width = 300;
            mDrawable.setBounds(0,0,height , width);
            canvas.translate(getPaletteCenterX()-(height/2),getPaletteCenterY()-(width/2));
            mDrawable.draw(canvas);
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
        stroke_width = w;
        invalidate();
    }

    private void updateCornerCircleLocation(double x, double y) {
        findCircleCornerPointAndUpdateListener(getPaletteCenterX(), getPaletteCenterY(), x, y, (mWidth / 2));
    }

    public void findCircleCornerPointAndUpdateListener(double x1, double y1, double x2, double y2, double diatanceOfThirdPoint) {

//    calculate distance between the two points
        double diatanceOfTwoPoints = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
        double x;
        double y;
        double T = diatanceOfThirdPoint / diatanceOfTwoPoints;

        // finding point C coordinate
        x = (1 - T) * x1 + T * x2;
        y = (1 - T) * y1 + T * y2;
//        for getting color of given x and y
        try {
            mCurrentColor = imgbmp.getPixel((int) x, (int) y);
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

        }
    }


    public void setListener(PaletteListener listener) {
        mListener = listener;

        // We'll start listening for touches now that the implementer cares about them
        if (listener == null) {
            setOnTouchListener(null);
        } else {
//            setOnTouchListener(mTouchListener);
            // Notify the listener of our current color
            //   mListener.onColorSelected(mCurrentColor);
        }
    }

    public interface PaletteListener {
        void onColorSelected(int color);

        void onSelectXY(double x, double y);
    }


    public float getPaletteRadius() {
        return mWidth / 2;
    }

    public float getPaletteCenterX() {
        return mView.getWidth() / 2;
    }

    public float getPaletteCenterY() {
        return mView.getHeight() / 2;
    }


    private RectF getRectangle(int margin2) {
        margin = margin2;
        left = margin;
        right = (this.getWidth() - margin);
        mWidth = right - left;
        top = ((this.getHeight() - mWidth) / 2);
        bottom = ((mWidth + top));
        return new RectF(left, top, right, bottom);
    }
}
