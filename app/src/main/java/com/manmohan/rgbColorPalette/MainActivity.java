package com.manmohan.rgbColorPalette;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
PaletteView paletteView;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        paletteView = findViewById(R.id.palette_view);
        paletteView.setListener(new PaletteView.PaletteListener() {
            @Override
            public void onColorSelected(int color) {
                Log.i(TAG, "onColorSelected: color rgb"+ Color.red(color)+" , "+Color.green(color)+" , "+Color.blue(color));
            }

            @Override
            public void onSelectXY(double x, double y) {

            }
        });
        paletteView.setCENTER_IMAGE_DRAWABLE(ContextCompat.getDrawable(this,R.drawable.bulb));
//        paletteView.setCenterCircleRadius(100);  //for setting center circle radius
//        paletteView.setStrokeWidth(100);  //for setting outer color circle stroke width


    }
}