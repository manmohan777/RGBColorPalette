# RGBColorPalette



<img src="readmeFiles/demo.gif" width="200">

Created a new design for color picker in android.



<h2>Documentation</h2>

For using this color picker you have to copy  <b> "PaletteView.java" </b> file into your project Directory

In your layout file add this to your xml
```xml
<com.yourOrganisation.yourProjectName.PaletteView
    android:id="@+id/palette_view"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"/>
```       
    
Replace "com.yourOrganisation.yourProjectName" with your package name.

In your java class you can use it like the following

```java

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
        paletteView.setStrokeWidth(100);
        paletteView.setCENTER_IMAGE_DRAWABLE(ContextCompat.getDrawable(this,R.drawable.bulb));

    }
}

```

<b> public void onColorSelected(int color) </b> is a callback function which gives the color selected by user.

<b>  public void onSelectXY(double x, double y) </b>  is a callback function which gives x and y coordinate selected by user.

<b> paletteView.setStrokeWidth(int i ) </b>  is used to set the stroke width of the color palette.

<b> paletteView.setCENTER_IMAGE_DRAWABLE(Drawable drawable) </b> is used to set the center image.

<H2>License</H2>
	
 	 Copyright 2020 Manmohan Singh Chauhan
 	
 	 Licensed under the Apache License, Version 2.0 (the "License");
 	 you may not use this file except in compliance with the License.
 	 You may obtain a copy of the License at
 	
 	     http://www.apache.org/licenses/LICENSE-2.0
 	
 	 Unless required by applicable law or agreed to in writing, software
	 distributed under the License is distributed on an "AS IS" BASIS,
 	 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 	 See the License for the specific language governing permissions and
 	 limitations under the License.
 	

<h2>Devoleped By</h2>
MANMOHAN SINGH CHAUHAN


